package guessapp;

import java.util.Random;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GuessLogic {
    // Переменные для диапазона и логики игры
    private int lowBound, highBound, guessNum, probNum;

    // Сцена и контейнер для интерфейса
    private Scene sgl;
    private VBox vb;

    // Метки, текстовое поле и кнопки для управления игрой
    private Label lblBounds, lblResult;
    private TextField txtNumber;
    private Button btnAccept, btnOptions, btnNewGame;

    // Генератор случайных чисел и ссылка на главное окно (Stage)
    private Random rnd;
    private Stage stg;

    // Объект GuessOptions для окна настроек
    private GuessOptions gow;

    public GuessLogic(Stage s) {
        stg = s; // Сохраняем главное окно
        // Устанавливаем диапазон и создаем генератор случайных чисел
        lowBound = 1;
        highBound = 100;
        rnd = new Random();

        generateNumber(); // Генерируем случайное число
        formScene(); // Создаем и настраиваем интерфейс игры
    }

    void formScene() {
        // Инициализируем объект GuessOptions, передаем Stage и текущий объект GuessLogic
        gow = new GuessOptions(stg, this);

        // Создаем контейнер VBox с интервалом 10 пикселей между элементами
        vb = new VBox(10);
        vb.setAlignment(Pos.CENTER); // Выравниваем элементы по центру
        vb.setPadding(new Insets(10)); // Добавляем отступы по краям

        // Создаем и инициализируем метки для диапазона чисел и результата игры
        lblBounds = new Label("Загадано число от " + lowBound + " до " + highBound);
        lblResult = new Label("Игра еще не началась");

        // Создаем текстовое поле для ввода числа
        txtNumber = new TextField("Введите число");

        // Создаем кнопку "Принять значение" и назначаем ей действие при нажатии
        btnAccept = new Button("Принять значение");
        btnAccept.setOnAction(eh -> {
            // Получаем введенное число и сохраняем в probNum
            probNum = Integer.parseInt(txtNumber.getText());
            checkNumber(); // Проверяем, угадал ли игрок число
        });

        // Создаем кнопку "Настройки" для перехода к окну настроек
        btnOptions = new Button("Настройки");
        btnOptions.setOnAction(eh -> stg.setScene(gow.getScene())); // Открываем сцену настроек

        // Создаем кнопку "Новая игра" для начала новой игры
        btnNewGame = new Button("Новая игра");
        btnNewGame.setOnAction(eh -> {
            generateNumber(); // Генерируем новое случайное число
            lblResult.setText("Начинаем игру!"); // Обновляем сообщение
        });

        // Добавляем все элементы в VBox, чтобы они располагались вертикально
        vb.getChildren().addAll(btnNewGame, lblBounds, txtNumber, btnAccept, lblResult, btnOptions);

        // Создаем сцену с VBox и устанавливаем ее на главное окно Stage
        sgl = new Scene(vb, 300, 400);
        stg.setScene(sgl);
        stg.setTitle("Угадай число"); // Устанавливаем заголовок окна
        stg.show(); // Отображаем окно
    }

    public void generateNumber() {
        // Генерируем случайное число от lowBound до highBound
        guessNum = rnd.nextInt(highBound - lowBound + 1) + lowBound;
    }

    void checkNumber() {
        // Проверяем введенное игроком число probNum по сравнению с загаданным guessNum
        if (probNum > guessNum) {
            lblResult.setText("Загаданное число меньше"); // Число больше загаданного
        } else if (probNum < guessNum) {
            lblResult.setText("Загаданное число больше"); // Число меньше загаданного
        } else {
            lblResult.setText("Вы угадали число!"); // Игрок угадал число
        }
    }

    public Scene getScene() {
        // Возвращаем сцену игры, чтобы переключиться на неё из окна настроек
        return sgl;
    }

    public void setLowHighBound(int valLB, int valHB) {
        // Устанавливаем новые границы диапазона и обновляем метку lblBounds
        lowBound = valLB;
        highBound = valHB;
        lblBounds.setText("Загадано число от " + lowBound + " до " + highBound);
        generateNumber(); // Генерируем новое число в новом диапазоне
        lblResult.setText("Начинаем игру!"); // Обновляем сообщение
    }
}
