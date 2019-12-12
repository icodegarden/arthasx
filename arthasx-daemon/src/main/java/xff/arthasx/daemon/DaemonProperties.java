package xff.arthasx.daemon;

import xff.arthasx.common.util.NetUtils;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class DaemonProperties {

	public static final String ARTHASX_HOME = "arthasx.home";

	String arthasxHome;

	String targetIp = NetUtils.getLocalHost();

	public DaemonProperties() {
		arthasxHome = System.getProperty(ARTHASX_HOME);
		if (arthasxHome == null || arthasxHome.isEmpty()) {
			throw new IllegalArgumentException(ARTHASX_HOME + " must not empty");
		}
	}

	public String getArthasxHome() {
		return arthasxHome;
	}

	public void setArthasHome(String arthasxHome) {
		this.arthasxHome = arthasxHome;
	}

	public String getTargetIp() {
		return targetIp;
	}

	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}

}
