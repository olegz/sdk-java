package io.cloudevents.spring.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.v1.CloudEventBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventAttributeUtilsTests {

	@Test
	public void testWithEmpty() {
		Map<String, Object> headers = new HashMap<>();
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(headers);
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getId()).isNull();
		assertThat(attributes.getSource()).isNull();
		assertThat(attributes.getType()).isNull();
	}

	@Test
	public void testWithPrefix() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce-scpecversion", "1.0");
		headers.put("ce-id", "A234-1234-1234");
		headers.put("ce-source", "https://spring.io/");
		headers.put("ce-type", "org.springframework");
		headers.put("ce-datacontenttype", "application/json");
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(headers);
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

	@Test
	public void testWithNoPrefix() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("id", "A234-1234-1234");
		headers.put("source", "https://spring.io/");
		headers.put("type", "org.springframework");
		headers.put("datacontenttype", "application/json");
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(headers);
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

	@Test
	public void testWithNative() {
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils
				.toMutable(new CloudEventBuilder().withId("A234-1234-1234")
						.withSource(URI.create("https://spring.io/")).withType("org.springframework").build());
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
	}

	@Test
	public void testToHeadersNoPrefix() {
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils
				.toMutable(new CloudEventBuilder().withId("A234-1234-1234")
						.withSource(URI.create("https://spring.io/")).withType("org.springframework").build());
		Map<String, Object> headers = attributes.toMap(null);
		assertThat(headers.get("id")).isEqualTo("A234-1234-1234");
		assertThat(headers.get("specversion")).isEqualTo("1.0");
		assertThat(headers.get("source")).isEqualTo("https://spring.io/");
		assertThat(headers.get("type")).isEqualTo("org.springframework");
	}

	@Test
	public void testToHeaders() {
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils
				.toMutable(new CloudEventBuilder().withId("A234-1234-1234")
						.withSource(URI.create("https://spring.io/")).withType("org.springframework").build());
		Map<String, Object> headers = attributes.toMap("ce-");
		assertThat(headers.get("ce-id")).isEqualTo("A234-1234-1234");
		assertThat(headers).doesNotContainKey("id");
		assertThat(headers.get("ce-specversion")).isEqualTo("1.0");
		assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/");
		assertThat(headers.get("ce-type")).isEqualTo("org.springframework");
	}

}
