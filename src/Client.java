import java.net.*;
import java.io.*;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.Arrays;


public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean listener_run = true;



    private String[] eParse(String str, int flag){
        //System.out.println(str);
        if(flag == 0)
            return str.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        else
            return str.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
    }

    private void Visual(){
        int n = 0;
        int m = 0;
        String table = "";
        String sqP = "";
        try{
            n = Integer.parseInt(in.readLine());
            m = Integer.parseInt(in.readLine());
            table = in.readLine();
            sqP= in.readLine();
        } catch (IOException e) {
            System.out.println(e);
        }
        String[] t_arr= eParse(table, 1);
        String[] sq_arr = eParse(sqP, 0);

        


        int sq[] = new int[n * m];
        for(int i = 0; i < n * m; ++i) {
            sq[i] = Integer.parseInt(sq_arr[i]);
        }

        
        char matrix[][] = new char[n + n - 1][m + m - 1];
        for(int i = 0; i < n + n - 1; ++i){
            for(int j = 0; j < m + m - 1; ++j){
                if(i % 2 == 0 && j % 2 == 0)
                    matrix[i][j] = '*';
                else
                    matrix[i][j] = ' ';
            }

        }
        
        
        for(int i = 0; i < t_arr.length && t_arr[i] != ""; ++i){
            String cor_str[] = t_arr[i].split(" ");
            //System.out.println(t_arr[i]);
            

            int k = 0; 
            if(cor_str[0].equals(""))
                k = 1;
            int cor[] = {Integer.parseInt(cor_str[k].replaceAll("\\s", "")), Integer.parseInt(cor_str[k + 1].replaceAll("\\s", ""))};
            if(cor[1] - cor[0] == 1){
                matrix[2 * (cor[0]/m)][2 * (cor[0] % m) + 1] = '-';
            } else{
                matrix[2 * (cor[0]/m) + 1][2 * (cor[0] % m)] = '|';
            }
            
        } 

        for(int i = 0; i < n + n - 1; ++i){
            for(int j = 0; j < m + m - 1; ++j){
                System.out.print(matrix[i][j]);
            }
            System.out.print("\n");
        }     
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        Scanner inTest = new Scanner(System.in);
        String resp;
        while (listener_run) {
            try {
                resp = in.readLine();

                if (resp.equals("fine")) {
                    continue;
                } else if (resp.equals("input")) {
                    System.out.println("Your turn");
                    String message;

                    message = inTest.next();

                    out.println(message);
                } else if (resp.equals("table")) {
                    Visual();
                } else if (resp.equals("quit")) {
                    listener_run = false;
                } else {
                    System.out.println(resp);
                }

            } catch (IOException e) {
                System.out.println(e);
            }
        }

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