package org.knight.websocketchat;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 处理 Http 请求
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;
    private static final File INDEX;

    static {
        File file = new Resource().getIndexFile();
        INDEX = file;
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    /**
     * 覆盖了 channelRead0() 事件处理方法。
     * 每当从服务端读到客户端写入信息时，
     * 其中如果你使用的是 Netty 5.x 版本时，需要把 channelRead0() 重命名为messageReceived()
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            RandomAccessFile file = new RandomAccessFile(INDEX, "r");

            HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

            boolean keepAlive = HttpHeaders.isKeepAlive(request);

            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);

            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            
            file.close();
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        System.out.println("send100Continue--->" + response.toString());
        ctx.writeAndFlush(response);
    }

    /**
    * exceptionCaught() 事件处理方法是当出现 Throwable 对象才会被调用，
    * 即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时。
    * 在大部分情况下，捕获的异常应该被记录下来并且把关联的 channel 给关闭掉。
    * 然而这个方法的处理方式会在遇到不同异常的情况下有不同的实现，
    * 比如你可能想在关闭连接之前发送一个错误码的响应消息。
    */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
    	Channel incoming = ctx.channel();
		System.out.println("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
	}
}
