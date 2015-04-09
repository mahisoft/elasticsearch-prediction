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
import static com.mahisoft.elasticsearchprediction.domain.DataType.STRING;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.mahisoft.elasticsearchprediction.domain.DataType;
import com.mahisoft.elasticsearchprediction.plugin.domain.IndexAttributeDefinition;
import com.mahisoft.elasticsearchprediction.plugin.domain.IndexValue;
import com.mahisoft.elasticsearchprediction.plugin.exception.FileModelException;
import com.mahisoft.elasticsearchprediction.plugin.exception.PredictionException;

public class WekaPredictorEngineTest {

	private static final Logger LOGGER = LogManager.getLogger(WekaPredictorEngineTest.class);

	private static final String EXCEPTION_MSG = "Expecting exception";

	private static final String TEST_MODEL = "src/test/resources/test.model";

	private WekaPredictorEngine wekaPredictorEngine;

	private String[] attributes = { "age", "workclass", "fnlwgt", "education", "education_num", "marital_status",
			"occupation", "relationship", "race", "sex", "capital_gain", "capital_loss", "hours-per-week",
			"native-country" };

	private Object[] instance = { 39d, "State-gov", 77516d, "Bachelors", 13d, "Never-married", "Adm-clerical",
			"Not-in-family", "White", "Male", 2174d, 0d, 40d, "United-States" };

	private DataType[] dataTypes = { DOUBLE, STRING, DOUBLE, STRING, DOUBLE, STRING, STRING, STRING, STRING, STRING,
			DOUBLE, DOUBLE, DOUBLE, STRING };

	@Test
	public void ifModelIsNullThenCreateWekaPredictorEngineThrowsFileModelException() {
		try {
			wekaPredictorEngine = new WekaPredictorEngine(null);
			fail(EXCEPTION_MSG);
		} catch (FileModelException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifModelIsNotNullThenCreateWekaPredictorEngineIsSuccess() {
		try {
			wekaPredictorEngine = new WekaPredictorEngine(TEST_MODEL);
		} catch (FileModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifInstanceIsNullThenPredictionThrowsPredictionException() {
		try {
			wekaPredictorEngine = new WekaPredictorEngine(TEST_MODEL);
			wekaPredictorEngine.getPrediction(null);
			fail(EXCEPTION_MSG);
		} catch (FileModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		} catch (PredictionException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifInstanceIsNotValidThenPredictionThrowsPredictionException() {
		try {
			wekaPredictorEngine = new WekaPredictorEngine(TEST_MODEL);
			double result = wekaPredictorEngine.getPrediction(getNoValidInstance());
			LOGGER.error(result);
		} catch (FileModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		} catch (PredictionException e) {
			LOGGER.info(e);
		}
	}
	
	@Test
	public void ifInstanceIsValidThenPredictionIsSuccess() {
		try {
			wekaPredictorEngine = new WekaPredictorEngine(TEST_MODEL);
			wekaPredictorEngine.getPrediction(getInstance());
		} catch (FileModelException | PredictionException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	private Collection<IndexValue> getInstance() {
		Collection<IndexValue> values = new ArrayList<IndexValue>();

		for (int i = 0; i < dataTypes.length; i++) {
			values.add(new IndexValue(new IndexAttributeDefinition(attributes[i], dataTypes[i]), instance[i]));
		}

		return values;
	}
	
	private Collection<IndexValue> getNoValidInstance() {
		Collection<IndexValue> values = new ArrayList<IndexValue>();

		for (int i = 0; i < 5; i++) {
			values.add(new IndexValue(new IndexAttributeDefinition(attributes[i], dataTypes[i]), instance[i]));
		}

		return values;
	}
}
