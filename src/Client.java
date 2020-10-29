import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean listener_run = true;

    

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        Runnable task = () -> {
            String resp;
            while(listener_run){
                try{
                    resp = in.readLine();
                    //System.out.println(resp);
                    if(resp.equals("fine"))
                        System.out.println(resp);
                } catch (IOException e){
                    System.out.println(e);
                }
            }
            Thread.currentThread().interrupt();
        };

        Thread thread = new Thread(task);
        thread.start();


        int a;
        String resp = in.readLine();
        System.out.println(resp);
        Scanner inTest = new Scanner(System.in);
        a = inTest.nextInt();
        inTest.close();
    }
 

 
    public void stopConnection() throws IOException {
        listener_run = false;
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