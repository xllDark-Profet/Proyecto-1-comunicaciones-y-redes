package Sockets;



import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class Server {
    ServerSocket server;
    Socket socket;
    final private int PUERTO = 8080;
    DataOutputStream output;
    InputStream input;

    public void Server(){

    }

    public void iniciar() throws IOException {
     //   System.out.println("Iniciando servidor Proxy ");
        //crea servidor
        server = new ServerSocket(this.PUERTO);
        try{
            while (true) {

                //crea socket del cliente
                socket = new Socket("192.168.0.3",8080);



                //el servidor espera solicitud
                socket = server.accept();

            //    System.out.println(server.getLocalPort() + " : " + server.getInetAddress().toString());

                InputStream input = socket.getInputStream();

                output = new DataOutputStream(socket.getOutputStream());

      //        HttpClient client = HttpClient.newHttpClient();

      //        String headerRequest = leerSolicitud(input);




                URL url = new URL("http://info.cern.ch/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Upgrade-Insecure-Requests", "1");
                con.connect();
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));

                String inputLine, body = "";
                while ((inputLine = in.readLine()) != null)
                    body = body + inputLine;
                in.close();







                // Send response
                output.write("HTTP/1.0 200 OK\r\n".getBytes());
                output.write("Date: Sat, 23 Oct 2021 20:33:58 GMT\r\n".getBytes());
                output.write("Content-type: text/html\r\n".getBytes());
                output.write("\r\n".getBytes()); // End of headers
                output.write(body.getBytes());


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
   //   System.out.println(resultado.toString());
        return resultado.toString();
    }
}

