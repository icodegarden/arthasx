package xff.arthasx.ctrl.controller.kubernetes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodList;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Controller
@RequestMapping("ui/v1/arthasx/kubernetes")
public class ViewController {

	@Autowired
	ObjectMapper om;

	@Autowired
	CoreV1Api api;

	@RequestMapping("/pods")
	public String pods(@RequestParam(defaultValue = "") String labels, HttpServletRequest request) throws Exception {
		request.setAttribute("labels", labels);
		ArrayList<Map> v1pods = new ArrayList<>();
		if (!labels.isEmpty()) {
			labels = "arthasx in (v1)," + labels;
		} else {
			labels = "arthasx in (v1)";
		}
		V1PodList list = api.listPodForAllNamespaces(null, null, null, labels, null, null, null, null, null);
		list.getItems().forEach(item -> {
			try {
				String json = om.writeValueAsString(item);
				Map v1pod = om.readValue(json, Map.class);

				Map metadata = (Map) v1pod.get("metadata");
				Map<String, Object> annotations = (Map) metadata.get("annotations");
				if (annotations != null) {
					Set<String> keySet = annotations.keySet();
					new ArrayList<>(keySet).forEach(k -> {
						annotations.put(k.replace(".", ""), annotations.get(k));
					});
				}

				v1pods.add(v1pod);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		
		request.setAttribute("pods", v1pods);
		return "pod";
	}
}
