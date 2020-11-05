import java.net.*;
import java.util.ArrayList;
import java.io.*;




public class Server {
    private ServerSocket serverSocket;
    private boolean isGoing = true;
    private int turn = 0;
    private int num_cl = 0;
    private String log = "";
    private ArrayList<String> game;
    private ArrayList<String> border;
    private ArrayList<Integer> border_point;


    private  int n = 3;
    private  int m = 3;

    public Server(){
        game = new ArrayList<String>();
        border = new ArrayList<String>();
        border_point = new ArrayList<Integer>();
        
        for(int i = 0; i < n - 1; ++i){
            border.add(i + " " + (i + 1));
            border.add(n * (m - 1) + i + " " + (n * (m - 1) + i + 1));
            border_point.add(i);
            border_point.add(n * (m - 1) + i);
        }
        border_point.add(n - 1);
        border_point.add(n * m  - 1);


        for(int i = 0; i < m - 1; ++i){
            border.add((i * n) + " " + (i * n + n));
            border.add((i * n + n - 1) + " " + (i * n + 2 *n -1));
            if(i != 0 && i != m - 1){
                border_point.add(i * n);
                border_point.add(i * n + n - 1);
            }
        }
        System.out.println(border_point);

    }
 
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (isGoing)
            new EchoClientHandler(serverSocket.accept()).start();
    }
 
    public void stop() throws IOException {
        serverSocket.close();
    }
 
    private class EchoClientHandler extends Thread  {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int local_num = -1;
        private boolean report = true;
        private int win_point = 0;
        private boolean isWinning = false;
 
        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
 
        private int Validate(int first, int second){
            if(first < 0 || second < 0)
                return 2;

            int min = Integer.min(first, second);
            int max = Integer.max(first, second);   

            if(min + 1 != max && min + n != max)
                return 2;

            if(border_point.indexOf(min) != -1 && border_point.indexOf(max) != -1)
                if(border.indexOf(min + " " + max) == -1)
                    return 2;
            
            if(game.indexOf(min + " " + max) != -1)
                return 1;

            return 0;
        }

        private boolean isSquare(int first, int second){
            boolean placment = (first + 1 == second ? true : false);

            if(placment == true)
            {
                if(first - n >= 0 && second - n >= 0){
                    if(game.indexOf((first - n) + " " + first) != -1 && 
                    game.indexOf((second - n) + " " + second) != -1 &&
                    game.indexOf((first - n) + " " + (second - n)) != -1){
                        System.out.println("H-");
                        return true;
                        
                    }
                }

                if(first + n < n * m && second + n < n * m){
                    if(game.indexOf(first + " " + (first + n)) != -1 && 
                    game.indexOf(second + " " + (second + n)) != -1 &&
                    game.indexOf((first + n) + " " + (second + n)) != -1){
                        System.out.println("H+");
                        return true;
                    }
                }

            } else {
                if(first % n != 0){
                    if(game.indexOf((first - 1) + " " + first) != -1 && 
                    game.indexOf((second - 1)+ " " + second) != -1 &&
                    game.indexOf((first - 1) + " " + (second - 1)) != -1){
                        System.out.println("V-");
                        return true;
                    }
                }

                if((first + 1) % n != 0){
                    System.out.println("V+ test");
                    System.out.println(first + " " + (first + 1));
                    System.out.println(second + " " + (second + 1));
                    System.out.println((first + 1) + " " + (second + 1));
                    if(game.indexOf(first + " " + (first + 1)) != -1 && 
                    game.indexOf(second + " " + (second + 1)) != -1 &&
                    game.indexOf((first + 1) + " " + (second + 1)) != -1){
                        System.out.println("V+");
                        return true;
                    }
                }
            }

            
            return false;
        }


        private void ProcedTurn(){
            if(log != "" && report == true){
                out.println(log);
                log = "";
            }
            int isInvalid = 0;
            String resp = "";

            int input_count = 0;

            int[] input_i = {-1, -1};

            //Parsing
            while (isInvalid == 1 || isInvalid == 2 || input_count < 2){
                out.println("input");
                try{
                    resp = in.readLine();
                } catch (IOException e){
                    System.out.println(e);
                }
                System.out.println(resp);
                
                try{
                    input_i[input_count] = Integer.parseInt(resp);
                } catch (NumberFormatException e){
                    input_i[input_count] = -1;
                }
                input_count++;
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
            }

            int min = Integer.min(input_i[0], input_i[1]);
            int max = Integer.max(input_i[0], input_i[1]);
            game.add(min + " " + max);
            System.out.println(game);

            log += min + " " + max + "\n";

            if(isSquare(min, max) == true){
                log +=  "player " + local_num + " got 1 point\n";
                out.println("You got a point");
                report = false;
                win_point++;

                if(win_point > (n - 1)*(m - 1) / 2)
                {
                    isWinning = true;
                    isGoing = false;
                }
            }else{
                turn = (turn + 1)%2;
                report = true;
            }

        }

        //  Handler main thread
        public void run() {
            
                local_num = num_cl;
                num_cl++;
                System.out.println(local_num);
            try{
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            } catch( IOException e){
                System.out.println(e);
            }
            while (isGoing) {
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException er){
                        System.out.println(er);
                }
                if(local_num == turn){
                    ProcedTurn();
                } else
                    out.println("fine");
            }

            if(isWinning == true){
                out.println("you win");
                out.println("quit");
            } else {
                out.println("you lost");
                out.println("quit");
            }

            try{
                in.close();
                out.close();
                clientSocket.close();
            } catch( IOException err){
                System.out.println(err);
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
