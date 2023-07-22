import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        //Socket creation, allowing connection to server
        Socket s = new Socket("localhost", 7777);

        //Creation of input and output streams for
        //Communication between client and server consoles
        InputStream instream = s.getInputStream();
        OutputStream outstream = s.getOutputStream();
        Scanner in = new Scanner(instream);
        PrintWriter out = new PrintWriter(outstream);
        Scanner sc = new Scanner(System.in);

        //Boolean to check while loop
        boolean quit = false;

        //Output for client connection
        System.out.println("Client: connected to server");

        //While loop to continually take input from
        //Server and respond
        while (!quit){

            String request = sc.nextLine();
            String other = request + "\n";

            //Outputting to client console through System.out
            //Outputting to server through out (PrintWriter)
            System.out.println("Client: sent '" + request + "' to server");
            out.print(other);
            out.flush();

            //in (Scanner) takes the server response and assigns
            //it to a String which is output to client console
            String response = in.nextLine();
            System.out.println("Client: received message from server '" + response + "'");

            //If client submits "QUIT" close all streams
            //and socket and end while loop
            if (request.equals("QUIT")){
                instream.close();
                outstream.close();
                s.close();
                quit = true;
            }
        }
    }
}
