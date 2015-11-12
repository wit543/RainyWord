import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WIT-PC on 11/11/2558.
 */
public class ServerP2P {
    private Map<String, ClientThread> clientList = new HashMap<String, ClientThread>();
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
                    ClientThread clientThread = new ClientThread(socket);
                    clientThread.start();
                    clientList.put(String.valueOf(socket.getLocalPort()),clientThread);

                    System.out.print("create Socket"+clientThread.toString());
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientThread extends Thread{
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String input;
        private String output;
        private String name;
        ClientThread(Socket socket){
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
            if(s.startsWith("[name]")){
                this.name = s.substring(5);
            }
            if(s.equals("[get]List")){
                out.print("[list]"+Arrays.toString(clientList.keySet().toArray()));
            }
            if(s.startsWith("[connect]")){
                ClientThread clientThread =clientList.get(s.substring(7));
                if(clientThread!=null){
                    out.print("[connect]["+clientThread.socket.getInetAddress()+"]");
                    clientThread.out.print("[connect]["+this.socket.getInetAddress()+"]");
                }
                else{
                    out.print("[error]not found");
                }
            }
            System.out.println("Client: "+s);

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
                out.println("[get]name");
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

    }
    public void start(){
        clientHandler.start();
    }
    public static ServerP2P getInstance(){
        if(serverP2P==null){
            serverP2P = new ServerP2P();
        }
        return serverP2P;
    }
    public static void main(String[] arga){
        ServerP2P serverP2P = new ServerP2P();
        serverP2P.start();
    }
}
