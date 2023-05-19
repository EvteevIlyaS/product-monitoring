package com.ilyaevteev.productmonitoring.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVHelper {
    public static final String[] PRODUCT_HEADERS = {"name", "categoryId"};
    public static final String[] PRODUCT_PRICE_HEADERS = {"storeId", "productId", "price"};
    private static final String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Map<String, String>> csvToEntities(InputStream is, String[] fields) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<Map<String, String>> entities = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            csvRecords.forEach(csvRecord -> {
                Map<String, String> entity = new HashMap<>();
                for (String field: fields) {
                    entity.put(field, csvRecord.get(field));
                }
                entities.add(entity);
            });

            return entities;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse CSV file");
        }
    }
}
