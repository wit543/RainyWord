import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WIT-PC on 11/11/2558.
 */
public class ServerP2P {
    private Map<String,ClientThead> clientList = new HashMap<String,ClientThead>();
    private int portNumber = 12345;
    private String hostName = "";
    private static ServerP2P serverP2P;
    private ServerSocket serverSocket;
    private Thread clientHandler;

    private class ClientHandler extends Thread{
        private ServerSocket serverSocket;
        ClientHandler(ServerSocket serverSocket){
            this.serverSocket = serverSocket;
        }
        public void run() {
            while (true) {
                try{
                    Socket socket = serverSocket.accept();

                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientThead extends Thread{
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String input;
        private String output;
        private String name;
        ClientThead(Socket socket){
            this.socket=socket;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientList.put(String.valueOf(socket.getLocalPort()),this);
        }
        private String processInput(String s){
            String toClient = "";
            if(s.startsWith("[name]")){
                this.name = s.substring(5);
            }
            return toClient;
        }
        public void run(){
            try {
                while ((input = in.readLine()) != null) {
                    output = processInput(input);
                    out.println(output);
                    if (output.equals("<end>"))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public String getClientName(){
            if(name==null){
                output="[get]name";
            }
            return name;
        }
    }
    private ServerP2P(){
        try {
            serverSocket = new ServerSocket( portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientHandler = new ClientHandler(serverSocket);
        clientHandler.start();
    }
    public static ServerP2P getInstance(){
        if(serverP2P==null){
            serverP2P = new ServerP2P();
        }
        return serverP2P;
    }
}
