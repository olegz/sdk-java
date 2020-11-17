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
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.SpecVersion;

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

    private final String prefixToUse;

    public SpringCloudEventAttributes(Map<String, Object> headers, String prefixToUse) {
        super(headers);
        this.prefixToUse = prefixToUse;
    }

    public SpringCloudEventAttributes(Map<String, Object> headers) {
        this(headers, null);
    }

    public CloudEventAttributes setSpecversion(String specversion) {
        this.setAtttribute(CloudEventAttributeUtils.SPECVERSION, specversion);
        return this;
    }

    @Override
    public SpecVersion getSpecVersion() {
        String specVersion = (String) this.getAttribute(CloudEventAttributeUtils.SPECVERSION);
        return SpecVersion.parse(specVersion);
    }

    public SpringCloudEventAttributes setId(String id) {
        this.setAtttribute(CloudEventAttributeUtils.ID, id);
        return this;
    }

    @Override
    public String getId() {
        Object id = this.getAttribute(CloudEventAttributeUtils.ID);
        if (!(id instanceof UUID)) {
            id = null;
        }
        return (String) id;
    }

    public CloudEventAttributes setType(String type) {
        this.setAtttribute(CloudEventAttributeUtils.TYPE, type);
        return this;
    }

    @Override
    public String getType() {
        return (String) this.getAttribute(CloudEventAttributeUtils.TYPE);
    }

    public CloudEventAttributes setSource(URI source) {
        this.setAtttribute(CloudEventAttributeUtils.SOURCE, source.toASCIIString());
        return this;
    }

    @Override
    public URI getSource() {
        String source = (String) this.getAttribute(CloudEventAttributeUtils.SOURCE);
        try {
            return new URI(source);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Failed to create source URI from " + source, e);
        }
    }

    public CloudEventAttributes setDataContentType(String datacontenttype) {
        this.setAtttribute(CloudEventAttributeUtils.DATACONTENTTYPE, datacontenttype);
        return this;
    }

    @Override
    public String getDataContentType() {
        return (String) this.getAttribute(CloudEventAttributeUtils.DATACONTENTTYPE);
    }

    public CloudEventAttributes setDataSchema(String dataschema) {
        this.setAtttribute(CloudEventAttributeUtils.DATASCHEMA, dataschema);
        return this;
    }

    @Override
    public URI getDataSchema() {
        String dataschema = (String) this.getAttribute(CloudEventAttributeUtils.DATASCHEMA);
        try {
            return new URI(dataschema);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Failed to create dataschema URI from " + dataschema, e);
        }
    }

    public CloudEventAttributes setSubject(String subject) {
        this.setAtttribute(CloudEventAttributeUtils.SUBJECT, subject);
        return this;
    }

    @Override
    public String getSubject() {
        return (String) this.getAttribute(CloudEventAttributeUtils.SUBJECT);
    }

    public CloudEventAttributes setTime(String time) {
        this.setAtttribute(CloudEventAttributeUtils.TIME, time);
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
    public Object getAttribute(String attributeName) throws IllegalArgumentException {
        if (this.containsKey(CloudEventAttributeUtils.ATTR_PREFIX + attributeName)) {
            return this.get(CloudEventAttributeUtils.ATTR_PREFIX + attributeName);
        }
        else if (this.containsKey(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + attributeName)) {
            return this.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + attributeName);
        }
        return this.get(attributeName);
    }

    /**
     * Determines if this instance of {@link CloudEventAttributes} represents valid Cloud
     * Event. This implies that it contains all 4 required attributes (id, source, type &
     * specversion)
     * @return true if this instance represents a valid Cloud Event
     */
    public boolean isValidCloudEvent() {
        return StringUtils.hasText(this.getId()) && StringUtils.hasText(this.getSource().toASCIIString())
                && StringUtils.hasText(this.getSpecVersion().name()) && StringUtils.hasText(this.getType());
    }

    String getAttributeName(String attributeName) {
        if (this.containsKey(CloudEventAttributeUtils.ATTR_PREFIX + attributeName)) {
            return CloudEventAttributeUtils.ATTR_PREFIX + attributeName;
        }
        else if (this.containsKey(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + attributeName)) {
            return CloudEventAttributeUtils.HTTP_ATTR_PREFIX + attributeName;
        }
        return attributeName;
    }

    private CloudEventAttributes setAtttribute(String attrName, String attrValue) {
        if (StringUtils.hasText(this.prefixToUse)) {
            this.remove(this.getAttributeName(attrName));
            this.put(this.prefixToUse + attrName, attrValue);
        }
        else {
            this.put(this.getAttributeName(attrName), attrValue);
        }
        return this;
    }
}
