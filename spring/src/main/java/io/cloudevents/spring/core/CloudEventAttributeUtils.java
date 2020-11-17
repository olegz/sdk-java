/*
 * Copyright 2020-Present The CloudEvents Authors
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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.lang.Nullable;

import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Miscellaneous utility methods to deal with Cloud Events - https://cloudevents.io/. <br>
 * Primarily intended for the internal use within the framework;
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 */
public final class CloudEventAttributeUtils {

	private CloudEventAttributeUtils() {
	}

	/**
	 * String value of 'application/cloudevents' mime type.
	 */
	public static String APPLICATION_CLOUDEVENTS_VALUE = "application/cloudevents";

	/**
	 * {@link MimeType} instance representing 'application/cloudevents' mime type.
	 */
	public static MimeType APPLICATION_CLOUDEVENTS = MimeTypeUtils.parseMimeType(APPLICATION_CLOUDEVENTS_VALUE);

	/**
	 * Prefix for attributes.
	 */
	public static String ATTR_PREFIX = "ce_";

	/**
	 * Prefix for attributes.
	 */
	public static String HTTP_ATTR_PREFIX = "ce-";

	/**
	 * Value for 'data' attribute.
	 */
	public static String DATA = "data";

	/**
	 * Value for 'data' attribute with prefix.
	 */
	public static String CANONICAL_DATA = ATTR_PREFIX + DATA;

	/**
	 * Value for 'id' attribute.
	 */
	public static String ID = "id";

	/**
	 * Value for 'id' attribute with prefix.
	 */
	public static String CANONICAL_ID = ATTR_PREFIX + ID;

	/**
	 * Value for 'source' attribute.
	 */
	public static String SOURCE = "source";

	/**
	 * Value for 'source' attribute with prefix.
	 */
	public static String CANONICAL_SOURCE = ATTR_PREFIX + SOURCE;

	/**
	 * Value for 'specversion' attribute.
	 */
	public static String SPECVERSION = "specversion";

	/**
	 * Value for 'specversion' attribute with prefix.
	 */
	public static String CANONICAL_SPECVERSION = ATTR_PREFIX + SPECVERSION;

	/**
	 * Value for 'type' attribute.
	 */
	public static String TYPE = "type";

	/**
	 * Value for 'type' attribute with prefix.
	 */
	public static String CANONICAL_TYPE = ATTR_PREFIX + TYPE;

	/**
	 * Value for 'datacontenttype' attribute.
	 */
	public static String DATACONTENTTYPE = "datacontenttype";

	/**
	 * Value for 'datacontenttype' attribute with prefix.
	 */
	public static String CANONICAL_DATACONTENTTYPE = ATTR_PREFIX + DATACONTENTTYPE;

	/**
	 * Value for 'dataschema' attribute.
	 */
	public static String DATASCHEMA = "dataschema";

	/**
	 * Value for 'dataschema' attribute with prefix.
	 */
	public static String CANONICAL_DATASCHEMA = ATTR_PREFIX + DATASCHEMA;

	/**
	 * Value for 'subject' attribute.
	 */
	public static String SUBJECT = "subject";

	/**
	 * Value for 'subject' attribute with prefix.
	 */
	public static String CANONICAL_SUBJECT = ATTR_PREFIX + SUBJECT;

	/**
	 * Value for 'time' attribute.
	 */
	public static String TIME = "time";

	/**
	 * Value for 'time' attribute with prefix.
	 */
	public static String CANONICAL_TIME = ATTR_PREFIX + TIME;

	/**
	 * Checks if {@link Message} represents cloud event in binary-mode.
	 */
	public static boolean isBinary(Map<String, Object> headers) {
		SpringCloudEventAttributes attributes = new SpringCloudEventAttributes(headers);
		return attributes.isValidCloudEvent();
	}

	public static SpringCloudEventAttributes get(CloudEventAttributes attributes) {
		return get(attributes, ATTR_PREFIX);
	}

	public static SpringCloudEventAttributes get(CloudEventAttributes attributes, String prefix) {
		if (attributes instanceof SpringCloudEventAttributes) {
			return (SpringCloudEventAttributes) attributes;
		}
		SpringCloudEventAttributes instance = new SpringCloudEventAttributes(new HashMap<>(), prefix);
		return instance;
	}

	/**
	 * Will construct instance of {@link CloudEventAttributes} setting its required
	 * attributes.
	 * @param ce_id value for Cloud Event 'id' attribute
	 * @param ce_specversion value for Cloud Event 'specversion' attribute
	 * @param ce_source value for Cloud Event 'source' attribute
	 * @param ce_type value for Cloud Event 'type' attribute
	 * @return instance of {@link CloudEventAttributes}
	 */
	public static SpringCloudEventAttributes get(String ce_id, String ce_specversion, String ce_source,
			String ce_type) {
		Assert.hasText(ce_id, "'ce_id' must not be null or empty");
		Assert.hasText(ce_specversion, "'ce_specversion' must not be null or empty");
		Assert.hasText(ce_source, "'ce_source' must not be null or empty");
		Assert.hasText(ce_type, "'ce_type' must not be null or empty");
		Map<String, Object> requiredAttributes = new HashMap<>();
		requiredAttributes.put(CloudEventAttributeUtils.CANONICAL_ID, ce_id);
		requiredAttributes.put(CloudEventAttributeUtils.CANONICAL_SPECVERSION, ce_specversion);
		requiredAttributes.put(CloudEventAttributeUtils.CANONICAL_SOURCE, ce_source);
		requiredAttributes.put(CloudEventAttributeUtils.CANONICAL_TYPE, ce_type);
		return new SpringCloudEventAttributes(requiredAttributes);
	}

	/**
	 * Will construct instance of {@link CloudEventAttributes} Should default/generate
	 * cloud event ID and SPECVERSION.
	 * @param ce_source value for Cloud Event 'source' attribute
	 * @param ce_type value for Cloud Event 'type' attribute
	 * @return instance of {@link CloudEventAttributes}
	 */
	public static SpringCloudEventAttributes get(String ce_source, String ce_type) {
		return get(UUID.randomUUID().toString(), "1.0", ce_source, ce_type);
	}

	public static String determinePrefixToUse(Map<String, Object> messageHeaders) {
		Set<String> keys = messageHeaders.keySet();
		if (keys.contains("user-agent")) {
			return CloudEventAttributeUtils.HTTP_ATTR_PREFIX;
		}
		else {
			return CloudEventAttributeUtils.ATTR_PREFIX;
		}
	}

	/**
	 * Typically called by Consumer.
	 * 
	 */
	public static SpringCloudEventAttributes generateAttributes(Message<?> message,
			CloudEventAttributesProvider provider) {
		SpringCloudEventAttributes attributes = generateDefaultAttributeValues(
				new SpringCloudEventAttributes(message.getHeaders()),
				message.getPayload().getClass().getName().getClass().getName(), null);
		return CloudEventAttributeUtils.get(provider.generateOutputAttributes(attributes));
	}

	public static SpringCloudEventAttributes generateAttributes(Message<?> inputMessage, String typeName,
			String sourceName) {
		SpringCloudEventAttributes attributes = new SpringCloudEventAttributes(inputMessage.getHeaders(),
				CloudEventAttributeUtils.determinePrefixToUse(inputMessage.getHeaders()));
		return generateDefaultAttributeValues(attributes, typeName, sourceName);
	}

	private static SpringCloudEventAttributes generateDefaultAttributeValues(SpringCloudEventAttributes attributes,
			@Nullable String source, @Nullable String type) {
		if (attributes.isValidCloudEvent()) {
			if (source != null) {
				attributes = attributes.setSource(URI.create(source));
			}
			if (type != null) {
				attributes = attributes.setType(type);
			}
			return attributes.setId(UUID.randomUUID().toString());
		}
		return attributes;
	}

}
