import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static final int PORT = 1234;
    private static final AtomicBoolean lock = new AtomicBoolean(false);

    //TODO: Finally per tancar els bufferedreaders i tal


    public static void main(String[] args) {
        ServerSocket serverSocket;
        Socket socket;
        Thread resposta;
        Thread escolta;

        //Inicialitza
        try {
            serverSocket = new ServerSocket(PORT);
            socket = serverSocket.accept();
            serverSocket.close(); //Tanquem el serversocket per evitar noves connexions
            System.out.println("Connexió acceptada");
            //Inicialitzem els Threads quan arriba el client per a millor gestió de recursos
            resposta = new Thread(new ResponseHandler(socket, lock, true), "Thread Resposta");
            escolta = new Thread(new ListenHandler(socket, lock, true), "Thread Escolta");
            resposta.start();
            escolta.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}