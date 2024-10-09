package org.example;

import javax.swing.*;
import java.sql.*;
import java.util.*;

public class ReadDataFromDB {


    public static void calculateConsumption(Connection conn, ReactorHolder reactorHolder) {
        String[] years = {"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024"};

        try (Statement reactorStmt = conn.createStatement();
             ResultSet reactors = reactorStmt.executeQuery("SELECT reactorName, thermalcapacity, type FROM reactor")) {

            while (reactors.next()) {
                String reactorName = reactors.getString("reactorName");
                double thermalCapacity = reactors.getDouble("thermalcapacity");
                String type = reactors.getString("type");

                double burnup = getBurnup(type);

                for (String year : years) {
                    double loadFactor = getLoadFactor(conn, reactorName, year);
                    double consumption = (thermalCapacity * loadFactor) / burnup;
                    reactorHolder.addConsumption(reactorName, year, consumption);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Структура БД не соответствует", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static double getBurnup(String type) throws SQLException {
        JSONReader jsonReader = new JSONReader();
        Map<String, Double> burnupMap = jsonReader.getReactorDataMap();

        for (Map.Entry<String, Double> entry : burnupMap.entrySet()) {
            if (entry.getKey().equals(type)) {
                return entry.getValue();
            }
        }


        return 10.0; // Для двух реакторов, у которых не было указано
    }

    private static double getLoadFactor(Connection conn, String reactorName, String year) throws SQLException {
        String queryLoadFactor = "SELECT consumption FROM loadfactor WHERE reactorName = ? AND year = ?";
        PreparedStatement pstmt = conn.prepareStatement(queryLoadFactor);
        pstmt.setString(1, reactorName);
        pstmt.setString(2, year);
        ResultSet loadFactorResult = pstmt.executeQuery();

        double loadFactor = 0.0;
        if (loadFactorResult.next()) {
            loadFactor = loadFactorResult.getDouble("consumption");
        }

        return loadFactor / 100;
    }

    public static LinkedHashMap<String, LinkedHashMap<String, Double>> calculateConsumptionByCountry(Connection conn, ReactorHolder reactorHolder) {
        LinkedHashMap<String, LinkedHashMap<String, Double>> consumptionByCountry = new LinkedHashMap<>();

        String[] years = {"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024"};

        try (Statement countryStmt = conn.createStatement();
             ResultSet countries = countryStmt.executeQuery("SELECT country FROM country")) {

            while (countries.next()) {
                String country = countries.getString("country");
                LinkedHashMap<String, Double> yearConsumption = new LinkedHashMap<>();

                String query = "SELECT reactorName FROM reactor WHERE country = ?";
                try (PreparedStatement reactorStmt = conn.prepareStatement(query)) {
                    reactorStmt.setString(1, country);
                    ResultSet reactors = reactorStmt.executeQuery();

                    List<String> reactorNames = new ArrayList<>();
                    while (reactors.next()) {
                        String reactorName = reactors.getString("reactorName");
                        reactorNames.add(reactorName);
                    }

                    for (String year : years) {
                        double totalConsumption = 0.0;
                        for (String reactorName : reactorNames) {
                            HashMap<String, Double> reactorData = reactorHolder.getReactorData(reactorName);
                            if (reactorData.containsKey(year)) {
                                totalConsumption += reactorData.get(year);
                            }
                        }
                        yearConsumption.put(year, totalConsumption);
                    }
                }

                consumptionByCountry.put(country, yearConsumption);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consumptionByCountry;
    }


    public static LinkedHashMap<String, LinkedHashMap<String, Double>> calculateConsumptionByRegion(Connection conn, ReactorHolder reactorHolder) {
        LinkedHashMap<String, LinkedHashMap<String, Double>> consumptionByRegion = new LinkedHashMap<>();

        String[] years = {"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024"};

        try (Statement regionStmt = conn.createStatement();
             ResultSet regions = regionStmt.executeQuery("SELECT region FROM region")) {

            while (regions.next()) {
                String region = regions.getString("region");
                LinkedHashMap<String, Double> yearConsumption = new LinkedHashMap<>();

                String query = "SELECT country FROM country WHERE region = ?";
                try (PreparedStatement countryStmt = conn.prepareStatement(query)) {
                    countryStmt.setString(1, region);
                    ResultSet countries = countryStmt.executeQuery();

                    List<String> countryNames = new ArrayList<>();
                    while (countries.next()) {
                        String countryName = countries.getString("country");
                        countryNames.add(countryName);
                    }

                    for (String year : years) {
                        double totalConsumption = 0.0;
                        for (String countryName : countryNames) {
                            LinkedHashMap<String, Double> countryData = calculateConsumptionByCountry(conn, reactorHolder).get(countryName);
                            if (countryData != null && countryData.containsKey(year)) {
                                totalConsumption += countryData.get(year);
                            }
                        }
                        yearConsumption.put(year, totalConsumption);
                    }
                }

                consumptionByRegion.put(region, yearConsumption);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consumptionByRegion;
    }

    public static LinkedHashMap<String, LinkedHashMap<String, Double>> calculateConsumptionByCompany(Connection conn, ReactorHolder reactorHolder) {
        LinkedHashMap<String, LinkedHashMap<String, Double>> consumptionByCountry = new LinkedHashMap<>();

        String[] years = {"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024"};

        try (Statement companyStmt = conn.createStatement();
             ResultSet companies = companyStmt.executeQuery("SELECT company FROM company")) {

            while (companies.next()) {
                String company = companies.getString("company");
                LinkedHashMap<String, Double> yearConsumption = new LinkedHashMap<>();

                String query = "SELECT reactorName FROM reactor WHERE owner = ?";
                try (PreparedStatement reactorStmt = conn.prepareStatement(query)) {
                    reactorStmt.setString(1, company);
                    ResultSet reactors = reactorStmt.executeQuery();

                    List<String> reactorNames = new ArrayList<>();
                    while (reactors.next()) {
                        String reactorName = reactors.getString("reactorName");
                        reactorNames.add(reactorName);
                    }

                    for (String year : years) {
                        double totalConsumption = 0.0;
                        for (String reactorName : reactorNames) {
                            LinkedHashMap<String, Double> reactorData = reactorHolder.getReactorData(reactorName);
                            if (reactorData.containsKey(year)) {
                                totalConsumption += reactorData.get(year);
                            }
                        }
                        yearConsumption.put(year, totalConsumption);
                    }
                }

                consumptionByCountry.put(company, yearConsumption);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consumptionByCountry;
    }
}


