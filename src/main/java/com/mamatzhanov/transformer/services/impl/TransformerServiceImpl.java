package com.mamatzhanov.transformer.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mamatzhanov.transformer.services.TransformerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.time.LocalDate;

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

        StringBuilder sb = new StringBuilder();
        long countRecords = getCountRecords(filePath);
        String line;
        boolean flag = true;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            while ((line = br.readLine()) != null) {
                if (flag) {
                    sb.append("Records: ").append(countRecords).append(System.lineSeparator());
                    flag = false;
                    continue;
                }
                sb.append(line).append(System.lineSeparator());
            }
            sb.append(node).append(System.lineSeparator());
            log.info("Add record to file [{}] {}", fileName, node);
            log.info("Records number: {}", countRecords);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        FileWriter fw = new FileWriter(filePath.toFile());
        fw.write(sb.toString());
        fw.close();
    }

    private long getCountRecords(Path path) {
        long lines = 0L;
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            log.error(e.getMessage());
            return 0;
        }
        return lines;
    }

}
