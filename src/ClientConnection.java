import java.io.*;
import java.net.*;
public class ClientConnection implements Runnable
{

    private String name;
    private byte[] buffer;
    
    private DatagramSocket socket;
    private DatagramPacket packet;
    private BufferedReader r = null;
    private InetAddress address;
    private int cp;
    String received = "0";
    StringBuffer message = new StringBuffer();
    String ClientName = null;
    private static int seqno = 0;
    private int r_count=0;//to keep tab of any retransmission
    
    public ClientConnection(DatagramSocket clientSocket, DatagramPacket packet, String name, InetAddress address, int cp) throws IOException
    {
        this.socket = clientSocket;
        socket.setSoTimeout(5000);
        this.packet = packet;
        this.name = name;
        this.address =  address;
        this.cp = cp;
        
    }

    public void run()
    {   
    	while(true){
        try
        {   
        	if(received == null){
            try{
            	buffer = new byte[128];
            	packet = new DatagramPacket(buffer, buffer.length);
            	socket.receive(packet);
            }catch(SocketTimeoutException e){
            	continue;
            }
            }
        	received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(received);
            ClientName = received.split("/")[1];
            System.out.println(ClientName);
        	byte[] buffer = new byte[128];
        	System.out.println(address.toString());
            //System.out.println(socket.getLocalSocketAddress().toString());
            //System.out.println(Thread.currentThread().getState());
            //System.out.println(packet.getPort());
            //System.out.println(cp);
			if(received.contains("Inform and Update") || received.charAt(1)=='0'){
				System.out.println("inside if block");
				if(r_count == 1){
					message.delete(0, message.length());
					message.insert(0,received.charAt(0)+":");
					buffer=message.toString().getBytes();
					packet = new DatagramPacket(buffer, buffer.length, address,cp);
					socket.send(packet);
					continue;
				}
				r_count=1;
				informAndUpdate();
			}
			else if(received.contains("Query for Content")){
				queryForContent(packet.getAddress());
		
			}
			else if(received.contains("Exit")){
				exit();
				break;
			}
			
			System.out.println(received);
			
			received = null;
			
		}catch(SocketTimeoutException e){
		    System.out.println("Message not received. Send again");
		}
		catch(IOException e){
			e.printStackTrace();
		}
    }
		
	}
	

public synchronized void informAndUpdate(){
	String data = null;
	int j =1;
	PrintWriter out1 = null;
	char old = received.charAt(0);
	char n = '0';
	System.out.println("INF");
	
	while(true){
	try{
		
		data = received;
		if(received == null){
		     socket.receive(packet);
			 data= new String(packet.getData(), 0, packet.getLength());
			 n = data.charAt(0);
		     j =0;
		    
		if(old == n){
			message.delete(0, message.length());
			message.append("200/Ok/\r\n");
			message.insert(0,data.charAt(0)+":");
			buffer = message.toString().getBytes();
			packet = new DatagramPacket(buffer, buffer.length, address,cp);
			socket.send(packet);
			continue;//ignore the current fragment and wait for the next
		}
		old = n;
		}
		FileWriter file = new FileWriter("list_server.txt", true);
		out1 = new PrintWriter(file);
		byte[] buffer = new byte[2048];
		packet = new DatagramPacket(buffer, buffer.length);
		String[] content = received.split("\r\n");
		int count = j;
		while(j < content.length){
		    System.out.println("inside");
			out1.println(content[j]+"/"+ClientName);
		    System.out.println(content[1]);
		    j++;
		}
		//out1.flush();
		message.delete(0, message.length());
		
		if(j == count){
			message.append("400/Unable to service the request!/\r\n");
		}else{
			message.append("200/Ok/\r\n");
		}
		System.out.println("outside while");
		message.insert(0,data.charAt(0)+":");
		buffer = message.toString().getBytes();
		packet = new DatagramPacket(buffer, buffer.length, address,cp);
		socket.send(packet);
		
		if(received.length()<=128 || data.charAt(1)== '0'){
			break;
		}
			received = null;
	    }catch(FileNotFoundException e){
			System.err.println("Cannot open the file- list_client.txt");
		}catch(SocketTimeoutException e){
		    System.out.println("Done ");
		    out1.close();
		}catch(Throwable e){
			e.printStackTrace();
		} finally {
			System.out.println("Inside finally");
			
			out1.close();
		}
	}
}

public void queryForContent(InetAddress address){
	
	int off =0;//fragmentation offset
	int flag = 0;//fragmentation flag
	
	String sp = null;

	while(true){
	try{
	r = new BufferedReader(new FileReader("list_server.txt"));
	byte[] buffer = new byte[128];
	packet = new DatagramPacket(buffer, buffer.length);
	String filename = received.split("\r\n")[1];
	System.out.println(filename);
	String Line = r.readLine();
	message.delete(0, message.length());
	message.append("200/Ok/\r\n");
	buffer = message.toString().getBytes();
	while(Line != null){
		if(Line.contains(filename)|| filename.equals("all")){
			message.append(Line+"\r\n");
		}
		Line = r.readLine();
	}
	message.append("\r\n");
	if(message.toString().split("\r\n").length ==1){
		message.delete(0,message.length());
		message.append("400/Unable to service the request!/\r\n");
	}
	message.insert(0,received.charAt(0)+":");
	
	sp = message.toString();
	if(sp.length()>128){
		flag =1;
		message.insert(off,seqno);
		message.insert(off+1,flag);
		buffer=message.substring(off, off+128).getBytes();
		packet = new DatagramPacket(buffer, buffer.length, address,cp);
		socket.send(packet);
		off= off +128;
		sp = message.toString();
		sp = sp.substring(off, sp.length());
        
	}else if(sp.length()<128 && flag == 1){
		flag=0;
		message.insert(off,seqno);
		message.insert(off+1,flag);
		sp = message.toString();
		buffer=sp.getBytes();
		packet = new DatagramPacket(buffer, buffer.length, address,cp);
		socket.send(packet);
		
	}else{
	buffer=sp.getBytes();
	packet = new DatagramPacket(buffer, buffer.length, address,cp);
	socket.send(packet);
    
	}
	r.close();
	if(flag == 0){
		break;
	}

	
	}catch(SocketTimeoutException e){
	    System.out.println("Done query");
	    //out1.close();
	}catch(FileNotFoundException e){
		System.err.println("Cannot open the file.");
	}
	catch(IOException e){
		e.printStackTrace();
	}
  }
	
}
public void exit(){
	try{
	File inputFile = new File("list_server.txt");
	File tempFile = new File("myTempFile.txt");

	BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true));
    
	String currentLine;

	while((currentLine = reader.readLine()) != null) {
		if(currentLine.contains(received.split("/")[1])){
			continue;
		}
		else{
			writer.append(currentLine+"\n");
		}
	}
	writer.close(); 
	reader.close(); 
	
	boolean successful = inputFile.delete();
	System.out.println("Delete successful"+successful);
	successful = tempFile.renameTo(inputFile);
	System.out.println(ClientName+"Exit"+successful);
	message.delete(0, message.length());
	if(successful){
	message.append("200/Exit Successful/\r\n");
	}else{
    message.append("400/Unable to service the request!/\r\n");
	}
	message.insert(0,received.charAt(0)+":");
	buffer=message.toString().getBytes();
	packet = new DatagramPacket(buffer, buffer.length, address,cp);
	socket.send(packet);	
	
}catch(IOException e){
	e.printStackTrace();
}
}

}
    
        