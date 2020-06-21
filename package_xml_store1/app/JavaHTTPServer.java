import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.lang.StringBuilder;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.io.StringReader;

// The tutorial can be found just here on the SSaurel's Blog :
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class JavaHTTPServer implements Runnable{

    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    // port to listen connection
    static final int PORT = 8080;

    // verbose mode
    static final boolean verbose = true;

    // Client Connection via Socket Class
    private Socket connect;

    public JavaHTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // we listen until user halts server execution
            while (true) {
                JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());

                if (verbose) {
                    System.out.println("Connecton opened. (" + new Date() + ")");
                }

                // create dedicated thread to manage the client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        // we manage our particular client connection
        BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
        String fileRequested = null;
        int[] stocks = {0, 516, 519, 651, 320, 296, 278, 249, 231, 160, 137, 180};

        try {
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(connect.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            // get first line of the request from the client
            String input;
            if (in.ready()){
                input = in.readLine();
                // we parse the request with a string tokenizer
                StringTokenizer parse = new StringTokenizer(input);
                String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
                // we get file requested
                fileRequested = parse.nextToken().toLowerCase();

                // we support only POST and HEAD methods, we check
                if (!method.equals("POST")  &&  !method.equals("HEAD") && !method.equals("OPTIONS") ) {
                    if (verbose) {
                        System.out.println("501 Not Implemented : " + method + " method.");
                    }

                    // we return the not supported file to the cli1235ent
                    String response = "501, method not implemented";
                    int conten_length = (int) response.length();
                    String content = "text/plain";

                    // we send HTTP Headers with data to client
                    out.println("HTTP/1.1 501 Not Implemented");
                    out.println("Server: Java HTTP Server");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    out.println("Content-length: " + conten_length);
                    out.println("Access-Control-Allow-Headers: " + "Content-type");
                    out.println("Access-Control-Allow-Origin: " + "*");
                    out.println(); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer
                    // file
                    out.println(response);
                    dataOut.flush();

                } else {
                    // POST or OPTIONS method
                    if (!fileRequested.endsWith("/stocks")) {
                        // RESPOND WITH 404
                        String response = "404, invalid URL";
                        int conten_length = (int) response.length();
                        String content = "text/plain";

                        out.println("HTTP/1.1 404 Not Found");
                        out.println("Server: Java HTTP Server");
                        out.println("Date: " + new Date());
                        out.println("Content-type: " + content);
                        out.println("Content-length: " + conten_length);
                        out.println("Access-Control-Allow-Headers: " + "Content-type");
                        out.println("Access-Control-Allow-Origin: " + "*");
                        out.println(); // blank line between headers and content, very important !
                        out.flush(); // flush character output stream buffer


                        out.println(response);
                        out.flush();

                        if (verbose) {
                            System.out.println("404 Not Found; endpoint: " + fileRequested + "." );
                        }

                    } else{
                        if (method.equals("OPTIONS")){ //OPTIONS method used to enforce CORS policy

                            // send HTTP Headers
                            out.println("HTTP/1.1 200 OK");
                            out.println("Server: Java HTTP Server");
                            out.println("Date: " + new Date());
                            out.println("Content-length: " + 0);
                            out.println("Access-Control-Allow-Headers: " + "Content-type");
                            out.println("Access-Control-Allow-Origin: " + "*");
                            out.println(); // blank line between headers and content, very important !
                            out.flush(); // flush character output stream buffer

                            if (verbose) {
                                System.out.println("200 Success; endpoint: " + fileRequested + "; method: OPTIONS." );
                            }
                        } else {    //POST /stocks
                            //read input
                            //read headers
                            int body_length = 0;
                            while((input = in.readLine()) != null && !input.equals("") ){
                                if (input != null && !input.equals("")){
                                    parse = new StringTokenizer(input);
                                    if (parse.nextToken().equals("Content-Length:")){
                                        body_length = Integer.parseInt(parse.nextToken());
                                    }
                                }
                            }

                            //read body
                            StringBuilder request_body = new StringBuilder(128);
                            int readed_body = 0;
                            while (readed_body < body_length) {
                                readed_body++;
                                request_body.append((char) in.read());
                            }
                            String xml = request_body.substring(0);

                            //parse xml
                            try {
                                SAXParserFactory factory = SAXParserFactory.newInstance();
                                SAXParser saxParser = factory.newSAXParser();
                                BuggyParser handler = new BuggyParser();

                                saxParser.parse(new InputSource(new StringReader(xml)), handler);
                                System.out.println("Parsed: ");
                                System.out.println(handler.getParsed());
                                String parsed = handler.getParsed();
                                //check if parsed is a number
                                try{
                                    int productId;
                                    productId = Integer.parseInt(parsed);
                                    //check if productId exists
                                    if (productId < 1 || productId >= stocks.length) {
                                        //TODO send 404 invalid productID
                                        String response = "404, no product with specified productId";
                                        int conten_length = (int) response.length();
                                        String content = "text/plain";

                                        out.println("HTTP/1.1 404 Not Found");
                                        out.println("Server: Java HTTP Server");
                                        out.println("Date: " + new Date());
                                        out.println("Content-type: " + content);
                                        out.println("Content-length: " + conten_length);
                                        out.println("Access-Control-Allow-Headers: " + "Content-type");
                                        out.println("Access-Control-Allow-Origin: " + "*");
                                        out.println(); // blank line between headers and content, very important !
                                        out.flush(); // flush character output stream buffer
                                        out.println(response);
                                        out.flush();

                                        if (verbose) {
                                            System.out.println("404 Not Found; endpoit: " + fileRequested + ", productId: " + productId + ".");
                                        }
                                    } else {
                                        //send 200, stocks[productId]
                                        String response = String.valueOf(stocks[productId]);
                                        int conten_length = (int) response.length();
                                        String content = "text/plain";
                                        // send HTTP Headers
                                        out.println("HTTP/1.1 200 OK");
                                        out.println("Server: Java HTTP Server");
                                        out.println("Date: " + new Date());
                                        out.println("Content-type: " + content);
                                        out.println("Content-length: " + conten_length);
                                        out.println("Access-Control-Allow-Headers: " + "Content-type");
                                        out.println("Access-Control-Allow-Origin: " + "*");
                                        out.println(); // blank line between headers and content, very important !
                                        out.flush(); // flush character output stream buffer
                                        out.println(response);
                                        out.flush();

                                        if (verbose) {
                                            System.out.println("200 Success; endpoint: " + fileRequested + "; method: POST, productId: " + productId + ".");
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    //TODO send 400 invalid productID
                                    String response = "Invalid <productID> content: " + parsed;
                                    int conten_length = (int) response.length();
                                    String content = "text/plain";
                                    // send HTTP Headers
                                    out.println("HTTP/1.1 400 Bad Request");
                                    out.println("Server: Java HTTP Server");
                                    out.println("Date: " + new Date());
                                    out.println("Content-type: " + content);
                                    out.println("Content-length: " + conten_length);
                                    out.println("Access-Control-Allow-Headers: " + "Content-type");
                                    out.println("Access-Control-Allow-Origin: " + "*");
                                    out.println(); // blank line between headers and content, very important !
                                    out.flush(); // flush character output stream buffer
                                    out.println(response);
                                    out.flush();

                                    if (verbose) {
                                        System.out.println("400 Bad Request: invalid productId format; endpoint: " + fileRequested + "; method: POST; productId: " + parsed + ".");
                                    }
                                }
                            } catch (SAXException e) {
                                //TODO send 400 invalid xml format
                                String response = "Invalid xml format";
                                int conten_length = (int) response.length();
                                String content = "text/plain";
                                // send HTTP Headers
                                out.println("HTTP/1.1 400 Bad Request");
                                out.println("Server: Java HTTP Server");
                                out.println("Date: " + new Date());
                                out.println("Content-type: " + content);
                                out.println("Content-length: " + response);
                                out.println("Access-Control-Allow-Headers: " + "Content-type");
                                out.println("Access-Control-Allow-Origin: " + "*");
                                out.println(); // blank line between headers and content, very important !
                                out.flush(); // flush character output stream buffer
                                out.println(response);
                                out.flush();
                                if (verbose) {
                                    System.out.println("400 Bad Request: invalid xml format; endpoint: " + fileRequested + "; method: POST.");
                                }
                                System.out.println(e.getMessage());
                                System.out.println(e.toString());
                                e.printStackTrace();
                            } catch (Exception e) {
                                // send HTTP Headers
                                out.println("HTTP/1.1 500 Internal Server Error");
                                out.println("Server: Java HTTP Server");
                                out.println("Date: " + new Date());
                                out.println("Content-length: " + 0);
                                out.println("Access-Control-Allow-Headers: " + "Content-type");
                                out.println("Access-Control-Allow-Origin: " + "*");
                                out.println(); // blank line between headers and content, very important !
                                out.flush(); // flush character output stream buffer
                                if (verbose) {
                                        System.out.println("500 Internal Server Error; endpoint: " + fileRequested + "; method: POST.");
                                }
                                System.out.println(e.getMessage());
                                System.out.println(e.toString());
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }

        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
            ioe.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }

            if (verbose) {
                System.out.println("Connection closed.\n");
            }
        }
    }

}

