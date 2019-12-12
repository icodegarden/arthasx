package xff.arthasx.ctrl.controller.kubernetes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import xff.arthasx.common.Constants;
import xff.arthasx.common.Result;
import xff.arthasx.common.util.StringUtils;
import xff.arthasx.ctrl.AttachProperties;
import xff.arthasx.ctrl.controller.BaseController;
import xff.arthasx.ctrl.kubernetes.KubernetesExec;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Controller
@RequestMapping("ui/v1/arthasx/kubernetes")
public class KubernetesAttachController extends BaseController {

	@Autowired
	AttachProperties attachProperties;

	RestTemplate restTemplate;
	{
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		simpleClientHttpRequestFactory.setConnectTimeout(3000);
		simpleClientHttpRequestFactory.setReadTimeout(3000);
		restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
	}

	@RequestMapping("/attach")
	public String attach(@RequestParam String namespace, @RequestParam String pod, @RequestParam String containerName,
			@RequestParam String containerIp, @RequestParam String arthasxhome, @RequestParam String jpsKeywords,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			boolean attached = false;
			try {
				restTemplate.getForObject("http://" + containerIp + ":" + Constants.ARTHAS_WS_PORT, String.class);
				attached = true;
			} catch (Exception e) {
				// skip
			}

			final String agentId = pod + "-" + containerName;
			if (!attached) {
				KubernetesExec kubernetesExec = new KubernetesExec(namespace, pod, containerName);
				final String tunnelServerAddress = attachProperties.getTunnelServerAddress();
				Result<Void> result = kubernetesExec.arthasAttach(arthasxhome, tunnelServerAddress, jpsKeywords,
						containerIp, agentId);
				if (!result.isSuccess()) {
					responseWrite(response, "attach failed, messages:" + result.getMessage());
					return null;
				}
			}
			String attachRedirectAddress = attachProperties.getRedirect().getAddress();
			if (attachRedirectAddress == null) {
				attachRedirectAddress = request.getServerName();
			}
			// to arthas web console
			return "redirect:http://" + attachRedirectAddress + ":" + attachProperties.getRedirect().getTunnelWebPort()
					+ "?port=" + attachProperties.getRedirect().getTunnelServerPort() + "&agentId=" + agentId;
		} catch (Exception e) {
			String error = "attach failed:\n" + StringUtils.toString(e);
			responseWrite(response, error);
			return null;
		}
	}
}
