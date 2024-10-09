package org.example;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GUI extends JFrame {
    private File selectedFile;
    private Manager calculation;

    public GUI() {
        setTitle("Лабораторная работа 4");
        setSize(350, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JButton chooseDatabaseButton = new JButton("Выбрать базу данных");
        chooseDatabaseButton.setBounds(50, 50, 220, 30);

        JButton calculateCountryButton = new JButton("Рассчитать для стран");
        calculateCountryButton.setBounds(50, 100, 220, 30);

        JButton calculateRegionButton = new JButton("Рассчитать для регионов");
        calculateRegionButton.setBounds(50, 150, 220, 30);

        JButton calculateCompanyButton = new JButton("Рассчитать для компаний");
        calculateCompanyButton.setBounds(50, 200, 220, 30);

        JButton goodBye = new JButton("Завершение работы");
        goodBye.setBounds(50, 250, 220, 30);

        add(chooseDatabaseButton);
        add(calculateCountryButton);
        add(calculateRegionButton);
        add(calculateCompanyButton);
        add(goodBye);

        calculation = new Manager();

        chooseDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFile = new File(chooseDatabaseFile());
                if (selectedFile != null) {
                    System.out.println("Выбранный файл базы данных: " + selectedFile.getAbsolutePath());
                } else {
                    System.out.println("Файл базы данных не выбран или не существует.");
                }
            }
        });

        goodBye.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        calculateCountryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    executeCalculation("...", "countries_consumption.xlsx", workbook -> {
                        calculation.CalculateForCountries(selectedFile.getAbsolutePath(), workbook);
                    });
                } else {
                    System.out.println("Файл базы данных не выбран.");
                }
            }
        });

        calculateRegionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    executeCalculation("...", "regions_consumption.xlsx", workbook -> {
                        calculation.CalculateForRegion(selectedFile.getAbsolutePath(), workbook);
                    });
                } else {
                    System.out.println("Файл базы данных не выбран.");
                }
            }
        });

        calculateCompanyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    executeCalculation("...", "companies_consumption.xlsx", workbook -> {
                        calculation.CalculateForCompany(selectedFile.getAbsolutePath(), workbook);
                    });
                } else {
                    System.out.println("Файл базы данных не выбран.");
                }
            }
        });
    }

    private void executeCalculation(String message, String outputFileName, CalculationTask task) {
        new Thread(() -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                task.execute(workbook);
                String outputPath = System.getProperty("user.dir") + System.getProperty("file.separator") + outputFileName;
                try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
                    workbook.write(fileOut);
                    System.out.println(outputPath);
                }

                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Расчёт выполнен успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE));
            } catch (SQLException | IOException ep) {
                ep.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Ошибка при выполнении расчёта.", "Ошибка", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private String chooseDatabaseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SQLite Database Files", "sqlite");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists() && selectedFile.getName().endsWith(".sqlite")) {
                return selectedFile.getAbsolutePath();
            } else {
                JOptionPane.showMessageDialog(null, "Не подходящий файл. Пожалуйста, выберите файл с расширением .sqlite", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    interface CalculationTask {
        void execute(Workbook workbook) throws SQLException, IOException;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}

