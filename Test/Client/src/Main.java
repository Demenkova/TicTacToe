import org.w3c.dom.ls.LSOutput;

import javax.crypto.spec.PSource;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 3345);
            Scanner sc = new Scanner(System.in);
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());

            System.out.println("Client connected to server.\n");

            while (!client.isClosed()) {
                int[] move = new int[2];
                char[][] table;
                System.out.println("Enter X and Y (1..3):");
                String[] moveStr;
                String answer;

                do {
                    moveStr = sc.nextLine().split("[, ]+");
                    move[0] = Integer.parseInt(moveStr[0]) - 1;
                    move[1] = Integer.parseInt(moveStr[1]) - 1;

                    output.writeObject(move);
                    output.flush();
                    answer = (String)input.readObject();
                    if(!(answer.equals("OK") || answer.equals("YOU WIN!") || answer.equals("Sorry, DRAW!")))
                        System.out.println(answer);
                    output.reset();
                } while(!(answer.equals("OK") || answer.equals("YOU WIN!") || answer.equals("Sorry, DRAW!")));

                if(answer.equals("YOU WIN!") || answer.equals("Sorry, DRAW!")) {
                    System.out.println(answer);
                    break;
                }

                table = (char[][])input.readObject();
                showTable(table);

                answer = (String)input.readObject();
                if(answer.equals("YOU LOSE!") || answer.equals("Sorry, DRAW!")) {
                    System.out.println(answer);
                    break;
                }
            }

            input.close();
            output.close();
            client.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void showTable(char[][] table) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                System.out.print(table[i][j] + " ");
            System.out.println();
        }
    }
}