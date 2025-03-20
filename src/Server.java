import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        ServerSocket serverSocket;
        Socket socket;
        Thread resposta;
        Thread escolta;

        //Inicialitza
        try {
            serverSocket = new ServerSocket(PORT);
            socket = serverSocket.accept();
            //Inicialitzem els Threads quan arriba el client per a millor gestió de recursos
            resposta = new Thread(new Resposta(socket), "Thread Resposta");
            escolta = new Thread(new Escolta(socket), "Thread Escolta");
            resposta.start();
            escolta.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Escolta implements Runnable {
        Socket socket;

        public Escolta(Socket socket)  {
            this.socket = socket;
        }

        @Override
        public void run() {
            String cadenaRebuda = "";
            String name = Thread.currentThread().getName();
            System.out.println(name);

            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                while (!cadenaRebuda.equals("FI")) {
                    cadenaRebuda = dis.readUTF();
                    System.out.println(cadenaRebuda);
                }

                dis.close();

            } catch (IOException io) {
                System.out.println("Ha hagut un problema inicialitzant el servidor:\n\n" +
                        io.getMessage());
            }
        }
    }

    public static class Resposta implements Runnable {
        Socket socket;
        String missatge;

        public Resposta(Socket socket)  {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                missatge = "";

                while (!missatge.equals("FI")) {
                    missatge = br.readLine();
                    dos.writeUTF(missatge);
                }

                dos.close();
                br.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}