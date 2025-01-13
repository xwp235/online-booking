package jp.onlinebooking.web.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ReusableHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final ByteArrayInputStream requestInputStream;

    public ReusableHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        var outputRequestStream = new ByteArrayOutputStream();
        StreamUtils.copy(request.getInputStream(),outputRequestStream);
        var originalBody = outputRequestStream.toString(StandardCharsets.UTF_8);
        requestInputStream = new ByteArrayInputStream(originalBody.getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {

        // 重置requestInputStream流，将缓冲区重置到标记0的位置
        requestInputStream.reset();

        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return requestInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public int read() throws IOException {
                return requestInputStream.read();
            }

        };
    }

}
