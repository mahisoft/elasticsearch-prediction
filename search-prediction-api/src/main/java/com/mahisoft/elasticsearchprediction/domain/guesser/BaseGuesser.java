package com.mahisoft.elasticsearchprediction.domain.guesser;

import com.mahisoft.elasticsearchprediction.domain.DataType;

public abstract class BaseGuesser {

    public abstract DataType guess(String data);

}
