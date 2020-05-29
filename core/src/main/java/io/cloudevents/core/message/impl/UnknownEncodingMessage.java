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

package io.cloudevents.core.message.impl;

import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.Message;
import io.cloudevents.core.message.StructuredMessageVisitor;
import io.cloudevents.visitor.*;

public class UnknownEncodingMessage implements Message {
    @Override
    public Encoding getEncoding() {
        return Encoding.UNKNOWN;
    }

    @Override
    public <T extends CloudEventVisitor<V>, V> V visit(CloudEventVisitorFactory<T, V> visitorFactory) throws CloudEventVisitException, IllegalStateException {
        throw new IllegalStateException("Unknown encoding");
    }

    @Override
    public void visitAttributes(CloudEventAttributesVisitor visitor) throws CloudEventVisitException {
        throw new IllegalStateException("Unknown encoding");
    }

    @Override
    public void visitExtensions(CloudEventExtensionsVisitor visitor) throws CloudEventVisitException {
        throw new IllegalStateException("Unknown encoding");
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws CloudEventVisitException, IllegalStateException {
        throw new IllegalStateException("Unknown encoding");
    }
}
