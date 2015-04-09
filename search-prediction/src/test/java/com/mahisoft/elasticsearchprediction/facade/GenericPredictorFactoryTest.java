package com.mahisoft.elasticsearchprediction.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mahisoft.elasticsearchprediction.plugin.engine.PredictorEngine;
import com.mahisoft.elasticsearchprediction.plugin.engine.weka.WekaPredictorEngine;
import com.mahisoft.elasticsearchprediction.plugin.exception.FileModelException;
import com.mahisoft.elasticsearchprediction.plugin.utils.PluginProperties;

public class GenericPredictorFactoryTest {

	private static final Logger LOGGER = LogManager.getLogger(GenericPredictorFactoryTest.class);

	private static final String WEKA_TEST_MODEL = "src/test/resources/weka/test.model";

	@Mock
	private PluginProperties pluginProperties;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void ifClassifierLibIsNullThenReturnNull() {
		try {
			when(pluginProperties.getClassifier()).thenReturn(null);
			PredictorEngine predictorEngine = GenericPredictorFactory.getPredictor(pluginProperties);
			assertNull(predictorEngine);
		} catch (FileModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifClassifierLibIsNotValidThenReturnNull() {
		try {
			when(pluginProperties.getClassifier()).thenReturn("lib");
			PredictorEngine predictorEngine = GenericPredictorFactory.getPredictor(pluginProperties);
			assertNull(predictorEngine);
		} catch (FileModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifClassifierLibIsWekaThenReturnWekaPredictorEngine() {
		try {
			when(pluginProperties.getClassifier()).thenReturn("weka");
			when(pluginProperties.getModelPath()).thenReturn(WEKA_TEST_MODEL);

			PredictorEngine predictorEngine = GenericPredictorFactory.getPredictor(pluginProperties);
			assertEquals(predictorEngine.getClass(), WekaPredictorEngine.class);
		} catch (FileModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}
}
