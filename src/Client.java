import java.io.*;
import java.net.Socket;

public class Client {

    private static final int PORT = 1234;

    public static void main(String[] args) {
        String host = "localhost";
        Thread resposta;
        Thread escolta;


        try {
            Socket socket = new Socket(host, PORT);
            escolta = new Thread(new Escolta(socket), "Thread Escolta");
            resposta = new Thread(new Resposta(socket), "Thread Escolta");
            escolta.start();
            resposta.start();
            escolta.join();
            resposta.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Escolta implements Runnable{
        Socket socket;

        public Escolta(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            DataInputStream dis;
            String cadenaServidor = "";
            try {
                dis = new DataInputStream(socket.getInputStream());

                while(!cadenaServidor.equals("FI")){
                    cadenaServidor = dis.readUTF();
                    System.out.println(cadenaServidor);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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

