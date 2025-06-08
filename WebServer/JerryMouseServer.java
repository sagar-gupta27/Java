import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class JerryMouseServer {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("HTTP server listening on port 8080...");

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Connection established with client :" + clientSocket.getInetAddress());

                // Set read timeout of 5 seconds (5000 milliseconds)
                clientSocket.setSoTimeout(5000);

                try {
                    HttpRequest httpRequest = HttpRequestParser.parse(clientSocket);
                    System.out.println("Parsed HTTP request:");
                    System.out.println(httpRequest);

                    // Prepare response
                    HttpResponse response = new HttpResponse();
                    response.body = "Hello! You requested " + httpRequest.path;

                    // Send response
                    sendResponse(clientSocket, response);

                } catch (SocketTimeoutException e) {
                    System.err.println("Client read timed out: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("IO Exception while parsing request: " + e.getMessage());
                } finally {
                    try {
                        clientSocket.close();
                        System.out.println("Client socket closed");
                    } catch (IOException e) {
                        System.err.println("Error closing client socket: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void sendResponse(Socket clientSocket, HttpResponse response) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.print(response.toHttpString());
        writer.flush();
    }
}
