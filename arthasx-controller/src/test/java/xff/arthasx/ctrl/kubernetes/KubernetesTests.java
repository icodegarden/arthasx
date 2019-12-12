package xff.arthasx.ctrl.kubernetes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodList;
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
	    Process proc = exec.exec("default","order-deployment-5b9b698cf9-r5294",new String[] {"ps","-ef"},null,true,true);
	    proc.waitFor();
	    List<String> sa = new ArrayList<String>();
		
	    FutureTask f = new FutureTask<Object>(new Callable() {
	    	@Override
	    	public Object call() throws Exception {
	    		try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));) {
	    			String line;
	    			while ((line = reader.readLine()) != null) {
	    				sa.add(line);
	    			}
	    		}catch (Exception e) {
	    			throw new RuntimeException(e);
				}
	    		System.out.println(77);
	    		return sa;
	    	}
		});
	    Thread thread = new Thread(f);
	    thread.setDaemon(false);
	    thread.start();
	    try{
	    	f.get(1000,TimeUnit.MILLISECONDS);
	    }catch (Exception e) {
	    	proc.destroy();
		}
	    System.out.println(sa);
	    
	    Assert.assertEquals(proc.exitValue(), 0);
	    Thread.sleep(3000);
	}
}
