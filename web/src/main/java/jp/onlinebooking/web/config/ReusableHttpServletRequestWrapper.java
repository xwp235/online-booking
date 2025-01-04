package jp.onlinebooking.web.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringEscapeUtils;
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
        // todo 对可能为xss攻击的内容进行转义处理
        var sanitizedBody = originalBody;
        System.out.println(sanitizedBody);
        requestInputStream = new ByteArrayInputStream(sanitizedBody.getBytes(StandardCharsets.UTF_8));
    }

    // 用于安全处理请求体内容
    private String sanitizeBody(String originalBody) {
        String sanitizedBody = StringEscapeUtils.escapeHtml4(originalBody);
        sanitizedBody = sanitizedBody.replaceAll("(?i)<script", "&lt;script")
                .replaceAll("(?i)</script", "&lt;/script");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(originalBody);
            // 对 JSON 数据进行处理（比如移除不必要的字段）
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理 JSON 解析异常
            return sanitizedBody;
        }
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
