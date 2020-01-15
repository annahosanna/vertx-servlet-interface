package com.collokia.webapp.routes;

// import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang.NotImplementedException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class VertxHttpServletResponse implements HttpServletResponse {
    final RoutingContext context;
    private final DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    private final VertxServletOutputStream outBuffer = new VertxServletOutputStream();
    private final PrintWriter outWriter = new PrintWriter(outBuffer);

    public void writeToVertx() {
        // The wrapper should call this when it is ready to send the response buffered here.  This could be changed to have it called directly,
        // but not all frameworks use the output stream in the same way, so I chose to wait until I was sure I, the wrapping code, wanted to write.

        Buffer output = Buffer.buffer(this.bufferBytes());
        context.response().end(output);
    }

    public byte[] bufferBytes() {
        return outBuffer.bufferBytes();
    }

    public VertxHttpServletResponse(RoutingContext context) {
       this.context = context;
    }

    @Override
    public void addCookie(javax.servlet.http.Cookie cookie) {
    	io.vertx.core.http.Cookie vertxCookie = io.vertx.core.http.Cookie.cookie(cookie.getName(), cookie.getValue());
    	vertxCookie.setDomain(cookie.getDomain());
        vertxCookie.setHttpOnly(cookie.isHttpOnly());
        vertxCookie.setMaxAge(cookie.getMaxAge());
        vertxCookie.setPath(cookie.getPath());
        vertxCookie.setSecure(cookie.getSecure());
    	context.addCookie(vertxCookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return context.response().headers().contains(name);
    }

    @Override
    public String encodeURL(String url) {
        return url; // encoding usually involves adding session information and such, but doesn't really apply to vertx
    }

    @Override
    public String encodeRedirectURL(String url) {
        return url; // encoding usually involves adding session information and such, but doesn't really apply to vertx

    }

    @Override
    public String encodeUrl(String url) {
        return url; // encoding usually involves adding session information and such, but doesn't really apply to vertx
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return url; // encoding usually involves adding session information and such, but doesn't really apply to vertx
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        context.response().setStatusCode(sc).setStatusMessage(msg).end();
    }

    @Override
    public void sendError(int sc) throws IOException {
        context.response().setStatusCode(sc).end();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        context.response().putHeader("location", location).setStatusCode(302).end();
    }

    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, dateFormat.format(new Date(date)));
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, dateFormat.format(new Date(date)));
    }

    @Override
    public void setHeader(String name, String value) {
        context.response().putHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        LinkedList<String> headers = new LinkedList<>(context.response().headers().getAll(name) );
        headers.add(value);
        context.response().putHeader(name,headers);
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, String.valueOf(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, String.valueOf(value));
    }

    @Override
    public void setStatus(int sc) {
        context.response().setStatusCode(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        context.response().setStatusCode(sc).setStatusMessage(sm);
    }

    @Override
    public int getStatus() {
        return context.response().getStatusCode();
    }

    @Override
    public String getHeader(String name) {
        return context.response().headers().get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return context.response().headers().getAll(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return context.response().headers().names();
    }

    @Override
    public String getCharacterEncoding() {
        throw new NotImplementedException();
    }

    @Override
    public String getContentType() {
        return getHeader(io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE.toString());
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outBuffer;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return outWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new NotImplementedException();
    }

    @Override
    public void setContentLength(int len) {
        throw new NotImplementedException();
    }

    @Override
    public void setContentLengthLong(long len) {
        throw new NotImplementedException();
    }

    @Override
    public void setContentType(String type) {
        setHeader(io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE.toString(), type);
    }

    @Override
    public void setBufferSize(int size) {
        // just ignore
    }

    @Override
    public int getBufferSize() {
        // TODO: does this even matter?
        return outBuffer.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        outBuffer.flush();
    }

    @Override
    public void resetBuffer() {
        outBuffer.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        // since we defer writing, it is never committed
        return false;
    }

    @Override
    public void reset() {
        context.response().setStatusCode(HttpResponseStatus.OK.code());
        context.response().setStatusMessage("");
        resetBuffer();
    }

    @Override
    public void setLocale(Locale loc) {
        throw new NotImplementedException();
    }

    @Override
    public Locale getLocale() {
        throw new NotImplementedException();
    }
}
