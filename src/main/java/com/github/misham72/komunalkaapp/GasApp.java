package com.github.misham72.komunalkaapp;

import com.github.misham72.komunalkacalculator.KomunalkaCalculator;
import com.github.misham72.komunalkafilemanager.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class GasApp extends JPanel {

        private final KomunalkaCalculator calculator;
        private final FileManager fileManager;
        private final String fileName = FileManager.getFilePath("Gas.txt");

    private final Preferences prefs = Preferences.userNodeForPackage(ElectricityApp.class);
    private static final String PREF_CURRENT_DATA = "GAS_CURRENT_DATA";
    private static final String PREF_PREVIOUS_DATA = "GAS_PREVIOUS_DATA";
    private static final String PREF_TARIFF = "GAS_TARIFF";

        public GasApp() {
            this.calculator = new KomunalkaCalculator();
            this.fileManager = new FileManager();

            setLayout(new GridLayout(7, 2, 10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Добавляем отступы

        // Компоненты интерфейса
        JTextField currentDataField = new JTextField();
        JTextField previousDataField = new JTextField();
        JTextField tariffField = new JTextField();

        // ЗАГРУЖАЕМ сохраненные значения
        currentDataField.setText(prefs.get(PREF_CURRENT_DATA, ""));
        previousDataField.setText(prefs.get(PREF_PREVIOUS_DATA, ""));
        tariffField.setText(prefs.get(PREF_TARIFF, ""));

        JLabel consumptionLabel = new JLabel("Расход: -");
        consumptionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        consumptionLabel.setForeground(Color.red);

            JLabel paymentLabel = new JLabel("К оплате: -");
            paymentLabel.setFont(new Font("Arial", Font.BOLD, 16));
            paymentLabel.setForeground(Color.red);

            JLabel dateTimeLabel = new JLabel("Дата и время последней операции: -");
            dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));

            JButton calculateButton = new JButton("Рассчитать");
            calculateButton.setBackground(Color.getHSBColor(0.60f, 0.40f, 0.99f));
            calculateButton.setOpaque(true);
            calculateButton.setBorderPainted(false);
            calculateButton.setFont(new Font("Arial", Font.BOLD, 16));

            JButton showHistoryButton = new JButton("Показать историю");
            showHistoryButton.setBackground(Color.getHSBColor(0.60f, 0.40f, 0.99f));
            showHistoryButton.setOpaque(true);
            showHistoryButton.setBorderPainted(false);
            showHistoryButton.setFont(new Font("Arial", Font.BOLD, 16));

            // Добавляем компоненты в панель
            add(new JLabel("Текущие показания:"));
            add(currentDataField);
            add(new JLabel("Предыдущие показания:"));
            add(previousDataField);
            add(new JLabel("Тариф (руб.):"));
            add(tariffField);
            add(consumptionLabel);
            add(paymentLabel);
            add(dateTimeLabel);
            add(new JLabel()); // Пустое поле для выравнивания
            add(calculateButton);
            add(showHistoryButton);

        // Навигация по полям с Enter
        currentDataField.addActionListener(_ -> previousDataField.requestFocus());
        previousDataField.addActionListener(_ -> tariffField.requestFocus());
        tariffField.addActionListener(_ -> calculateButton.doClick());

            // Логика кнопки "Рассчитать"
            calculateButton.addActionListener(_ -> {
                try {
                    double currentReading = Double.parseDouble(currentDataField.getText());
                    double previousReading = Double.parseDouble(previousDataField.getText());
                    double tariff = Double.parseDouble(tariffField.getText());

                // СОХРАНЯЕМ НАСТРОЙКИ ПРИ РАСЧЕТЕ
                prefs.put(PREF_CURRENT_DATA, currentDataField.getText());
                prefs.put(PREF_PREVIOUS_DATA, previousDataField.getText());
                prefs.put(PREF_TARIFF, tariffField.getText());

                // Производим расчёты
                double consumption = calculator.calculateConsumption(currentReading, previousReading);
                double payment = calculator.calculatePayment(consumption, tariff);

                    String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                    consumptionLabel.setText(String.format("Расход: %.2f", consumption));
                    paymentLabel.setText(String.format("К оплате: %.2f руб.", payment));
                    dateTimeLabel.setText("Дата и время последней операции: " + formattedDateTime);

                    String unit = "куб.м.";

                // СОХРАНЯЕМ В ФАЙЛ (как в MTSsimApp делает кнопка "Сохранить в файл")
                fileManager.formatMeterReadingPaymentData(fileName, currentReading, previousReading, consumption, tariff, payment, unit, formattedDateTime);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные числа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при записи в файл: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
// Кнопка "Показать историю"
        showHistoryButton.addActionListener(_ -> {
            try {
                String history = fileManager.loadFromFile(fileName);
                if (history.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "История пуста для ресурса: Газ", "Информация", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Код создания диалогового окна с историей
                    JTextArea textArea = new JTextArea(20, 50);
                    textArea.setText(history);
                    textArea.setEditable(true);

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    // Создаем две кнопки
                    JButton markPaidButton = new JButton("✅ ОПЛАЧЕНО");
                    markPaidButton.setFont(new Font("Arial", Font.BOLD, 14));
                    markPaidButton.setBackground(new Color(200, 255, 200));
                    markPaidButton.setOpaque(true);
                    markPaidButton.setBorderPainted(false);

                    JButton saveButton = new JButton("💾 Сохранить");
                    saveButton.setFont(new Font("Arial", Font.BOLD, 14));
                    saveButton.setBackground(new Color(144, 238, 144));
                    saveButton.setOpaque(true);
                    saveButton.setBorderPainted(false);

                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    buttonPanel.add(markPaidButton);
                    buttonPanel.add(saveButton);

                        // Создаем основную панель для содержимого
                        JPanel mainPanel = new JPanel(new BorderLayout());
                        mainPanel.add(scrollPane, BorderLayout.CENTER);
                        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

                    JDialog dialog = new JDialog();
                    dialog.setTitle("История (Газ) - Редактирование");
                    dialog.setModal(true);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.getContentPane().add(mainPanel);
                    dialog.pack();
                    dialog.setSize(800, 600);
                    dialog.setLocationRelativeTo(this);

                    // Обработчик кнопки "✅ ОПЛАЧЕНО"
                    markPaidButton.addActionListener(_ -> {
                        try {
                            int caretPos = textArea.getCaretPosition();
                            int lineNum = textArea.getLineOfOffset(caretPos);
                            int start = textArea.getLineStartOffset(lineNum);
                            int end = textArea.getLineEndOffset(lineNum);

                            // (убираем перевод строки)
                            String lineText = textArea.getText(start, end - start);
                            lineText = lineText.replace("\n", "").replace("\r", "");

                            // Если строка еще не помечена
                            if (!lineText.startsWith("[ОПЛАЧЕНО]")) {
                                // Заменяем строку
                                textArea.replaceRange("[ОПЛАЧЕНО] " + lineText, start, end);
                            }
                        } catch (Exception ex) {
                            // Игнорируем ошибки курсора
                        }
                    });

                    // Обработчик кнопки "💾 Сохранить"
                    saveButton.addActionListener(_ -> {
                        try {
                            fileManager.textWindow(fileName, textArea.getText());
                            JOptionPane.showMessageDialog(dialog,
                                    "Изменения успешно сохранены!",
                                    "Успех",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Ошибка при сохранении: " + ex.getMessage(),
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    dialog.setVisible(true);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки истории: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });


    }
}
