package io.cloudevents.spring.functions;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.function.core.FunctionInvocationHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(FunctionInvocationHelper.class)
class CloudEventsFunctionExtensionConfiguration {

    @Bean
    public CloudEventsFunctionInvocationHelper functionInvocationHelper() {
        return new CloudEventsFunctionInvocationHelper();
    }
}
