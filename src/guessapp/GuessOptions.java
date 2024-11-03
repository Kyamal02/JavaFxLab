package guessapp;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GuessOptions {
    // задание(3)
    private Label tfMaxAttemptsLabel; // текст, который будет выше tfMaxAttempts
    private TextField tfMaxAttempts; // Поле для ввода максимального количества попыток

    // Главный контейнер VBox и контейнеры HBox для меток и полей ввода
    private VBox vbAll; // основной вертикальный контейнер
    private HBox hbLabels, hbTextFields, logHbox, buttonsHbox; // горизонтальные контейнеры для меток и полей ввода

    // Метки и текстовые поля для ввода границ диапазона
    private Label lblLeftBound, lblRightBound; // метки для полей ввода границ диапазона
    private TextField tfLeftBound, tfRightBound; // поля ввода для левой и правой границ

    // Кнопка для установки новых значений диапазона
    private Button btnSetOptions; // кнопка для подтверждения установленных значений
    private Button btnCancel; // задание(5) кнопка "Отмена"

    // Задание(4): Переменные для функциональности логирования
    private CheckBox cbLoggingEnabled; // CheckBox для включения/выключения логирования
    private Button btnClearLog; // Кнопка для очистки лог-файла

    // Сцена окна настроек и объекты Stage и GuessLogic для переключения окон и доступа к логике игры
    private Scene sgo; // сцена окна настроек
    private Stage sto; // главное окно приложения
    private GuessLogic glb; // объект GuessLogic для доступа к основным настройкам игры

    // Конструктор класса GuessOptions
    public GuessOptions(Stage st, GuessLogic gl) {
        sto = st; // сохраняем главное окно для управления сценами
        glb = gl; // сохраняем объект GuessLogic для доступа к логике игры

        formSceneO(); // создаем интерфейс окна настроек
    }

    // Метод для создания интерфейса окна настроек
    public void formSceneO() {
        vbAll = new VBox(30); // основной вертикальный контейнер с интервалом между элементами 30 пикселей
        vbAll.setAlignment(Pos.CENTER); // центрируем элементы внутри VBox

        // Создаем контейнер для меток и добавляем метки для границ диапазона
        hbLabels = new HBox(35); // контейнер для меток левой и правой границ
        hbLabels.setAlignment(Pos.CENTER); // центрируем метки
        lblLeftBound = new Label("Левая граница"); // метка для левой границы диапазона
        lblRightBound = new Label("Правая граница"); // метка для правой границы диапазона
        hbLabels.getChildren().addAll(lblLeftBound, lblRightBound); // добавляем метки в горизонтальный контейнер

        // Создаем контейнер для полей ввода и добавляем текстовые поля для границ диапазона
        hbTextFields = new HBox(20); // контейнер для полей ввода значений границ
        hbTextFields.setAlignment(Pos.CENTER); // центрируем поля ввода
        tfLeftBound = new TextField(); // поле ввода для левой границы
        tfRightBound = new TextField(); // поле ввода для правой границы
        tfLeftBound.setPrefWidth(70);
        tfRightBound.setPrefWidth(70);
        hbTextFields.getChildren().addAll(tfLeftBound, tfRightBound); // добавляем поля в горизонтальный контейнер

        // задание(3) создание метки и текстового поля для количества попыток
        tfMaxAttemptsLabel = new Label("Максимальное количество попыток (0 - бесконечно)");
        tfMaxAttempts = new TextField(); // поле для ввода количества попыток
        tfMaxAttempts.setMaxWidth(70); // задаем ширину для поля

        // задание(3) при нажатии мыши выделяем весь текст в поле tfMaxAttempts
        tfMaxAttempts.setOnMousePressed(eh -> tfMaxAttempts.selectAll());

        // При клике на текстовые поля для границ выделяем текст
        tfLeftBound.setOnMousePressed(eh -> tfLeftBound.selectAll());
        tfRightBound.setOnMousePressed(eh -> tfRightBound.selectAll());

        // Создаем кнопку "Установить значения" и задаем обработчик нажатия
        btnSetOptions = new Button("Установить значения");
        btnSetOptions.setOnAction(eh -> {
            setLoHiBnds(); // вызываем метод для применения новых значений
        });

        // задание(5)
        btnCancel = new Button("Отмена");
        btnCancel.setOnAction(eh -> {
            // Возвращаемся на основную сцену без изменений
            sto.setScene(glb.getScene()); // возвращаемся на основную сцену
            glb.returnFocusToGuessField(); // возвращаем фокус на поле ввода
        });

        // Задание(4): Создаем CheckBox для включения логирования и кнопку для очистки лог-файла
        cbLoggingEnabled = new CheckBox("Включить логирование"); // CheckBox для логирования

        btnClearLog = new Button("Очистить лог-файл"); // кнопка для очистки лог-файла
        btnClearLog.setOnAction(eh -> {
            glb.clearLogFile(); // вызываем метод для очистки лог-файла в GuessLogic
        });

        logHbox = new HBox(35); // контейнер для чекбокса и для кнопки очистки логирования
        logHbox.setAlignment(Pos.CENTER); // центрируем
        logHbox.getChildren().addAll(cbLoggingEnabled, btnClearLog);

        buttonsHbox = new HBox(20);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.getChildren().addAll(btnSetOptions, btnCancel);

        // Добавляем все элементы в основной контейнер VBox
        vbAll.getChildren().addAll(hbLabels, hbTextFields, tfMaxAttemptsLabel,
                tfMaxAttempts, logHbox, buttonsHbox);

        // Создаем сцену для окна настроек и задаем размер
        sgo = new Scene(vbAll, 400, 500); // создаем сцену с главным контейнером vbAll
    }

    // Метод для установки границ диапазона и максимального числа попыток
    private void setLoHiBnds() {
        try {
            // Пробуем преобразовать введенные значения границ и количества попыток в целые числа
            int lb = Integer.parseInt(tfLeftBound.getText()); // левая граница диапазона
            int hb = Integer.parseInt(tfRightBound.getText()); // правая граница диапазона
            int maxAttempts = Integer.parseInt(tfMaxAttempts.getText()); // максимальное количество попыток

            // Проверяем, что левая граница меньше правой
            if (lb >= hb) {
                tfLeftBound.setText("Левая < правая"); // предупреждаем, если левая граница больше правой
                tfRightBound.setText("Левая < правая");
            } else {
                // Устанавливаем значения границ диапазона и максимального числа попыток в GuessLogic
                glb.applySettings(lb, hb, maxAttempts, cbLoggingEnabled.isSelected()); // задание(5)
                sto.setScene(glb.getScene()); // возвращаемся на основную сцену только при корректных значениях
                glb.returnFocusToGuessField(); // возвращаем фокус на поле ввода в основной сцене
            }
        } catch (NumberFormatException e) {
            // задание(3) проверяем введенные значения и выводим предупреждения, если нецелые числа
            if (!isInteger(tfLeftBound.getText())) {
                tfLeftBound.setText("Введите целое"); // сообщение об ошибке в левой границе
            }
            if (!isInteger(tfRightBound.getText())) {
                tfRightBound.setText("Введите целое"); // сообщение об ошибке в правой границе
            }
            if (!isInteger(tfMaxAttempts.getText())) {
                tfMaxAttempts.setText("Введите целое"); // сообщение об ошибке в количестве попыток
            }
        }
    }

    // Метод для получения сцены настроек, чтобы переключиться на неё из GuessLogic
    public Scene getScene() {
        return sgo;
    }

    // Метод для обновления полей ввода текущими настройками
    public void updateFields() {
        tfLeftBound.setText(String.valueOf(glb.getLowBound()));
        tfRightBound.setText(String.valueOf(glb.getHighBound()));
        tfMaxAttempts.setText(String.valueOf(glb.getMaxAttempts()));
        cbLoggingEnabled.setSelected(glb.isLoggingEnabled());
    }


    // задание(1) метод для установки фокуса на левое поле ввода
    public void setFocusOnLeftBound() {
        Platform.runLater(() -> {
            tfLeftBound.requestFocus();
            tfLeftBound.selectAll(); // выделяем текст
        });
    }

    // задание(3) Метод для проверки, является ли строка целым числом
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str); // проверяем преобразование в целое число
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
