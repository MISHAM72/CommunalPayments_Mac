package com.github.misham72.KomunalkaApp;

import com.github.misham72.KomunalkaCalculator.KomunalkaCalculator;
import com.github.misham72.KomunalkaFileManager.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ElectricityApp extends JPanel {

	private final KomunalkaCalculator calculator;
	private final FileManager fileManager;
    private final String fileName = FileManager.getFilePath("Свет.txt");

	public ElectricityApp() {
		this.calculator = new KomunalkaCalculator();
		this.fileManager = new FileManager();


			setLayout(new GridLayout(7, 2, 10, 10));
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Добавляем отступы

			// Компоненты интерфейса
			JTextField currentDataField = new JTextField();
			JTextField previousDataField = new JTextField();
			JTextField tariffField = new JTextField();

			JLabel consumptionLabel = new JLabel("Расход: -");
			consumptionLabel.setFont(new Font("Arial", Font.BOLD, 16));
			consumptionLabel.setForeground(Color.red);

			JLabel paymentLabel = new JLabel("К оплате: -");
			paymentLabel.setFont(new Font("Arial", Font.BOLD, 16));
			paymentLabel.setForeground(Color.red);

			JLabel dateTimeLabel = new JLabel("Дата и время последней операции: -");
			dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));

			JButton calculateButton = new JButton("Рассчитать");
			calculateButton.setBackground(Color.green);
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

			currentDataField.addActionListener(_ -> previousDataField.requestFocus());
			previousDataField.addActionListener(_ -> tariffField.requestFocus());
			tariffField.addActionListener(_ -> calculateButton.doClick()); //  <-- Имитируем клик по кнопке

			calculateButton.addActionListener(_ -> {
				try {
					double currentReading = Double.parseDouble(currentDataField.getText());
					double previousReading = Double.parseDouble(previousDataField.getText());
					double tariff = Double.parseDouble(tariffField.getText());

					// Производим расчёты
					double consumption = calculator.calculateConsumption(currentReading, previousReading);
					double payment = calculator.calculatePayment(consumption, tariff);

					String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
					consumptionLabel.setText(String.format("Расход: %.2f", consumption));
					paymentLabel.setText(String.format("К оплате: %.2f руб.", payment));
					dateTimeLabel.setText("Дата и время последней операции: " + formattedDateTime);

					String unit = "кВт.";

					fileManager.formatMeterReadingPaymentData(fileName, currentReading, previousReading, consumption, tariff, payment, unit, formattedDateTime);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "Введите корректные числа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
				}catch (IOException ex) {
					JOptionPane.showMessageDialog(this, "Ошибка при записи в файл: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
			});

			// Логика кнопки "Показать историю"
			showHistoryButton.addActionListener(_ -> {
				try {
					String history = fileManager.loadFromFile(fileName);
					if (history.isEmpty()) {
						JOptionPane.showMessageDialog(this, "История пуста для ресурса: Свет", "Информация", JOptionPane.INFORMATION_MESSAGE);
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
						dialog.setTitle("История (Свет) - Редактирование");
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