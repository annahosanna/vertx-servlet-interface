package com.collokia.webapp.routes;

// Context attributes are used to communicate between handlers - but that should not go here
// Attributes of each request are prepopulated with context attributes (but do not set context attributes)
// HttpServletRequest is a single request within an HttpSession.
// Always populate javax.servlet.request.X509Certificate if there are client certificates

import io.vertx.core.MultiMap;
// import io.netty.handler.codec.http.HttpHeaders;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang.NotImplementedException;

import javax.servlet.*;

// This includes a cookie type
import javax.servlet.http.*;
import java.io.*;
import java.net.URI;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import io.vertx.core.http.HttpServerRequest;
import com.collokia.webapp.routes.VertxHttpSession;
import com.collokia.webapp.routes.VertxServletInputStream;

/**
* HttpServletRequest wrapper over a vert.x {@link io.vertx.core.http.HttpServerRequest}
*/
public class VertxHttpServletRequest implements HttpServletRequest {

  private final RoutingContext context;
  private final URI requestUri;
  private final DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  public VertxHttpServletRequest(RoutingContext context) {
    this.context = context;
    this.requestUri = URI.create(context.request().absoluteURI());
  }


  @Override
  public String getAuthType() {
    // TODO: AUTH -- if supporting vertx-auth we would need to do something here, and other methods below (marked with TODO: AUTH)
    return null;
  }

  @Override
  public javax.servlet.http.Cookie[] getCookies() {

    // Was a Set of cookies - now a sting,cookie map
    Map<String, io.vertx.core.http.Cookie> cookiesMapped = context.cookieMap();
    // Would need a helper function to convert this to a Set
    Set<io.vertx.core.http.Cookie> cookies = new HashSet<>(cookiesMapped.values());
    javax.servlet.http.Cookie[] results = new javax.servlet.http.Cookie[cookies.size()];
    int i = 0;
    for (io.vertx.core.http.Cookie oneCookie : cookies) {
      results[i] = new javax.servlet.http.Cookie(oneCookie.getName(), oneCookie.getValue());
      results[i].setDomain(oneCookie.getDomain());
      results[i].setPath(oneCookie.getPath());
    }
    return results;
  }

  @Override
  public long getDateHeader(String name) {
    String header = context.request().headers().get(name);
    if (header == null) {
      return -1;
    }
    synchronized (this) {
      try {
        return dateFormat.parse(header).getTime();
      } catch (ParseException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  @Override
  public String getHeader(String name) {
    return context.request().headers().get(name);
  }


  @Override
  public Enumeration<String> getHeaders(String name) {
    // return Collections.enumeration(context.request().headers().getAll(name));
    return getHeaders(name, context.request());
  }

  public Enumeration<String> getHeaders(String name, HttpServerRequest request) {
    final MultiMap headers = request.headers();
    if (headers == null) {
      return Collections.emptyEnumeration();
    }
    final List<String> allValues = headers.getAll(name);
    if (allValues == null) {
      return Collections.emptyEnumeration();
    }
    final Iterator<String> it = allValues.iterator();
    if (it == null) {
      return null;
    }
    return new Enumeration<String>() {

      @Override
      public final boolean hasMoreElements() {
        return it.hasNext();
      }

      @Override
      public final String nextElement() {
        return it.next();
      }
    };
  }


  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(context.request().headers().names());
  }

  @Override
  public int getIntHeader(String name) {
    String header = context.request().headers().get(name);
    if (header == null) {
      return -1;
    }
    return Integer.parseInt(header);
  }


  @Override
  public String getMethod() {
    return context.request().method().toString();
  }

  @Override
  public String getPathInfo() {
    return context.request().path();
  }

  @Override
  public String getPathTranslated() {
    // TODO: is this the same as return context.normalisedPath();
    throw new NotImplementedException();
  }


  @Override
  public String getContextPath() {
    // TODO: assuming we don't really mount a servlet context, root is ok
    return "/";
  }

  @Override
  public String getQueryString() {
    return context.request().query();
  }

  @Override
  public String getRemoteUser() {
    // TODO: AUTH -- some kind of Session implementation
    throw new NotImplementedException();
  }

  @Override
  public boolean isUserInRole(String role) {
    // TODO: AUTH -- some kind of Session implementation. context.user().isAuthorized(role, asyncCallback) may return
	// implementation specific results
    return false;
  }


  @Override
  public Principal getUserPrincipal() {
    // TODO: AUTH -- would require conversion from context.user().principle() and convert it
    // but that type is a json value whose data is auth provider dependent
    return null;
  }

  @Override
  public String getRequestedSessionId() {
    return context.session().id();
  }


  @Override
  public String getRequestURI() {
    if (requestUri == null) {
      return null;
    }
    return requestUri.getPath();
  }


  @Override
  public StringBuffer getRequestURL() {
    String uri = context.request().absoluteURI();
    if (uri == null) {
      return null;
    }
    int index = uri.indexOf("?");
    return new StringBuffer(index >= 0 ? uri.substring(0, index) : uri);
  }

  @Override
  public String getServletPath() {
    // TODO:  again, no real servlet, so this maybe could be context.currentRoute().getPath()
    throw new NotImplementedException();
  }

  @Override
  public HttpSession getSession(boolean create) {
    return new VertxHttpSession(context.session());
  }

  @Override
  public HttpSession getSession() {
    return new VertxHttpSession(context.session());
  }


  @Override
  public String changeSessionId() {
    throw new NotImplementedException();
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    throw new NotImplementedException();
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    throw new NotImplementedException();
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    throw new NotImplementedException();
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    throw new NotImplementedException();
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    // TODO: AUTH
    throw new NotImplementedException();
  }

  @Override
  public void login(String username, String password) throws ServletException {
    // TODO: AUTH
    throw new NotImplementedException();
  }


  @Override
  public void logout() throws ServletException {
    context.clearUser();
    context.session().destroy();
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    throw new NotImplementedException();
  }


  @Override
  public Part getPart(String name) throws IOException, ServletException {
    throw new NotImplementedException();
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
    throw new NotImplementedException();
  }

//	X509Certificate certs[] = (X509Certificate[]) request
//   .getAttribute("javax.servlet.request.X509Certificate");
// which is the same as httpserverrequest.netsocket.SSLSession.getPeerCertificates
// There is one ServeletContext Per web application.
  @Override
  public Object getAttribute(String name) {
    return context.data().get(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return Collections.enumeration(context.data().keySet());
  }

  @Override
  public String getCharacterEncoding() {
	// TODO: encoding of input stream
    throw new NotImplementedException();
  }


  @Override
  public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
    throw new NotImplementedException();
  }


  // TODO: This is the length made available by the input stream with a fall back to content-length header which is only required for HTTP 1.0
  @Override
  public int getContentLength() {
    String header = context.request().headers().get(io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH.toString());
    if (header == null) {
      return -1;
    }
    return Integer.parseInt(header);
  }


  @Override
  public long getContentLengthLong() {
    String header = context.request().headers().get(io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH.toString());
    if (header == null) {
      return -1;
    }
    return Long.parseLong(header);
  }


  @Override
  public String getContentType() {
    return context.request().headers().get(io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE.toString());
  }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new VertxServletInputStream(new ByteArrayInputStream(context.getBody.getBytes()));
    }



  @Override
  public String getParameter(String name) {
    String value = context.request().params().get(name);
    if (value != null) {
      return value;
    }
    List<String> values = context.request().formAttributes().getAll(name);
    if (values != null && !values.isEmpty()) {
      return values.get(0);
    }
    return null;
  }

  @Override
  public Enumeration<String> getParameterNames() {
	// Or empty enumeration if none (post params also included)
    List<String> names = new ArrayList<>(context.request().params().names());
    if (!context.request().formAttributes().isEmpty()) {
      names.addAll(context.request().formAttributes().names());
    }
    return Collections.enumeration(names);
  }

  @Override
  public String[] getParameterValues(String name) {

    List<String> values = context.request().params().getAll(name);
    if (!context.request().formAttributes().isEmpty()) {
      List<String> formValues = context.request().formAttributes().getAll(name);
      if (formValues != null && !formValues.isEmpty()) {
        values.addAll(formValues);
      }
    }

    if (values != null && !values.isEmpty()) {
      return values.toArray(new String[values.size()]);
    }

    // TODO: This looks like it does nothing with the values  variable
	 @Override
    public Map<String, String[]> getParameterMap() {
        MultiMap map = httpServerRequest.params();
        Map<String, String[]> parameterMap = new HashMap<>();
        for (String name : httpServerRequest.params().names()) {
            List<String> values = map.getAll(name);
            parameterMap.put(name, values.toArray(new String[values.size()]));
        }
        return parameterMap;
    return EMPTY_STRING_ARRAY;
  }
    }
	

  //       for (Map.Entry<String, String> e : context.request().params()) {
  //           List<String> values = map.get(e.getKey());
  //           if (values == null) {
  //               values = new ArrayList<>();
  //               map.put(e.getKey(), values);
  //           }
  //           values.add(e.getValue());
  //       }
  //       for (Map.Entry<String, String> e : context.request().params()) {
  //           List<String> values = map.get(e.getKey());
  //           if (values == null) {
  //               values = new ArrayList<>();
	//     }
	// 	    if (e.getValue != null) {
  //           		values.add(e.getValue());
	// 		 map.put(e.getKey(), values);
	// 	    }
  //       }
  // // TODO: This looks like it does nothing with the values  variable
  // @Override
  // public Map<String, String[]> getParameterMap() {
  //   Map<String, List<String>> map = new HashMap<>();

  //       for (Map.Entry<String, String> e : context.request().formAttributes().entries()) {
  //           List<String> values = map.get(e.getKey());
  //           if (values == null) {
  //               values = new ArrayList<>();
  //               map.put(e.getKey(), values);
  //           }
  //           values.add(e.getValue());
  //       }
  //       for (Map.Entry<String, String> e : context.request().formAttributes().entries()) {
  //           List<String> values = map.get(e.getKey());
  //           if (values == null) {
  //               values = new ArrayList<>();
  //           } 
	// 	    if (e.getValue != null) {
  //           		values.add(e.getValue());
	// 		 map.put(e.getKey(), values);
	// 	    }
        
  //       }
  //   for (Map.Entry<String, String> e : context.request().params()) {
  //     List<String> values = map.get(e.getKey());
  //     if (values == null) {
  //       values = new ArrayList<>();
  //       map.put(e.getKey(), values);
  //     }
  //     values.add(e.getValue());
  //   }

  //   for (Map.Entry<String, String> e : context.request().formAttributes().entries()) {
  //     List<String> values = map.get(e.getKey());
  //     if (values == null) {
  //       values = new ArrayList<>();
  //       map.put(e.getKey(), values);
  //     }
  //     values.add(e.getValue());
  //   }

  //   Map<String, String[]> arrayMap = new HashMap<>();

  //   for (Map.Entry<String, List<String>> e : map.entrySet()) {
  //     arrayMap.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
  //   }

  //   return arrayMap;
  // }

  @Override
  public String getProtocol() {
    return context.request().version().name();
  }

  @Override
  public String getScheme() {
    return requestUri.getScheme();
  }

  @Override
  public String getServerName() {
    return requestUri.getHost();
  }

  @Override
  public int getServerPort() {
    int port = requestUri.getPort();
    if (port == 0) {
      return ("https".equals(getScheme())) ? 443 : 80;
    }
    return port;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public String getRemoteAddr() {
    SocketAddress address = context.request().remoteAddress();
    if (address == null) {
      return null;
    }
    return address.toString();
  }


  @Override
  public String getRemoteHost() {
    return getRemoteAddr();
  }


  @Override
  public void setAttribute(String name, Object o) {
	// Attributes are not persisted between requests so this is not really useful
    context.put(name, o);
  }


  @Override
  public void removeAttribute(String name) {
    context.data().remove(name);
  }


	// Get the highest preference/first locale
	@Override
    public Locale getLocale() {
	List<LanguageHeader> languages = this.context.acceptableLanguages()
        // io.vertx.ext.web.Locale locale = this.context.event.preferredLocale();
        return new Locale(locale.language(), locale.country(), locale.variant());
    }

  @Override
  public Locale getLocale() {
    String header = context.request().headers().get(io.netty.handler.codec.http.HttpHeaderNames.ACCEPT_LANGUAGE.toString());
    if (header == null) {
      return Locale.US;
    }
    return new Locale(header);
  }

    @Override
    public Enumeration<Locale> getLocales() {
	    // Use acceptableLanguages not acceptableLocales
        return Collections.enumeration(this.context.acceptableLocales().stream()
                .map(locale ->
                        new Locale(locale.language(), locale.country(), locale.variant()))
                .collect(toList()));
    }

  @Override
  public boolean isSecure() {
    // TODO: would be nice if this looked at the proxy / load balancer header too.  But I think servlet spec only talks about the local server itself
    return getScheme().equalsIgnoreCase("https");
  }


  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    throw new NotImplementedException();
  }


  @Override
  public String getRealPath(String path) {
    throw new NotImplementedException();
  }

  @Override
  public int getRemotePort() {
    // TODO: not important
    return context.request().remoteAddress() == null ? 0 : context.request().remoteAddress().port();
    // throw new NotImplementedException();
  }


  @Override
  public String getLocalName() {
    // TODO: we don't have the name handy, ip address works?
    return context.request().localAddress().host();
  }


  @Override
  public String getLocalAddr() {
    // TODO: Double check what toString does
    return context.request().localAddress().toString();
  }

  @Override
  public int getLocalPort() {
    return context.request().localAddress().port();
  }

  @Override
  public ServletContext getServletContext() {
    // TODO: doing this means we never end bleeding and implement another billion parts of servlet spec
	// Really need javax.servlet.request.X509Certificate. As in:
	// java.security.cert.X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
    throw new NotImplementedException();
  }


  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    throw new NotImplementedException();
  }


  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
    throw new NotImplementedException();
  }


  @Override
  public boolean isAsyncStarted() {
    return false;
  }


  @Override
  public boolean isAsyncSupported() {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext() {
    throw new NotImplementedException();
  }

  @Override
  public DispatcherType getDispatcherType() {
    return DispatcherType.REQUEST;
  }
}
