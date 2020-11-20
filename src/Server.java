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
    private ArrayList<String> square_point;

    private  int n = 0;
    private  int m = 0;

    public Server(int _n, int _m){
        n = _n;
        m = _m;
        game = new ArrayList<String>();
        border = new ArrayList<String>();
        border_point = new ArrayList<Integer>();
        square_point = new ArrayList<String>(n * m);

        for(int i = 0; i < n * m; ++i){
            square_point.add("-1");
        }

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
        System.out.println("cols: " + n + " rows: " + m);
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
            //Parse error
            if(first < 0 || second < 0)
                return 2;

            int min = Integer.min(first, second);
            int max = Integer.max(first, second);   

            //!(horizontal || vertical) detection
            if(min + 1 != max && min + n != max)
                return 2;

            //Cross border detection
            if(border_point.indexOf(min) != -1 && border_point.indexOf(max) != -1)
                if(border.indexOf(min + " " + max) == -1)
                    return 2;
            
            //Duplicate detection
            if(game.indexOf(min + " " + max) != -1)
                return 1;

            return 0;
        }

        private boolean isSquare(int first, int second){
            boolean placment = (first + 1 == second ? true : false);
            boolean isGotPoint = false;
            String res = "" + local_num;
            if(placment == true)
            {
                //horizontal detection
                if(first - n >= 0 && second - n >= 0){
                    if(game.indexOf((first - n) + " " + first) != -1 && 
                    game.indexOf((second - n) + " " + second) != -1 &&
                    game.indexOf((first - n) + " " + (second - n)) != -1){
                        square_point.set(first - n, res);
                        log += "   got 1 point\n";
                        out.println("You got a point");
                        win_point++;                        
                        isGotPoint = true;
                    }
                }

                if(first + n < n * m && second + n < n * m){
                    if(game.indexOf(first + " " + (first + n)) != -1 && 
                    game.indexOf(second + " " + (second + n)) != -1 &&
                    game.indexOf((first + n) + " " + (second + n)) != -1){
                        square_point.set(first, res);
                        log += "   got 1 point\n";
                        out.println("You got a point");
                        win_point++;                        
                        isGotPoint = true;
                    }
                }

            } else {
                //vertical detection
                if(first % n != 0){
                    if(game.indexOf((first - 1) + " " + first) != -1 && 
                    game.indexOf((second - 1)+ " " + second) != -1 &&
                    game.indexOf((first - 1) + " " + (second - 1)) != -1){
                        square_point.set(first - 1, res);
                        log += "   got 1 point\n";
                        out.println("You got a point");
                        win_point++;                        
                        isGotPoint = true;
                    }
                }

                if((first + 1) % n != 0){
                    if(game.indexOf(first + " " + (first + 1)) != -1 && 
                    game.indexOf(second + " " + (second + 1)) != -1 &&
                    game.indexOf((first + 1) + " " + (second + 1)) != -1){
                        square_point.set(first, res);
                        log += "   got 1 point\n";
                        out.println("You got a point");
                        win_point++;                        
                        isGotPoint = true;
                    }
                }
            }

            return isGotPoint;
        }

        private void ProcedTurn(){
            if(log != "" && report == true){
                out.println("Oponent done:");
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
                //System.out.println("Local num: " + local_num + " Response: " + resp);
                if(resp.equals("table")){
                    out.println("table");
                    out.println(n);
                    out.println(m);
                    out.println(game);
                    out.println(square_point);
                    out.println("You have "+ win_point + (win_point == 1 ? " point" : " points"));
                    continue;
                }
                try{
                    input_i[input_count] = Integer.parseInt(resp);
                } catch (NumberFormatException e){
                    input_i[input_count] = -1;
                }
                input_count++;
                if(input_count == 2){
                    isInvalid = Validate(input_i[0], input_i[1]);
                    if (isInvalid == 2){
                        out.println("Can't parse");
                        input_count = 0;
                    }
                    if (isInvalid == 1){
                        out.println("Line already drawn");
                        input_count = 0;
                    }
                }  
            }

            int min = Integer.min(input_i[0], input_i[1]);
            int max = Integer.max(input_i[0], input_i[1]);
            game.add(min + " " + max);
            //System.out.println(game);

            log += "   placed "+ min + "-" + max + "\n";

            if(isSquare(min, max) == true){
                //System.out.println(square_point);
                
                report = false;
                if(win_point > (n - 1)*(m - 1) / 2){
                    isWinning = true;
                    isGoing = false;
                }

                if(game.size() == (2 * n * m - m - n)){
                    System.out.println("Draw");
                    isGoing = false;
                }

            } else {
                turn = (turn + 1)%2;
                report = true;
            }

        }

        //  Handler main thread
        public void run() {
            
                local_num = num_cl;
                num_cl++;
                //System.out.println(local_num);
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
                }
            }

            if(isWinning == true){
                out.println("You win");
                out.println("quit");
            } else {
                out.println("You lost");
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
            System.out.println("Start Server");
            Server server;
            if(args.length == 2){
                try{
                    server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    server = new Server(4,4);
                }
            } else
                server = new Server(4,4);
            server.start(8080);
            server.stop();
    }
}
