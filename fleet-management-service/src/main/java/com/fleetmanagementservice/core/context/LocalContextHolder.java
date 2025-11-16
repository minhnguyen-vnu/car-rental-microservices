package com.fleetmanagementservice.core.context;

public class LocalContextHolder {
    private static final ThreadLocal<RequestContext> CTX = new ThreadLocal<>();

    private LocalContextHolder() {}

    public static void set(RequestContext ctx) { CTX.set(ctx); }

    public static RequestContext get() { return CTX.get(); }

    public static void clear() { CTX.remove(); }
}
