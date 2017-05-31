import java.io.*;
import java.net.*;
public class Listen implements Runnable{
private boolean running = true;
private ServerSocket SSocket = null;
Listen() throws IOException{
	SSocket = new ServerSocket(6787,10);
}
	public void run(){
		while(running){
		try{ 
	
	Socket p2p = SSocket.accept();
	System.out.println(p2p.getInetAddress().toString());
    Thread t = new Thread(new PeerConnection(p2p));
    t.start();
    }catch(IOException e){
    	//System.out.println("No peer connections");
    	continue;
    }
	}
	}
    
	public void kill(){
		running=false;
	}
   
}
