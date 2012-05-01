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
package com.netbout.inf.ray;

import java.util.Random;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case of {@link DefaultIndex}.
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class DefaultIndexTest {

    /**
     * DefaultIndex can replace values.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void replacesValues() throws Exception {
        final Index index = new DefaultIndex();
        final long msg = new Random().nextLong();
        final String value = "some text \u0433!";
        index.add(msg, "first value");
        index.add(msg, "second value");
        index.replace(msg, value);
        MatcherAssert.assertThat(
            index.values(msg),
            Matchers.allOf(
                (Matcher) Matchers.hasSize(1),
                Matchers.hasItem(value)
            )
        );
        MatcherAssert.assertThat(
            index.msgs(value),
            Matchers.hasItem(msg)
        );
    }

    /**
     * DefaultIndex can order message numbers propertly.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void ordersNumbersProperly() throws Exception {
        final Index index = new DefaultIndex();
        final long msg = new Random().nextLong();
        final String value = "text-\u0433!";
        for (int pos = 1; pos < 10; ++pos) {
            index.add(msg - pos, value);
        }
        long before = msg;
        for (Long num : index.msgs(value)) {
            MatcherAssert.assertThat(
                num,
                Matchers.lessThan(before)
            );
            before = num;
        }
    }

}
