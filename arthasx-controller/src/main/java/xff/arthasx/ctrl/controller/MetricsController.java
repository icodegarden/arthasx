package xff.arthasx.ctrl.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * @author Fangfang.Xu
 *
 */
@RestController
public class MetricsController {

	@RequestMapping("/health")
	public ResponseEntity<String> start() {
		return ResponseEntity.ok("{\"status\":\"UP\"}");
	}

}
