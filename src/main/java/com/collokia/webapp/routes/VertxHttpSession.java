package com.collokia.webapp.routes;


import io.vertx.ext.web.Session;
import org.apache.commons.lang.NotImplementedException;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public class VertxHttpSession implements HttpSession {
  private final Session session;

  VertxHttpSession(Session session) {
    this.session = session;
  }

  @Override
  public long getCreationTime() {
    throw new NotImplementedException();
  }

  @Override
  public String getId() {
    return session.id();
  }

  @Override
  public long getLastAccessedTime() {
    return session.lastAccessed();
  }

  @Override
  public ServletContext getServletContext() {
    throw new NotImplementedException();
  }

  @Override
  public void setMaxInactiveInterval(int interval) {
    throw new NotImplementedException();
  }

  @Override
  public int getMaxInactiveInterval() {
    throw new NotImplementedException();
  }

  // Deprecated with no replacement per spec
  @SuppressWarnings("deprecation")
  @Override
  public HttpSessionContext getSessionContext() {
    throw new NotImplementedException();
  }

  @Override
  public Object getAttribute(String name) {
    return session.get(name);
  }

  @Override
  public Object getValue(String name) {
    return session.get(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return Collections.enumeration(session.data().keySet());
  }

  @Override
  public String[] getValueNames() {
    return (String[]) session.data().keySet().toArray();
  }

  @Override
  public void setAttribute(String name, Object value) {
    if (value == null) {
      session.remove(name);
    } else {
      session.put(name, value);
    }
  }

  @Override
  public void putValue(String name, Object value) {
    if (value == null) {
      session.remove(name);
    } else {
      session.put(name, value);
    }
  }

  @Override
  public void removeAttribute(String name) {
    session.remove(name);
  }

  @Override
  public void removeValue(String name) {
    session.remove(name);
  }

  @Override
  public void invalidate() {
    session.destroy();
  }

  @Override
  public boolean isNew() {
    return false;
  }
}
