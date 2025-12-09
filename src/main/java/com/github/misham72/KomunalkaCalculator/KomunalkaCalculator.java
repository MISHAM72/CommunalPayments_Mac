 package com.github.misham72.KomunalkaCalculator;

//      Класс, занимающийся исключительно
//      расчетами расхода электроэнергии
public class KomunalkaCalculator {
    // Метод для расчета расхода электроэнергии
    public double calculateConsumption(double currentReading, double previousReading) {
        return currentReading - previousReading;
    }

    // Метод для расчета суммы оплаты
    public double calculatePayment(double consumption, double tariff) {
        return consumption * tariff;
    }
}
