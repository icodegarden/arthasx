package xff.arthasx.ctrl.kubernetes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.Exec;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
/**
 * 
 * @author Fangfang.Xu
 *
 */
public class KubernetesTests {

	CoreV1Api api;

	@Before
	public void before() throws Exception {
		ApiClient client = Config
				.fromConfig(Thread.currentThread().getContextClassLoader().getResourceAsStream("kube.config"));
		Configuration.setDefaultApiClient(client);

		api = new CoreV1Api();
	}

	@Test
	public void testListPods() throws Exception {
		V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		System.out.println(list.getItems().stream().map(item -> {
			return item.getMetadata().getName();
		}).collect(Collectors.toList()));
	}
	
	@Test
	public void testExec() throws Exception {
	    Exec exec = new Exec();
	    Process proc = exec.exec("default","order-deployment-5b9b698cf9-r5294",new String[] {"pwd"},null,true,true);

	    proc.waitFor();
	    
	    List<String> sa = new ArrayList<String>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));) {
			String line;
			while ((line = reader.readLine()) != null) {
					sa.add(line);
			}
		}

	    proc.destroy();
	    
	    System.out.println(sa);
	    
	    Assert.assertEquals(proc.exitValue(), 0);
	}
}
