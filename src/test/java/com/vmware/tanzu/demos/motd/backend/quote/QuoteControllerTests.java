/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.motd.backend.quote;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class QuoteControllerTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private QuoteHistoryRepository repo;

    @Test
    void getQuote() {
        repo.deleteAll();

        final var resp = rest.getForEntity("/api/v1/quote", Quote.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody().message()).isNotEmpty();
        assertThat(resp.getBody().source()).isNotEmpty();
        assertThat(resp.getHeaders().get("MOTD-Backend-Source")).isNotEmpty();

        final var resp2 = rest.getForEntity("/api/v1/quote", Quote.class);
        assertThat(resp2.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp2).isEqualTo(resp);
    }

    @Test
    void refreshQuote() {
        repo.deleteAll();

        final var resp = rest.postForEntity("/api/v1/quote", null, Quote.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody().message()).isNotEmpty();
        assertThat(resp.getBody().source()).isNotEmpty();
        assertThat(resp.getHeaders().get("MOTD-Backend-Source")).isNotEmpty();

        final var resp2 = rest.getForEntity("/api/v1/quote", Quote.class);
        assertThat(resp2.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp2).isEqualTo(resp);
    }
}
