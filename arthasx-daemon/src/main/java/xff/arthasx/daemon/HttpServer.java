package xff.arthasx.daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import xff.arthasx.common.AnsiLog;
import xff.arthasx.common.Constants;
import xff.arthasx.common.api.RuntimeExec;
import xff.arthasx.common.api.Exec.ExecResult;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public final class HttpServer {

	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("server.port", SSL ? "8442" : "8079"));

	public static void main(String[] args) throws Exception {
		prepareArthasFiles(args);

		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(1);
		try {
			HttpServerHandler serverHandler = new HttpServerHandler();

			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new HttpServerInitializer(sslCtx, serverHandler));

			Channel ch = b.bind(PORT).sync().channel();

			AnsiLog.info(
					"Open your web browser and navigate to " + (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');

			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void prepareArthasFiles(String[] args) throws Exception {
		AnsiLog.info("prepare arthas files...");
		// prepare arthas files
		final String arthasDir = Constants.ARTHAS_DIR;
		final String attachScriptFileName = "attach.sh";
		List<String> filesToCopy = new ArrayList<String>(Arrays.asList("arthas-agent.jar", "arthas-boot.jar",
				"arthas-client.jar", "arthas-core.jar", "arthas-spy.jar", "as-service.bat", "as.bat", "as.sh",
				"install-local.sh", attachScriptFileName));

		File copyToDir = new File("arthasx");
		String arthasxHome = copyToDir.getAbsolutePath();

		if (!copyToDir.exists()) {
			boolean result = copyToDir.mkdirs();
			AnsiLog.info("mkdir:" + arthasxHome + (result ? " OK" : " Failed"));
			if (!result) {
				System.exit(-1);
			}
		} else {
			File[] existFiles = copyToDir.listFiles();
			if (existFiles != null && existFiles.length > 0) {
				for (File existFile : existFiles) {
					if (filesToCopy.remove(existFile.getName())) {
						AnsiLog.info("exists file:" + existFile.getName() + " in " + arthasxHome);
					}
				}
			}
		}
		if (!filesToCopy.isEmpty()) {
			AnsiLog.info("needs copy arthas files:" + filesToCopy);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			for (String arthasFile : filesToCopy) {
				AnsiLog.info("starting copy arthas file:" + arthasFile);
				InputStream is = null;
				FileOutputStream os = null;
				try {
					is = classLoader.getResourceAsStream(arthasDir + "/" + arthasFile);
					os = new FileOutputStream(arthasxHome + "/" + arthasFile);
					byte[] buf = new byte[10240];
					int i = 0;
					while ((i = is.read(buf)) != -1) {
						os.write(buf, 0, i);
					}
				} finally {
					if (os != null) {
						os.close();
					}
					if (is != null) {
						is.close();
					}
				}
				AnsiLog.info("succeed copy arthas file:" + arthasFile);
			}
		}

		final String cmd = "chmod 544 " + arthasxHome + "/" + attachScriptFileName;
		ExecResult execResult = new RuntimeExec().exec(cmd);
		if (!execResult.isSuccess()) {
			AnsiLog.error("failed exec cmd:{}, exit value:{}, message:{}", cmd, execResult.getExitValue(),
					execResult.toStringFullMessages());
			System.exit(-1);
		}

		System.setProperty(DaemonProperties.ARTHASX_HOME, arthasxHome);
	}
}