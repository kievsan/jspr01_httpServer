package ru.mail.knhel7;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Server implements IServer {

    protected final IHandlerPool handlers = Handlers.getHandlerPool(
            validMethodList(), validPathList());

    @Override
    public void connect(Socket socket) {
        try (final var in = socket.getInputStream();
             final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            System.out.println(socket.getLocalPort() + " port.");  //++++++++++++++++++
            final Request request = new RequestInString(validMethodList(), validPathList());
            request.parse(in);
            System.out.println("Пришедший запрос: " + request); //+++++++++++++++++++

            handlers.get(request.method(), request.path())
                    .ifPresent(doIt -> {
                        System.out.println(request.method() + "  " + request.path());
                        doIt.handle(request, out);
                    });   // do it, handle it!

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CopyOnWriteArrayList<String> validPathList() {
        return new CopyOnWriteArrayList<String>(List.of(
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
        ));
    }

    @Override
    public CopyOnWriteArrayList<String>  validMethodList() {
        return new CopyOnWriteArrayList<String>(List.of("GET", "POST"));
    }
}
