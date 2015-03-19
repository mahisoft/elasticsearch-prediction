package com.mahisoft.elasticsearchprediction.domain.guesser;

import com.mahisoft.elasticsearchprediction.domain.DataType;

public class BasicGuesser extends BaseGuesser {

    @Override
    public DataType guess(String attribute) {
        return isInteger(attribute) || isDouble(attribute) ? DataType.DOUBLE : DataType.STRING;
    }

    private boolean isInteger(String attribute) {
        try {
            Integer.parseInt(attribute);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String attribute) {
        try {
            Double.parseDouble(attribute);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
