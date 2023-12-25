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
        final var threadPool = Executors.newFixedThreadPool(threads);

        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                var socket = serverSocket.accept();
                threadPool.submit(() -> connect(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
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
