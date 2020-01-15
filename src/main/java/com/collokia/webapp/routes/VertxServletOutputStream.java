package com.collokia.webapp.routes;


import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VertxServletOutputStream extends ServletOutputStream {
	
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public byte[] bufferBytes() {
        return buffer.toByteArray();
    }

    public int getBufferSize() {
        // TODO: does this even matter?
        return buffer.size();
    }

    public void resetBuffer() {
        buffer.reset();
    }
    
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
    }

    @Override
    public void flush() throws IOException {
        buffer.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        buffer.close();
    }
}
