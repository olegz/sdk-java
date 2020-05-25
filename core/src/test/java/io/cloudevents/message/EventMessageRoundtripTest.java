/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.message;

import io.cloudevents.impl.CloudEventImpl;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.mock.MockBinaryMessage;
import io.cloudevents.mock.MockStructuredMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class EventMessageRoundtripTest {

    /**
     * This test doesn't test extensions in event because the CSVFormat doesn't support it
     *
     * @param input
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEventsWithoutExtensions")
    void structuredToEvent(CloudEventImpl input) {
        assertThat(input.asStructuredMessage(CSVFormat.INSTANCE).toEvent())
            .isEqualTo(input);
    }

    /**
     * This test doesn't test extensions in event because the CSVFormat doesn't support it
     *
     * @param input
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEventsWithoutExtensions")
    void structuredToMockStructuredMessageToEvent(CloudEventImpl input) {
        assertThat(input.asStructuredMessage(CSVFormat.INSTANCE).visit(new MockStructuredMessage()).toEvent())
            .isEqualTo(input);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEvents")
    void binaryToEvent(CloudEventImpl input) {
        assertThat(input.asBinaryMessage().toEvent())
            .isEqualTo(input);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEvents")
    void binaryToMockBinaryMessageToEvent(CloudEventImpl input) {
        assertThat(input.asBinaryMessage().visit(new MockBinaryMessage()).toEvent())
            .isEqualTo(input);
    }

}
