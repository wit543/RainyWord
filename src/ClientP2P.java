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
    /**
     * Name of the Client.
     */
    private String name="client";
    /**
     * Server IP
     */
    private String hostName = "127.0.0.1";
    /**
     * Server Port
     */
    private int port =12345;
    /**
     * Socket which The Client use to connect to server.
     */
    private Socket socket;
    /**
     * A Thread to handle a connection between server and client.
     */
    private ServerThread serveThread;

    /**
     * A Thread the manage the connection to the server.
     */
    private class ServerThread extends Thread{
        /**
         * A socket which the client use.
         */
        private Socket socket;
        /**
         * Use to send message to server.
         */
        private PrintWriter out;
        /**
         * Use to receive message from server.
         */
        private BufferedReader in;
        /**
         * The message from server.
         */
        private String input;
        /**
         * The message to server.
         */
        private String output;

        /**
         * Initialize socket, out, in.
         * @param socket Socket which Client use.
         */
        ServerThread(Socket socket){
            this.socket=socket;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * ProcessInput From server, handle all the message from server
         * [host] =
         * [connect] = get other client IP (use to connect to other client)
         * [list]  = a list of available client
         * @param s A message from the server
         * @return A message to output(currently not use)
         */
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

        /**
         * Change a String of list to ArrayList
         * @param s string that contain array
         * @return arrayList from the parameter
         */
        private ArrayList<String> stringToArrayList(String s){
            return new ArrayList<String>(Arrays.asList(s.substring(6).substring(s.indexOf("["), s.length() - 1).split(",")));
        }

        /**
         * for run the Thread
         */
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

        /**
         * sending String to server.
         * @param s String that want to be sent.
         */
        public void toServer(String s){
            out.println(s);
        }
    }

    /**
     * Constructor for initialize socket and Thread.
     */
    ClientP2P(){
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        serveThread = new ServerThread(socket);
    }

    /**
     * Set Client name.
     * @param name Name of the Client.
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Set server port
     * @param port Server port
     */
    public void setPort(int port){
        this.port = port;
    }

    /**
     * Start a thread to handle message from server.
     */
    public void start(){
        serveThread.start();
    }

    /**
     * Send message to Server (use for test only)
     * @param s String which wanted to be sent.
     */
    public void toServer(String s){
        serveThread.toServer(s);
    }

    /**
     * Main method
     * @param args input from console.
     */
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
