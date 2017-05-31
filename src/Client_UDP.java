import java.io.*;
import java.net.*;
import java.util.*;
public class Client_UDP extends Thread {
private	DatagramSocket s = null;
private ServerSocket SSocket = null;
private	BufferedReader in = null;
private DatagramPacket p = null;
private static int counter = 1;
private static int seqno = 1;
private static Listen l =null;
private int port = 4445;
StringBuffer message = new StringBuffer();

public Client_UDP()throws IOException{
	s = new DatagramSocket();
	try{
		s.setSoTimeout(4000);
		in = new BufferedReader(new FileReader("list_client.txt"));
		
	}catch(FileNotFoundException e){
		System.err.println("Cannot open the file");
	}
  }

		
	
	
public void run(){
	    
		while(true){
		try{    
			    InetAddress addr = InetAddress.getByName("192.168.0.174");
                String hostname = addr.getHostName();
			    System.out.println("Choose a Method:");
				System.out.println("1.Inform and Update");
				System.out.println("2.Query For Content");
				System.out.println("3.Exit");
				System.out.println("4.Request for a file");
				//@SuppressWarnings("resource")
				Scanner i = new Scanner(System.in);
				int input = i.nextInt();
				System.out.println("You chose"+input);
				String request = null;
			    
			    request = i.nextLine();
			    
			    //i.close();
			    switch(input){
				case 1:{
					try{
						message.delete(0, message.length());
						message.append("Inform and Update/"+hostname+"/192.168.0.174/\r\n");
						String line = in.readLine();
						while(line != null){
							message.append(line+"\r\n");
							line = in.readLine();
							System.out.println(message);
						}
						message.append("\r\n");
						in.close();
						method();
					}catch(FileNotFoundException e){
						System.err.println("Cannot open the file");
					}break;
				}
				case 2:{
					 message.delete(0, message.length());
					 message.append("Query for Content/"+hostname+"/192.168.0.174/\r\n");
					 System.out.println("Enter filename as:filename.txt");
				     request = i.nextLine();
				     message.append(request+"\r\n");
				     System.out.println(message.toString());
				     message.append("\r\n");
					 method();
					 break;
				}
				case 3:{
					 message.delete(0, message.length());
				     message.append("Exit/"+hostname+"/192.168.0.174/\r\n");
				     method();
				     break;
				}
				case 4:{
					System.out.println("Enter filename and host as:filename.txt hostname");
			    	request = i.nextLine();
			    	//String filename = request.split(" ")[0];
			    	requestFile(request);
					break;}
			    }
		
			    if(input == 3){
			    	System.out.println("kill");
			    	in.close();
			    	s.close();
			    	l.kill();
			    	break;
			    }			
			    
	    }catch(IOException e){
			e.printStackTrace();
		}
		}
		
	}


public void requestFile(String request){
	Socket client = null;
	PrintWriter out1 = null;
	try{
	   String rq = "GET "+request.split(" ")[0]+" HTTP/1.1\r\n";
	   client = new Socket(InetAddress.getByName(request.split(" ")[1].split("/")[1]),6787);
	   System.out.println(client.getLocalPort());
	   OutputStream outToServer = client.getOutputStream();
	   outToServer.write(rq.getBytes());
	   
	   InputStream inFromServer = client.getInputStream();
	   BufferedReader in = new BufferedReader(new InputStreamReader(inFromServer));
	   File f = new File(request.split(" ")[0]);
	   String line = in.readLine();
	   System.out.println(line);
	   FileWriter file = new FileWriter(f, true);
	   out1 = new PrintWriter(file);
	   if(line.contains("200")){
	   line = in.readLine();
	   while(line != null){
	   	out1.println(line);
	   	line = in.readLine();
	   }
	   out1.close();
	   in.close();
	   client.close();
	   }
	   else{
		   out1.close();
		   in.close();
		   client.close(); 
	   }
	   }catch(IOException e)
	   {
	       e.printStackTrace();
	    }
}





public void method(){
	

	int off =0;//fragmentation offset
	int flag = 0;//fragmentation flag
	message.insert(0,seqno);
	String sp = null;
	sp=message.toString();
	if(message.length()>128){
		flag=1;
		message.insert(1,flag);
		sp = message.toString();
		sp = sp.substring(off,off+128);
	}
	while(true){
	try{
	byte[] buffer = new byte[128];
	InetAddress address = InetAddress.getByName("localhost");
	
	buffer = sp.getBytes();
	System.out.println(buffer.length);
	p = new DatagramPacket(buffer, buffer.length, address, port);
	s.send(p);
	buffer = new byte[128];
	p = new DatagramPacket(buffer, buffer.length);
	s.receive(p);
	seqno++;
	String received = new String(p.getData(), 0, p.getLength());
    System.out.println(received);
    port = p.getPort();
	if(flag == 0){
		break;
	}
	if(message.substring(off+128,message.length()).length()>128){
		off=off+128;
		message.insert(off,seqno);
		message.insert(off+1,flag);
		sp = message.toString();
		sp = sp.substring(off,off+128);
        
	}else if((message.substring(off+128,message.length()).length()<128 && flag ==1)){
		flag=0;
		off=off+128;
		message.insert(off,seqno);
		message.insert(off+1,flag);
		sp = message.toString();
		sp = sp.substring(off,sp.length());
		
	}
		//in.close();
	}catch(SocketTimeoutException e){
		System.out.println("Acknowledgement not received!Resending...");
	}catch(IOException e){
		e.printStackTrace();
	}
}
	
}

	
	public static void main(String[] args)throws IOException {
		try { 
		    (new Client_UDP()).start();
		    l=new Listen();
		    Thread t = new Thread(l);
		    t.start();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}