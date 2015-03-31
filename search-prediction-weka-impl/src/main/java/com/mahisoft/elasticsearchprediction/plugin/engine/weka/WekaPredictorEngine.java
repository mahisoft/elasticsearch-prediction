/*====================================================================================
    Copyright(c) 2015 Mahisoft Inc. (Yessika Labrador, Federico Ponte and Joaquin Delgado)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.


    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

    See the GNU General Public License <http://www.gnu.org/licenses/> for more details.
======================================================================================*/

package com.mahisoft.elasticsearchprediction.plugin.engine.weka;

import static com.mahisoft.elasticsearchprediction.domain.DataType.DOUBLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import com.mahisoft.elasticsearchprediction.plugin.domain.IndexValue;
import com.mahisoft.elasticsearchprediction.plugin.engine.PredictorEngine;
import com.mahisoft.elasticsearchprediction.plugin.exception.FileModelException;
import com.mahisoft.elasticsearchprediction.plugin.exception.PredictionException;

public class WekaPredictorEngine implements PredictorEngine {

	private static final ESLogger LOGGER = Slf4jESLoggerFactory.getLogger(PredictorEngine.class.getSimpleName());

	private static AbstractClassifier predictor = null;

	public WekaPredictorEngine(String modelPath) throws FileModelException {
		LOGGER.info(this.getClass() + " class is initialized");
		loadModel(modelPath);
	}

	public synchronized double getPrediction(Collection<IndexValue> values) throws PredictionException {
		Instance instance = createInstance(values);

		return predict(predictor, instance);
	}

	public Instance createInstance(Collection<IndexValue> values) {
		Instance instance = new DenseInstance(values.size() + 1);
		Instances dataSet = createDataSet(values);
		dataSet.setClassIndex(dataSet.numAttributes() - 1);

		instance.setDataset(dataSet);
		int i = 0;
		for (IndexValue value : values) {
			if (value.getDefinition().getType() == DOUBLE) {
				instance.setValue(i, (Double) value.getValue());
			} else {
				instance.setValue(i, (String) value.getValue());
			}
			i++;
		}

		return instance;
	}

	public Instances createDataSet(Collection<IndexValue> values) {
		List<Attribute> attributes = new ArrayList<Attribute>(values.size());

		for (IndexValue value : values) {
			if (value.getDefinition().getType() == DOUBLE) {
				attributes.add(new Attribute(value.getDefinition().getName()));
			} else {
				List<String> nominal = new ArrayList<String>(1);
				nominal.add(value.getValue().toString());

				attributes.add(new Attribute(value.getDefinition().getName(), nominal));
			}
		}

		attributes.add(new Attribute("target"));

		return new Instances("dataset", (ArrayList<Attribute>) attributes, 0);
	}

	public double predict(AbstractClassifier predictor, Instance instance) throws PredictionException {
		try {
			return predictor.classifyInstance(instance);
		} catch (Exception e) {
			LOGGER.error(instance.toString(), e);
			throw new PredictionException(e);
		}
	}

	public void loadModel(String modelPath) throws FileModelException {
		try {
			LOGGER.info("Loading model " + modelPath);
			predictor = (AbstractClassifier) weka.core.SerializationHelper.read(modelPath);
		} catch (Exception e) {
			String message = "Problem loading model";
			LOGGER.error(message, e);
			throw new FileModelException(message);
		}
	}

}
