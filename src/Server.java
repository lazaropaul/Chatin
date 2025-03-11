import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        int port = 1234;
        ServerSocket serverSocket;
        Socket socket;
        Thread t1;
        Thread t2;

        //Inicialitza
        try {
            serverSocket = new ServerSocket(PORT);
            socket = serverSocket.accept();
            //Inicialitzem els Threads quan arriba el client per a millor gestió de recursos
            t1 = new Thread(new ClientArrival(socket), "Thread 1");
            t2 = new Thread(new ClientArrival(socket), "Thread 2");
            t1.start();
            t2.start();
        } catch (IOException io){
            System.out.println("Ha hagut un problema inicialitzant el servidor:\n\n" +
                    io.getMessage());
        }
    }

    public static class ClientArrival implements Runnable {
        Socket socket;

        public ClientArrival(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String cadenaRebuda = "";
            String name = Thread.currentThread().getName();
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                while (!cadenaRebuda.equals("FI")) {
                    cadenaRebuda = dis.readUTF();
                    System.out.println(name + " " + cadenaRebuda);
                    dos.writeUTF(cadenaRebuda.toUpperCase());
                }

                dis.close();
                dos.close();
                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}