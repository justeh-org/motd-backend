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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class QuoteController {
    private static final String HEADER_BACKEND_SOURCE = "MOTD-Backend-Source";
    private final QuoteService quoteService;

    QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping(value = "/api/v1/quote", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(responses = {
            @ApiResponse(headers = {
                    @Header(name = HEADER_BACKEND_SOURCE)
            })
    })
    ResponseEntity<Quote> dailyQuote() {
        return wrapQuote(quoteService.dailyQuote());
    }

    @PostMapping(value = "/api/v1/quote", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(responses = {
            @ApiResponse(headers = {
                    @Header(name = HEADER_BACKEND_SOURCE)
            })
    })
    ResponseEntity<Quote> refreshDailyQuote() {
        return wrapQuote(quoteService.refreshDailyQuote());
    }

    private ResponseEntity<Quote> wrapQuote(Quote quote) {
        return ResponseEntity.ok()
                .header(HEADER_BACKEND_SOURCE, quoteService.source())
                .body(quote);
    }
}
