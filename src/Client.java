import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        Thread resposta;

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        try {
            Socket socket = new Socket(host, port);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            resposta = new Thread(new Resposta(socket, dis), "Thread Escolta");
            resposta.start();
            resposta.join();

            String cadenaLlegida = "";

            while(!cadenaLlegida.equals("FI")){
                cadenaLlegida = br.readLine(); //Lectura del input de la consola
                dos.writeUTF(cadenaLlegida); //Ho enviem pel socket
                dos.flush();
                //System.out.println(dis.readUTF()); //Mostra la cadena retornada pel server
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static class Resposta implements Runnable{
        DataInputStream dis;
        Socket socket;

        public Resposta(Socket socket, DataInputStream dis) {
            this.dis = dis;
            this.socket = socket;
        }

        @Override
        public void run() {
            String cadenaServidor = "";
            try {
                while(!cadenaServidor.equals("FI")){
                    cadenaServidor = dis.readUTF();
                    System.out.println(cadenaServidor);
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

