package Sockets;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
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

                HttpClient client = HttpClient.newHttpClient();
                String headerRequest = leerSolicitud(input);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://info.cern.ch/"))
                        //.header("Host", "info.cern.ch") - : restricted header name: "Connection"
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0")
                        .header("Accept"," text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                        .header("Accept-Encoding", "gzip, deflate")
                        //.header("Connection", "keep-alive") - : restricted header name: "Connection"
                        .header("Upgrade-Insecure-Requests", "1")
                        .GET()
                        .build();

                System.out.println(request.headers().toString());

                



                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF("adios");
                socket.close();
            }

        } catch (IOException | URISyntaxException e) {
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

