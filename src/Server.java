import java.net.*;
import java.io.*;


public class Server {
    private ServerSocket serverSocket;
    private static boolean isGoing = true;
 
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept()).start();
    }
 
    public void stop() throws IOException {
        serverSocket.close();
    }
 
    private static class EchoClientHandler extends Thread  {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
 
        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
 
        public void run()  {
            try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
            
            String inputLine;
            while (isGoing) {
                try{
                Thread.sleep(2000);
                } catch (InterruptedException e){
                    System.out.println(e);
                }
                out.println("fine");
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch( IOException e){
            System.out.println(e);
        } 
    }
}

    public static void main(String[] args) throws IOException {
        System.out.println("start");
        Server server=new Server();
        server.start(8080);
        server.stop();
    }
}