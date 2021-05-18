package Server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EchoThread extends Thread {
    private Socket socket;
    private ConcurrentHashMap<String, String> cityTemp; //это сохранение локальное в потоке, работающем с одним Клиентом и доступа к температуре, переданной другими не будет
    private static Logger log = Logger.getLogger(EchoThread.class.getName());

    public EchoThread(Socket socket, ConcurrentHashMap<String, String> cityTemp) {
        this.socket = socket;
        this.cityTemp = cityTemp;
    }

    public void run() {
        FileHandler fh = null;
        try {
            fh = new FileHandler("%tLogApp");
            log.addHandler(fh);

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String temp = bufferedReader.readLine();
                Pattern temperaturePattern = Pattern.compile("[0-9]");
                Matcher matcher = temperaturePattern.matcher(temp);
                if (matcher.find()) {
                    cityTemp.put(temp.split("\\+")[1], temp.split("\\+")[0]);
                } else {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.write(cityTemp.get(temp));
                    printWriter.flush();
                    printWriter.close();
                }
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println( " ошибка : " + e);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка", e);
        }
    }
}
