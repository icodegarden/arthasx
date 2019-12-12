package xff.arthasx.ctrl.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import xff.arthasx.common.Constants;
import xff.arthasx.common.Result;
import xff.arthasx.common.util.NetUtils;
import xff.arthasx.ctrl.AttachProperties;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Controller
@RequestMapping("ui/v1/arthasx")
public class AttachController extends BaseController {

	@Autowired
	AttachProperties attachProperties;

	RestTemplate restTemplate;
	{
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();

		simpleClientHttpRequestFactory.setConnectTimeout(3000);
		simpleClientHttpRequestFactory.setReadTimeout(30000);
		restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
	}

	@RequestMapping("/attach")
	public String attach(@RequestParam String daemonAddress, @RequestParam Integer daemonPort,
			@RequestParam String jpsKeywords, @RequestParam String agentId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String localHost = NetUtils.getLocalHost();
		try {
			Result<Void> result = restTemplate
					.getForObject("http://" + daemonAddress + ":" + daemonPort + "/api/v1/attach?tunnelServerAddress="
							+ localHost + "&jpsKeywords=" + jpsKeywords + "&agentId=" + agentId, Result.class);
			if (!result.isSuccess()) {
				responseWrite(response, result.getMessage());
				return null;
			}
			String localAddr = request.getLocalAddr();
			return "redirect:http://" + localAddr + ":" + attachProperties.getRedirect().getTunnelWebPort() + "?port="
					+ attachProperties.getRedirect().getTunnelServerPort() + "&agentId=" + agentId;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			HttpHeaders httpHeaders = e.getResponseHeaders();
			List<String> msg = httpHeaders.get(Constants.HTTP_HEADER_ERROR_MSG);
			if (msg != null && !msg.isEmpty()) {
				responseWrite(response, msg.toString());
				return null;
			}
			responseWrite(response, e.getMessage());
			return null;
		} catch (ResourceAccessException e) {
			if (e.getCause() instanceof java.net.ConnectException) {
				responseWrite(response,
						"please ensure target server process deploy with arthasx-daemon(8079) and is available \n");
			}
			responseWrite(response, e.getMessage());
			return null;
		}
	}
}
