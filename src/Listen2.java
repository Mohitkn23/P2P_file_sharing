import java.io.*;
import java.net.*;
public class Listen2 implements Runnable{
private boolean running = true;
	public void run(){
		while(running){
		try{
	ServerSocket SSocket = new ServerSocket(6786,10);
	SSocket.setSoTimeout(5000);
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
