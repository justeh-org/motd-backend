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

import com.github.javafaker.Faker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "motd.features.quotes.elderScrolls")
class ElderScrollsQuoteGenerator implements QuoteGenerator {
    private final Faker faker;

    ElderScrollsQuoteGenerator(Faker faker) {
        this.faker = faker;
    }

    @Override
    public Quote nextQuote() {
        return new Quote(faker.elderScrolls().quote(), "Quotes from Elder Scrolls");
    }

    @Override
    public String type() {
        return "elderScrolls";
    }
}
