import java.net.*;
import java.io.*;


public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


        String resp;
        while(true){
            resp = in.readLine();
            System.out.println(resp);
        }
    }
 

 
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("start client");
        Client client = new Client();
        client.startConnection("127.0.0.1", 8080);
        System.out.println("client finished");
        client.stopConnection();
    }
}