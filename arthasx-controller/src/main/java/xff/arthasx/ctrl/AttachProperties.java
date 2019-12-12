package xff.arthasx.ctrl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import xff.arthasx.common.util.NetUtils;
/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
@ConfigurationProperties(prefix = "arthasx.attach")
public class AttachProperties {
	
	private String tunnelServerAddress = NetUtils.getLocalHost();
	
	private Redirect redirect = new Redirect();
	
	public String getTunnelServerAddress() {
		return tunnelServerAddress;
	}

	public void setTunnelServerAddress(String tunnelServerAddress) {
		this.tunnelServerAddress = tunnelServerAddress;
	}

	public Redirect getRedirect() {
		return redirect;
	}

	public void setRedirect(Redirect redirect) {
		this.redirect = redirect;
	}

	public class Redirect{
		private String address;
		private int tunnelWebPort = 8081;;
		private int tunnelServerPort = 7777;
		
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public int getTunnelWebPort() {
			return tunnelWebPort;
		}
		public void setTunnelWebPort(int tunnelWebPort) {
			this.tunnelWebPort = tunnelWebPort;
		}
		public int getTunnelServerPort() {
			return tunnelServerPort;
		}
		public void setTunnelServerPort(int tunnelServerPort) {
			this.tunnelServerPort = tunnelServerPort;
		}
	}
}
