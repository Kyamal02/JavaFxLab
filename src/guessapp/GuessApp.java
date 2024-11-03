package guessapp;

import javafx.application.Application;
import javafx.stage.Stage;

public class GuessApp extends Application {

    public static void main(String[] args) {
        launch(args); // Запускает приложение JavaFX, что в свою очередь вызывает метод start()
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Создаем объект класса GuessLogic и передаем ему объект Stage (главное окно)
        // GuessLogic будет управлять логикой игры и интерфейсом
        GuessLogic glw = new GuessLogic(stage);

        stage.setResizable(false); // Запрещаем изменение размеров окна
    }
}
