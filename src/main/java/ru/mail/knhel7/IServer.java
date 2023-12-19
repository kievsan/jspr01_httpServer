package ru.mail.knhel7;


import java.net.Socket;
import java.util.List;

public interface IServer {
    void start(int port, int threads);
    void connect(Socket socket);
    List<String> validPathList();
}
