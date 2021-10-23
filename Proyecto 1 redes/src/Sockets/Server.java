package Sockets;



import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
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

    public void iniciar(String dir) throws IOException {

        String path = dir;
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
                        .uri(new URI(path))
                        //.header("Host", "info.cern.ch") - : restricted header name: "Connection"
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0")
                        .header("Accept"," text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,/;q=0.8")
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

    public void direccionar(String dir) throws IOException{

        String path = dir;

        //crea servidor
        server = new ServerSocket(this.PUERTO);

        try{
            while (true) {
                socket = new Socket("192.168.20.23",8080);
                socket = server.accept();
                InputStream input = socket.getInputStream();

                output = new DataOutputStream(socket.getOutputStream());

                URL url = new URL(path);
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

    public String mapearRespuesta(HttpURLConnection httpURLConnection) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append(httpURLConnection.getResponseCode())
                .append(" ")
                .append(httpURLConnection.getResponseMessage())
                .append("\n");

        Map<String, List<String>> map = httpURLConnection.getHeaderFields();

        for (Map.Entry<String, List<String>> entry : map.entrySet())
        {
            if (entry.getKey() == null)
                continue;
            builder.append( entry.getKey())
                    .append(": ");

            List<String> headerValues = entry.getValue();

            Iterator<String> it = headerValues.iterator();
            if (it.hasNext()) {
                builder.append(it.next());

                while (it.hasNext()) {
                    builder.append(", ")
                            .append(it.next());
                }
            }

            builder.append("\n");
        }
        return builder.toString();
    }

}

