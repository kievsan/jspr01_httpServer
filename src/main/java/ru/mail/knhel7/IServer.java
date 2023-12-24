package ru.mail.knhel7;


import java.net.Socket;
import java.util.List;
import java.util.Map;

public interface IServer {
    void start(int nPort);
    void connect(Socket socket);
    List<String> validPathList();
    List<String> validMethodList();
    public int getPort();
    public int getThreads();
}
