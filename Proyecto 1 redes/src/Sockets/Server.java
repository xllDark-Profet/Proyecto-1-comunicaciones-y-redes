package Sockets;



import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Server {

    ServerSocket server;
    Socket socket;
    final private int PUERTO = 8080;
    OutputStream output;
    InputStream input;

    public void Server(){

    }

    public void iniciar() throws IOException {


        System.out.println("Iniciando servidor Proxy ");
        //crea servidor
        server = new ServerSocket(this.PUERTO);


        System.out.println("Inet address: "+socket.getInetAddress());
        System.out.println("Port number: "+socket.getLocalPort());
        try{
            while (true) {

                //crea socket del cliente
                socket = new Socket();
                //el servidor espera solicitud
                socket = server.accept();

                InputStream input = socket.getInputStream();

                String headerRequest = leerSolicitud(input);

                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void direccionar() throws IOException{

        server = new ServerSocket(this.PUERTO); //crea servidor
        socket = new Socket("192.168.0.3", this.PUERTO); //crea cliente

            while (true) {

                try {

                    socket = server.accept(); //servidor se conecta con el cliente

                    input = socket.getInputStream();

                    output = socket.getOutputStream();

                    if (input.available() > 0 && socket.isConnected()) {

                        String request = leerSolicitud(input);

                        if (request.contains("POST") | request.contains("GET")) {

                            String direccionUrl = request.split("\n")[0].split(" ", 3)[1].strip(); //obtiene direccion url

                            //System.out.println("direccion:" + direccionUrl);

                            URL url = new URL(direccionUrl);

                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            mapearHeadersSolicitud(request, con); //mapea headers para ejecutar la solicitud

                            con.connect(); // ejecuta solicitud

                            String body = leerBody(url);

                            String response = "HTTP/1.1 " + con.getResponseCode() + " " + con.getResponseMessage() + "\r\n";
                            response = response + mapearRespuesta(con) + "\r\n" + body; //costruye respuesta



                            System.out.println( "\n" + response + "\n");


                            if (!socket.isClosed()) {
                                output.write(response.getBytes());  // enviar respuesta
                                output.flush();
                            }

                            con.disconnect();
                            socket.close();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }

    public void mapearHeadersSolicitud(String request, HttpURLConnection con) throws ProtocolException {

        List<String> lineas = Arrays.asList(request.split("\n"));

        boolean primeraLinea = true;

        for (String linea : lineas){

            if(!linea.contains("HTTP") && linea.contains(":"))
            {
                String[] header = linea.split(":",2);
                System.out.println(header[0] + header[1]);
                con.addRequestProperty(header[0].strip(),header[1].strip());
            }
            else if (primeraLinea)
            {
                String[] primeralinea = linea.split(" ", 3);
                con.setRequestMethod(primeralinea[0].strip());
                primeraLinea = false;
            }
        }
    }



    public String mapearRespuesta(HttpURLConnection httpURLConnection) throws IOException {
        StringBuilder builder = new StringBuilder();

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

    public String leerBody(URL url) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));

        String inputLine, body = "";
        while ((inputLine = in.readLine()) != null)
            body = body + inputLine;
        in.close();

        return body;
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

