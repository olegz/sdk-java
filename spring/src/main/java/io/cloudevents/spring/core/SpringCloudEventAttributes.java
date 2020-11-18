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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.SpecVersion;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

/**
 * Utility class to assist with accessing and setting Cloud Events attributes from
 * {@link MessageHeaders}. <br>
 * <br>
 * It is effectively a wrapper over a {@link Map}. It also provides best effort to both
 * discover the actual attribute name (regardless of the prefix) as well as set
 * appropriate attribute name. <br>
 * <br>
 * For example, If there is an attribute `ce-source` or `ce_source` or 'source`, by simply
 * calling getSource() we'll discover it and will return its value. <br>
 * Similar effort will happen during the setting of the attribute. If you provide
 * {@link #prefixToUse} we will use it otherwise we'll attempt to determine based on
 * current execution context which prefix to use.
 *
 * @author Oleg Zhurakousky
 * @author Dave Syer
 */
public class SpringCloudEventAttributes extends HashMap<String, Object> implements CloudEventAttributes {

	private static final long serialVersionUID = 5393610770855366497L;

	SpringCloudEventAttributes(Map<String, Object> headers) {
		super(headers);
		safe(headers, CloudEventAttributeUtils.SOURCE);
		safe(headers, CloudEventAttributeUtils.DATASCHEMA);
	}

	private void safe(Map<String, Object> headers, String key) {
		Object value = headers.get(key);
		if (value != null) {
			put(key, value.toString());
		}
	}

	public SpringCloudEventAttributes setSpecVersion(String specversion) {
		this.setAttribute(CloudEventAttributeUtils.SPECVERSION, specversion);
		return this;
	}

	@Override
	public SpecVersion getSpecVersion() {
		String specVersion = (String) this.getAttribute(CloudEventAttributeUtils.SPECVERSION);
		return specVersion == null ? SpecVersion.V1 : SpecVersion.parse(specVersion);
	}

	public SpringCloudEventAttributes setId(String id) {
		this.setAttribute(CloudEventAttributeUtils.ID, id);
		return this;
	}

	@Override
	public String getId() {
		Object id = this.getAttribute(CloudEventAttributeUtils.ID);
		return id == null ? null : id.toString();
	}

	public SpringCloudEventAttributes setType(String type) {
		this.setAttribute(CloudEventAttributeUtils.TYPE, type);
		return this;
	}

	@Override
	public String getType() {
		return (String) this.getAttribute(CloudEventAttributeUtils.TYPE);
	}

	public SpringCloudEventAttributes setSource(URI source) {
		this.setAttribute(CloudEventAttributeUtils.SOURCE, source.toString());
		return this;
	}

	@Override
	public URI getSource() {
		Object value = this.getAttribute(CloudEventAttributeUtils.SOURCE);
		return value == null ? null : URI.create((String) value);
	}

	public SpringCloudEventAttributes setDataContentType(String datacontenttype) {
		this.setAttribute(CloudEventAttributeUtils.DATACONTENTTYPE, datacontenttype);
		return this;
	}

	@Override
	public String getDataContentType() {
		return (String) this.getAttribute(CloudEventAttributeUtils.DATACONTENTTYPE);
	}

	public SpringCloudEventAttributes setDataSchema(URI dataschema) {
		this.setAttribute(CloudEventAttributeUtils.DATASCHEMA, dataschema);
		return this;
	}

	@Override
	public URI getDataSchema() {
		return (URI) this.getAttribute(CloudEventAttributeUtils.DATASCHEMA);
	}

	public SpringCloudEventAttributes setSubject(String subject) {
		this.setAttribute(CloudEventAttributeUtils.SUBJECT, subject);
		return this;
	}

	@Override
	public String getSubject() {
		return (String) this.getAttribute(CloudEventAttributeUtils.SUBJECT);
	}

	public SpringCloudEventAttributes setTime(String time) {
		this.setAttribute(CloudEventAttributeUtils.TIME, time);
		return this;
	}

	@Override
	public OffsetDateTime getTime() {
		String time = (String) this.getAttribute(CloudEventAttributeUtils.TIME);
		return OffsetDateTime.parse(time);
	}

	/**
	 * Will delegate to the underlying {@link Map} returning the value for the requested
	 * attribute or null.
	 */
	@Override
	public Object getAttribute(String attributeName) {
		return this.get(attributeName);
	}

	/**
	 * Determines if this instance of {@link CloudEventAttributes} represents valid Cloud
	 * Event. This implies that it contains all 4 required attributes (id, source, type &
	 * specversion)
	 * @return true if this instance represents a valid Cloud Event
	 */
	public boolean isValidCloudEvent() {
		return StringUtils.hasText(this.getId()) && this.getSource() != null
				&& StringUtils.hasText(this.getSource().toString())
				&& StringUtils.hasText(this.getSpecVersion().toString()) && StringUtils.hasText(this.getType());
	}

	/**
	 * Will convert these attributes to {@link Map} of Spring {@link Message} headers
	 * where each attribute will be prefixed with the value of 'prefixToUse'.
	 * @param prefixToUse prefix to be used on attributes
	 * @return map of Spring's {@link Message} headers.
	 */
	public Map<String, ?> toMessageHeaders(String prefixToUse) {
		Map<String, Object> result = new HashMap<>();
		for (String key : this.getAttributeNames()) {
			Object value = this.getAttribute(key);
			if (value != null) {
				result.put(prefixToUse + key, value);
			}
		}
		return result;
	}

	private SpringCloudEventAttributes setAttribute(String attrName, Object attrValue) {
		this.put(attrName, attrValue);
		return this;
	}

}
