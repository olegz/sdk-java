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

package io.cloudevents.spring.messaging;

import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.CloudEventAttributesProvider;
import io.cloudevents.spring.core.SpringCloudEventAttributes;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

/**
 * Miscellaneous utility methods to deal with Cloud Events - https://cloudevents.io/. <br>
 * Primarily intended for the internal use within the framework;
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @since 3.1
 */
public final class CloudEventMessageUtils {

	private static final ContentTypeResolver contentTypeResolver = new DefaultContentTypeResolver();

	private CloudEventMessageUtils() {

	}

	@SuppressWarnings("unchecked")
	public static Message<?> toBinary(Message<?> inputMessage, MessageConverter messageConverter) {

		Map<String, Object> headers = inputMessage.getHeaders();
		SpringCloudEventAttributes attributes = CloudEventAttributeUtils.generateAttributes(headers);

		// first check the obvious and see if content-type is `cloudevents`
		if (!attributes.isValidCloudEvent() && headers.containsKey(MessageHeaders.CONTENT_TYPE)) {
			MimeType contentType = contentTypeResolver.resolve(inputMessage.getHeaders());
			if (contentType.getType().equals(CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS.getType()) && contentType
					.getSubtype().startsWith(CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS.getSubtype())) {

				String dataContentType = StringUtils.hasText(attributes.getDataContentType())
						? attributes.getDataContentType() : MimeTypeUtils.APPLICATION_JSON_VALUE;

				String suffix = contentType.getSubtypeSuffix();
				MimeType cloudEventDeserializationContentType = MimeTypeUtils
						.parseMimeType(contentType.getType() + "/" + suffix);
				Message<?> cloudEventMessage = MessageBuilder.fromMessage(inputMessage)
						.setHeader(MessageHeaders.CONTENT_TYPE, cloudEventDeserializationContentType)
						.setHeader(CloudEventAttributeUtils.CANONICAL_DATACONTENTTYPE, dataContentType).build();
				Map<String, Object> structuredCloudEvent = (Map<String, Object>) messageConverter
						.fromMessage(cloudEventMessage, Map.class);
				Message<?> binaryCeMessage = buildCeMessageFromStructured(structuredCloudEvent,
						inputMessage.getHeaders());
				return binaryCeMessage;
			}
		}
		else if (StringUtils.hasText(attributes.getDataContentType())) {
			return MessageBuilder.fromMessage(inputMessage)
					.setHeader(MessageHeaders.CONTENT_TYPE, attributes.getDataContentType()).build();
		}
		return inputMessage;
	}

	/**
	 * Typically called by Consumer.
	 * 
	 */
	public static SpringCloudEventAttributes generateAttributes(Message<?> message,
			CloudEventAttributesProvider provider) {
		SpringCloudEventAttributes attributes = CloudEventAttributeUtils.generateAttributes(message.getHeaders())
				.setType(message.getPayload().getClass().getName().getClass().getName());
		return CloudEventAttributeUtils.get(provider.generateOutputAttributes(attributes));
	}

	private static Message<?> buildCeMessageFromStructured(Map<String, Object> structuredCloudEvent,
			MessageHeaders originalHeaders) {
		Object data = null;
		if (structuredCloudEvent
				.containsKey(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + CloudEventAttributeUtils.DATA)) {
			data = structuredCloudEvent.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + CloudEventAttributeUtils.DATA);
			structuredCloudEvent.remove(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + CloudEventAttributeUtils.DATA);
		}
		else if (structuredCloudEvent.containsKey(CloudEventAttributeUtils.CANONICAL_DATA)) {
			data = structuredCloudEvent.get(CloudEventAttributeUtils.CANONICAL_DATA);
			structuredCloudEvent.remove(CloudEventAttributeUtils.CANONICAL_DATA);
		}
		else if (structuredCloudEvent.containsKey(CloudEventAttributeUtils.DATA)) {
			data = structuredCloudEvent.get(CloudEventAttributeUtils.DATA);
			structuredCloudEvent.remove(CloudEventAttributeUtils.DATA);
		}
		Assert.notNull(data, "'data' must not be null");
		MessageBuilder<?> builder = MessageBuilder.withPayload(data);
		SpringCloudEventAttributes attributes = CloudEventAttributeUtils.generateAttributes(structuredCloudEvent);
		String prefixToUse = CloudEventAttributeUtils.determinePrefixToUse(originalHeaders);
		builder.copyHeaders(getHeaders(attributes, prefixToUse));
		builder.copyHeaders(originalHeaders);
		return builder.build();
	}

	public static Map<String, ?> getHeaders(CloudEventAttributes attributes, String prefixToUse) {
		Map<String, Object> result = new HashMap<>();
		for (String key : attributes.getAttributeNames()) {
			Object value = attributes.getAttribute(key);
			if (value != null) {
				result.put(prefixToUse + key, value);
			}
		}
		return result;
	}

}
