package com.collokia.webapp.routes;


import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;

public class VertxServletOutputStream extends ServletOutputStream {
	
    private HttpServerResponse resp;
    private Buffer buffer;
	
    public VertxServletOutputStream(HttpServerResponse resp) {
        this.resp = resp;
        this.buffer = Buffer.buffer();
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
        buffer.appendByte((byte)b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffer.appendBytes(b);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.appendBytes(b, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        if (buffer.length() > 0) {
            resp.write(buffer);
            buffer = Buffer.buffer();
        }
    }

    @Override
    public void close() throws IOException {
        if (!resp.ended()) {
            resp.end(buffer);
        }
    }

    public byte[] bufferBytes() {
        return buffer.getBytes();
    }

    public int getBufferSize() {
        // TODO: does this even matter?
        return buffer.length();
    }

    public void resetBuffer() {
        buffer = Buffer.buffer();
    }
    

}

/*
abstract boolean	isReady()
This method can be used to determine if data can be written without blocking.
void	print(boolean b)
Writes a boolean value to the client, with no carriage return-line feed (CRLF) character at the end.
void	print(char c)
Writes a character to the client, with no carriage return-line feed (CRLF) at the end.
void	print(double d)
Writes a double value to the client, with no carriage return-line feed (CRLF) at the end.
void	print(float f)
Writes a float value to the client, with no carriage return-line feed (CRLF) at the end.
void	print(int i)
Writes an int to the client, with no carriage return-line feed (CRLF) at the end.
void	print(long l)
Writes a long value to the client, with no carriage return-line feed (CRLF) at the end.
void	print(String s)
Writes a String to the client, without a carriage return-line feed (CRLF) character at the end.
void	println()
Writes a carriage return-line feed (CRLF) to the client.
void	println(boolean b)
Writes a boolean value to the client, followed by a carriage return-line feed (CRLF).
void	println(char c)
Writes a character to the client, followed by a carriage return-line feed (CRLF).
void	println(double d)
Writes a double value to the client, followed by a carriage return-line feed (CRLF).
void	println(float f)
Writes a float value to the client, followed by a carriage return-line feed (CRLF).
void	println(int i)
Writes an int to the client, followed by a carriage return-line feed (CRLF) character.
void	println(long l)
Writes a long value to the client, followed by a carriage return-line feed (CRLF).
void	println(String s)
Writes a String to the client, followed by a carriage return-line feed (CRLF).

void	close()
Closes this output stream and releases any system resources associated with this stream.
void	flush()
Flushes this output stream and forces any buffered output bytes to be written out.
void	write(byte[] b)
Writes b.length bytes from the specified byte array to this output stream.
void	write(byte[] b, int off, int len)
Writes len bytes from the specified byte array starting at offset off to this output stream.
If b is null, a NullPointerException is thrown.
If off is negative, or len is negative, or off+len is greater than the length of the array b, then an IndexOutOfBoundsException is thrown.
IOException - if an I/O error occurs. In particular, an IOException is thrown if the output stream is closed.
abstract void	write(int b)
Writes the specified byte to this output stream.
*/