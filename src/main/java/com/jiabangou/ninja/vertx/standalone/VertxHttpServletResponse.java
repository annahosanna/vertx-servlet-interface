package com.jiabangou.ninja.vertx.standalone;

import com.jiabangou.ninja.vertx.standalone.utils.DateGenerator;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * VertxHttpServletResponse
 * Created by freeway on 16/8/18.
 */
public class VertxHttpServletResponse implements HttpServletResponse {

    public static final String CONTENT_LENGTH =  HttpHeaders.CONTENT_LENGTH.toString();
    private HttpServerResponse resp;
    private RoutingContext event;
    private String _characterEncoding;
    private String _contentType;
    private int _bufferSize;

    private ServletOutputStream servletOutputStream;
    private PrintWriter printWriter;

    public VertxHttpServletResponse(RoutingContext event) {
        this.event = event;
        this.resp = event.response();
        servletOutputStream = new VertxServletOutputStream(resp);
        printWriter = new VertxPrintWriter(servletOutputStream);
    }

    private static io.vertx.ext.web.Cookie cookieConverter(javax.servlet.http.Cookie cookie) {
        io.vertx.ext.web.Cookie vertxCookie = io.vertx.ext.web.Cookie.cookie(cookie.getName(), cookie.getValue());
        vertxCookie.setDomain(cookie.getDomain());
        vertxCookie.setHttpOnly(cookie.isHttpOnly());
        vertxCookie.setMaxAge(cookie.getMaxAge());
        vertxCookie.setPath(cookie.getPath());
        vertxCookie.setSecure(cookie.getSecure());
        return vertxCookie;
    }

    @Override
    public void addCookie(Cookie cookie) {
        this.event.addCookie(cookieConverter(cookie));
    }

    @Override
    public boolean containsHeader(String s) {
        return resp.headers().contains(s);
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
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {
        setHeader(s, DateGenerator.formatDate(l));
    }

    @Override
    public void addDateHeader(String s, long l) {
        addHeader(s, DateGenerator.formatDate(l));
    }

    @Override
    public void setHeader(String s, String s1) {
        resp.headers().set(s, s1);
    }

    @Override
    public void addHeader(String s, String s1) {
        resp.headers().add(s, s1);
    }

    @Override
    public void setIntHeader(String s, int i) {
        setHeader(s, String.valueOf(i));
    }

    @Override
    public void addIntHeader(String s, int i) {
        addHeader(s, String.valueOf(i));
    }

    @Override
    public void setStatus(int i) {
        resp.setStatusCode(i);
    }

    @Override
    public void setStatus(int i, String s) {
        resp.setStatusMessage(s);
        setStatus(i);
    }

    @Override
    public int getStatus() {
        return resp.getStatusCode();
    }

    @Override
    public String getHeader(String s) {
        return resp.headers().get(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return resp.headers().getAll(s);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return resp.headers().names();
    }

    @Override
    public String getCharacterEncoding() {

        if (_characterEncoding == null)
            _characterEncoding = StandardCharsets.UTF_8.name();
        return _characterEncoding;
    }

    @Override
    public String getContentType() {
        return _contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {

        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public void setCharacterEncoding(String s) {
        _characterEncoding = s;
        String contentType = _contentType;
        if (contentType != null && _characterEncoding != null) {
            contentType = contentType.contains("; ")?contentType.split(";")[0]:contentType + "; charset=" + _characterEncoding;
            setHeader(HttpHeaders.CONTENT_TYPE.toString(), contentType);
        }
    }

    @Override
    public void setContentLength(int i) {
        setIntHeader(CONTENT_LENGTH, i);
    }

    @Override
    public void setContentLengthLong(long l) {
        setHeader(CONTENT_LENGTH, String.valueOf(l));
    }

    @Override
    public void setContentType(String s) {
        if (s == null) {
            return;
        }
        _contentType = s;
        String contentType = _contentType.contains("; ") ? _contentType.split(";")[0] : _contentType;
        if (_characterEncoding != null) {
            contentType = contentType + "; charset=" + _characterEncoding;
        }
        setHeader(HttpHeaders.CONTENT_TYPE.toString(), contentType);
    }

    @Override
    public void setBufferSize(int i) {
        _bufferSize = i;
    }

    @Override
    public int getBufferSize() {
        return _bufferSize;
    }

    @Override
    public void flushBuffer() throws IOException {
        this.servletOutputStream.flush();
    }

    @Override
    public void resetBuffer() {
        if (isCommitted())
            throw new IllegalStateException("Committed");

    }

    @Override
    public boolean isCommitted() {
        return resp.ended();
    }

    @Override
    public void reset() {
    }

    // How do I create a new Locale without using a deprecated method
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
