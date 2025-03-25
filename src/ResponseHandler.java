import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResponseHandler implements Runnable {
    Socket socket;
    String missatge;
    DataOutputStream dos;
    BufferedReader br;
    AtomicBoolean lock;
    Boolean serverMode;


    public ResponseHandler(Socket socket, AtomicBoolean lock, Boolean serverMode) throws IOException {
        this.socket = socket;
        dos = new DataOutputStream(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(System.in));
        missatge = "";
        this.lock = lock;
        this.serverMode = serverMode;
    }

    @Override
    public void run() {
        try {
            if(serverMode) missatge = "Connexió acceptada";

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

                Thread.sleep(100);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}