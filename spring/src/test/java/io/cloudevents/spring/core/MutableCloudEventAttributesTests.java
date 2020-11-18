/*
 * Copyright 2019-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.core;

import java.util.Collections;

import io.cloudevents.SpecVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
public class MutableCloudEventAttributesTests {

	@Test
	void testEmpty() throws Exception {
		MutableCloudEventAttributes attributes = new MutableCloudEventAttributes(Collections.emptyMap());
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getId()).isNull();
	}

	@Test
	void testSetAttribute() throws Exception {
		MutableCloudEventAttributes attributes = new MutableCloudEventAttributes(Collections.emptyMap());
		attributes.setAttribute(CloudEventAttributeUtils.ID, "A1234-1234");
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getId()).isEqualTo("A1234-1234");
	}

}
