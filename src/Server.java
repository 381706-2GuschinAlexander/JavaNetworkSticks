import java.net.*;
import java.io.*;


public class Server {
    private ServerSocket serverSocket;
    private static boolean isGoing = true;
    private static int turn = 0;
    private static int num_cl = 0;
 
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
        private int local_num = -1;

 
        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
 

        //  Handler main thread
        public void run()  {
            try{
                local_num = num_cl;
                num_cl++;
                System.out.println(local_num);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                while (isGoing) {
                    try{
                        Thread.sleep(2000);
                    } catch (InterruptedException e){
                        System.out.println(e);
                    }


                    if(local_num == turn){
                        out.println("input");
                        System.out.println(local_num + " " + turn);
                        String resp = "";
                        resp = in.readLine();
                        if(resp.equals("in")){
                            /*
                                TODO if turn not result
                            */
                            System.out.println("turn");
                        }
                        turn = (turn + 1)%2;
                    } else
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