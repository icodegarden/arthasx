package xff.arthasx.common.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import xff.arthasx.common.AnsiLog;
import xff.arthasx.common.Result;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public abstract class Exec {

	protected static final String ARTHASATTACH_PLACEHOLDER_ARTHASXHOME = "${arthasxhome}";
	protected static final String ARTHASATTACH_PLACEHOLDER_TARGETIP = "${targetIp}";
	protected static final String ARTHASATTACH_PLACEHOLDER_TUNNELSERVERADDRESS = "${tunnelServerAddress}";
	protected static final String ARTHASATTACH_PLACEHOLDER_AGENTID = "${agentId}";
	protected static final String ARTHASATTACH_PLACEHOLDER_PID = "${pid}";

	protected static final String ARTHASATTACH_CMD = ARTHASATTACH_PLACEHOLDER_ARTHASXHOME + "/attach.sh --arthasx-home="
			+ ARTHASATTACH_PLACEHOLDER_ARTHASXHOME + " --target-ip=" + ARTHASATTACH_PLACEHOLDER_TARGETIP
			+ " --tunnel-server=" + ARTHASATTACH_PLACEHOLDER_TUNNELSERVERADDRESS + " --agent-id="
			+ ARTHASATTACH_PLACEHOLDER_AGENTID + " --pid=" + ARTHASATTACH_PLACEHOLDER_PID;

	public ExecResult exec(String... cmds) throws IOException {
		return exec(null, null, null, cmds);
	}

	public abstract ExecResult exec(List<String> includes, List<String> excludes, Integer lineIndex, String... cmds)
			throws IOException;

	public abstract ExecResult getJVMPids(String jpsKeywords) throws IOException;

	public Result<Void> arthasAttach(String arthasxhome, String tunnelServerAddress, String jpsKeywords,
			String targetIp, String agentId) throws IOException {
		// get target jvm pid
		ExecResult execResult = getJVMPids(jpsKeywords);
		if (!execResult.isSuccess()) {
			return Result.builder()
					.buildFailed("get JVMPid failed, messages:" + execResult.toStringFullMessages());
		}
		List<String> jvmPids = execResult.getLines();
		if (jvmPids.size() != 1) {
			return Result.builder().buildFailed("JVMPid not exactly found, jpsKeywords:" + jpsKeywords
					+ " ,found JVMPids:" + jvmPids + " ,messages:" + execResult.toStringFullMessages());
		}

		final String cmd = ARTHASATTACH_CMD.replace(ARTHASATTACH_PLACEHOLDER_AGENTID, agentId)
				.replace(ARTHASATTACH_PLACEHOLDER_ARTHASXHOME, arthasxhome)
				.replace(ARTHASATTACH_PLACEHOLDER_TARGETIP, targetIp)
				.replace(ARTHASATTACH_PLACEHOLDER_TUNNELSERVERADDRESS, tunnelServerAddress)
				.replace(ARTHASATTACH_PLACEHOLDER_PID, jvmPids.get(0));

		// attach
		execResult = doArthasAttach(cmd);

		AnsiLog.info("attach info:");
		AnsiLog.info(execResult.toStringFullMessages());
		if (!execResult.isSuccess()) {
			return Result.builder().buildFailed("attach failed, messages:" + execResult.toStringFullMessages());
		}
		return Result.builder().buildSuccess(null);
	}

	protected abstract ExecResult doArthasAttach(String cmd) throws IOException;

	protected ExecResult extractResponse(Process p, List<String> includes, List<String> excludes, Integer lineIndex)
			throws IOException {
		try {
			try {
				p.waitFor(3000, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				//
			}
			List<String> lines = new ArrayList<String>(0);
			List<String> fullMessages = new ArrayList<String>(0);
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					fullMessages.add(line);
					if (includes != null) {
						for (String include : includes) {
							if (!line.contains(include)) {
								line = null;
								break;
							}
						}
					}
					if (line != null) {
						if (excludes != null) {
							for (String exclude : excludes) {
								if (line.contains(exclude)) {
									line = null;
									break;
								}
							}
						}
					}
					if (line != null) {
						if (lineIndex != null) {
							lines.add(line.split("\\s+")[lineIndex]);
						} else {
							lines.add(line);
						}
					}
				}
			}finally {
				if(reader != null) {
					reader.close();
				}
			}
			return new ExecResult(p.exitValue(), lines, fullMessages);
		} finally {
			p.destroy();
		}
	}

	public static class ExecResult {
		private int exitValue;
		private List<String> lines;
		private List<String> fullMessages;

		public ExecResult(int exitValue, List<String> lines, List<String> fullMessages) {
			this.exitValue = exitValue;
			this.lines = lines;
			this.fullMessages = fullMessages;
		}

		public boolean isSuccess() {
			return exitValue == 0;
		}

		public int getExitValue() {
			return exitValue;
		}

		public List<String> getLines() {
			return lines;
		}

		public String toStringFullMessages() {
			return fullMessages.stream().collect(Collectors.joining(";\n"));
		}
	}
}
