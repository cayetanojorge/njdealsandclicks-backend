package com.njdealsandclicks.config.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CachingHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private final CharArrayWriter charArray = new CharArrayWriter();
    private final PrintWriter writer = new PrintWriter(charArray);

    public CachingHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    public String getContent() {
        writer.flush();
        return charArray.toString();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Use getWriter()");
    }
} 
