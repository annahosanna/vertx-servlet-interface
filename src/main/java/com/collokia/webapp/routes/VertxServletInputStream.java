package com.collokia.webapp.routes;


import javax.servlet.*;
import java.io.*;

public class VertxServletInputStream extends ServletInputStream {
  private final ByteArrayInputStream stream;

  VertxServletInputStream(ByteArrayInputStream stream) {
    this.stream = stream;
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public boolean isReady() {
    return stream.available() > 0;
  }

  @Override
  public void setReadListener(ReadListener readListener) {

  }

  @Override
  public int read() throws IOException {
    return stream.read();
  }
}
