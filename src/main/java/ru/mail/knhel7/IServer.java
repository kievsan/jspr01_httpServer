package ru.mail.knhel7;


import java.net.Socket;
import java.util.List;
import java.util.Map;

public interface IServer {
    void start(int nPort);
    void connect(Socket socket);
    public int getPort();
    public int getThreads();
}
