package ru.mail.knhel7;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public interface IHandlerPool {
    boolean add(String method, String path, IHandler handler);
    void del(String method, String path, IHandler handler);
    Optional<IHandler> get(String method, String path);
    Map<String, Map<String, IHandler>> get();

    List<String> pathList();
    List<String> methodList();
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
