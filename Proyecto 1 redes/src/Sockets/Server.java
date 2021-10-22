package Sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;


public class Server {
    ServerSocket server;
    Socket socket;
    private int puerto = 8080;
    DataOutputStream output;
    InputStream input;

    public void Server(){

    }

    public void iniciar(){
        try{
            //crea servidor
            server = new ServerSocket(this.puerto);
            //crea socket del cliente
            socket = new Socket();
            //el cliente hace una solicitud al servidor
            socket = server.accept();

            InputStream input = socket.getInputStream();

            leerSolicitud( input);
            output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("adios");
            socket.close();

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

