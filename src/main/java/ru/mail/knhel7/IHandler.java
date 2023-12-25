package ru.mail.knhel7;

import java.io.BufferedOutputStream;



@FunctionalInterface
public interface IHandler {
    default String err() {
        return "";
    };
    void handle(Request request, BufferedOutputStream out);
}
