package ru.mail.knhel7;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Handlers {

    private Handlers(){}

    public static IHandlerPool getHandlerPool(List<String> validMethods, List<String> validPaths) {

        List<String> final_validMethods = validMethods.isEmpty() ? List.of("GET") : validMethods;
        List<String> final_validPaths = validPaths.isEmpty() ? List.of("/") : validPaths;

        return new IHandlerPool() {

            private final List<String> validPaths = final_validPaths.stream()
                    .map(String::trim).map(String::toLowerCase).toList();
            private final List<String> validMethods = final_validMethods.stream()
                    .map(String::trim).map(String::toUpperCase).toList();
            private final Map<String, Map<String, IHandler>> handlers = new ConcurrentHashMap<>();

            @Override
            public boolean add(String method, String path, IHandler handler) {
                return addHandler(method, path, handler, handlers);
            }

            @Override
            public void del(String method, String path, IHandler handler) {

            }

            @Override
            public Optional<IHandler> get(String method, String path) {
                Map<String, Map<String, IHandler>> handlersMap = handlersMap(method, path);
                if (handlersMap.isEmpty()) {
                    return Optional.empty();
                }
                String _method = String.valueOf(
                        handlersMap(method, path).keySet().stream().findFirst());
                String _path= String.valueOf(
                        handlersMap(method, path).get(_method).keySet().stream().findFirst());
                System.out.print("Выбран обработчик " + method + "  " + path); //+++++++

                return Optional.of(handlersMap.get(_method).get(_path));
            }

            @Override
            public Map<String, Map<String, IHandler>> get() {
                return handlers;
            }

            @Override
            public List<String> pathList() {
                var paths = new ConcurrentHashMap<String, IHandler>().keySet();
                handlers.forEach((_method, mapa) -> paths.addAll(mapa.keySet()));
                return paths.stream().sorted().toList();
            }

            @Override
            public List<String> methodList() {
                return handlers.keySet().stream().sorted().toList();
            }

            @Override
            public int count() {
                int counter = 0;
                for (Map.Entry<String, Map<String, IHandler>> entry : get().entrySet()) {
                    counter += entry.getValue().entrySet().size();
                }
                return counter;
            }

            public boolean addHandler(String method, String path, IHandler handler,
                                      Map<String, Map<String, IHandler>> handlers) {
                if (!validPaths.contains(path) || !validMethods.contains(method)) {
                    System.out.println("Unexpected http method or resource path");
                    return false;
                }
                Map<String, IHandler> pathMap = handlers.get(method);
                if (null == pathMap || pathMap.isEmpty()) {
                    pathMap = new ConcurrentHashMap<>();
                    pathMap.put(path, handler);
                    handlers.put(method, pathMap);
                } else {
                    pathMap.putIfAbsent(path, handler);
                }
                return true;
            }

            public Map<String, Map<String, IHandler>> handlersMap(String method, String path) {
                if (handlers.isEmpty()) {
                    return handlers;
                }
                final String method_ = validMethod(method);
                final String path_ = validPath(path);

                if (method.isEmpty() && path.isEmpty()) {
                    return handlers;
                }

                Map<String, Map<String, IHandler>> targetHandlersMap = new ConcurrentHashMap<>();

                if (method_.isEmpty()) {
                    handlers.forEach((_method, pathHandlersMap) -> {
                        if (pathHandlersMap.containsKey(path_)) {
                            addHandler(_method, path_, pathHandlersMap.get(path_), targetHandlersMap);
                        }
                    });
                    return targetHandlersMap;
                }
                if (path_.isEmpty()) {
                    if (handlers.containsKey(method_)) {
                        targetHandlersMap.put(method_, handlers.get(method_));
                    }
                    return targetHandlersMap;
                }
                if (handlers.containsKey(method_)) {
                    if (handlers.get(method_).containsKey(path_)) {
                        addHandler(method_, path_, handlers.get(method_).get(path_), targetHandlersMap);
                    }
                }

                return targetHandlersMap;
            }

            public boolean isValidMethod(String method) {
                return validMethods.contains(method);
            }

            public boolean isValidPath(String path) {
                return validPaths.contains(path);
            }

            public String validMethod(String method) {
                if (!method.isEmpty()) {
                    method = method.toUpperCase();
                    method = isValidMethod(method) ? method : "";
                }
                return method;
            }

            public String validPath(String path) {
                path = path.toLowerCase();
                path = isValidPath(path) ? path : "";
                return path;
            }
        };
    }
}
