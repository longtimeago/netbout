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
package com.netbout.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import com.netbout.spi.Attachment;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;

/**
 * Cached attachment.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 2.4
 */
@Immutable
@ToString
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "sql", "bout", "label" })
final class MkAttachment implements Attachment {

    /**
     * SQL data source provider.
     */
    private final transient Sql sql;

    /**
     * Bout.
     */
    private final transient long bout;

    /**
     * Attachment name.
     */
    private final transient String label;

    /**
     * Public ctor.
     * @param src Source
     * @param bot Bout number
     * @param name Name
     */
    MkAttachment(final Sql src, final long bot, final String name) {
        this.sql = src;
        this.bout = bot;
        this.label = name;
    }

    @Override
    public String name() {
        return this.label;
    }

    @Override
    public String ctype() throws IOException {
        try {
            return new JdbcSession(this.sql.source())
                .sql("SELECT ctype FROM attachment WHERE bout = ? AND name = ?")
                .set(this.bout)
                .set(this.label)
                .select(new SingleOutcome<String>(String.class));
        } catch (final SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public boolean unseen() throws IOException {
        throw new UnsupportedOperationException("#unseen()");
    }

    @Override
    public InputStream read() throws IOException {
        try {
            return IOUtils.toInputStream(
                new JdbcSession(this.sql.source())
                    // @checkstyle LineLength (1 line)
                    .sql("SELECT data FROM attachment WHERE bout = ? AND name = ?")
                    .set(this.bout)
                    .set(this.label)
                    .select(new SingleOutcome<String>(String.class)),
                CharEncoding.UTF_8
            );
        } catch (final SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(final InputStream stream, final String ctype)
        throws IOException {
        try {
            new JdbcSession(this.sql.source())
                // @checkstyle LineLength (1 line)
                .sql("UPDATE attachment SET data = ? WHERE bout = ? AND name = ?")
                .set(IOUtils.toString(stream, CharEncoding.UTF_8))
                .set(this.bout)
                .set(this.label)
                .update(Outcome.VOID);
        } catch (final SQLException ex) {
            throw new IOException(ex);
        }
    }
}
