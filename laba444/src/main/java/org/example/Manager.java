package org.example;

import org.apache.poi.ss.usermodel.Workbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class Manager {

    public void CalculateForCountries(String fileName, Workbook workbook) throws SQLException {
        ReactorHolder reactorHolder = new ReactorHolder();
        ReadDataFromDB readDataFromDB = new ReadDataFromDB();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + fileName)) {
            System.out.println("jdbc:sqlite:" + fileName);
            if (conn != null) {
                readDataFromDB.calculateConsumption(conn, reactorHolder);
                LinkedHashMap<String, LinkedHashMap<String, Double>> consumptionByCountry = readDataFromDB.calculateConsumptionByCountry(conn, reactorHolder);

                ExcelExporter.exportToExcel(consumptionByCountry, "Потребление по cтранам", workbook);
            }

        }
    }

    public void CalculateForRegion(String fileName, Workbook workbook) throws SQLException {
        ReactorHolder reactorHolder = new ReactorHolder();
        ReadDataFromDB readDataFromDB = new ReadDataFromDB();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + fileName)) {
            if (conn != null) {
                readDataFromDB.calculateConsumption(conn, reactorHolder);
                LinkedHashMap<String, LinkedHashMap<String, Double>> consumptionByRegion = readDataFromDB.calculateConsumptionByRegion(conn, reactorHolder);

                ExcelExporter.exportToExcel(consumptionByRegion, "Потребление по регионам", workbook);
            }

        }
    }

    public void CalculateForCompany(String fileName, Workbook workbook) throws SQLException {
        ReactorHolder reactorHolder = new ReactorHolder();
        ReadDataFromDB readDataFromDB = new ReadDataFromDB();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + fileName)) {
            if (conn != null) {
                readDataFromDB.calculateConsumption(conn, reactorHolder);
                LinkedHashMap<String, LinkedHashMap<String, Double>> consumptionByCompany = readDataFromDB.calculateConsumptionByCompany(conn, reactorHolder);

                ExcelExporter.exportToExcel(consumptionByCompany, "Потребление по компаниям", workbook);
            }

        }
    }

}

