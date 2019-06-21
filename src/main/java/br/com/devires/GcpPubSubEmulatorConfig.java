package br.com.devires;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.NoCredentials;

@Configuration
@ConditionalOnProperty(name = "spring.cloud.gcp.emulator-enabled", havingValue = "true")
public class GcpPubSubEmulatorConfig {

	@Bean
	public CredentialsProvider credentialsProvider() {
		return NoCredentials::getInstance;
	}

}
