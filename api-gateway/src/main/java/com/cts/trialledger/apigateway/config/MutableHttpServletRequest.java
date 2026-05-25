package com.cts.trialledger.apigateway.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * MutableHttpServletRequest
 *
 * HttpServletRequest headers are read-only by the servlet spec.
 * This wrapper overrides getHeader() / getHeaders() / getHeaderNames()
 * so we can inject custom headers (X-User-Id, X-User-Role, etc.)
 * before the request is forwarded to the downstream microservice.
 *
 * This is the standard pattern for header mutation in servlet-based
 * Spring Cloud Gateway (gateway-server-webmvc).
 */
public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders = new HashMap<>();

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    /** Add or overwrite a header. */
    public void putHeader(String name, String value) {
        customHeaders.put(name.toLowerCase(), value);
    }

    /** Remove a header (e.g. strip Authorization before forwarding). */
    public void removeHeader(String name) {
        customHeaders.put(name.toLowerCase(), null); // null = removed
    }

    @Override
    public String getHeader(String name) {
        String key = name.toLowerCase();
        if (customHeaders.containsKey(key)) {
            return customHeaders.get(key); // returns null if explicitly removed
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String key = name.toLowerCase();
        if (customHeaders.containsKey(key)) {
            String value = customHeaders.get(key);
            if (value == null) return Collections.emptyEnumeration(); // removed
            return Collections.enumeration(List.of(value));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>();

        // Original headers (excluding removed ones)
        Enumeration<String> originalNames = super.getHeaderNames();
        while (originalNames.hasMoreElements()) {
            String name = originalNames.nextElement().toLowerCase();
            if (!customHeaders.containsKey(name) || customHeaders.get(name) != null) {
                names.add(name);
            }
        }

        // Add custom headers (excluding removed ones)
        customHeaders.forEach((k, v) -> {
            if (v != null) names.add(k);
        });

        return Collections.enumeration(names);
    }
}