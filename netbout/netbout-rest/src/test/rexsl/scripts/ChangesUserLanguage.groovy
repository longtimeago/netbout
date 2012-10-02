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
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
package com.netbout.rest.rexsl.scripts

import com.netbout.spi.Urn
import com.netbout.spi.client.RestSession
import com.netbout.spi.client.RestUriBuilder
import com.rexsl.test.RestTester
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

def misha = new RestSession(rexsl.home).authenticate(new Urn('urn:test:misha'), '')

RestTester.start(RestUriBuilder.from(misha))
    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML)
    .get('render front page of user')
    .assertStatus(HttpURLConnection.HTTP_OK)
    .assertXPath('/page/links/link[@rel="profile"]')
    .rel('/page/links/link[@rel="profile"]/@href')
    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML)
    .get('render user profile')
    .assertStatus(HttpURLConnection.HTTP_OK)
    .assertXPath('/page/links[not(link[@rel="search"])]')
    .assertXPath('/page/profile/locales/link[@rel="locale" and code="ru"]')
    .rel('/page/profile/locales/link[code="ru"]/@href')
    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML)
    .get('changing language to Russian')
    .assertStatus(HttpURLConnection.HTTP_SEE_OTHER)
    .follow()
    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML)
    .get('reading front page again')
    .assertStatus(HttpURLConnection.HTTP_OK)
    .assertXPath('/page/identity[locale="ru"]')

MatcherAssert.assertThat(misha.profile().locale(), Matchers.equalTo(new Locale('ru')))
