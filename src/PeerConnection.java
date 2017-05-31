import java.net.*;
import java.io.*;


public class PeerConnection implements Runnable
{
   //private ServerSocket serverSocket;
   private Socket p2p = null;
   
   public PeerConnection(Socket p2p) throws IOException
   {
	  this.p2p = p2p;
   }

   public void run()
   {
         int statusCode = 0;
         String statusMessage = null;
         String response = null;
         int flag = 1;
         try
         {   
        	 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(p2p.getInputStream()));
        	 String clientSentence=inFromClient.readLine();
             String[] request = clientSentence.split(" ");
             OutputStream os = p2p.getOutputStream();
             File myFile = new File(request[1]);
             if(!myFile.exists()){
            	 statusCode = 404;
            	 statusMessage = "Not Found";
            	 flag =0;
             }
             if(request.length != 3 || !(request[0].equals("GET"))){
            	 statusCode = 400;
            	 statusMessage = "Bad Request";
            	 flag =0;
             }
             else if(!request[2].contains("HTTP/1.1")){
            	 statusCode = 505;
            	 statusMessage = "HTTP Version Not Supported";
            	 flag = 0; 
             }
             
             response = "/" + statusCode + "/" + statusMessage + "/\r\n";
             if(flag == 0){
            	 os.write(response.getBytes());
            	 os.flush();
            	 p2p.close();
            	 return; 
             }
             else{
             statusCode = 200;
             statusMessage = "OK";
             response = "/" + statusCode + "/" + statusMessage + "/\r\n";
             byte [] mybytearray  = new byte [(int)myFile.length()];
             FileInputStream fis = new FileInputStream(myFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             bis.read(mybytearray,0,mybytearray.length);
             os = p2p.getOutputStream();
             os.write(response.getBytes());
             os.write(mybytearray,0,mybytearray.length);
             System.out.println("Sending...");
             os.flush();
             bis.close();
             p2p.close();
             }
          }catch(IOException e){
        	 e.printStackTrace();
         }
      }
   
}
