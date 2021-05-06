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
    private ConcurrentHashMap<String, String> cityTemp = new ConcurrentHashMap<>();
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
                String inputStr = temp.split("\\+")[0];
                String city = temp.split("\\+")[1];
                Pattern tempPattern = Pattern.compile("[0-9]");
                Pattern cityPattern = Pattern.compile("[a-zA-Z\\u0400-\\u04FF]");
                Matcher m1 = tempPattern.matcher(inputStr);
                Matcher m2 = cityPattern.matcher(inputStr);
                if (m1.find() && !cityTemp.containsKey(city)) {
                    cityTemp.put(city, inputStr);
                } else if (m2.find()) {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.write(cityTemp.get(inputStr));
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
