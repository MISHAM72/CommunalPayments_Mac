package com.github.misham72.KomunalkaApp;

import javax.swing.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateCalculator extends JPanel {

    /**
     * Вычисляет следующую дату платежа.
     * Если день платежа в текущем месяце еще не наступил (или сегодня), то это и есть дата.
     * Если уже прошел, то дата сдвигается на 1 период вперед.
     */
    public static LocalDate getNextPaymentDate(int monthsPeriod, int paymentDay) {
        LocalDate today = LocalDate.now();

        // Попробуем получить дату платежа в текущем месяце.
        // Используем min, чтобы не упасть, если в месяце меньше дней, чем paymentDay.
        int currentMonthLength = today.lengthOfMonth();
        int day = Math.min(paymentDay, currentMonthLength);

        LocalDate candidateDate = today.withDayOfMonth(day);

        if (candidateDate.isAfter(today) || candidateDate.isEqual(today)) {
            return candidateDate;
        } else {
            // Если дата в этом месяце уже прошла, следующая будет через период (например, через 1 месяц.)
            return candidateDate.plusMonths(monthsPeriod);
        }
    }

    /**
     * Вычисляет предыдущую дату платежа.
     * Логика обратная getNextPaymentDate: берем следующую и отнимаем период.
     */
    public static LocalDate getPreviousPaymentDate(int monthsPeriod, int paymentDay) {
        return getNextPaymentDate(monthsPeriod, paymentDay).minusMonths(monthsPeriod);
    }

    public static long calculateDaysToNextPayment(int monthsPeriod, int paymentDay) {
        LocalDate today = LocalDate.now();
        LocalDate nextPayment = getNextPaymentDate(monthsPeriod, paymentDay);
        return ChronoUnit.DAYS.between(today, nextPayment);
    }

    public static long calculateDaysFromPreviousPayment(int monthsPeriod, int paymentDay) {
        LocalDate today = LocalDate.now();
        LocalDate previousPayment = getPreviousPaymentDate(monthsPeriod, paymentDay);
        return ChronoUnit.DAYS.between(previousPayment, today);
    }
}