package ru.mail.knhel7;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;


public abstract class Server implements IServer {

    protected final long startTime = System.currentTimeMillis();
    protected final IHandlerPool handlers = Handlers.get();

    @Override
    public void connect(Socket socket) {
        try (final var in = socket.getInputStream();
             final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            var startTask = System.currentTimeMillis();
            final Request request = new RequestInString(
                    handlers.validMethodList(),
                    handlers.validPathList());
            request.parse(in);
            var handler = handlers.get(request.method(), request.path());
            handler.ifPresent(doIt -> doIt.handle(request, out));   // do it, handle it!
            socket.close();
            //++++++++++++++++++
            printResult(socket, request, handler, startTask);
            //++++++++++++++++++
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void printResult(Socket socket, Request request,
                                          Optional<IHandler> handler,
                                          long startTask) {
        System.out.print("CONNECT..." + socket.getLocalPort() + " port.");
        System.out.println("\nПолучен запрос: " + request);
        System.out.print(handler.isPresent() ? "\nСработал " : "\nОтсутствует ");
        System.out.println("обработчик для " + request.method() + "  " + request.path());
        System.out.print("Завершено за ");
        System.out.println((System.currentTimeMillis() - startTask - 0.0) / 1000 + "  сек.");
        System.out.print("\n=======================================\n");
    }
}
