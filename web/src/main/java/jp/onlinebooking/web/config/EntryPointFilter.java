package jp.onlinebooking.web.config;

import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@NonNullApi
public class EntryPointFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 生成一个唯一的 LOG_ID
        String logId = request.getHeader(Constants.X_REQUESTED_ID);
        if (StringUtils.isBlank(logId)) {
            logId = UUID
        }

        try {
            // 将 LOG_ID 放入 MDC
            MDC.put("logId", logId);
            var isGet = StringUtils.equalsIgnoreCase(request.getMethod(), HttpMethod.GET.name());
            if (!isGet && isAjaxRequest(request)) {
                filterChain.doFilter(new ReusableHttpServletRequestWrapper(request), response);
            } else {
                filterChain.doFilter(request, response);
            }
        } finally {
            MDC.remove("logId");
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        var acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        var contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        var contentLength = request.getContentLength();
        var acceptJson = acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE);
        var contentJson = contentType != null && StringUtils.equals(contentType, MediaType.APPLICATION_JSON_VALUE) && contentLength > 0;
        return acceptJson || contentJson;
    }

}
