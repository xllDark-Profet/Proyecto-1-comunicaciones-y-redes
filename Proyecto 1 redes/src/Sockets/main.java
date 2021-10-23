package Sockets;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        String path = args[0];
        Server server = new Server();
        server.iniciar(path);
        server.direccionar(path);
    }
}
