import java.io.*;
import java.net.*;
//import java.util.*;
public class ServerUDP extends Thread{
private DatagramSocket s = null;
private BufferedReader in = null;
private DatagramPacket p = null;
InetAddress address= null;
private static int i =1;
private Thread t = null;

ServerUDP() throws IOException{
	this("ServerThread");
}

ServerUDP(String thread_name)throws IOException{
	super(thread_name);
	s = new DatagramSocket(4445);
}
public void run(){
	int portno = 0;
	while(true){
		try{
			
			String name= "Client"+i;
			byte[] buffer = new byte[2048];
			p = new DatagramPacket(buffer, buffer.length);
			s.setSoTimeout(5000);
			s.receive(p); 
		    
			InetAddress address = p.getAddress();
			int cp = p.getPort();
			System.out.println(cp);
			if(i==1 || !(cp == portno)){
				  DatagramSocket s1 = new DatagramSocket();
				  t = new Thread(new ClientConnection(s1,p,name,address,cp));
				  t.setName(name);
				  t.start();
				  i++;
				}
			System.out.println(i);
			portno = cp;
			System.out.println(t.getName());
			System.out.println(t.getState());			
			}
			catch(SocketTimeoutException e){
			    continue;
			}catch(IOException e){
				e.printStackTrace();
			}
}
}

public static void main(String[] args) throws IOException {
    ServerUDP DS = new ServerUDP();
    DS.start();
}
}


