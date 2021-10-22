package Sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpRequest;


public class Server {
    ServerSocket server;
    Socket socket;
    final private int PUERTO = 8080;
    DataOutputStream output;
    InputStream input;

    public void Server(){

    }

    public void iniciar() throws IOException {
        System.out.println("Iniciando servidor Proxy ");
        //crea servidor
        server = new ServerSocket(this.PUERTO);
        try{
            while (true) {



                //crea socket del cliente
                socket = new Socket();
                //el servidor espera solicitud
                socket = server.accept();

                InputStream input = socket.getInputStream();

                leerSolicitud(input);

                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF("adios");
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String leerSolicitud(InputStream inputStream) throws IOException {
        StringBuilder resultado = new StringBuilder();
        do {
            resultado.append((char) inputStream.read());
        } while (inputStream.available() > 0);
        System.out.println(resultado.toString());
        return resultado.toString();
    }
}

