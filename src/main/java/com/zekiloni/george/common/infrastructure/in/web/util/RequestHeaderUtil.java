package com.zekiloni.george.common.infrastructure.in.web.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class RequestHeaderUtil {
    public static final String USER_AGENT = "User-Agent";
    public static final Set<String> IP_HEADER_CANDIDATES = Set.of(
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
    );
    public static final String UNKNOWN = "unknown";

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT);
    }

    public static String getIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
