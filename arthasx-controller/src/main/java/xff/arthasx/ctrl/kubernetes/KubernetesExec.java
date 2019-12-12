package xff.arthasx.ctrl.kubernetes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.ApiException;
import xff.arthasx.common.api.Exec;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class KubernetesExec extends Exec {

	private io.kubernetes.client.Exec exec;

	private String namespace;
	private String pod;
	private String containerName;

	public KubernetesExec(String namespace, String pod, String containerName) {
		this(new io.kubernetes.client.Exec(), namespace, pod, containerName);
	}

	public KubernetesExec(io.kubernetes.client.Exec exec, String namespace, String pod, String containerName) {
		this.exec = exec;
		this.namespace = namespace;
		this.pod = pod;
		this.containerName = containerName;
	}

	@Override
	public ExecResult exec(List<String> includes, List<String> excludes, Integer lineIndex, String... cmds)
			throws IOException {
		try {
			Process proc = exec.exec(namespace, pod, cmds, containerName, true, true);
			return extractResponse(proc, includes, excludes, lineIndex);
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ExecResult getJVMPids(String jpsKeywords) throws IOException {
		ExecResult execResult = null;
		try{
			execResult = exec(Arrays.asList(jpsKeywords), null, 0, "jps", "-l", "-v");
		}catch (Exception e) {
			//
		}
		if (execResult == null || !execResult.isSuccess() || execResult.getLines().isEmpty()) {
			List<String> cmds = Arrays.asList("sh", "-c", "ps -ef | grep java | grep " + jpsKeywords);
			final String cmdStr = cmds.stream().collect(Collectors.joining(" "));
			execResult = exec(null, Arrays.asList(cmdStr, "arthasx-daemon"), 1, cmds.toArray(new String[cmds.size()]));
		}
		return execResult;
	}

	@Override
	protected ExecResult doArthasAttach(String cmd) throws IOException {
		return exec("sh", "-c", cmd);
	}
}