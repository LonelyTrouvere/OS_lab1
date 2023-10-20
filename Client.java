import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException{
        System.out.println("Client started");
        Socket soc = new Socket("localhost", 8080);

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Give an EPS: ");
        String eps = input.readLine();
        
        PrintWriter out = new PrintWriter(soc.getOutputStream(), true );
        out.println(eps);

        BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        System.out.println(in.readLine());
    }
}
