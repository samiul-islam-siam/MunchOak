package com.example.try2;

public class HelloAppHolder {
    private static HelloApplication appInstance;

    public static void setAppInstance(HelloApplication app) {
        appInstance = app;
    }

    public static HelloApplication getAppInstance() {
        return appInstance;
    }
}
