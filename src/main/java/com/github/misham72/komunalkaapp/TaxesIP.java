package com.github.misham72.komunalkaapp;

import com.github.misham72.komunalkafilemanager.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class TaxesIP  extends JPanel {
    private final FileManager fileManager;
    private final String fileName = FileManager.getFilePath("Taxes.txt");

    // 2. Получаем доступ к хранилищу настроек
    private final Preferences prefs = Preferences.userNodeForPackage(MTSsimApp.class);

    // Ключи для сохранения настроек
    private static final String PREF_PAYMENT_DAY = "Taxes_PAYMENT_DAY";
    private static final String PREF_PERIOD = "Taxes_PERIOD";
    private static final String PREF_TARIFF = "Taxes_TARIFF";


    // Поля для хранения рассчитанных значений, чтобы их можно было использовать при сохранении.
    private long daysUntilPayment;
    private long daysFromPayment;
    private LocalDate nextPayment;
    private LocalDate previousPayment;

    public TaxesIP() {
        this.fileManager = new FileManager();
        LocalDate date = LocalDate.now();

        // Используем GridBagLayout для более гибкого расположения, но пока оставим GridLayout и добавим больше строк.
        setLayout(new GridLayout(9, 2, 3, 3)); // Увеличили количество строк
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Поля ввода
        add(new JLabel("День оплаты (число):"));
        // Получаем значение из настроек, если нет берем "23"
        String savedDay = prefs.get(PREF_PAYMENT_DAY, "30");
        JTextField paymentDayField = new JTextField(savedDay); // Значение по умолчанию
        add(paymentDayField);

        add(new JLabel("Период (мес.):"));
        String savedPeriod = prefs.get(PREF_PERIOD, "3");
        JTextField periodField = new JTextField(savedPeriod); // Значение по умолчанию
        add(periodField);

        add(new JLabel("Стоимость тарифа (руб.):"));
        String savedTariff = prefs.get(PREF_TARIFF, "13414");
        JTextField tariffField = new JTextField(savedTariff); // Значение по умолчанию
        add(tariffField);

        // Кнопка расчета
        JButton calculateButton = new JButton("Рассчитать даты");
        calculateButton.setBackground(Color.getHSBColor(0.60f, 0.40f, 0.99f));
        calculateButton.setOpaque(true);
        calculateButton.setBorderPainted(false);
        calculateButton.setFocusPainted(false);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 16));
        add(calculateButton);

        // Метки для вывода информации (изначально пустые или с дефолтным текстом)
        JLabel previousPaymentLabel = new JLabel(" Оплата была:   -");
        previousPaymentLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Чуть уменьшил шрифт, чтобы влезало
        previousPaymentLabel.setForeground(Color.blue);
        add(previousPaymentLabel);
        add(new JLabel());

        JLabel daysFromPaymentLabel = new JLabel(" Прошло: - дней,");
        daysFromPaymentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        daysFromPaymentLabel.setForeground(Color.blue);
        add(daysFromPaymentLabel);
        add(new JLabel());

        JLabel dayOfPaymentLabel = new JLabel(" Дата оплаты:                 -");
        dayOfPaymentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dayOfPaymentLabel.setForeground(Color.red);
        add(dayOfPaymentLabel);
        add(new JLabel());

        JLabel daysUntilPaymentLabel = new JLabel(" Оплата через:                        - дней.");
        daysUntilPaymentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        daysUntilPaymentLabel.setForeground(Color.red);
        add(daysUntilPaymentLabel);
        add(new JLabel());

        JLabel dateLabel = new JLabel(" Сегодня:                           " + date);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dateLabel.setForeground(Color.black);
        add(dateLabel);

        // Логика кнопки "Рассчитать"
        calculateButton.addActionListener(_ -> {
            try {
                String pDayStr = paymentDayField.getText();
                String periodStr = periodField.getText();
                String tariffStr = tariffField.getText();

                int paymentDay = Integer.parseInt(paymentDayField.getText());
                int monthsPeriod = Integer.parseInt(periodField.getText());

                // 4. Сохраняем введенные значения в настройки
                prefs.put(PREF_PAYMENT_DAY, pDayStr);
                prefs.put(PREF_PERIOD, periodStr);
                prefs.put(PREF_TARIFF, tariffStr);

                // Выполняем расчеты
                daysUntilPayment = DateCalculator.calculateDaysToNextPayment(monthsPeriod, paymentDay);
                daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(monthsPeriod, paymentDay);
                nextPayment = DateCalculator.getNextPaymentDate(monthsPeriod, paymentDay);
                previousPayment = DateCalculator.getPreviousPaymentDate(monthsPeriod, paymentDay);

                // Обновляем метки
                previousPaymentLabel.setText(" Оплата была:                 " + previousPayment);
                dayOfPaymentLabel.setText(" Дата оплаты:                  " + nextPayment);
                daysUntilPaymentLabel.setText(" Оплата через:                 " + daysUntilPayment + " дней.");
                daysFromPaymentLabel.setText(" Прошло:                           " + daysFromPayment + " дней,");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Введите корректные числа!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Автоматический расчет при запуске с дефолтными значениями
        calculateButton.doClick();


        JButton saveHistoryButton = new JButton("Сохранить в файл");
        saveHistoryButton.setBackground(Color.getHSBColor(0.60f, 0.40f, 0.99f));
        saveHistoryButton.setOpaque(true);
        saveHistoryButton.setBorderPainted(false);
        saveHistoryButton.setFocusPainted(false);
        saveHistoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        add(saveHistoryButton);

        saveHistoryButton.addActionListener(_ -> {
            try {
                // СОХРАНЯЕМ НАСТРОЙКИ ТАКЖЕ ПРИ СОХРАНЕНИИ ФАЙЛА
                prefs.put(PREF_PAYMENT_DAY, paymentDayField.getText());
                prefs.put(PREF_PERIOD, periodField.getText());
                prefs.put(PREF_TARIFF, tariffField.getText());

                // Получаем тариф из поля ввода при сохранении
                long priceTariff = Long.parseLong(tariffField.getText());
                String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy / HH:mm:ss"));

                fileManager.formatPaymentDate(fileName, daysUntilPayment, daysFromPayment, nextPayment, previousPayment, priceTariff, formattedDateTime);
                JOptionPane.showMessageDialog(this, "Данные успешно сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректная стоимость тарифа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Кнопка для загрузки истории
        JButton showHistoryButton = new JButton("Показать историю");
        showHistoryButton.setBackground(Color.getHSBColor(0.60f, 0.40f, 0.99f));
        showHistoryButton.setOpaque(true);
        showHistoryButton.setBorderPainted(false);
        showHistoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        add(showHistoryButton);
        showHistoryButton.addActionListener(_ -> {
            try {
                String history = fileManager.loadFromFile(fileName);
                if (history.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "История пуста для ресурса: Taxes", "Информация", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Используем JTextArea, вместо JTextPane для простоты (без цветов)
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
                    saveButton.setBackground(new Color(144, 238, 144)); // Светло-зеленый цвет
                    saveButton.setOpaque(true);
                    saveButton.setBorderPainted(false);

                    // Создаем панель для кнопки (чтобы выровнять по правому краю)
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    buttonPanel.add(markPaidButton);
                    buttonPanel.add(saveButton);

                    // Создаем основную панель для содержимого
                    JPanel mainPanel = new JPanel(new BorderLayout());
                    mainPanel.add(scrollPane, BorderLayout.CENTER);
                    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

                    // Создаем диалоговое окно
                    JDialog dialog = new JDialog();
                    dialog.setTitle("История (Taxes) - Редактирование");
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

                    // Обработчик кнопки сохранения
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

                    // Показываем диалоговое окно
                    dialog.setVisible(true);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки истории: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

    }
}

