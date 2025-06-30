package com.njdealsandclicks.config.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.*;

public class CachingHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final ServletOutputStream outputStream = new DelegatingServletOutputStream(buffer);
    private PrintWriter writer;

    private boolean writerUsed = false;
    private boolean outputStreamUsed = false;

    public CachingHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (writerUsed) {
            throw new IllegalStateException("getWriter() already called");
        }
        outputStreamUsed = true;
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStreamUsed) {
            throw new IllegalStateException("getOutputStream() already called");
        }
        writerUsed = true;
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(buffer, getCharacterEncoding()), true);
        }
        return writer;
    }

    public String getContentAsString() throws IOException {
        flushBuffer();
        return buffer.toString(getCharacterEncoding());
    }

    public byte[] getContentAsBytes() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    private static class DelegatingServletOutputStream extends ServletOutputStream {
        private final OutputStream stream;

        public DelegatingServletOutputStream(OutputStream stream) {
            this.stream = stream;
        }

        @Override
        public void write(int b) throws IOException {
            stream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // no-op
        }
    }
}

