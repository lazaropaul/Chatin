import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private static final int PORT = 1234;
    private static final String HOST = "localhost";
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    private static Socket socket;

    //TODO: Lock not working, si es tanca el servidor de cop es segueix executant el codi

    public static void main(String[] args) {

        Thread resposta;
        Thread escolta;


        try {
            socket = new Socket(HOST, PORT);
            escolta = new Thread(new Escolta(), "Thread Escolta");
            resposta = new Thread(new Resposta(), "Thread Escolta");
            escolta.start();
            resposta.start();
        } catch (ConnectException connectException) {
            System.out.println("Servidor no disponible");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Escolta implements Runnable {

        public Escolta()  {
        }

        @Override
        public void run() {
            String cadenaRebuda = "";

            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                while (!lock.get()) {
                    cadenaRebuda = dis.readUTF();
                    if(!cadenaRebuda.isEmpty()){
                        System.out.println("Servidor: <<" + cadenaRebuda + ">>");
                        if(cadenaRebuda.equals("FI")){
                            lock.set(true);
                            dis.close();
                            socket.close();
                        }
                    }
                    //Maybe put a if finalizing the code
                    Thread.sleep(500);
                }

            } catch (IOException | InterruptedException io) {
                System.out.println("S'ha interromput la connexió amb el servidor");
                lock.set(true); //Evitem que es segueixi escoltant al servidor
            }
        }
    }

    public static class Resposta implements Runnable {
        String missatge;
        DataOutputStream dos;
        BufferedReader br;


        public Resposta() throws IOException {
            dos = new DataOutputStream(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));
            missatge = "";
        }

        @Override
        public void run() {
            try {
                while(!lock.get()){
                    if(br.ready()){
                        missatge = br.readLine();
                    }

                    if (!lock.get()){
                        dos.writeUTF(missatge);
                        if(missatge.equals("FI")){
                            lock.set(true);
                            br.close();
                            dos.close();
                            socket.close();
                        }
                        missatge = "";
                    }

                    Thread.sleep(500);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}

