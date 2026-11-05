package com.vitzemtsov.flowmanager.constants;

public final class ApiPaths {

    private ApiPaths() {
    }

    public static final String API_V1 = "/api/v1";
    public static final String FILES = API_V1 + "/files";

    public static final String FILE_ID = "/{id}";
    public static final String FILE_STATUS = FILE_ID + "/status";
    public static final String FILE_CONTENT = FILE_ID + "/content";

    public static final String SUBSCRIPTIONS = API_V1 + "/subscriptions";
    public static final String SUBSCRIPTION_LOGIN = "/{login}";
}