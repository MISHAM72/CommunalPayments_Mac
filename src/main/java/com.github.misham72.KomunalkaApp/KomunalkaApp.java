
package com.github.misham72.KomunalkaApp;

import javax.swing.*;
import java.awt.*;


public class KomunalkaApp {


    public void run() {
        System.out.println("Создаем окно приложения...");

        // Инициализируем фрейм
        JFrame frame = new JFrame("Communal Payments");
        frame.setSize(1200, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Создаём панели вкладок
        JTabbedPane tabbedPane = new JTabbedPane();
        // Добавляем вкладки (импорты классов для каждой панели)

        tabbedPane.addTab("Свет", new ElectricityApp());
        tabbedPane.addTab("Газ", new GasApp());
        tabbedPane.addTab("Вода", new WaterApp());
        tabbedPane.addTab("ZONT", new ZONTsimApp());
        tabbedPane.addTab("Мусор", new GarbageApp());
        tabbedPane.addTab("Роутер", new RouterApp());
        tabbedPane.addTab("MTC", new MTSsimApp());
        tabbedPane.addTab("Тинькофф", new TINKOFFsimApp());
        tabbedPane.addTab("ИП-налоги", new TaxesIP());
        tabbedPane.addTab("Страхование ОСАГО ", new OsagoAvtoApp());
        tabbedPane.addTab("Troyka", new TroykaApp());
        // Добавляем TabbedPane в окно
        frame.add(tabbedPane, BorderLayout.CENTER);
        // Отображаем фрейм
        System.out.println("Отображаем окно...");
        frame.setVisible(true);
    }
}
