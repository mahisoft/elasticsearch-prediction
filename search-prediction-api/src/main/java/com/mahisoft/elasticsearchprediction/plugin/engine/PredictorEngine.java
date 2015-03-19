package com.mahisoft.elasticsearchprediction.plugin.engine;

import java.util.Collection;

import com.mahisoft.elasticsearchprediction.plugin.domain.IndexValue;
import com.mahisoft.elasticsearchprediction.plugin.exception.PredictionException;

public interface PredictorEngine {

	public double getPrediction(Collection<IndexValue> values) throws PredictionException;
	
}
