import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

//Server implements Runnable to allow for multithreading
public class Server implements Runnable{

    //Creation of Socket and Streams to allow for
    //Connection and communication between client and server
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    //ArrayList holds number of tokens which have been
    //Submitted by the clients
    //AtomicInteger keeps track of each client's number
    static final ArrayList<String> tokens = new ArrayList<String>();
    static AtomicInteger clientNum = new AtomicInteger(0);

    //Constructor with Socket object
    public Server(Socket socket){
        this.socket = socket;
    }

    //Main method which handles IOException
    public static void main(String[] args) throws IOException {

        //ServerSocket object is created to bridge the connection
        //Between the sockets of the client and server
        ServerSocket server = new ServerSocket(7777);

        System.out.println("Server: waiting for a client to connect");

        //While loop which handles thread creation before
        //Allowing the threads to operate as programmed
        while (true){

            //The server accepts the connection from the client
            //Output highlights the connection acceptance
            Socket client = server.accept();
            System.out.println("Server: client " + clientNum.incrementAndGet() + " connected");

            //Thread is created for the new client connection
            //Thread is started which begins the run method
            Server ser = new Server(client);

            Thread thread = new Thread(ser);

            thread.start();
        }
    }

    //Run method which commences upon thread.start
    public void run() {

        //Synchronised block to make sure threads don't access
        //the same blocks of code at the same time
        synchronized (socket) {

            //Two try blocks which will test for errors within the code
            try {
                try {


                    //Creation of Scanner and PrintWriter objects to allow
                    //For communication between server and client
                    in = new Scanner(socket.getInputStream());

                    out = new PrintWriter(socket.getOutputStream());

                    //Calling the doService method
                    doService();

                    //When the doService method finishes (client quits) the finally block
                    //Is activated confirming client disconnected and closing the socket
                } finally {
                    System.out.println("Server: client " + clientNum + " disconnected");
                    socket.close();
                }

                //IOException catching the errors within the first try
                //Block of the run method
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    //doService method which throws IOException
    public void doService() throws IOException {

        //While loop to check if there are more incoming
        //Messages from the client
        while (true){
            if (!in.hasNext())
                return;

            //Assigning the incoming messages to a String
            String request = in.next();

            //Applying functionality to the specific requests
            //Through if else statements
            //If the client requests a submission
            if(request.startsWith("SUBMIT")){

                String added = in.next();

                //Output confirmation of client request
                System.out.println("Server: received message from client " + clientNum + " '" + request + " " + added + "'");

                //If ArrayList is full, cancel request and confirm error
                if (tokens.size() >= 10) {
                    System.out.println("ERROR - maximum number of tokens reached");
                    out.println("ERROR - maximum number of tokens reached");
                }

                //If the submission is already added, cancel request and confirm error
                else if(tokens.contains(added)){
                    System.out.println("ERROR - already contains " + added);
                    out.println("ERROR - already contains " + added);
                }

                //Else add the submission and output the response to the server
                //Console and output the response to the client
                else {
                    tokens.add(added);
                    out.println("OK");
                    System.out.println("Server: sent response 'OK' to client " + clientNum);
                }

                //Make sure the output to the client is sent immediately
                out.flush();
            }

            //If the client requests a retrieval
            else if(request.equals("RETRIEVE")){

                //Output request to the console
                System.out.println("Server: received message from client " + clientNum + " '" + request + "'");
                out.flush();

                //If the list is empty, i.e. nothing to retrieve
                //Send an error
                if (tokens.isEmpty()){
                    out.println("ERROR");
                }

                //Else send the sorted array to the client
                //To display the array with white space a string builder is
                //Used where the contents of the array are assigned to the
                //String builder in their order
                else {
                    Collections.sort(tokens);
                    StringBuilder tokensToString = new StringBuilder(" ");
                    for (String token : tokens) {
                        tokensToString.append(token).append(" ");
                    }
                    out.println(tokensToString);

                    System.out.println("Server: sent response '" + tokensToString + "'");
                }
            }

            //If quit is requested close the socket
            else if(request.equals("QUIT")){
                socket.close();
            }

            //If the command is incorrect confirm this to console
            //And client
            else {
                out.println("SUBMIT RETRIEVE QUIT");
                System.out.println("INCORRECT COMMAND");
            }

            //Make sure output to client is done immediately
            out.flush();
        }
    }
}