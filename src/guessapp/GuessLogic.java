package guessapp;

import java.util.Random;

import javafx.application.Platform;
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
    private int lowBound, highBound, guessNum, probNum; // диапазон значений и переменные для текущей попытки и загаданного числа

    // задание(3) переменные для отслеживания попыток
    private int maxAttempts = 0; // максимальное количество попыток, 0 означает отсутствие ограничений
    private int currentAttempts = 0; // счётчик для отслеживания текущего числа попыток
    private Label lblAttempts; // задание(3) метка для отображения оставшихся попыток

    // Сцена и контейнер для интерфейса
    private Scene sgl; // основная сцена для игры
    private VBox vb; // вертикальный контейнер для элементов управления

    // Метки, текстовое поле и кнопки для управления игрой
    private Label lblBounds, lblResult; // метка для отображения диапазона и результата попытки
    private TextField txtNumber; // поле для ввода числа игроком
    private Button btnAccept, btnOptions, btnNewGame; // кнопки для подтверждения, открытия настроек и начала новой игры

    // Генератор случайных чисел и ссылка на главное окно (Stage)
    private final Random rnd; // генератор случайных чисел для выбора загаданного числа
    private final Stage stg; // главное окно приложения

    // Объект GuessOptions для окна настроек
    private GuessOptions gow; // объект для управления настройками игры

    // Конструктор класса GuessLogic
    public GuessLogic(Stage s) {
        // задание(0)
        s.setResizable(false); // запрещаем изменение размеров окна
        stg = s; // сохраняем главное окно в переменной stg
        lowBound = 1; // устанавливаем нижнюю границу диапазона
        highBound = 100; // устанавливаем верхнюю границу диапазона
        rnd = new Random(); // инициализируем генератор случайных чисел
        generateNumber(); // генерируем случайное число в установленном диапазоне
        formScene(); // создаем и настраиваем интерфейс игры
    }

    // Метод для создания и настройки элементов интерфейса
    private void formScene() {
        gow = new GuessOptions(stg, this); // создаем объект GuessOptions для настройки параметров игры
        // Создаем контейнер VBox с интервалом 10 пикселей между элементами
        vb = new VBox(10);
        vb.setAlignment(Pos.CENTER); // Выравниваем элементы по центру
        vb.setPadding(new Insets(10)); // Добавляем отступы по краям

        // Создаем метки и текстовые поля
        lblBounds = new Label("Загадано число от " + lowBound + " до " + highBound); // отображение текущего диапазона
        lblResult = new Label("Игра еще не началась"); // отображение сообщения о начале игры
        lblAttempts = new Label("Осталось попыток: бесконечность"); // задание(3) метка для отображения оставшихся попыток

        txtNumber = new TextField("Введите число");
        txtNumber.setMaxWidth(150);

        txtNumber.setOnMouseClicked(e -> txtNumber.clear()); // очищаем текстовое поле при нажатии на него

        Platform.runLater(() -> {
            txtNumber.requestFocus(); // устанавливаем фокус на текстовое поле
            txtNumber.selectAll(); // выделяем текст в поле для удобства ввода
        });

        // Создаем кнопку "Принять значение" и задаем действие при нажатии
        btnAccept = new Button("Принять значение");
        btnAccept.setOnAction(eh -> handleGuess()); // обработчик нажатия кнопки для проверки введенного значения

        // Создаем кнопку "Настройки" для перехода к окну настроек
        btnOptions = new Button("Настройки");
        btnOptions.setOnAction(eh -> {
            stg.setScene(gow.getScene()); // переключаемся на сцену с настройками
            gow.setFocusOnLeftBound(); // фокус на поле ввода левой границы диапазона
        });

        // Создаем кнопку "Новая игра" для начала новой игры
        btnNewGame = new Button("Новая игра");
        btnNewGame.setOnAction(eh -> startNewGame()); // обработчик для начала новой игры

        // Добавляем все элементы управления в вертикальный контейнер
        vb.getChildren().addAll(btnNewGame, lblBounds, txtNumber, btnAccept, lblResult, lblAttempts, btnOptions);

        // Настраиваем сцену и устанавливаем её на главное окно
        sgl = new Scene(vb, 400, 500); // создаем сцену с контейнером vb
        stg.setScene(sgl); // устанавливаем сцену на главное окно
        stg.setTitle("Угадай число"); // заголовок окна
        stg.show(); // отображаем окно
    }

    // Метод для обработки попытки угадать число
    private void handleGuess() {
        try {
            probNum = Integer.parseInt(txtNumber.getText()); // пробуем преобразовать введенное значение в целое число

            // задание(3) проверка, что введенное число находится в диапазоне
            if (isOutOfRange(probNum)) { // проверка на соответствие диапазону
                lblResult.setText("Введите число в диапазоне от " + lowBound + " до " + highBound);
                txtNumber.setText(""); // очищаем поле для ввода нового значения
                return; // выходим из обработчика, чтобы не увеличивать счётчик попыток
            }

            // задание(3) если число в диапазоне, продолжаем проверку
            currentAttempts++; // увеличиваем счётчик попыток
            updateAttemptsLabel(); // обновляем метку для оставшихся попыток
            checkNumber(); // проверяем введенное значение на совпадение с загаданным

            // задание(3) проверка на исчерпание попыток
            if (maxAttempts > 0 && currentAttempts >= maxAttempts) { // если количество попыток достигло предела
                btnAccept.setDisable(true); // отключаем кнопку после исчерпания попыток
                lblResult.setText("Попытки закончились! Число не угадано."); // отображаем сообщение об окончании попыток
            }
        } catch (NumberFormatException e) { // если введенное значение не является числом
            txtNumber.setText("Введите целое число"); // выводим сообщение об ошибке
        }
    }

    // Метод для начала новой игры
    private void startNewGame() {
        // Проверяем, были ли попытки в предыдущей игре
        if (currentAttempts > 0) {
            lblResult.setText("Вы прервали игру после " + currentAttempts + " попыток.");
        } else {
            lblResult.setText("Начинаем новую игру!");
        }

        generateNumber(); // Генерируем новое случайное число
        txtNumber.setText("Введите число"); // Подсказка для ввода нового числа
        txtNumber.requestFocus(); // Устанавливаем фокус на поле ввода
        currentAttempts = 0; // Сбрасываем счётчик попыток
        updateAttemptsLabel(); // Обновляем метку для оставшихся попыток
        btnAccept.setDisable(false); // Включаем кнопку для нового ввода
    }

    // Проверка, что введенное число находится в диапазоне
    private boolean isOutOfRange(int number) {
        return number < lowBound || number > highBound; // проверка на соответствие диапазону
    }

    // Метод для генерации нового случайного числа в диапазоне
    public void generateNumber() {
        guessNum = rnd.nextInt(highBound - lowBound + 1) + lowBound; // случайное число в пределах lowBound и highBound
    }

    // Метод для проверки введенного числа
    void checkNumber() {
        // Сравниваем введенное число probNum с загаданным guessNum
        if (probNum > guessNum) {
            lblResult.setText("Загаданное число меньше"); // сообщение, если введенное число больше загаданного
        } else if (probNum < guessNum) {
            lblResult.setText("Загаданное число больше"); // сообщение, если введенное число меньше загаданного
        } else {
            lblResult.setText("Вы угадали число! Попыток: " + currentAttempts); // сообщение, если число угадано
            btnAccept.setDisable(true); // отключаем кнопку после успешного угадывания
        }
    }

    // Метод для возвращения сцены игры
    public Scene getScene() {
        // Возвращаем сцену игры, чтобы переключиться на неё из окна настроек
        return sgl;
    }

    // Метод для установки границ диапазона
    public void setLowHighBound(int valLB, int valHB) {
        lowBound = valLB; // установка новой левой границы диапазона
        highBound = valHB; // установка новой правой границы диапазона
        lblBounds.setText("Загадано число от " + lowBound + " до " + highBound); // обновление метки диапазона
        generateNumber(); // генерируем новое число в обновленном диапазоне
        lblResult.setText("Начинаем игру!"); // обновляем сообщение
    }

    // задание(1) метод для переключения фокуса
    public void returnFocusToGuessField() {
        Platform.runLater(() -> {
            txtNumber.requestFocus(); // установка фокуса на поле ввода числа
            txtNumber.selectAll(); // выделение текста в поле ввода
        });
    }

    // задание(3) метод для установки максимального количества попыток
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts; // установка нового значения максимального числа попыток
        currentAttempts = 0; // сброс счётчика при обновлении
        updateAttemptsLabel(); // задание(3) обновляем метку оставшихся попыток
        btnAccept.setDisable(false);
    }

    // Метод для обновления метки, отображающей оставшиеся попытки
    private void updateAttemptsLabel() { // задание(3) обновляем текст метки попыток
        if (maxAttempts > 0) {
            lblAttempts.setText("Осталось попыток: " + (maxAttempts - currentAttempts));
        } else {
            lblAttempts.setText("Осталось попыток: бесконечность");
        }
    }


}
