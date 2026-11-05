package com.vitzemtsov.flowmanager.constants;

public final class SecurityPaths {

    private SecurityPaths() {
    }
    public static final String ACTUATOR_HEALTH = "/actuator/health";
    public static final String ACTUATOR_INFO = "/actuator/info";
    public static final String ACTUATOR_PROMETHEUS = "/actuator/prometheus";

    public static final String API_ADMIN_ALL = "/api/admin/**";
    public static final String API_ALL = "/api/**";
}