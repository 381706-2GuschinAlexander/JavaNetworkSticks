import java.net.*;
import java.io.*;


public class Server {
    private ServerSocket serverSocket;
    private static boolean isGoing = true;
    private static int turn = 0;
    private static int num_cl = 0;
    private static String log = "";
 
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
 
        private int Validate(int first, int second){
            /*
                Section - if line is already drawn 
            */
            
            if(first == -1 || second == -1)
                return 2;
            return 0;
        }

        private void ProcedTurn(){
            
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
                        if(log != ""){
                            out.println(log);
                            log = "";
                        }
                        //System.out.println(local_num + " " + turn);
                        int isInvalid = 0;
                        String resp = "";

                        int input_count = 0;

                        int[] input_i = {-1, -1};
                        //Parsing
                        while (isInvalid == 1 || isInvalid == 2 || input_count < 2){
                            out.println("input");
                            resp = in.readLine();
                            System.out.println(resp);
                            
                            try{
                                input_i[input_count] = Integer.parseInt(resp);
                            } catch (NumberFormatException e){
                                input_i[input_count] = -1;
                            }
                            if(input_count == 2){
                                isInvalid = Validate(input_i[0], input_i[1]);
                                if (isInvalid == 2){
                                    out.println("can't parse");
                                    input_count = 0;
                                }
                                if (isInvalid == 1){
                                    out.println("line already drawn");
                                    input_count = 0;
                                }
                                
                            }
                            input_count++;
                        }

                        log += input_i[0] + " " + input_i[1];
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