package ru.mail.knhel7;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Handlers {

    public static final List<String> validPaths = List.of(
            "/index.html",
            "/spring.svg",
            "/spring.png",
            "/resources.html",
            "/styles.css",
            "/app.js",
            "/links.html",
            "/forms.html",
            "/classic.html",
            "/events.html",
            "/events.js"
    );
    public static final List<String> validMethods = List.of("GET", "POST");

    private Handlers(){}

    public static IHandlerPool get() {
        return PoolHolder.HOLDER_INSTANCE;
    }

    private static class PoolHolder {
        public static final IHandlerPool HOLDER_INSTANCE = new IHandlerPool() {

            private List<String> validPaths =
                    new CopyOnWriteArrayList<>(Handlers.validPaths);
            private List<String> validMethods =
                    new CopyOnWriteArrayList<>(Handlers.validMethods);
            private final Map<String, Map<String, IHandler>> handlers =
                    new ConcurrentHashMap<>();

            @Override
            public Map<String, Map<String, IHandler>> get() {
                return handlers;
            }

            @Override
            public List<String> validPathList() {
                return validPaths;
            }

            @Override
            public List<String> validMethodList() {
                return validMethods;
            }
        };
    };
}
