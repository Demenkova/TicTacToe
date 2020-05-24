import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        List<ServerThreads> threads = new ArrayList<>();

        try {
            ServerSocket server = new ServerSocket(3345);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            int i = 0;
            while (!server.isClosed()) {
                Socket client = server.accept();
                threads.add(new ServerThreads(client));
                threads.get(i).start();

                System.out.println("Client " + i + " connected");
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}