package io.cloudevents.spring.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.context.config.JsonMessageConverter;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.SpringCloudEventAttributes;

public class CloudEventMessageUtilsTests {

    String payloadWithHttpPrefix = "{\n" +
            "    \"ce-specversion\" : \"1.0\",\n" +
            "    \"ce-type\" : \"org.springframework\",\n" +
            "    \"ce-source\" : \"https://spring.io/\",\n" +
            "    \"ce-id\" : \"A234-1234-1234\",\n" +
            "    \"ce-datacontenttype\" : \"application/json\",\n" +
            "    \"ce-data\" : {\n" +
            "        \"version\" : \"1.0\",\n" +
            "        \"releaseName\" : \"Spring Framework\",\n" +
            "        \"releaseDate\" : \"24-03-2004\"\n" +
            "    }\n" +
            "}";
    String payloadNoPrefix = "{\n" +
            "    \"specversion\" : \"1.0\",\n" +
            "    \"type\" : \"org.springframework\",\n" +
            "    \"source\" : \"https://spring.io/\",\n" +
            "    \"id\" : \"A234-1234-1234\",\n" +
            "    \"datacontenttype\" : \"application/json\",\n" +
            "    \"data\" : {\n" +
            "        \"version\" : \"1.0\",\n" +
            "        \"releaseName\" : \"Spring Framework\",\n" +
            "        \"releaseDate\" : \"24-03-2004\"\n" +
            "    }\n" +
            "}";
    String payloadNoDataContentType = "{\n" +
            "    \"ce_specversion\" : \"1.0\",\n" +
            "    \"ce_type\" : \"org.springframework\",\n" +
            "    \"ce_source\" : \"https://spring.io/\",\n" +
            "    \"ce_id\" : \"A234-1234-1234\",\n" +
            "    \"ce_datacontenttype\" : \"application/json\",\n" +
            "    \"data\" : {\n" +
            "        \"version\" : \"1.0\",\n" +
            "        \"releaseName\" : \"Spring Framework\",\n" +
            "        \"releaseDate\" : \"24-03-2004\"\n" +
            "    }\n" +
            "}";

    @SuppressWarnings("unchecked")
    @Test
    public void testStructuredToBinaryWithPrefix() {
        Message<String> structuredMessage = MessageBuilder.withPayload(payloadWithHttpPrefix).setHeader(MessageHeaders.CONTENT_TYPE,
                CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

        JsonMessageConverter converter = new JsonMessageConverter(new JacksonMapper(new ObjectMapper()));
        Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils.toBinary(structuredMessage, converter);
        SpringCloudEventAttributes attributes = CloudEventAttributeUtils.wrap(binaryMessage.getHeaders());
        assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
        assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
        assertThat(attributes.getType()).isEqualTo("org.springframework");
        assertThat(attributes.getDataContentType()).isEqualTo("application/json");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStructuredToBinaryNoPrefix() {
        Message<String> structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(MessageHeaders.CONTENT_TYPE,
                CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

        JsonMessageConverter converter = new JsonMessageConverter(new JacksonMapper(new ObjectMapper()));
        Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils.toBinary(structuredMessage, converter);
        SpringCloudEventAttributes attributes = CloudEventAttributeUtils.wrap(binaryMessage.getHeaders());
        assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
        assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
        assertThat(attributes.getType()).isEqualTo("org.springframework");
        assertThat(attributes.getDataContentType()).isEqualTo("application/json");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStructuredToBinaryNoDataContentType() {
        Message<String> structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(MessageHeaders.CONTENT_TYPE,
                CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

        JsonMessageConverter converter = new JsonMessageConverter(new JacksonMapper(new ObjectMapper()));
        Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils.toBinary(structuredMessage, converter);
        SpringCloudEventAttributes attributes = CloudEventAttributeUtils.wrap(binaryMessage.getHeaders());
        assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
        assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
        assertThat(attributes.getType()).isEqualTo("org.springframework");
        assertThat(attributes.getDataContentType()).isEqualTo("application/json");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testStructuredToBinaryBackToMessageHeaders() {
        JsonMessageConverter converter = new JsonMessageConverter(new JacksonMapper(new ObjectMapper()));
        Message<String> structuredMessage = MessageBuilder.withPayload(payloadWithHttpPrefix).setHeader(MessageHeaders.CONTENT_TYPE,
                CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

        Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils.toBinary(structuredMessage, converter);
        assertThat(binaryMessage.getHeaders().containsKey("ce-data")).isFalse();
        SpringCloudEventAttributes attributes = CloudEventAttributeUtils.wrap(binaryMessage.getHeaders());

        Map headers = attributes.toMessageHeaders(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX);
        assertThat(headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.ID)).isEqualTo("A234-1234-1234");
        assertThat(headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.SOURCE)).isEqualTo("https://spring.io/");
        assertThat(headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.TYPE)).isEqualTo("org.springframework");
        assertThat(headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.DATACONTENTTYPE)).isEqualTo("application/json");

        structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(MessageHeaders.CONTENT_TYPE,
                CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

        binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils.toBinary(structuredMessage, converter);
        assertThat(binaryMessage.getHeaders().containsKey("data")).isFalse();
        attributes = CloudEventAttributeUtils.wrap(binaryMessage.getHeaders());

        headers = attributes.toMessageHeaders(CloudEventAttributeUtils.HTTP_ATTR_PREFIX);
        assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + CloudEventAttributeUtils.ID)).isEqualTo("A234-1234-1234");
        assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + CloudEventAttributeUtils.SOURCE)).isEqualTo("https://spring.io/");
        assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + CloudEventAttributeUtils.TYPE)).isEqualTo("org.springframework");
        assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + CloudEventAttributeUtils.DATACONTENTTYPE)).isEqualTo("application/json");
    }
}
