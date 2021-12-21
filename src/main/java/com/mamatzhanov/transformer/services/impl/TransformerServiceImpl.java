package com.mamatzhanov.transformer.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mamatzhanov.transformer.services.TransformerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
@Service
public class TransformerServiceImpl implements TransformerService {

    @Value("${file.path}")
    private String path;

    @Override
    public JsonNode transformerToJSON(String xmlValue) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readTree(xmlValue.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public synchronized void saveJSONtoFile(JsonNode node) throws IOException {
        String fileName = node.get("Type").asText() + "-" + LocalDate.now() + ".log";
        Path filePath = Paths.get(path + fileName);
        File file = filePath.toFile();
        if (!file.exists()) { file.createNewFile(); }

        Stream<String> stream = Files.lines(filePath);
        long countRecords = stream.filter(line -> line.startsWith("{")).count();
        stream.close();

        StringBuilder sb = new StringBuilder();

        if (countRecords == 0) { sb.append("Records: ").append(countRecords + 1).append(System.lineSeparator()).append(node).append(System.lineSeparator()); }
        else {
            sb.append("Records: ").append(countRecords + 1).append(System.lineSeparator());
            stream = Files.lines(filePath);
            stream.filter(line -> line.startsWith("{")).forEach(e -> {
                sb.append(e).append(System.lineSeparator());
            });
            sb.append(node).append(System.lineSeparator());
            log.info("Add record to file [{}] {}", fileName, node);
            log.info("Records number: {}", countRecords + 1);
        }

        FileWriter fw = new FileWriter(filePath.toFile());
        fw.write(sb.toString());
        fw.close();
    }
}