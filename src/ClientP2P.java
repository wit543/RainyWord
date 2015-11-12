import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by WIT-PC on 12/11/2558.
 */
public class ClientP2P {
    private String name="client";
    private String hostName = "127.0.0.1";
    private int port =12345;
    private Socket socket;
    private ServerThread serveThread;
    private class ServerThread extends Thread{
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String input;
        private String output;
        ServerThread(Socket socket){
            this.socket=socket;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private String processInput(String s){
            String toClient = "";
            if(s.startsWith("[host]")){
//                this.host = s.substring(5);
            }
            else if(s.startsWith("[connect]")){
                int address = Integer.parseInt(s.substring(10),s.length()-1);

                // use address to connect to other client
            }
            else if(s.startsWith("[list]")){
                ArrayList arrayList = stringToArrayList(s);
            }
            System.out.println("server: "+s);
            return toClient;
        }
        private ArrayList<String> stringToArrayList(String s){
            new ArrayList<String>(Arrays.asList(s.substring(6).substring(s.indexOf("["), s.length() - 1).split(",")));
        }
        public void run(){
            try {
                while ((input = in.readLine()) != null) {
                    output = processInput(input);
                    if (output.equals("<end>"))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void toServer(String s){
            out.println(s);
        }
    }
    ClientP2P(){
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        serveThread = new ServerThread(socket);
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPort(int port){
        this.port = port;
    }
    public void start(){
        serveThread.start();
    }
    public void toServer(String s){
        serveThread.toServer(s);
    }
    public static void main(String[] args){
        ClientP2P clientP2P = new ClientP2P();
        clientP2P.start();
        Scanner scanner = new Scanner(System.in);

        while (true){
            String s = scanner.nextLine();
            System.out.println("client: "+s);
            clientP2P.toServer(s);
        }
    }
}
