package xff.arthasx.ctrl.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
/**
 * 
 * @author Fangfang.Xu
 *
 */
public abstract class BaseController {
	
	public void responseWrite(HttpServletResponse response, String msg) throws IOException {
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(msg);
	}
}
