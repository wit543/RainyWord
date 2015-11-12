import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Server {
	private HomeServer homeFrame;
	private static int serverTime2;
	private static String serverName;
	private static String clientName;
	static ObjectOutputStream oos;
	static ObjectInputStream ois;
	static Object response = null;
	static String responseText = null;
	public Server() throws NumberFormatException, ScriptException, IOException{
		serverName = JOptionPane.showInputDialog("Please enter you name");
		homeFrame = HomeServer.createAndShowGUI();
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server();

		System.out.println("Rainy Word is running.");
		int clientNumber = 0;
		ServerSocket listener = new ServerSocket(10096);
		try {
			while (true) {
				System.out.println(clientNumber);
				new Capitalizer(listener.accept(), clientNumber++).start();

			}
		} finally {
			listener.close();
		}
		
	}
	private static class Capitalizer extends Thread {
		private Socket socket;
		private int clientNumber;

		public Capitalizer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with client# " + clientNumber + " at " + socket);
		}
		
		
		private void log(String message) {
			System.out.println(message);
		}
		public void run() {

			try {
				System.out.print("hello");
				java.io.OutputStream os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				InputStream is = socket.getInputStream();
				ois = new ObjectInputStream(is);
				oos.writeObject(serverName);
				clientName = ois.readObject().toString();
				GameServer.setServerName(serverName);
				GameServer.setClientName(clientName);
				while(true){
					response = ois.readObject();
					responseText = response.toString();
					System.out.println("server receive "+ responseText);
					try{
						LinkedListItr itr = (LinkedListItr) response;
						String s = itr.current.element.word;
						GameServer.wordList.remove(GameServer.wordList.find(s));
					}catch (Exception e){
					}
					if(response.equals("client ready")){
						GameServer.t1.start();
						GameServer.gameStarted = true;
						GameServer.inputField.setText("");
						GameServer.inputField.setEnabled(true);
						GameServer.inputField.requestFocus();
						GameServer.t2.start();
						GameServer.startTimer();
					}else if(response.equals("addClientScore")){
						GameServer.addClientScore();
					}
					
				}
				
			
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
}
