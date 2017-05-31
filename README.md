# P2P_file_sharing
Created a Peer-to-Peer file sharing system that uses a centralised directory server. It consists of:
1. A brief survey summarizing existing P2P architectures for file sharing.
2. A design of P2P file sharing system, including its protocols.
3. A demonstration of your P2P system using at least three different hosts.

The code is developed using Java on Eclipse v4.4.0
jdk(Java Development Kit)v1.8.0_60
jre(Java Run Environment) 1.8.0_60

P2P File Sharing Application:
1. Create a workspace (asked first time when it opens after installation)in Eclipse where all the codes you write will be saved.

2. Create a project and load all the files into it.

3. Compile the entire project. Right click on the project folder shown at the left hand side and choose 'Build Project'.

4. Run ServerUDP.java

5. Run ClientUDP.java

6. A user interface will be shown. Give an input for any request to the SeverUDP.java(Three requests - Inform and Update, Query 
   for Content, Exit)

7. Input 1(Inform and Update) - will update the list of files you have in the file at Server.

8. Input 2(Query for Content)
   After that either enter as requested or enter 'all' if you wish to retrieve the entire list of files at the directory.

9. Input 4 will enable you to request a specific file from any of the hosts(list of hosts having that file displayed as a result 
   of the request:'Query for Content'). Enter the filename and hostname as asked and displayed in the console. If successful a new
   file can be seen created at the workspace which is the one you requested or otherwise an appropriate status code and status message
   will be sent by the host to which you have requested the file.

10. The same can be if two or more clients or peers are to be involved with each client running on a diffrent computer and the sever         running on another.(Multithreading has been implemented to avoid erroneous interaction when many clients are involved in the             communication)

11. If you wish to quit, issue an exit command(Input 3) which closes the client automatically and the server needs to be treminated         manually.
