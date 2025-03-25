import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;


public class ListenHandler implements Runnable {
    Socket socket;
    String cadenaRebuda;
    DataInputStream dis;
    AtomicBoolean lock;
    Boolean serverMode;

    public ListenHandler(Socket socket, AtomicBoolean lock, Boolean serverMode) throws IOException {
        this.socket = socket;
        this.cadenaRebuda = "";
        this.dis = new DataInputStream(socket.getInputStream());
        this.lock = lock;
        this.serverMode = serverMode;
    }

    @Override
    public void run() {
        try {
            while (!lock.get()) {
                cadenaRebuda = dis.readUTF();
                if (!cadenaRebuda.isEmpty()) {
                    if(serverMode){
                        System.out.println("Client: <<" + cadenaRebuda + ">>");
                    } else {
                        System.out.println("Server: <<" + cadenaRebuda + ">>");
                    }
                    if (cadenaRebuda.equals("FI")) {
                        lock.set(true);
                        dis.close();
                        socket.close();
                    }
                }
                Thread.sleep(100);
            }

        } catch (IOException | InterruptedException io) {
            if(serverMode){
                System.out.println("S'ha interromput la connexió amb el client");
            } else {
                System.out.println("S'ha interromput la connexió amb el servidor");
            }
            lock.set(true); //Evitem que se segueixi escoltant al client
        }
    }
}
