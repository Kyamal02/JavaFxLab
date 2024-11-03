package guessapp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    // Задание(4): переменная для логирования
    private boolean loggingEnabled = false; // флаг для включения/отключения логирования
    // Задание(4): константа для имени лог-файла
    private static final String LOG_FILE_NAME = "src/guessapp/LogGuess.txt"; // имя лог-файла

    // задание(5): переменная для отслеживания прерывания игры
    private boolean wasInterrupted = false;

    // Конструктор класса GuessLogic
    public GuessLogic(Stage s) {
        // задание(0)
        s.setResizable(false); // запрещаем изменение размеров окна
        stg = s; // сохраняем главное окно в переменной stg
        lowBound = 1; // устанавливаем нижнюю границу диапазона
        highBound = 100; // устанавливаем верхнюю границу диапазона
        rnd = new Random(); // инициализируем генератор случайных чисел
        formScene(); // создаем и настраиваем интерфейс игры
        initializeGame(); // инициализируем игру
    }

    // Метод для создания и настройки элементов интерфейса
    private void formScene() {
        gow = new GuessOptions(stg, this); // создаем объект GuessOptions для настройки параметров игры
        // Создаем контейнер VBox с интервалом 10 пикселей между элементами
        vb = new VBox(10);
        vb.setAlignment(Pos.CENTER); // Выравниваем элементы по центру
        vb.setPadding(new Insets(10)); // Добавляем отступы по краям

        // Создаем метки и текстовые поля
        lblBounds = new Label(); // метка для отображения диапазона будет обновлена при инициализации
        lblResult = new Label("Игра еще не началась"); // отображение сообщения о начале игры
        lblAttempts = new Label(); // задание(3) метка для отображения оставшихся попыток будет обновлена при инициализации

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
            gow.updateFields(); // обновляем поля ввода текущими настройками
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

    // Метод для инициализации игры (используется при запуске и изменении настроек)
    private void initializeGame() {
        // задание(5): если игра была прервана, выводим сообщение
        if (wasInterrupted) {
            lblResult.setText("Игра прервана. Предыдущих попыток: " + currentAttempts);
            wasInterrupted = false; // сбрасываем флаг прерывания
        } else {
            lblResult.setText("Начинаем игру!"); // обновляем сообщение
        }

        generateNumber(); // генерируем новое случайное число
        currentAttempts = 0; // сбрасываем счётчик попыток
        updateAttemptsLabel(); // обновляем метку для оставшихся попыток
        lblBounds.setText("Загадано число от " + lowBound + " до " + highBound); // обновляем метку диапазона
        txtNumber.setText("Введите число"); // очищаем поле ввода
        btnAccept.setDisable(false); // включаем кнопку ввода
        txtNumber.requestFocus(); // ставим фокус на поле ввода

        // Логируем начало новой игры
        if (loggingEnabled) {
            String logMessage = "Новая игра. Диапазон: от " + lowBound + " до " + highBound + ". Максимальное количество попыток: " +
                    (maxAttempts > 0 ? maxAttempts : "бесконечность");
            writeLog(logMessage);
        }
    }

    // Метод для обработки попытки угадать число
    private void handleGuess() {
        try {
            probNum = Integer.parseInt(txtNumber.getText().trim()); // пробуем преобразовать введенное значение в целое число

            // задание(3) проверка, что введенное число находится в диапазоне
            if (isOutOfRange(probNum)) { // проверка на соответствие диапазону
                lblResult.setText("Введите число в диапазоне от " + lowBound + " до " + highBound);
                txtNumber.setText(""); // очищаем поле для ввода нового значения

                // Логируем выход за пределы диапазона
                if (loggingEnabled) {
                    writeLog("Введено число вне диапазона: " + probNum);
                }

                return; // выходим из обработчика, чтобы не увеличивать счётчик попыток
            }

            // задание(3) если число в диапазоне, продолжаем проверку
            currentAttempts++; // увеличиваем счётчик попыток
            updateAttemptsLabel(); // обновляем метку для оставшихся попыток
            String verdict = checkNumber(); // проверяем введенное значение на совпадение с загаданным

            // Задание(4): логируем попытку
            if (loggingEnabled) {
                int remainingAttempts = (maxAttempts > 0) ? (maxAttempts - currentAttempts) : -1;
                String logMessage = "Попытка " + currentAttempts + ": введено " + probNum + ", вердикт: " + verdict + ", осталось попыток: " + (remainingAttempts >= 0 ? remainingAttempts : "бесконечность");
                writeLog(logMessage);
            }

            // задание(3) проверка на исчерпание попыток
            if (maxAttempts > 0 && currentAttempts >= maxAttempts && probNum != guessNum) { // если количество попыток достигло предела и число не угадано
                btnAccept.setDisable(true); // отключаем кнопку после исчерпания попыток
                lblResult.setText("Попытки закончились! Число не угадано."); // отображаем сообщение об окончании попыток

                // Задание(4): логируем окончание игры
                if (loggingEnabled) {
                    writeLog("Попытки закончились. Число не угадано.");
                }
            }
        } catch (NumberFormatException e) { // если введенное значение не является числом
            txtNumber.setText("Введите целое число"); // выводим сообщение об ошибке
            lblResult.setText("Некорректный ввод!");

            // Логируем некорректный ввод
            if (loggingEnabled) {
                writeLog("Некорректный ввод: " + txtNumber.getText().trim());
            }
        }
    }

    // Метод для начала новой игры по нажатию кнопки "Новая игра"
    private void startNewGame() {
        // Проверяем, были ли попытки в предыдущей игре
        if (currentAttempts > 0) {
            lblResult.setText("Вы прервали игру после " + currentAttempts + " попыток.");

            // Задание(4): логируем прерывание игры
            if (loggingEnabled) {
                writeLog("Игра прервана после " + currentAttempts + " попыток.");
            }
        } else {
            lblResult.setText("Начинаем новую игру!");

            // Задание(4): логируем начало новой игры
            if (loggingEnabled) {
                writeLog("Игра начата заново.");
            }
        }

        initializeGame(); // Инициализируем игру
    }

    // Проверка, что введенное число находится в диапазоне
    private boolean isOutOfRange(int number) {
        return number < lowBound || number > highBound; // проверка на соответствие диапазону
    }


    // Метод для проверки введенного числа
    String checkNumber() {
        String verdict;

        // Сравниваем введенное число probNum с загаданным guessNum
        if (probNum > guessNum) {
            verdict = "Загаданное число меньше"; // сообщение, если введенное число больше загаданного
            lblResult.setText(verdict);
        } else if (probNum < guessNum) {
            verdict = "Загаданное число больше"; // сообщение, если введенное число меньше загаданного
            lblResult.setText(verdict);
        } else {
            verdict = "Вы угадали число! Попыток: " + currentAttempts; // сообщение, если число угадано
            lblResult.setText(verdict);
            btnAccept.setDisable(true); // отключаем кнопку после успешного угадывания

            // Задание(4): логируем успешное угадывание
            if (loggingEnabled) {
                writeLog("Число угадано за " + currentAttempts + " попыток.");
            }
        }
        return verdict; // возвращаем вердикт для логирования
    }

    // Метод для обновления метки, отображающей оставшиеся попытки
    private void updateAttemptsLabel() { // задание(3) обновляем текст метки попыток
        if (maxAttempts > 0) {
            lblAttempts.setText("Осталось попыток: " + (maxAttempts - currentAttempts));
        } else {
            lblAttempts.setText("Осталось попыток: бесконечность");
        }
    }

    // Задание(4): Метод для записи в лог-файл
    private void writeLog(String message) {
        if (loggingEnabled) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_NAME, true));
                writer.println(message);
                writer.close();
            } catch (IOException e) {
                System.out.println("Ошибка при записи в лог-файл: " + e.getMessage());
            }
        }
    }

    // Метод для возвращения сцены игры
    public Scene getScene() {
        // Возвращаем сцену игры, чтобы переключиться на неё из окна настроек
        return sgl;
    }

    // Метод для генерации нового случайного числа в диапазоне
    public void generateNumber() {
        guessNum = rnd.nextInt(highBound - lowBound + 1) + lowBound; // случайное число в пределах lowBound и highBound
    }

    // Метод для установки границ диапазона
    public void setLowHighBound(int valLB, int valHB) {
        this.lowBound = valLB;
        this.highBound = valHB;
    }

    // задание(3) метод для установки максимального количества попыток
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    // Задание(5): Метод для применения настроек и перезапуска игры
    public void applySettings(int valLB, int valHB, int maxAttempts, boolean loggingEnabled) {
        // задание(5): если игра не завершена, устанавливаем флаг прерывания
        if (currentAttempts > 0 && !btnAccept.isDisabled()) {
            wasInterrupted = true;

            // Задание(5): логируем прерывание игры при изменении настроек
            if (this.loggingEnabled) {
                writeLog("Игра прервана из-за изменения настроек после " + currentAttempts + " попыток.");
            }
        }

        // Применяем новые настройки
        setLowHighBound(valLB, valHB);
        setMaxAttempts(Math.max(0, maxAttempts));
        setLoggingEnabled(loggingEnabled);

        initializeGame(); // Инициализируем игру с новыми настройками
    }

    // задание(1) метод для переключения фокуса
    public void returnFocusToGuessField() {
        Platform.runLater(() -> {
            txtNumber.requestFocus(); // установка фокуса на поле ввода числа
            txtNumber.selectAll(); // выделение текста в поле ввода
        });
    }


    // Задание(4): Метод для установки флага логирования
    public void setLoggingEnabled(boolean enabled) {
        if (enabled != loggingEnabled) {
            loggingEnabled = enabled;
            writeLog("Логирование " + (enabled ? "включено" : "отключено") + ".");
        }
    }

    // Задание(4): Метод для очистки лог-файла
    public void clearLogFile() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_NAME));
            writer.print(""); // очищаем содержимое файла
            writer.close();
        } catch (IOException e) {
            System.out.println("Ошибка при очистке лог-файла: " + e.getMessage());
        }
    }


    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    // Геттеры для текущих настроек
    public int getLowBound() {
        return lowBound;
    }

    public int getHighBound() {
        return highBound;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

}
