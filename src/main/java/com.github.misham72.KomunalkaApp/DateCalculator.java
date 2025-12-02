package com.github.misham72.KomunalkaApp;


import javax.swing.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;


public class DateCalculator extends JPanel {
	/* ....................................................................................................*/

	public static LocalDate getNextPaymentDate(int monthsPeriod, int paymentDay) {
		LocalDate today = LocalDate.now();
		return alignToPeriodEnd(today, monthsPeriod, paymentDay);
	}
	/* ....................................................................................................*/

	public static LocalDate getPreviousPaymentDate(int monthsPeriod, int paymentDay) {
		LocalDate today = LocalDate.now();
		return alignToPeriodStart(today, monthsPeriod, paymentDay);
	}
	/* *******************************************************************************************************/

	public static long calculateDaysToNextPayment(int monthsPeriod, int paymentDay) {
		LocalDate today = LocalDate.now();
		LocalDate nextPayment = alignToPeriodEnd(today, monthsPeriod, paymentDay);
		return ChronoUnit.DAYS.between(today, nextPayment);/* находим кол-во дней от сегодня до оплаты */
	}

	/* ....................................................................................................*/
    private static LocalDate alignToPeriodEnd(LocalDate today, int monthsPeriod, int paymentDay) {
        // 1. Сначала проверяем, не является ли это особым случаем (исключением)
        // Это заменяет "магические числа" на явную логику.
        LocalDate specialDate = getSpecialAnnualDeadline(today.getYear(), monthsPeriod, paymentDay);
        if (specialDate != null) {
            return specialDate;
        }

        // 2. Стандартная логика для обычных периодов
        int currentMonth = today.getMonthValue();/* текущий месяц */
        int currentYear = today.getYear();
        int startMonth = ((currentMonth - 1) / monthsPeriod) * monthsPeriod + 1;/* начало периода */
        int endMonth = startMonth + monthsPeriod - 1;

        return LocalDate.of(currentYear, endMonth, paymentDay);
    }
    /**
     * Метод для обработки нестандартных годовых платежей.
     * Вместо вычитания дней (minusDays(61)) мы явно указываем целевую дату.
     */
    private static LocalDate getSpecialAnnualDeadline(int year, int monthsPeriod, int paymentDay) {
        if (monthsPeriod == 12) {
            // Было: periodEnd.minusDays(61) от 27 декабря -> попадало на ~27 Октября.
            if (paymentDay == 27) {
                return LocalDate.of(year, Month.OCTOBER, 27);
            }
            // Было: periodEnd.plusDays(62) от 24 декабря -> попадало на ~24 Февраля следующего года.
            if (paymentDay == 24) {
                return LocalDate.of(year + 1, Month.FEBRUARY, 24);
            }
        }
        return null; // Не особый случай
    }

    /* ***************************************************************************************************** */
    /* ***************************************************************************************************** */

	public static long calculateDaysFromPreviousPayment(int monthsPeriod, int paymentDay) {
		LocalDate today = LocalDate.now();
		LocalDate previousPayment = alignToPeriodStart(today, monthsPeriod, paymentDay);
		return ChronoUnit.DAYS.between(previousPayment, today);
	}

	/*.................................................................................................*/
    private static LocalDate alignToPeriodStart(LocalDate today, int monthsPeriod, int paymentDay) {
        // 1. Проверяем особые случаи начала периода
        LocalDate specialStart = getSpecialStartDate(today, monthsPeriod, paymentDay);
        if (specialStart != null) {
            return specialStart;
        }

        // 2. Стандартная логика
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int startMonth = ((currentMonth - 1) / monthsPeriod) * monthsPeriod + 1; // Начало периода

        return LocalDate.of(currentYear, startMonth, 1);
    }

    /**
     * Обработка смещений начала периода.
     * Логика: если период начинается не с 1 числа месяца или смещен относительно календарного квартала.
     **/
    private static LocalDate getSpecialStartDate(LocalDate today, int monthsPeriod, int paymentDay) {
        int year = today.getYear();

        // Ежемесячные особые случаи
        if (monthsPeriod == 1) {
            // Было: minusDays(1) от 1-го числа -> Конец предыдущего месяца
            if (paymentDay == 30) {
                // Берем первое число текущего месяца и отступаем 1 день назад -> последний день пред. месяца.
                return LocalDate.of(year, today.getMonth(), 1).minusDays(1);
            }
            // Было: minusDays(8) -> 23-24 число предыдущего месяца
            if (paymentDay == 23) {
                return LocalDate.of(year, today.getMonth(), 1).minusDays(8);
            }
        }

        // Квартальные
        if (monthsPeriod == 3) {
            // Было: minusDays(1) -> Сдвиг квартала на 1 день назад?
            // Здесь нужно уточнение логики, но пока оставим эквивалент.
            int currentMonth = today.getMonthValue();
            int startMonth = ((currentMonth - 1) / monthsPeriod) * monthsPeriod + 1;
            return LocalDate.of(year, startMonth, 1).minusDays(1);
        }

        // Годовые особые случаи
        if (monthsPeriod == 12) {
            // Было: minusDays(66) от 1 января -> ~27 Октября предыдущего года.
            if (paymentDay == 27) {
                return LocalDate.of(year - 1, Month.OCTOBER, 27);
            }
            // Было: plusDays(54) от 1 января -> ~24 Февраля текущего года.
            if (paymentDay == 24) {
                return LocalDate.of(year, Month.FEBRUARY, 24);
            }
        }

        return null;
    }
}




































