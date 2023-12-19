package ru.mail.knhel7;


public class Main {
  public static void main(String[] args) throws InterruptedException {
    IServer server = Servers.getServer();
    server.start(9999, 64);
  }
}
