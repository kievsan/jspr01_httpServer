package ru.mail.knhel7;

import java.io.BufferedOutputStream;

@FunctionalInterface
public interface IHandler {
    void handle(Request request, BufferedOutputStream out);
}
