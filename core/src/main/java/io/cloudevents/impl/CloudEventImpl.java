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

package io.cloudevents.impl;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.format.EventFormat;
import io.cloudevents.message.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;

public final class CloudEventImpl implements CloudEvent, BinaryMessage, BinaryMessageExtensions {

    private final AttributesInternal attributes;
    private final byte[] data;
    private final Map<String, Object> extensions;

    public CloudEventImpl(Attributes attributes, byte[] data, Map<String, Object> extensions) {
        Objects.requireNonNull(attributes);
        this.attributes = (AttributesInternal) attributes;
        this.data = data;
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public SpecVersion getSpecVersion() {
        return this.attributes.getSpecVersion();
    }

    @Override
    public String getId() {
        return this.attributes.getId();
    }

    @Override
    public String getType() {
        return this.attributes.getType();
    }

    @Override
    public URI getSource() {
        return this.attributes.getSource();
    }

    @Override
    public String getDataContentType() {
        return this.attributes.getDataContentType();
    }

    @Override
    public URI getDataSchema() {
        return this.attributes.getDataSchema();
    }

    @Override
    public String getSubject() {
        return this.attributes.getSubject();
    }

    @Override
    public ZonedDateTime getTime() {
        return this.attributes.getTime();
    }

    @Override
    public Object getAttribute(String extensionName) {
        throw new UnsupportedOperationException("Temporary not supported. Use individual methods for specific attributes.");
    }

    @Override
    public Set<String> getAttributeNames() {
        throw new UnsupportedOperationException("Temporary not supported.");
    }

    @Override
    public Object getExtension(String extensionName) {
        return this.extensions.get(extensionName);
    }

    @Override
    public Set<String> getExtensionNames() {
        return this.extensions.keySet();
    }

    public Map<String, Object> getExtensions() {
        return this.extensions;
    }

    public AttributesInternal getAttributes() {
        return this.attributes;
    }

//    @Override
//    public <V extends BinaryMessageVisitor<R>, R> R visit(BinaryMessageVisitorFactory<V, R> visitorFactory)
//            throws MessageVisitException, IllegalStateException {
//        // TODO Auto-generated method stub
//        return null;
//    }

    @Override
    public byte[] getData() {
        return this.data;
    }

//    @Override
//    public Map<String, Object> getExtensions() {
//        return Collections.unmodifiableMap(extensions);
//    }
//
    public CloudEventImpl toV03() {
        return new CloudEventImpl(
            attributes.toV03(),
            data,
            extensions
        );
    }

    public CloudEventImpl toV1() {
        return new CloudEventImpl(
            attributes.toV1(),
            data,
            extensions
        );
    }
//
//    // Message impl
//
    public BinaryMessage asBinaryMessage() {
        return this;
    }

    public StructuredMessage asStructuredMessage(EventFormat format) {
        CloudEvent ev = this;
        // TODO This sucks, will improve later
        return new StructuredMessage() {
            @Override
            public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
                return visitor.setEvent(format, format.serialize(ev));
            }
        };
    }

    @Override
    public void visitExtensions(BinaryMessageExtensionsVisitor visitor) throws MessageVisitException {
        // TODO to be improved
        for (Map.Entry<String, Object> entry : this.extensions.entrySet()) {
            if (entry.getValue() instanceof String) {
                visitor.setExtension(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Number) {
                visitor.setExtension(entry.getKey(), (Number) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                visitor.setExtension(entry.getKey(), (Boolean) entry.getValue());
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside extensions map: " + entry);
            }
        }
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
        BinaryMessageVisitor<V> visitor = visitorFactory.createBinaryMessageVisitor(this.attributes.getSpecVersion());
        this.attributes.visitAttributes(visitor);
        this.visitExtensions(visitor);

        if (this.data != null) {
            visitor.setBody(this.data);
        }

        return visitor.end();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudEventImpl that = (CloudEventImpl) o;
        return Objects.equals(attributes, that.attributes) &&
            Arrays.equals(data, that.data) &&
            Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, data, extensions);
    }

    @Override
    public String toString() {
        return "CloudEvent{" +
            "attributes=" + attributes +
            ((this.data != null) ? ", data=" + new String(this.data, StandardCharsets.UTF_8) : "") +
            ", extensions=" + extensions +
            '}';
    }
}
