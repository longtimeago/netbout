/**
 * Copyright (c) 2009-2011, netBout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code occasionally and without intent to use it, please report this
 * incident to the author by email.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.netbout.hub;

import com.netbout.bus.Bus;
import com.netbout.spi.Bout;
import com.netbout.spi.BoutNotFoundException;
import com.netbout.spi.Identity;
import com.netbout.spi.UnreachableIdentityException;
import com.ymock.util.Logger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Identity.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class HubIdentityOrphan implements Identity {

    /**
     * Default photo of identity.
     */
    private static final String DEFAULT_PHOTO =
        "http://img.netbout.com/unknown.png";

    /**
     * The bus.
     */
    private final transient Bus bus;

    /**
     * The catalog.
     */
    private final transient Catalog catalog;

    /**
     * The manager of bouts.
     */
    private final transient BoutMgr manager;

    /**
     * The name.
     */
    private final transient String iname;

    /**
     * The photo.
     */
    private transient URL iphoto;

    /**
     * List of bouts where I'm a participant.
     */
    private transient Set<Long> ibouts;

    /**
     * List of aliases.
     */
    private transient Set<String> ialiases;

    /**
     * Public ctor.
     * @param ibus The bus
     * @param ctlg The catalog
     * @param mgr Manager of bouts
     * @param name The identity's name
     * @checkstyle ParameterNumber (3 lines)
     */
    public HubIdentityOrphan(final Bus ibus, final Catalog ctlg,
        final BoutMgr mgr, final String name) {
        this.bus = ibus;
        this.catalog = ctlg;
        this.manager = mgr;
        this.iname = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Identity)
            && this.name().equals(((Identity) obj).name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String user() {
        throw new IllegalStateException(
            String.format(
                "Identity '%s' is not assigned to any user yet",
                this.name()
            )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return this.iname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bout start() {
        final Long num = this.manager.create();
        BoutDt data;
        try {
            data = this.manager.find(num);
        } catch (com.netbout.spi.BoutNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
        final ParticipantDt dude = data.addParticipant(this.name());
        dude.setConfirmed(true);
        Logger.debug(
            this,
            "#start(): bout #%d started",
            num
        );
        this.myBouts().add(num);
        return new HubBout(this.catalog, this.bus, this, data);
    }

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (4 lines)
     */
    @Override
    public Bout bout(final Long number) throws BoutNotFoundException {
        final HubBout bout;
        bout = new HubBout(
            this.catalog,
            this.bus,
            this,
            this.manager.find(number)
        );
        return bout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bout> inbox(final String query) {
        final List<Bout> list = new ArrayList<Bout>();
        for (Long num : this.myBouts()) {
            try {
                list.add(this.bout(num));
            } catch (com.netbout.spi.BoutNotFoundException ex) {
                throw new IllegalStateException(ex);
            }
        }
        Logger.debug(
            this,
            "#inbox('%s'): %d bouts found",
            query,
            list.size()
        );
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL photo() {
        if (this.iphoto == null) {
            final String url = this.bus.make("get-identity-photo")
                .synchronously()
                .arg(this.name())
                .asDefault(this.DEFAULT_PHOTO)
                .exec();
            this.iphoto = new PhotoProxy(this.DEFAULT_PHOTO).normalize(url);
        }
        return this.iphoto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhoto(final URL url) {
        synchronized (this) {
            this.iphoto = new PhotoProxy(this.DEFAULT_PHOTO).normalize(url);
        }
        this.bus.make("identity-mentioned")
            .synchronously()
            .arg(this.name())
            .asDefault(true)
            .exec();
        this.bus.make("changed-identity-photo")
            .synchronously()
            .arg(this.name())
            .arg(this.iphoto.toString())
            .asDefault(true)
            .exec();
    }

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (4 lines)
     */
    @Override
    public Identity friend(final String name)
        throws UnreachableIdentityException {
        final Identity identity = this.catalog.make(name);
        Logger.debug(
            this,
            "#friend('%s'): found",
            name
        );
        return identity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Identity> friends(final String keyword) {
        final Set<Identity> friends = this.catalog.findByKeyword(keyword);
        Logger.debug(
            this,
            "#friends('%s'): found %d friends",
            keyword,
            friends.size()
        );
        return friends;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> aliases() {
        final Set<String> list = new HashSet<String>(this.myAliases());
        Logger.debug(
            this,
            "#aliases(): %d returned",
            list.size()
        );
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void alias(final String alias) {
        if (this.myAliases().contains(alias)) {
            Logger.debug(
                this,
                "#alias('%s'): it's already set for '%s'",
                alias,
                this.name()
            );
        } else {
            this.bus.make("added-identity-alias")
                .asap()
                .arg(this.name())
                .arg(alias)
                .asDefault(true)
                .exec();
            Logger.debug(
                this,
                "#alias('%s'): added for '%s'",
                alias,
                this.name()
            );
            this.myAliases().add(alias);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invited(final Bout bout) {
        this.myBouts().add(bout.number());
    }

    /**
     * Return a link to my list of bouts.
     * @return The list of them
     */
    private Set<Long> myBouts() {
        synchronized (this) {
            if (this.ibouts == null) {
                this.ibouts = new CopyOnWriteArraySet<Long>(
                    (List<Long>) this.bus
                        .make("get-bouts-of-identity")
                        .synchronously()
                        .arg(this.name())
                        .asDefault(new ArrayList<Long>())
                        .exec()
                );
            }
        }
        return this.ibouts;
    }

    /**
     * Returns a link to the list of aliases.
     * @return The link to the list of them
     */
    private Set<String> myAliases() {
        synchronized (this) {
            if (this.ialiases == null) {
                this.ialiases = new CopyOnWriteArraySet<String>(
                    (List<String>) this.bus
                        .make("get-aliases-of-identity")
                        .synchronously()
                        .arg(this.name())
                        .asDefault(new ArrayList<String>())
                        .exec()
                );
            }
        }
        return this.ialiases;
    }

}
