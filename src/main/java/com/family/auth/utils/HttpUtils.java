package com.family.auth.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Optional;

public class HttpUtils {


    private static final String client_id = "client_id";

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static String getServerAddress() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 36000);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                .map(ServletRequestAttributes::getRequest);
    }

    public static String getClientAddress(HttpServletRequest request) {
        // Nginx 反向代理设置X-Real-IP
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.hasText(ip)) {
            // 通过apache反向代理设置了X-Forwarded-For
            ip = request.getHeader("X-Forwarded-For");
            if (StringUtils.hasText(ip)) {
                // In a chain of proxy servers where this is fully utilized,
                // the first "for" parameter will disclose the client where
                // the request was first made, followed by any subsequent proxy identifiers.
                ip = ip.split(",")[0];
            } else {
                // 不经过反向代理访问
                ip = request.getRemoteAddr();
            }
        }

        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (Throwable cause) {
                Logger logger = LoggerFactory.getLogger(HttpUtils.class);
                logger.error("InetAddress.getLocalHost().getHostAddress()", cause);
            }
        }

        return ip;
    }

    public static String getClientId(HttpServletRequest request) {
        String clientId = request.getHeader(client_id);
        if (!StringUtils.hasText(clientId)) {
            clientId = request.getParameter(client_id);
        }

        // fix some languages, such as C#, http header set multiple times issues
        final String separator = ",";
        if (StringUtils.hasText(clientId) && clientId.contains(separator)) {
            return clientId.split(separator)[0];
        }

        return clientId;
    }

    public static String getUsername(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new IllegalArgumentException("用户必须经过OA统一登录认证");
        }

        return principal.getName();
    }

    public static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName();
        }

        return "";
    }

    public static void trustAllHttpsCerts() throws Exception {
        TrustManager[] trustAllCerts = { new TrustAllTrustManager() };
        SSLContext sc = SSLContext.getInstance("SSL");
        SSLSessionContext sslsc = sc.getServerSessionContext();
        sslsc.setSessionTimeout(0);
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier hv = (urlHostName, session) -> true;

        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    static class TrustAllTrustManager implements TrustManager, X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
