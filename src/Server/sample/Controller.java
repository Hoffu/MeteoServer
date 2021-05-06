package Server.sample;

import javafx.event.ActionEvent;
import Server.model.EchoThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Controller {
    private ServerSocket server;
    private Thread thread;
    private static Logger log = Logger.getLogger(Controller.class.getName());
    private ConcurrentHashMap<String, String> cityTemp = new ConcurrentHashMap<>();

    public void initialize() {
        cityTemp.put("Москва", "4");
        cityTemp.put("Воронеж", "8");
        cityTemp.put("Омск", "-2");
    }

    public void startClicked(ActionEvent actionEvent) {
        thread = new Thread(() -> {
            try {
                FileHandler xmlFile = new FileHandler ("logServer.xml", true);
                log.addHandler (xmlFile);
                FileHandler txtFile = new FileHandler ("logServer.%u.%g.txt", true);
                SimpleFormatter txtFormatter = new SimpleFormatter ();
                txtFile.setFormatter (txtFormatter);
                log.addHandler (txtFile);
                server = new ServerSocket(3214);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Ошибка", e);
                e.printStackTrace();
            }
            while (true) {
                Socket socket = null;
                try {
                    socket = server.accept();
                    new EchoThread(socket, cityTemp).start();
                } catch (IOException e) {
                    System.out.println( " ошибка : " + e);
                }
            }
        });
        thread.start();
    }

    public void stopClicked(ActionEvent actionEvent) {
        try {
            FileHandler xmlFile = new FileHandler ("log.xml", true);
            log.addHandler (xmlFile);
            FileHandler txtFile = new FileHandler ("logServer.%u.%g.txt", true);
            SimpleFormatter txtFormatter = new SimpleFormatter ();
            txtFile.setFormatter (txtFormatter);
            log.addHandler (txtFile);
            server.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка", e);
            System.out.println( "ошибка сервера: " + e);
        }
    }
}
