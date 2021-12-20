package com.mamatzhanov.transformer.services;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface TransformerService {
    JsonNode transformerToJSON (String xmlValue) throws IOException;
    void saveJSONtoFile (JsonNode node) throws IOException;
}
