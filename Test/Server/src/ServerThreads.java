import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ServerThreads extends Thread {
    private Socket client;
    private final char SIGN_X = 'x';
    private final char SIGN_O = 'o';
    private final char SIGN_EMPTY = '.';
    private char[][] table;
    private Random random;
    private Scanner scanner;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ServerThreads(Socket client) throws IOException {
         this.client = client;
         random = new Random();
         scanner = new Scanner(System.in);
         table = new char[3][3];
         output = new ObjectOutputStream(client.getOutputStream());
         input= new ObjectInputStream(client.getInputStream());
    }

    @Override
    public void run() {
        try {
            initTable();

            while (!client.isClosed()) {
                turnHuman();
                if (checkWin(SIGN_X)) {
                    output.writeObject("YOU WIN!");
                    output.flush();
                    break;
                }
                if (isTableFull()) {
                    output.writeObject("Sorry, DRAW!");
                    output.flush();
                    break;
                }
                output.writeObject("OK");
                output.flush();
                turnAI();
                sendTable();

                if (checkWin(SIGN_O)) {
                    output.writeObject("YOU LOSE!");
                    output.flush();
                    break;
                }
                if (isTableFull()) {
                    output.writeObject("Sorry, DRAW!");
                    output.flush();
                    break;
                }

                output.writeObject("OK");
                output.flush();
                output.reset();
            }

            input.close();
            output.close();
            client.close();
        } catch (EOFException e) {
            System.out.println("Client disconnected");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void sendTable() throws IOException {
        output.writeObject(table);
        output.flush();
    }

    void initTable() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                table[i][j] = SIGN_EMPTY;
    }

    void turnHuman() throws IOException, ClassNotFoundException {
        boolean ok = true;
        int[] move;
        do {
            move = (int[])input.readObject();
            if (move[0] >= 0 && move[0] <= 2 && move[1] >= 0 && move[1] <= 2) {
                ok = table[move[1]][move[0]] != SIGN_O && table[move[1]][move[0]] != SIGN_X;
            } else ok = false;

            if (!ok) {
                output.writeObject("Not valid point");
                output.flush();
            }
            output.reset();
        } while (!ok);
        table[move[1]][move[0]] = SIGN_X;
    }

//    boolean isCellValid(int x, int y, ObjectOutputStream output) throws IOException {
//        if (x < 0 || y < 0 || x > 2 || y > 2) {
//            output.writeUTF("Not valid point");
//            return false;
//        }
//        return table[y][x] == SIGN_EMPTY;
//    }

    void turnAI() {
        int[] move = new int[2];
        boolean ok = true;

        do {
            move[0] = random.nextInt(3);
            move[1] = random.nextInt(3);

            ok = table[move[1]][move[0]] != SIGN_O && table[move[1]][move[0]] != SIGN_X;
        } while (!ok);

        table[move[1]][move[0]] = SIGN_O;
    }

    boolean checkWin(char dot) {
        for (int i = 0; i < 3; i++)
            if ((table[i][0] == dot && table[i][1] == dot &&
                    table[i][2] == dot) ||
                    (table[0][i] == dot && table[1][i] == dot &&
                            table[2][i] == dot))
                return true;
        if ((table[0][0] == dot && table[1][1] == dot &&
                table[2][2] == dot) ||
                (table[2][0] == dot && table[1][1] == dot &&
                        table[0][2] == dot))
            return true;
        return false;
    }

    boolean isTableFull() {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (table[row][col] == SIGN_EMPTY)
                    return false;
        return true;
    }
}