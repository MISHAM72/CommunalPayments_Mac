package com.github.misham72.KomunalkaApp;

import com.github.misham72.KomunalkaFileManager.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MTSsimApp extends JPanel {
    private final FileManager fileManager;
    private final String fileName = FileManager.getFilePath("МТС.txt");

    public MTSsimApp() {
        this.fileManager = new FileManager();

        long daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 23);
        long daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 23);
        LocalDate nextPayment = DateCalculator.getNextPaymentDate(1, 23);
        LocalDate previousPayment = DateCalculator.getPreviousPaymentDate(1, 23);
        long priceTariff = 905;
        String FormattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy / HH:mm:ss"));


        setLayout(new GridLayout(7, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Добавляем отступы

        JLabel previousPaymentLabel = new JLabel(" Предыдущая оплата:   " + previousPayment);
        previousPaymentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        previousPaymentLabel.setForeground(Color.blue);
        add(previousPaymentLabel);

        JLabel dateLabel = new JLabel(" Сегодня:          " + FormattedDateTime);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dateLabel.setForeground(Color.black);
        add(dateLabel);

        JLabel dayOfPaymentLabel = new JLabel(" Дата оплаты:                 " + nextPayment);
        dayOfPaymentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dayOfPaymentLabel.setForeground(Color.blue);
        add(dayOfPaymentLabel);
        add(new JLabel());
        add(new JLabel());

        JLabel daysUntilPaymentLabel = new JLabel(" Оплата через:                        " + daysUntilPayment + " дней.");
        daysUntilPaymentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        daysUntilPaymentLabel.setForeground(Color.red);
        add(daysUntilPaymentLabel);
        add(new JLabel());

        JLabel daysFromPaymentLabel = new JLabel(" С момента оплаты прошло: " + daysFromPayment + " дней,");
        daysFromPaymentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        daysFromPaymentLabel.setForeground(Color.blue);
        add(daysFromPaymentLabel);
        add(new JLabel());
        add(new JLabel());


        JLabel priceTariffLabel = new JLabel(" Стоимость тарифа: " + priceTariff + " рублей. ");
        priceTariffLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceTariffLabel.setForeground(Color.getHSBColor(0.9f, 0.85f, 0.8f));
        add(priceTariffLabel);
        add(new JLabel());

        JButton saveHistoryButton = new JButton("Сохранить в файл");
        saveHistoryButton.setBackground(Color.green);
        saveHistoryButton.setOpaque(true);
        saveHistoryButton.setBorderPainted(false);
        saveHistoryButton.setFocusPainted(false);
        saveHistoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        add(saveHistoryButton);
        saveHistoryButton.addActionListener(_ -> {
            try {
                fileManager.formatPaymentDate(fileName, daysUntilPayment, daysFromPayment, nextPayment, previousPayment, priceTariff, FormattedDateTime);
                JOptionPane.showMessageDialog(this, "Данные успешно сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(this, "История пуста для ресурса: MTS", "Информация", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JTextArea textArea = new JTextArea(20, 50);
                    textArea.setText(history);
                    textArea.setEditable(true);

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    // Создаем кнопку сохранения
                    JButton saveButton = new JButton("Сохранить");
                    saveButton.setFont(new Font("Arial", Font.BOLD, 14));
                    saveButton.setBackground(new Color(144, 238, 144)); // Светло-зеленый цвет

                    // Создаем панель для кнопки (чтобы выровнять по правому краю)
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    buttonPanel.add(saveButton);

                    // Создаем основную панель для содержимого
                    JPanel mainPanel = new JPanel(new BorderLayout());
                    mainPanel.add(scrollPane, BorderLayout.CENTER);
                    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

                    // Создаем диалоговое окно
                    JDialog dialog = new JDialog();
                    dialog.setTitle("История (МТС) - Редактирование");
                    dialog.setModal(true);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.getContentPane().add(mainPanel);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);

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






