/**
 * Copyright (c) 2009-2014, netbout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netbout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code accidentally and without intent to use it, please report this
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
package com.netbout.servlets;

import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import com.jcabi.manifests.Manifests;
import com.netbout.cached.CdBase;
import com.netbout.dynamo.DyBase;
import com.netbout.spi.Base;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application-wide listener that initializes the application on start
 * and shuts it down on stop.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Loggable(Loggable.DEBUG)
public final class LifecycleListener implements ServletContextListener {

    /**
     * The spi.
     */
    private transient Base base;

    /**
     * When was it started.
     */
    private final transient long start = System.currentTimeMillis();

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        try {
            Manifests.append(event.getServletContext());
        } catch (final IOException ex) {
            Logger.error(
                this, "#contextInitialized(): %[exception]s", ex
            );
            throw new IllegalStateException(ex);
        }
        this.base = new CdBase(new DyBase());
        event.getServletContext().setAttribute(Base.class.getName(), this.base);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        if (this.base == null) {
            Logger.warn(this, "#contextDestroyed(): HUB is null");
        } else {
            try {
                this.base.close();
            } catch (final IOException ex) {
                Logger.error(
                    this,
                    "#contextDestroyed(): %[exception]s",
                    ex
                );
            }
        }
        Logger.info(
            this,
            "#contextDestroyed(): app was alive for %[ms]s",
            System.currentTimeMillis() - this.start
        );
    }

}
