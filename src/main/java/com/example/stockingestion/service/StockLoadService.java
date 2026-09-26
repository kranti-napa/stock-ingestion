package com.example.stockingestion.service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.stockingestion.entity.StockPriceHistory;
import com.example.stockingestion.repository.StockRepository;

@Service
public class StockLoadService {

    private final StockRepository repo;

    public StockLoadService(StockRepository repo) {
        this.repo = repo;
    }

    public String load(MultipartFile[] files) throws Exception {

        for (MultipartFile file : files) {

            String symbol = extractStockSymbol(
                    file.getOriginalFilename());

            CSVParser parser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build()
                    .parse(new InputStreamReader(
                            file.getInputStream(),
                            StandardCharsets.UTF_8));

            for (CSVRecord record : parser) {

                Map<String, String> row = record.toMap();

                StockPriceHistory stock =
                        new StockPriceHistory();

                stock.setStockSymbol(symbol);
                stock.setSeries(
                        getValue(row, "Series"));
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("dd-MMM-yyyy");

                stock.setTradeDate(
                        LocalDate.parse(
                                getValue(row, "Date"),
                                formatter));

                stock.setOpenPrice(parseDouble(
                        getValue(row, "Open Price")));

                stock.setHighPrice(parseDouble(
                        getValue(row, "High Price")));

                stock.setLowPrice(parseDouble(
                        getValue(row, "Low Price")));

                stock.setClosePrice(parseDouble(
                        getValue(row, "Close Price")));

                repo.save(stock);
            }
        }

        return "Stock data loaded successfully";
    }

    private String extractStockSymbol(String fileName) {

        if (fileName == null) {
            return "UNKNOWN";
        }

        String cleanName = fileName
                .replace(".csv", "");

        String[] parts = cleanName.split("-");

        for (String part : parts) {

            String cleaned = part.trim();

            if (cleaned.matches("[A-Z]+")
                    && !cleaned.equalsIgnoreCase("TO")
                    && !cleaned.equalsIgnoreCase("ALL")
                    && !cleaned.equalsIgnoreCase("N")) {

                return cleaned.toUpperCase();
            }
        }

        return "UNKNOWN";
    }

    private String getValue(
            Map<String, String> row,
            String targetHeader) {

        for (String key : row.keySet()) {

            String cleanedKey = key
                    .replace("\"", "")
                    .replace("\uFEFF", "")
                    .trim();

            if (cleanedKey.equalsIgnoreCase(
                    targetHeader)) {

                return row.get(key);
            }
        }

        return "";
    }

    private Double parseDouble(String value) {

        if (value == null ||
                value.isBlank()) {

            return 0.0;
        }

        try {
            return Double.parseDouble(
                    value.replace(",", "")
                            .trim());

        } catch (Exception e) {
            return 0.0;
        }
    }
}