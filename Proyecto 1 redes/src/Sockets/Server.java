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

                            System.out.println("direccion:" + direccionUrl);

                            String host = " ";

                            List<String> sitioVirtual = verificarSitioVirtual(direccionUrl);
                            if(!sitioVirtual.isEmpty())
                                if(!direccionUrl.equals(sitioVirtual.get(0))) {
                                    direccionUrl = sitioVirtual.get(0);
                                    host = sitioVirtual.get(1);
                                }

                            URL url = new URL(direccionUrl);

                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            mapearHeadersSolicitud(request, con, host); //mapea headers para ejecutar la solicitud

                            con.connect(); // ejecuta solicitud

                            String body ="";

                            int status = con.getResponseCode();

                            if(status < 400)
                                body = leerBody(url);
                            else
                                input = con.getErrorStream();

                            String response = "HTTP/1.1 " + con.getResponseCode() + " " + con.getResponseMessage() + "\r\n";
                            response = response + mapearRespuesta(con) + "\r\n" + body; //costruye respuesta

                            System.out.println( "\n" + response + "\n");


                            if (!socket.isClosed()) {
                                output.write(response.getBytes());  // enviar respuesta
                                output.flush();
                            }

                            con.disconnect();
                        }
                        else if (request.contains("CONNECT"))
                        {
                            output.write(("HTTP/1.1 200 Connection Established").getBytes());
                            output.flush();
                      }
                        socket.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }

    public void mapearHeadersSolicitud(String request, HttpURLConnection con, String host) throws ProtocolException {

        List<String> lineas = Arrays.asList(request.split("\n"));

        boolean primeraLinea = true;

        for (String linea : lineas){

            if(!linea.contains("HTTP") && linea.contains(":"))
            {
                String[] header = linea.split(":",2);
                System.out.println(header[0] + header[1]);
                if(header[0].strip() == "Host" && host != " ")
                    con.addRequestProperty(header[0].strip(),host);
                else
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

    public List<String> verificarSitioVirtual(String url) throws IOException {
        File archivo = new File ("C:\\Users\\jamar\\IdeaProjects\\proxy\\src\\Sockets\\configuracion_hosts.txt");
        FileReader fr = new FileReader (archivo);
        BufferedReader br = new BufferedReader(fr);
        String linea;
        String host;
        List<String> respuesta = new ArrayList<String>();

        while((linea=br.readLine())!=null){

            String[] lineas = linea.split(",");

            if(url.contains(lineas[0]))
            {
                url = url.replace(lineas[0],(lineas[1].replace("-",lineas[2])));
                respuesta.add(url);

                host = lineas[3];
                respuesta.add(host);
            }

        }
        System.out.println("2-"+url);
        return respuesta;
    }

}

