package com.iamservice.core.context;

public class LocalContextHolder {
    private static final ThreadLocal<RequestContext> CTX = new ThreadLocal<>();

    private LocalContextHolder() {}

    public static void set(RequestContext ctx) { CTX.set(ctx); }

    public static RequestContext get() { return CTX.get(); }

    public static void clear() { CTX.remove(); }

    // helpers
    public static String username() {
        var c = CTX.get(); return c != null ? c.getAccount().getUsername() : null;
    }
    public static String phoneNumber() {
        var c = CTX.get(); return c != null ? c.getAccount().getPhone() : null;
    }
    public static String email() {
        var c = CTX.get(); return c != null ? c.getAccount().getEmail() : null;
    }
    public static Integer userId() {
        var c = CTX.get(); return c != null ? c.getAccount().getId() : null;
    }
    public static String role() {
        var c = CTX.get(); return c != null ? c.getAccount().getRole() : null;
    }
}
