package com.collokia.webapp.routes;

// import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class VertxHttpServletResponse implements HttpServletResponse {
  final RoutingContext context;
  private final DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
  private VertxServletOutputStream outBuffer = null;
  private final PrintWriter outWriter = new PrintWriter(outBuffer);
  final RoutingContext context;
  private final DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
  private VertxServletOutputStream outBuffer = null;
  private PrintWriter outWriter = null;
  final RoutingContext context;
  private final DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
  private VertxServletOutputStream outBuffer = null;
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
    this.outBuffer = new VertxServletOutputStream(context.response());
  }

  public VertxHttpServletResponse(RoutingContext context) {
      this.context = context;
      this.outBuffer = new VertxServletOutputStream(context.response());
  }
  public VertxHttpServletResponse(RoutingContext context) {
      this.context = context;
      this.outBuffer = new VertxServletOutputStream(context.response());
      this.outWriter = new PrintWriter(this.outBuffer);
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
  public String encodeURL(String s) {
    try {
      return URLEncoder.encode(s, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return s;
    }
  }

  @Override
  public String encodeURL(String url) {
    return url; // encoding usually involves adding session information and such, but doesn't really apply to vertx
  }

  @Override
  public String encodeURL(String url) {
    // Check this implementation to ensure the result format
    // Can use content-type to get char set
    try {
        return URLEncoder.encode(s, "UTF-8");
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        return s;
    }
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
  public String encodeUrl(String url) {
      return url; // encoding usually involves adding session information and such, but doesn't really apply to vertx
  }
  // https://commons.apache.org/proper/commons-validator/apidocs/index.html URLValidator
  // https://commons.apache.org/proper/commons-text/javadocs/api-release/index.html TextStringBuilder, StringEscapeUtils 
  // https://commons.apache.org/proper/commons-codec/apidocs/index.html (URLCodec)
  // https://hc.apache.org/httpcomponents-core-4.4.x/httpcore/apidocs/org/apache/http/entity/ContentType.html
  // https://developers.google.com/gdata/javadoc/com/google/gdata/util/ContentType.html
  // https://hc.apache.org/httpcomponents-core-4.4.x/httpcore/apidocs/org/apache/http/HttpStatus.html
  // https://hc.apache.org/httpcomponents-core-4.4.x/httpcore/apidocs/org/apache/http/HttpHeaders.html
  // https://hc.apache.org/httpcomponents-core-4.4.x/httpcore/apidocs/org/apache/http/message/BasicListHeaderIterator.html
  // https://hc.apache.org/httpcomponents-client-4.5.x/httpclient/apidocs/index.html (DateUtils, URIBuilder, URIUtils, URLEcondedUtils)
  //  IPAddressUtils

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

  // Content-Type header
  @Override
  public void setCharacterEncoding(String charset) {
    throw new NotImplementedException();
  }

  @Override
  public void setCharacterEncoding(String charset) {
      throw new NotImplementedException();
  }
  // THis is part of content-type
  @Override
  public void setCharacterEncoding(String charset) {
      throw new NotImplementedException();
  }

  // Content-Length header
  @Override
  public void setContentLength(int len) {
    java.lang.Integer intValue = java.lang.Integer(len); 
    setContentLength(intValue.longValue());
  }

  @Override
  public void setContentLength(long len) {
    setContentLengthLong(len);
  }

  @Override
  public void setContentLengthLong(long len) {
    java.lang.Long longValue = java.lang.Long(len) 
    setHeader(io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH.toString(), longValue.toString());
  }

  // Could also use https://developers.google.com/gdata/javadoc/com/google/gdata/util/ContentType.html
  // This also has the benifit of being able choose the best one from accept  
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
    return context.response().ended();
  }

  @Override
  public void reset() {
    context.response().setStatusCode(HttpResponseStatus.OK.code());
    context.response().setStatusMessage("");
    resetBuffer();
  }


  @Override
  public void setLocale(java.util.Locale locale) {
    // This should create a new LanguageHeader
    // prepended to the list
    if (locale == null) {
      locale = java.util.Locale.getDefault();
    }
    // io.vertx.ext.web.impl.ParsableLanguageValue newLH = new io.vertx.ext.web.impl.ParsableLanguageValue(locale.getLanguage() + "-" + locale.getCountry() + "-" + locale.getVariant());
    String newLocale = new String();
    if (!(StringUtils.isBlank(locale.getLanguage()))) {
      newLocale += locale.getLanguage();
      // if not blank append next by dash
        if (!(StringUtils.isBlank(locale.getCountry()))) {
          newLocale += "-" + locale.getCountry();
          // if not blank append next by dash
            if (!(StringUtils.isBlank(locale.getVariant()))) {
              newLocale += "-" + locale.getVariant();
            }
        }
      
    }
    if (event.response().headers().contains(io.vertx.core.http.HttpHeaders.ACCEPT_LANGUAGE)) {
      event.response().headers().remove(io.vertx.core.http.HttpHeaders.ACCEPT_LANGUAGE);
    }
    if (!(StringUtils.isBlank(newLocale))) {
      event.response().putHeader(io.vertx.core.http.HttpHeaders.ACCEPT_LANGUAGE, newLocale);
    }

  }
  

    @Override
    public java.util.Locale getLocale() {
        io.vertx.ext.web.LanguageHeader locale = event.preferredLanguage();
        if (locale == null) {
        	return java.util.Locale.getDefault();
        }
        // Return "" if subtags are null
        // Return java.util.Locale.getDefault() if empty or null
        String language = locale.tag();
        String country = locale.subtag(1);
        String variant = locale.subtag(2);
        language = language == null ? "" : language;
        country = country == null ? "" : country;
        variant = variant == null ? "" : variant;
        return new java.util.Locale(language, country, variant);
    }

}
