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

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

@Service
class QuoteService {
    private final Random random = new Random();
    private final Logger logger = LoggerFactory.getLogger(QuoteService.class);
    private final QuoteGenerator[] quoteGenerators;
    private final QuoteHistoryRepository repo;
    private final String source;

    QuoteService(QuoteGenerator[] quoteGenerators, QuoteHistoryRepository repo, HikariDataSource dataSource) {
        this.quoteGenerators = quoteGenerators;
        this.repo = repo;
        if (quoteGenerators.length == 0) {
            throw new IllegalArgumentException("No QuoteGenerator instances found");
        }

        final var genTypes = Arrays.stream(quoteGenerators).map(QuoteGenerator::type).sorted().collect(Collectors.toList());
        logger.info("Loaded quote generators: {}", String.join(", ", genTypes));

        var hostName = "localhost";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Unlikely to happen.
        }
        source = String.format("%s/%s", hostName, dataSource.getDriverClassName());
    }

    @Transactional
    public Quote dailyQuote() {
        final var today = LocalDate.now();
        final var dbQuoteOpt = repo.findByDateCreated(today);
        if (dbQuoteOpt.isPresent()) {
            logger.debug("Found existing today's quote: {}", today);
            final var dbQuote = dbQuoteOpt.get();
            return new Quote(dbQuote.getQuoteMessage(), dbQuote.getQuoteSource());
        }
        return refreshDailyQuote();
    }

    @Transactional
    public Quote refreshDailyQuote() {
        final QuoteGenerator gen = quoteGenerators[random.nextInt(quoteGenerators.length)];
        final var quote = gen.nextQuote();
        logger.info("Generating new quote using generator {}: {}", gen.type(), quote.message());

        final var today = LocalDate.now();
        final var dbQuote = repo.findByDateCreated(today).orElseGet(QuoteHistory::new);
        dbQuote.setDateCreated(today);
        dbQuote.setQuoteMessage(quote.message());
        dbQuote.setQuoteSource(quote.source());

        logger.debug("Saving today's quote to the database: {}", today);
        repo.saveAndFlush(dbQuote);

        return quote;
    }

    String source() {
        return source;
    }
}
