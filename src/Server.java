import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static final int PORT = 1234;
    private static final AtomicBoolean lock = new AtomicBoolean(false);

    //TODO: Llegir unicament un altre cop si el atomicBoolean el qual bloqueja la lectura es true https://medium.com/@rahul.tpointtech12/step-by-step-tutorial-on-java-atomicboolean-usage-8e958032b901
    //TODO: br.ready() per saber si la cadena esta buida
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
        Socket socket;
        String missatge;
        DataOutputStream dos;
        BufferedReader br;


        public Resposta(Socket socket) throws IOException {
            this.socket = socket;
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