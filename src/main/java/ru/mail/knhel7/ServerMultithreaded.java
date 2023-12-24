package ru.mail.knhel7;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class ServerMultithreaded extends Server {
    protected int port;
    protected final int threads;

    public ServerMultithreaded(int nThreads) {
        this.threads = nThreads;
    }

    @Override
    public void start(int nPort) {
        this.port = nPort;

        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.print("CONNECT...");
                final var socket = serverSocket.accept();
                Executors.newFixedThreadPool(threads).submit(() -> connect(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getThreads() {
        return threads;
    }

}
