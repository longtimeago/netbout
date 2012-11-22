/**
 * Copyright (c) 2009-2012, Netbout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
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
package com.netbout.inf;

import com.jcabi.log.Logger;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Folder in file system.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public interface Folder extends Closeable {

    /**
     * Get its full path.
     * @return The path
     * @throws IOException If some IO problem inside
     */
    File path() throws IOException;

    /**
     * Plain simple implementation of the folder.
     */
    final class Plain implements Folder {
        /**
         * The directory.
         */
        private final transient File directory;
        /**
         * Public ctor.
         * @param path Directory, where to mount locally
         * @throws IOException If some error inside
         */
        public Plain(@NotNull final File path) throws IOException {
            this.directory = path;
            Logger.debug(this, "#Plain(%s): ready to serve", path);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws IOException {
            Logger.debug(this, "#close(): nothing to do");
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return String.format(
                "plain:%s",
                this.directory.getAbsolutePath()
            );
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public File path() throws IOException {
            return this.directory;
        }
    }

}
