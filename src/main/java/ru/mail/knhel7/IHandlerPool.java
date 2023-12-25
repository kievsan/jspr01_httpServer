package ru.mail.knhel7;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public interface IHandlerPool {

    Map<String, Map<String, IHandler>> get();
    List<String> validMethodList();
    List<String> validPathList();

    default boolean add(String method, String path, IHandler handler) {
        return addHandler(method, path, handler, get());
    }

    default void del(String method, String path, IHandler handler) {
    }

    default Optional<IHandler> get(String method, String path) {
        Map<String, Map<String, IHandler>> handlersMap = getHandlers(method, path);
        if (handlersMap.isEmpty()) {
            return Optional.empty();
        }
        String _method = getHandlers(method, path).keySet().stream().findFirst().get();
        String _path = getHandlers(method, path).get(_method).keySet().stream().findFirst().get();
        System.out.println("Выбран обработчик " + _method + "  " + _path); //+++++++

        return Optional.of(handlersMap.get(_method).get(_path));
    }

    default List<String> pathList() {
        var paths = new ConcurrentHashMap<String, IHandler>().keySet();
        get().forEach((_method, mapa) -> paths.addAll(mapa.keySet()));
        return paths.stream().sorted().toList();
    }
    default List<String> methodList() {
        return get().keySet().stream().sorted().toList();
    }

    default boolean isValidMethod(String method) {
        return validMethodList().contains(method);
    }
    default boolean isValidPath(String path) {
        return validPathList().contains(path);
    }

    default String validateMethod(String method) {
        if (!method.isEmpty()) {
            method = method.toUpperCase();
            method = isValidMethod(method) ? method : "";
        }
        return method;
    }

    default String validatePath(String path) {
        path = path.toLowerCase();
        path = isValidPath(path) ? path : "";
        return path;
    }

    default Map<String, Map<String, IHandler>> getHandlers(String method, String path) {
        System.out.println("getting... Handler for " + method + "  " + path);

        final String method_ = validateMethod(method);
        final String path_ = validatePath(path);

        if (method.isEmpty() && path.isEmpty()) {
            return get();
        }

        Map<String, Map<String, IHandler>> targetHandlersMap = new ConcurrentHashMap<>();

        if (method_.isEmpty()) {
            get().forEach((_method, pathHandlersMap) -> {
                if (pathHandlersMap.containsKey(path_)) {
                    addHandler(_method, path_, pathHandlersMap.get(path_), targetHandlersMap);
                }
            });
            return targetHandlersMap;
        }
        if (path_.isEmpty()) {
            if (get().containsKey(method_)) {
                targetHandlersMap.put(method_, get().get(method_));
            }
            return targetHandlersMap;
        }
        if (get().containsKey(method_)) {
            if (get().get(method_).containsKey(path_)) {
                addHandler(method_, path_, get().get(method_).get(path_), targetHandlersMap);
            }
        }
        return targetHandlersMap;
    }

    default boolean addHandler(String method, String path, IHandler handler,
                              Map<String, Map<String, IHandler>> handlers) {
        if (!validPathList().contains(path) || !validMethodList().contains(method)) {
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

    default int count() {
        var counter = new AtomicInteger(0);
        for (Map.Entry<String, Map<String, IHandler>> entry : get().entrySet()) {
            counter.addAndGet(entry.getValue().entrySet().size());
        }
        return counter.intValue();
    }

    default void printHandlers() {
        System.out.println("Хэндлеры: " + count());
        for (Map.Entry<String, Map<String, IHandler>> entry : get().entrySet()) {
            System.out.print(entry.getKey() + ": ");
            for (Map.Entry<String, IHandler> innerEntry : entry.getValue().entrySet()) {
                System.out.print(innerEntry.getKey() + "; ");
            }
            System.out.println();
        }
    }
}
