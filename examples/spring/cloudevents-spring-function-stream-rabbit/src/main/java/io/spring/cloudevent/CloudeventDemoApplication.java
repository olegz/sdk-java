/*
 * Copyright 2020-2020 the original author or authors.
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

package io.spring.cloudevent;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.cloudevent.CloudEventAttributesProvider;
import org.springframework.context.annotation.Bean;

/**
 * Sample application that demonstrates how user functions can be triggered by cloud event.
 * Events can come from anywhere (e.g., HTTP, Messaging, RSocket etc).
 * Given that this particular sample comes already with spring-cloud-function-web support each
 * function is a valid REST endpoint where function name signifies URL path (e.g., http://localhost:8080/asPOJOMessage).
 *
 * Simply start the application and post cloud event to individual function - (see individual 'curl' command at each function).
 *
 * You can also run CloudeventDemoApplicationTests.
 *
 * @author Oleg Zhurakousky
 *
 */
@SpringBootApplication
public class CloudeventDemoApplication {

	public static void main(String[] args) throws Exception {
	    SpringApplication.run(CloudeventDemoApplication.class, args);
	}

	@Bean
	public Function<SpringReleaseEvent, SpringReleaseEvent> consumeAndProduceCloudEventAsPojoToPojo() {
		return event -> {
			event.setVersion("2.0");
			return event;
		};
	}

	@Bean
    public CloudEventAttributesProvider cloudEventAttributesProvider() {
        return attributes -> {
            attributes.setSource("https://interface21.com/").setType("com.interface21");
        };
    }

}
