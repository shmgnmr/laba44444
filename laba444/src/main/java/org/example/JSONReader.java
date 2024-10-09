package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class JSONReader {

    private Map<String, Double> reactorDataMap;

    public JSONReader() {
        reactorDataMap = new HashMap<>();
        readFile();
        reactorDataMap.put("GCR", 20.0);
        reactorDataMap.put("FBR", 140.0);
        reactorDataMap.put("HTGR", 70.0);
        reactorDataMap.put("HWDCR", 25.0);
        reactorDataMap.put("LWGR", 20.0);
        reactorDataMap.put("SGHWR", 8.0);
    }

    private void readFile() {
        JSONParser parser = new JSONParser();

        try (InputStreamReader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("ReactorType.json"))) {
            if (reader == null) {
                throw new IOException("Файл reactor.json не найден в папке resources");
            }

            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            for (Object key : jsonObject.keySet()) {
                String reactorName = (String) key;
                Object reactorData = jsonObject.get(reactorName);

                if (reactorData instanceof JSONObject) {
                    parseData((JSONObject) reactorData);
                } else {
                    System.out.println("Ошибка чтения данных: " + reactorName);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.out.println("Ошибка преобразования типа данных в JSON объект: " + e.getMessage());
        }
    }

    private void parseData(JSONObject reactorData) {
        String reactorClass = (String) reactorData.get("class");
        Double burnup = getValueAsDouble(reactorData, "burnup");

        if (reactorClass != null && burnup != null) {
            reactorDataMap.put(reactorClass, burnup);
        }
    }

    private Double getValueAsDouble(JSONObject reactorData, String key) {
        Number value = (Number) reactorData.get(key);
        return value != null ? value.doubleValue() : null;
    }

    public Map<String, Double> getReactorDataMap() {
        return reactorDataMap;
    }


}
