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

package com.vmware.tanzu.demos.motd.backend.status;

import com.vmware.tanzu.demos.motd.backend.quote.QuoteGenerator;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
class StatusController {
    private final HikariDataSource dataSource;
    private final List<String> quoteGeneratorTypes;

    public StatusController(HikariDataSource dataSource,
                            QuoteGenerator[] quoteGenerators) {
        this.dataSource = dataSource;
        quoteGeneratorTypes = Arrays.stream(quoteGenerators).map(QuoteGenerator::type).sorted().collect(Collectors.toList());
    }

    @GetMapping("/api/v1/status")
    Map<String, ?> status() {
        return Map.of(
                "datasource.url", dataSource.getJdbcUrl(),
                "datasource.driver", dataSource.getDriverClassName(),
                "quote.types", quoteGeneratorTypes
        );
    }
}
