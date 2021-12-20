package com.mamatzhanov.transformer.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mamatzhanov.transformer.services.TransformerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/")
public class AcceptXML {

    private final TransformerService transformerService;

    @Autowired
    public AcceptXML(TransformerService transformerService) {
        this.transformerService = transformerService;
    }

    @PostMapping(consumes = (MediaType.APPLICATION_XML_VALUE))
    public ResponseEntity<HttpStatus> getXML(@RequestBody String xmlValue) {
        try {
            JsonNode node = transformerService.transformerToJSON(xmlValue);
            transformerService.saveJSONtoFile(node);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}