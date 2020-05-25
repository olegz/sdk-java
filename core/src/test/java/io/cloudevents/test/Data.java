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

package io.cloudevents.test;

import io.cloudevents.CloudEvent;
import io.cloudevents.impl.BaseCloudEventBuilder;
import io.cloudevents.impl.CloudEventImpl;
import io.cloudevents.types.Time;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

public class Data {

    public static final String ID = "1";
    public static final String TYPE = "mock.test";
    public static final URI SOURCE = URI.create("http://localhost/source");
    public static final String DATACONTENTTYPE_JSON = "application/json";
    public static final String DATACONTENTTYPE_XML = "application/xml";
    public static final String DATACONTENTTYPE_TEXT = "text/plain";
    public static final URI DATASCHEMA = URI.create("http://localhost/schema");
    public static final String SUBJECT = "sub";
    public static final ZonedDateTime TIME = Time.parseTime("2018-04-26T14:48:09+02:00");

    public static byte[] DATA_JSON_SERIALIZED = "{}".getBytes();
    public static byte[] DATA_XML_SERIALIZED = "<stuff></stuff>".getBytes();
    public static byte[] DATA_TEXT_SERIALIZED = "Hello World Lorena!".getBytes();

    public static final CloudEventImpl V1_MIN = BaseCloudEventBuilder.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .build();

    public static final CloudEventImpl V1_WITH_JSON_DATA = BaseCloudEventBuilder.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEventImpl V1_WITH_JSON_DATA_WITH_EXT = BaseCloudEventBuilder.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .withExtension("astring", "aaa")
        .withExtension("aboolean", true)
        .withExtension("anumber", 10)
        .build();

    public static final CloudEventImpl V1_WITH_JSON_DATA_WITH_EXT_STRING = BaseCloudEventBuilder.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .withExtension("astring", "aaa")
        .withExtension("aboolean", "true")
        .withExtension("anumber", "10")
        .build();

    public static final CloudEventImpl V1_WITH_XML_DATA = BaseCloudEventBuilder.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_XML, DATA_XML_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEventImpl V1_WITH_TEXT_DATA = BaseCloudEventBuilder.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_TEXT, DATA_TEXT_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEventImpl V03_MIN = V1_MIN.toV03();
    public static final CloudEventImpl V03_WITH_JSON_DATA = V1_WITH_JSON_DATA.toV03();
    public static final CloudEventImpl V03_WITH_JSON_DATA_WITH_EXT = V1_WITH_JSON_DATA_WITH_EXT.toV03();
    public static final CloudEventImpl V03_WITH_JSON_DATA_WITH_EXT_STRING = V1_WITH_JSON_DATA_WITH_EXT_STRING.toV03();
    public static final CloudEventImpl V03_WITH_XML_DATA = V1_WITH_XML_DATA.toV03();
    public static final CloudEventImpl V03_WITH_TEXT_DATA = V1_WITH_TEXT_DATA.toV03();

    public static Stream<CloudEvent> allEvents() {
        return Stream.concat(v1Events(), v03Events());
    }

    public static Stream<CloudEventImpl> allEventsWithoutExtensions() {
        return Stream.concat(v1Events(), v03Events()).filter(e -> e.getExtensions().isEmpty());
    }

    public static Stream<CloudEvent> allEventsWithStringExtensions() {
        return Stream.concat(v1EventsWithStringExt(), v03EventsWithStringExt());
    }

    public static Stream<CloudEventImpl> v1Events() {
        return Stream.of(
            Data.V1_MIN,
            Data.V1_WITH_JSON_DATA,
            Data.V1_WITH_JSON_DATA_WITH_EXT,
            Data.V1_WITH_XML_DATA,
            Data.V1_WITH_TEXT_DATA
        );
    }

    public static Stream<CloudEventImpl> v03Events() {
        return Stream.of(
            Data.V03_MIN,
            Data.V03_WITH_JSON_DATA,
            Data.V03_WITH_JSON_DATA_WITH_EXT,
            Data.V03_WITH_XML_DATA,
            Data.V03_WITH_TEXT_DATA
        );
    }

    public static Stream<CloudEventImpl> v1EventsWithStringExt() {
        return v1Events().map(ce -> {
            io.cloudevents.v1.CloudEventBuilder builder = BaseCloudEventBuilder.buildV1(ce);
            ce.getExtensions().forEach((k, v) -> builder.withExtension(k, v.toString()));
            return builder.build();
        });
    }

    public static Stream<CloudEventImpl> v03EventsWithStringExt() {
        return v03Events().map(ce -> {
            io.cloudevents.v03.CloudEventBuilder builder = BaseCloudEventBuilder.buildV03(ce);
            ce.getExtensions().forEach((k, v) -> builder.withExtension(k, v.toString()));
            return builder.build();
        });
    }

}
