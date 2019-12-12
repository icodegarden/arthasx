package xff.arthasx.daemon;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import xff.arthasx.common.Constants;
import xff.arthasx.common.Result;
import xff.arthasx.common.api.RuntimeExec;
import xff.arthasx.common.util.NetUtils;
import xff.arthasx.common.util.StringUtils;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@io.netty.channel.ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

	private final Map<String, Controller> CONTROLLERS = new HashMap<String, Controller>() {
		private static final long serialVersionUID = 362498820763181265L;
		{
			IndexController indexController = new IndexController();
			HealthController healthController = new HealthController();
			AttachController attachController = new AttachController();
			put(indexController.requestmapping(), indexController);
			put(healthController.requestmapping(), healthController);
			put(attachController.requestmapping(), attachController);
		}
	};
	
	private final DaemonProperties daemonProperties = new DaemonProperties();

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
		try {
			if (msg instanceof HttpRequest) {
				HttpRequest req = (HttpRequest) msg;

				DefaultFullHttpResponse response = null;
				QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
				Controller controller = CONTROLLERS.get(queryStringDecoder.path());
				if (controller != null) {
					response = controller.handle(req);
				} else {
					response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST,
							Unpooled.wrappedBuffer(Unpooled.wrappedBuffer("request path not found".getBytes("utf-8"))));
				}
				// set error-msg header
				if (response.status().code() < 200 || response.status().code() >= 400) {
					response.headers().set(Constants.HTTP_HEADER_ERROR_MSG,
							new String(response.content().array(), "utf-8"));
				}

				boolean keepAlive = HttpUtil.isKeepAlive(req);

				response.headers().set(CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON).setInt(CONTENT_LENGTH,
						response.content().readableBytes());

				if (keepAlive) {
					if (!req.protocolVersion().isKeepAliveDefault()) {
						response.headers().set(CONNECTION, KEEP_ALIVE);
					}
				} else {
					// Tell the client we're going to close the connection.
					response.headers().set(CONNECTION, CLOSE);
				}

				ChannelFuture f = ctx.write(response);

				if (!keepAlive) {
					f.addListener(ChannelFutureListener.CLOSE);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	interface Controller {
		String requestmapping();

		DefaultFullHttpResponse handle(HttpRequest req) throws Throwable;
	}

	class IndexController implements Controller {
		private final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'A', 'r', 't', 'h', 'a', 's', 'x' };

		@Override
		public String requestmapping() {
			return "/";
		}

		@Override
		public DefaultFullHttpResponse handle(HttpRequest req) {
			return new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(CONTENT));
		}
	}

	class HealthController implements Controller {
		private final byte[] STATUS_UP;
		{
			try {
				STATUS_UP = "{\"status\":\"UP\"}".getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String requestmapping() {
			return "/health";
		}

		@Override
		public DefaultFullHttpResponse handle(HttpRequest req) {
			return new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(STATUS_UP));
		}
	}

	class AttachController implements Controller {
		ReentrantLock lock = new ReentrantLock();

		@Override
		public String requestmapping() {
			return "/api/v1/attach";
		}

		@Override
		public DefaultFullHttpResponse handle(HttpRequest req) throws Throwable {
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
			DefaultFullHttpResponse response;
			Map<String, List<String>> params = queryStringDecoder.parameters();
			if (!params.containsKey("tunnelServerAddress")) {
				return new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST,
						Unpooled.wrappedBuffer("reqest param tunnelServerAddress must not empty".getBytes("utf-8")));
			}
			if (!params.containsKey("jpsKeywords")) {
				return new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST,
						Unpooled.wrappedBuffer("reqest param jpsKeywords must not empty".getBytes("utf-8")));
			}
			if (!params.containsKey("agentId")) {
				return new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST,
						Unpooled.wrappedBuffer("reqest param agentId must not empty".getBytes("utf-8")));
			}
			String tunnelServerAddress = params.get("tunnelServerAddress").get(0);
			String jpsKeywords = params.get("jpsKeywords").get(0);
			String agentId = params.get("agentId").get(0);

			String targetIp = daemonProperties.getTargetIp();
			try {
				Result<Void> result = Result.builder().buildSuccess(null);
				lock.lock();
				try {
					if (!NetUtils.isPortUsing(Constants.ARTHAS_WS_PORT)) {
						result = new RuntimeExec().arthasAttach(daemonProperties.getArthasxHome(), tunnelServerAddress,
								jpsKeywords, targetIp, agentId);
						if (!result.isSuccess()) {
							return new DefaultFullHttpResponse(req.protocolVersion(),
									HttpResponseStatus.INTERNAL_SERVER_ERROR,
									Unpooled.wrappedBuffer(result.getMessage().getBytes("utf-8")));
						}
					}
				} finally {
					lock.unlock();
				}
				response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK,
						Unpooled.wrappedBuffer(result.toSimpleJson().getBytes("utf-8")));
			} catch (Exception e) {
				response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
						Unpooled.wrappedBuffer(StringUtils.toString(e).getBytes("utf-8")));
			}
			return response;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}