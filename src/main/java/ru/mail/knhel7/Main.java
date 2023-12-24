package ru.mail.knhel7;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Main {
    public static final String DIR = "public";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static void main(String[] args) throws UnsupportedOperationException {
        Server server = new ServerMultithreaded(64);
        initHandlers(server);

        server.handlers.printHandlers();
        System.out.println("\nSTART server: " + server.getThreads() + " threads.");

        server.start(9999);

  }

    private static void initHandlers(Server server) {

        addGETHandler(server, "/index.html");
        addGETHandler(server, "/spring.png");

        System.out.println(server.handlers.add(GET, "/classic.html",
                (request, out) -> {
                    final var response = new Response();
                    try {
                        if (request.isBadRequest()) {
                            out.write(response.getHeaderNotFound().getBytes());
                        } else {
                            final var filePath = Path.of(".", DIR, request.path());
                            response.setType(Files.probeContentType(filePath));
                            response.setLength(Files.size(filePath));

                            final var template = Files.readString(filePath);
                            final var content = template.replace(
                                    "{time}", LocalDateTime.now().toString()
                            ).getBytes();
                            response.setLength(content.length);

                            out.write(response.getHeaderOk().getBytes());
                            out.write(content);
                        }
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ));
    }

    private static void addGETHandler(Server server, String path) {
        System.out.println(server.handlers.add(GET, path,
                (request, out) -> {
                    final var response = new Response();
                    try {
                        if (request.isBadRequest()) {
                            out.write(response.getHeaderNotFound().getBytes());
                        } else {
                            final var filePath = Path.of(".", DIR, request.path());
                            response.setType(Files.probeContentType(filePath));
                            response.setLength(Files.size(filePath));

                            out.write(response.getHeaderOk().getBytes());
                            Files.copy(filePath, out);
                        }
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ));
    }


}
