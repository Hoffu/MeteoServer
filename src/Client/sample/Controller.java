package Client.sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    public Label label;
    public TextField textField;
    private static Logger log = Logger.getLogger(Controller.class.getName());
    private String city;
    private Stage stage;

    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initialize() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Метеосервер");
        dialog.setHeaderText("Введите свой город");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> city = s);
        Platform.runLater(() -> {
            stage.setTitle(city);
        });
    }

    public void buttonClicked(ActionEvent actionEvent) {
        Socket socket = null;
        try {
            FileHandler xmlFile = new FileHandler ("logClient.xml", true);
            log.addHandler (xmlFile);
            FileHandler txtFile = new FileHandler ("logClient.%u.%g.txt", true);
            SimpleFormatter txtFormatter = new SimpleFormatter ();
            txtFile.setFormatter (txtFormatter);
            log.addHandler (txtFile);

            socket = new Socket("localhost", 3214);
            Pattern tempPattern = Pattern.compile("[0-9]");
            Matcher matcher = tempPattern.matcher(textField.getText());
            String output = "";
            if (matcher.find()) {
                output = textField.getText() + "+" + city + "\n";
            } else {
                output = textField.getText() + "\n";
            }
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.write(output);
            printWriter.flush();

            if (!matcher.find()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String temp = bufferedReader.readLine();
                bufferedReader.close();
                label.setText("Температура в городе " + textField.getText() + " равна " + temp + " градусам");
            }

            printWriter.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка клиента", e);
            System.out.println( "клиента ошибка : " + e);
        }
    }
}
