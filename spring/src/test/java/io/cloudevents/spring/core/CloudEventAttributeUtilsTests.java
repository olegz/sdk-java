package io.cloudevents.spring.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.cloudevents.SpecVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventAttributeUtilsTests {

	@Test
	public void testWithPrefix() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce-id", "A234-1234-1234");
		headers.put("ce-source", "https://spring.io/");
		headers.put("ce-type", "org.springframework");
		headers.put("ce-datacontenttype", "application/json");
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.wrap(headers);
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
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.wrap(headers);
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

}
