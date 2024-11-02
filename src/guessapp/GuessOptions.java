package guessapp;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GuessOptions {
    //задание(3)
    private Label tfMaxAttemptsLabel; // текст, который будет выше tfMaxAttempts
    private TextField tfMaxAttempts; // Поле для ввода максимума попыток

    // Главный контейнер VBox и контейнеры HBox для меток и полей ввода
    private VBox vbAll;
    private HBox hbLabels, hbTextFields;

    // Метки и текстовые поля для ввода границ диапазона
    private Label lblLeftBound, lblRightBound;
    private TextField tfLeftBound, tfRightBound;

    // Кнопка для установки новых значений диапазона
    private Button btnSetOptions;

    // Сцена окна настроек и объекты Stage и GuessLogic для переключения окон и доступа к логике игры
    private Scene sgo;
    private Stage sto;
    private GuessLogic glb;

    public GuessOptions(Stage st, GuessLogic gl) {
        // Сохраняем главное окно Stage и объект GuessLogic для связи с игрой
        sto = st;
        glb = gl;

        formSceneO(); // Создаем интерфейс окна настроек
    }

    public void formSceneO() {
        // Инициализируем главный контейнер VBox с интервалом 30 пикселей
        vbAll = new VBox(30);
        vbAll.setAlignment(Pos.CENTER); // Центрируем элементы внутри VBox

        // Инициализируем контейнер для меток и добавляем метки для границ диапазона
        hbLabels = new HBox(35);
        hbLabels.setAlignment(Pos.CENTER);
        lblLeftBound = new Label("Левая граница");
        lblRightBound = new Label("Правая граница");
        hbLabels.getChildren().addAll(lblLeftBound, lblRightBound);

        // Инициализируем контейнер для полей ввода и добавляем сами поля
        hbTextFields = new HBox(20);
        hbTextFields.setAlignment(Pos.CENTER);
        tfLeftBound = new TextField("Введите левую границу");
        tfRightBound = new TextField("Введите правую границу");
        //задание(1) задаем предпочтительную ширину
        tfLeftBound.setPrefWidth(155);
        tfRightBound.setPrefWidth(155);


        hbTextFields.getChildren().addAll(tfLeftBound, tfRightBound);
        //задание(3)
        tfMaxAttemptsLabel = new Label("Максимальное количество попыток (0 бесконечность)");
        //задание(3) добавление текстового поля для того,
        //чтобы можно было задать максимальное количество попыток
        tfMaxAttempts = new TextField("0");
        tfMaxAttempts.setMaxWidth(155); // Задание ширины

        //задание(3) при нажатии мыши все выделяем
        tfMaxAttempts.setOnMousePressed(eh -> tfMaxAttempts.selectAll());

        //задание(1) при открытии настроек ждем всю отрисовку, ставим фокус
        // на левое поле и выделяем текст, который там есть
        Platform.runLater(() -> {
            tfLeftBound.requestFocus();
            tfLeftBound.selectAll();
        });

        // При клике на текстовые поля весь текст выделяется
        tfLeftBound.setOnMousePressed(eh -> tfLeftBound.selectAll());
        tfRightBound.setOnMousePressed(eh -> tfRightBound.selectAll());

        // Создаем кнопку "Установить значения" и устанавливаем действие при нажатии
        btnSetOptions = new Button("Установить значения");
        btnSetOptions.setOnAction(eh -> {
            setLoHiBnds(); // Устанавливаем новые значения диапазона
        });

        // Добавляем в главный контейнер VBox все созданные элементы
        vbAll.getChildren().addAll(hbLabels, hbTextFields, tfMaxAttemptsLabel, tfMaxAttempts, btnSetOptions);

        // Создаем сцену для окна настроек и задаем размер
        sgo = new Scene(vbAll, 400, 500);
    }

    public Scene getScene() {
        // Возвращаем сцену настроек, чтобы переключиться на неё из GuessLogic
        return sgo;
    }

    private void setLoHiBnds() {
        try {
            // Пробуем преобразовать значения левой и правой границ в целые числа
            int lb = Integer.parseInt(tfLeftBound.getText());
            int hb = Integer.parseInt(tfRightBound.getText());
            // задание(3) Пробуем преобразовать значение maxAttempts в целое число
            int maxAttempts = Integer.parseInt(tfMaxAttempts.getText());

            // Проверяем, что левая граница меньше правой
            if (lb >= hb) {
                tfLeftBound.setText("Левая < правая");
                tfRightBound.setText("Левая < правая");
            } else {
                // Устанавливаем новые границы диапазона только при корректном вводе
                glb.setLowHighBound(lb, hb);
                // задание(3) вызываем метод из класса GuessLogic, чтобы установить ограничение
                glb.setMaxAttempts(Math.max(0, maxAttempts)); // Установка ограничений на попытки
                // задание(2) переместили методы сюда, чтобы переключалось только когда нет ошибок
                sto.setScene(glb.getScene()); // Переход обратно на основную сцену
                // задание(1) чтобы при переключении обратно был фокус на поле
                glb.returnFocusToGuessField();
            }
        } catch (NumberFormatException e) {
            // задание(3) Проверка на некорректные значения в каждом поле
            if (!isInteger(tfLeftBound.getText())) {
                tfLeftBound.setText("Введите целое");
            }
            if (!isInteger(tfRightBound.getText())) {
                tfRightBound.setText("Введите целое");
            }
            if (!isInteger(tfMaxAttempts.getText())) {
                tfMaxAttempts.setText("Введите целое");
            }
        }
    }

    //задание(1) метод для переключения фокуса
    public void setFocusOnLeftBound() {
        Platform.runLater(() -> {
            tfLeftBound.requestFocus();
            tfLeftBound.selectAll();
        });
    }

    //задание(3) Метод для проверки, является ли строка целым числом
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}