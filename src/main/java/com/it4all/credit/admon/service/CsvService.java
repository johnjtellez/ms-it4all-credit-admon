package com.it4all.credit.admon.service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.it4all.credit.admon.service.progress.ProgressService;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvService<T> {

    private final JpaRepository<T, Long> repository;
    private final Class<T> type;

    @Autowired
    public CsvService(JpaRepository<T, Long> repository, Class<T> type) {
        this.repository = repository;
        this.type = type;
    }

	@Autowired
	private ProgressService progressSvr;

    @Async
    public void processCsv(Path filePath, String taskId) {
    	progressSvr.start(taskId);
        List<T> entities = new ArrayList<>();
        try (BufferedReader fileReader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.Builder.create()
                     .setHeader(getHeaders())
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(fileReader)) {

            List<CSVRecord> csvRecords = csvParser.getRecords();
            int totalRecords = csvRecords.size();
            int currentRecord = 0;

            for (CSVRecord csvRecord : csvRecords) {
                T entity = type.getDeclaredConstructor().newInstance();
                Field[] fields = type.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String value = csvRecord.get(field.getName());
                    if (value != null) {
                        if (field.getType() == Integer.class) {
                            field.set(entity, Integer.parseInt(value));
                        } else if (field.getType() == Byte.class) {
                            field.set(entity, Byte.parseByte(value));
                        } else {
                            field.set(entity, value);
                        }
                    }
                }
                entities.add(entity);
                currentRecord++;
                // Actualiza avance cada 300 registros
                if (currentRecord % 300 == 0) {
                	progressSvr.update(taskId, (currentRecord * 100) / totalRecords);
            	}
            }

            repository.saveAll(entities);
            progressSvr.finish(taskId);
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    public void exportCsv(Writer writer) {
        List<T> entities = repository.findAll();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(getHeaders()))) {
            for (T entity : entities) {
                csvPrinter.printRecord(getValues(entity));
            }
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("Error while exporting CSV file: " + e.getMessage());
        }
    }

    private String[] getHeaders() {
        // Obtener todos los campos excepto los que están anotados con @ManyToOne o @JoinColumn
        List<String> headers = Arrays.stream(type.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(ManyToOne.class) && !field.isAnnotationPresent(JoinColumn.class))
            .map(Field::getName)
            .collect(Collectors.toList());

        return headers.toArray(new String[0]);
    }

    private Object[] getValues(T entity) throws IllegalAccessException {
        // Obtener los valores de los campos excepto los que están anotados con @ManyToOne o @JoinColumn
        List<Object> values = Arrays.stream(type.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(ManyToOne.class) && !field.isAnnotationPresent(JoinColumn.class))
            .map(field -> {
                field.setAccessible(true);
                try {
                    return field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        return values.toArray();
    }
}
