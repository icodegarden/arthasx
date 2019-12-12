package xff.arthasx.common.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xff.arthasx.common.AnsiLog;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class RuntimeExec extends Exec {

	@Override
	public ExecResult exec(List<String> includes, List<String> excludes, Integer lineIndex, String... cmds)
			throws IOException {
		Process p = null;
		if (cmds.length == 1) {
			p = Runtime.getRuntime().exec(cmds[0]);
		} else {
			p = Runtime.getRuntime().exec(cmds);
		}
		return extractResponse(p, includes, excludes, lineIndex);
	}

	@Override
	public ExecResult getJVMPids(String jpsKeywords) throws IOException {
		String jps = "jps";
		File jpsFile = findJps();
		if (jpsFile != null) {
			jps = jpsFile.getAbsolutePath();
		} else {
			AnsiLog.warn("warn: jps not found under JAVA_HOME, JVMPids may be not found.");
		}

		ExecResult execResult = null;
		try {
			execResult = exec(Arrays.asList(jpsKeywords), null, 0, jps + " -l -v");
		} catch (Exception e) {
			//
		}
		if (execResult == null || !execResult.isSuccess() || execResult.getLines().isEmpty()) {
			execResult = exec(null, Arrays.asList("arthasx-daemon"), 1, "ps -ef | grep java | grep " + jpsKeywords);
		}
		return execResult;
	}

	private File findJps() {
		// Try to find jps under java.home and System env JAVA_HOME

		String javaHome = System.getProperty("java.home");
		String[] paths = { "bin/jps", "bin/jps.exe", "../bin/jps", "../bin/jps.exe" };

		List<File> jpsList = new ArrayList<File>();
		for (String path : paths) {
			File jpsFile = new File(javaHome, path);
			if (jpsFile.exists()) {
				AnsiLog.debug("Found jps: " + jpsFile.getAbsolutePath());
				jpsList.add(jpsFile);
			}
		}
		if (jpsList.isEmpty()) {
			AnsiLog.debug("Can not find jps under :" + javaHome);
			String javaHomeEnv = System.getenv("JAVA_HOME");
			AnsiLog.debug("Try to find jps under env JAVA_HOME :" + javaHomeEnv);
			for (String path : paths) {
				File jpsFile = new File(javaHomeEnv, path);
				if (jpsFile.exists()) {
					AnsiLog.debug("Found jps: " + jpsFile.getAbsolutePath());
					jpsList.add(jpsFile);
				}
			}
		}

		if (jpsList.isEmpty()) {
			AnsiLog.debug("Can not find jps under current java home: " + javaHome);
			return null;
		}

		// find the shortest path, jre path longer than jdk path
		if (jpsList.size() > 1) {
			Collections.sort(jpsList, new Comparator<File>() {
				@Override
				public int compare(File file1, File file2) {
					try {
						return file1.getCanonicalPath().length() - file2.getCanonicalPath().length();
					} catch (IOException e) {
						// ignore
					}
					return -1;
				}
			});
		}
		return jpsList.get(0);
	}

	@Override
	protected ExecResult doArthasAttach(String cmd) throws IOException {
		return exec(cmd);
	}
}
