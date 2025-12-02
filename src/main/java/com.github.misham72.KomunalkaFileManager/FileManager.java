package com.github.misham72.KomunalkaFileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

public class FileManager {
    // "/Users/mikhail/Documents/KomunalkaData/filename.txt"
    public static String getFilePath(String filename) {
        String userHome = System.getProperty("user.home");
        File documentsDir = new File(userHome, "Documents");
        File appDir = new File(documentsDir, "KomunalkaData");

        // Создаем папку один раз, если её нет
        if (!appDir.exists()) {
            if (!appDir.mkdirs()) {
                System.err.println("Не удалось создать папку данных: " + appDir.getAbsolutePath());
            }
        }
        return new File(appDir, filename).getAbsolutePath();
    }
    // -------------------

	public void formatPaymentDate(String fileName, long daysUntilPayment, long daysFromPayment,
									LocalDate nextPayment, LocalDate previousPayment,
									long priceTariff, String formattedDateTime) throws IOException {




		String format = String.format(
						"""
						Услуга: %s
						--------------------------------------------------------------------
						Предыдущая оплата:               - %s
						Дата оплаты:                     - %s
						Оплата через:                    - %d дней
						С момента оплаты прошло:         - %d дней
						Стоимость тарифа:                - %d руб.
						--------------------------------------------------------------
						   ////////////////////////////////////////////////////////////////////////////
						(%s)
						""",
				fileName,
				previousPayment,
				nextPayment, daysUntilPayment, daysFromPayment,
				priceTariff, formattedDateTime);
		Files.writeString(Paths.get(fileName), format,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND);
	}


	public void formatMeterReadingPaymentData(
			String fileName,
			double currentReading,
			double previousReading,
			double consumption,
			double tariff,
			double payment,
			String unit,
			String formattedDateTime) throws IOException {

		String format = String.format(
						                    """
						                    Услуга   - %s
						                    --------------------------------------------------------------
						                    Текущие показания:  - %.2f %s
						                    Предыдущие показания:  - %.2f %s
						                    Расход:              - %.2f %s
						                    Тариф:        - %.2f руб. / %s
						                    Сумма оплаты:- %.2f руб.
						                    --------------------------------------------------------------
						                    ( %s)
						                    =========================================================================//
						""",
				fileName,
				currentReading, unit,
				previousReading, unit,
				consumption, unit,
				tariff, unit,
				payment, formattedDateTime);
		Files.writeString(Paths.get(fileName), format,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND);
	}

public void textWindow(String fileName, String text) throws IOException {
    Files.writeString(Paths.get(fileName), text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
}

public String loadFromFile(String fileName) throws IOException {
	return Files.readString(Paths.get(fileName));
}
}


