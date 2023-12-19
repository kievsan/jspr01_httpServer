package ru.mail.knhel7;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;


public class Servers {

    private static List<String> validPaths = List.of(
            "/index.html",
            "/spring.svg",
            "/spring.png",
            "/resources.html",
            "/styles.css",
            "/app.js",
            "/links.html",
            "/forms.html",
            "/classic.html",
            "/events.html",
            "/events.js"
    );

    private Servers(){}

    public static IServer getServer() {

        return new IServer() {

            private int port;
            private int threads;

            @Override
            public List<String> validPathList() {
                return Servers.validPaths;
            }

            @Override
            public void start(int nPort, int nThreads) {
                this.port = nPort;
                this.threads = nThreads;

                try (final var serverSocket = new ServerSocket(port)) {
                    while (true) {
                        final var socket = serverSocket.accept();
                        Executors.newFixedThreadPool(threads).submit(() -> connect(socket));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connect(Socket socket) {
                try (socket;
                     final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     final var out = new BufferedOutputStream(socket.getOutputStream())
                ) {
                    final Response response = new Response();
                    final var requestLine = in.readLine();
                    final var parts = requestLine.split(" ");

                    if (parts.length != 3) {
                        return;
                    }

                    final var path = parts[1];
                    if (!this.validPathList().contains(path)) {
                        out.write(response.getHeaderNotFound().getBytes());
                        out.flush();
                        return;
                    }

                    final var filePath = Path.of(".", "public", path);
                    response.setType(Files.probeContentType(filePath));

                    // special case for classic
                    if (path.equals("/classic.html")) {
                        final var template = Files.readString(filePath);
                        final var content = template.replace(
                                "{time}",
                                LocalDateTime.now().toString()
                        ).getBytes();
                        response.setLength(content.length);
                        out.write(response.getHeaderOk().getBytes());
                        out.write(content);
                        out.flush();
                        return;
                    }

                    response.setLength(Files.size(filePath));
                    out.write(response.getHeaderOk().getBytes());
                    Files.copy(filePath, out);
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public int getPort() {
                return port;
            }

            public int getThreads() {
                return threads;
            }
        };
    }
}
