import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private static final int PORT = 1234;
    private static final String HOST = "localhost";
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    private static Socket socket;

    public static void main(String[] args) {

        Thread resposta;
        Thread escolta;


        try {
            socket = new Socket(HOST, PORT);
            escolta = new Thread(new Escolta(), "Thread Escolta");
            resposta = new Thread(new Resposta(), "Thread Escolta");
            escolta.start();
            resposta.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Escolta implements Runnable {

        public Escolta()  {
        }

        @Override
        public void run() {
            String cadenaRebuda = "";
            String name = Thread.currentThread().getName();
            System.out.println(name);

            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                while (!lock.get()) {
                    cadenaRebuda = dis.readUTF();
                    if(!cadenaRebuda.isEmpty()){
                        System.out.println(cadenaRebuda);
                        if(cadenaRebuda.equals("FI")){
                            lock.set(true);
                            dis.close();
                            socket.close();
                        }
                    }
                    //Maybe put a if finalizing the code
                    Thread.sleep(500);
                }

            } catch (IOException io) {
                System.out.println("Ha hagut un problema inicialitzant el servidor:\n\n" +
                        io.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

