package com.ilyaevteev.productmonitoring.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ExcelHelper {
    public static final String[] PRODUCT_HEADERS = {"name", "categoryId"};
    public static final String[] PRODUCT_PRICE_HEADERS = {"storeId", "productId", "price"};
    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final DataFormatter dataFormatter = new DataFormatter();

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Map<String, String>> excelToEntities(InputStream is, String[] fields) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Iterator<Row> rows = workbook.getSheetAt(0).rowIterator();
            List<Map<String, String>> entities = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                Map<String, String> entity = new HashMap<>();
                for (String field: fields) {
                    entity.put(field, dataFormatter.formatCellValue(cellsInRow.next()));
                }
                entities.add(entity);
            }

            return entities;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse Excel file");
        }
    }
}
