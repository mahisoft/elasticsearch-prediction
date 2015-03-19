package com.mahisoft.elasticsearchprediction.domain;

public enum DataType {
    DOUBLE, STRING;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

