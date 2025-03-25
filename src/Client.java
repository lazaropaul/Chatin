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
            resposta = new Thread(new ResponseHandler(socket, lock, false), "Thread Resposta");
            escolta = new Thread(new ListenHandler(socket, lock, false), "Thread Escolta");
            escolta.start();
            resposta.start();
        } catch (ConnectException connectException) {
            System.out.println("Servidor no disponible");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

