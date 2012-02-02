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
package com.netbout.inf.predicates;

import com.netbout.inf.Meta;
import com.netbout.inf.Msg;
import com.netbout.inf.Predicate;
import com.netbout.inf.PredicateException;
import com.netbout.spi.Message;
import com.netbout.spi.Urn;
import java.util.Date;
import java.util.Map;

/**
 * Variable.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
@Meta(extracts = true)
public final class VariablePred implements Predicate {

    /**
     * The value of it.
     */
    private final transient String name;

    /**
     * Public ctor.
     * @param value The value of it
     */
    public VariablePred(final String value) {
        this.name = value;
    }

    /**
     * Extracts necessary data from message.
     * @param msg The message to extract from
     * @param props Where to extract
     */
    public static void extract(final Message msg,
        final Map<String, Object> props) {
        // ...
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Msg msg, final int pos) {
        Object value;
        if ("text".equals(this.name)) {
            value = msg.<String>get("text");
        } else if ("bout.number".equals(this.name)) {
            value = msg.bout();
        } else if ("bout.date".equals(this.name)) {
            value = msg.<Date>get("bout.date");
        } else if ("bout.recent".equals(this.name)) {
            value = msg.<Date>get("bout.recent");
        } else if ("bout.title".equals(this.name)) {
            value = msg.<String>get("bout.title");
        } else if ("number".equals(this.name)) {
            value = msg.number();
        } else if ("date".equals(this.name)) {
            value = msg.<Date>get("date");
        } else if ("author.name".equals(this.name)) {
            value = msg.<Urn>get("author.name");
        } else if ("author.alias".equals(this.name)) {
            value = msg.<String>get("author.alias");
        } else {
            throw new PredicateException(
                String.format("Unknown variable '$%s'", this.name)
            );
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("$%s", this.name);
    }

}
