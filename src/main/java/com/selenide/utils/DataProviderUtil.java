package com.selenide.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


  // Utility to provide test data from external files (JSON, CSV).
  // Keeps the test logic clean and maintainable.

public class DataProviderUtil {

    private static final String DATA_PATH = "src/test/resources/testdata/";


     // Loads test data from a JSON file into a List of Maps.
     // @param fileName Name of the JSON file (e.g., "loginData.json")
     // @return List of Map<String, String> representing test data

    public static List<Map<String, String>> getJsonData(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(
                    new File(DATA_PATH + fileName),
                    new TypeReference<List<Map<String, String>>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException(" Failed to load JSON test data: " + fileName, e);
        }
    }


     // Loads test data from a CSV file into a List of Maps.
     // Each row = one test data set.
     // @param fileName Name of the CSV file (e.g., "loginData.csv")
     // @return List of Map<String, String>

    public static List<Map<String, String>> getCsvData(String fileName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileReader reader = new FileReader(DATA_PATH + fileName);
             CSVParser csvParser = CSVParser.parse(reader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .setIgnoreEmptyLines(true)
                             .setTrim(true)
                             .build())) {

            for (CSVRecord record : csvParser) {
                dataList.add(new HashMap<>(record.toMap()));
            }

        } catch (IOException e) {
            throw new RuntimeException("⚠️ Failed to load CSV test data: " + fileName, e);
        }

        return dataList;
    }

}
