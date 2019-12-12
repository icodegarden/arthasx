package xff.arthasx.ctrl.kubernetes;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@org.springframework.context.annotation.Configuration
public class KubernetesClientConfiguration {

	@ConditionalOnProperty(value = "arthasx.kubernetes.config.client", havingValue = "default", matchIfMissing = true)
	@Bean
	public ApiClient defaultClient() throws Exception {
		ApiClient client = Config.defaultClient();
		Configuration.setDefaultApiClient(client);
		return client;
	}

	@ConditionalOnProperty(value = "arthasx.kubernetes.config.client", havingValue = "dev")
	@Bean
	public ApiClient fromConfig() throws Exception {
		ApiClient client = Config
				.fromConfig(Thread.currentThread().getContextClassLoader().getResourceAsStream("kube.config"));
		Configuration.setDefaultApiClient(client);
		return client;
	}

	@Bean
	public CoreV1Api coreV1Api(ApiClient client) {
		return new CoreV1Api();
	}

}
