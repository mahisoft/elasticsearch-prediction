package com.mahisoft.elasticsearchprediction.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mahisoft.elasticsearchprediction.classifier.GenericClassifier;
import com.mahisoft.elasticsearchprediction.classifier.weka.WekaGenericClassifier;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;

public class GenericClassifierFactoryTest {

	private static final String CLASSIFIER_LIB_LABEL = "classifier.lib";

	@Mock
	private DataProperties dataProperties;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void ifClassifierLibIsNullThenReturnNull() {
		when(dataProperties.getValue(CLASSIFIER_LIB_LABEL)).thenReturn(null);
		GenericClassifier genericClassifier = GenericClassifierFactory.getClassifier(dataProperties);
		assertNull(genericClassifier);
	}

	@Test
	public void ifClassifierLibIsNotValidThenReturnNull() {
		when(dataProperties.getValue(CLASSIFIER_LIB_LABEL)).thenReturn("lib");
		GenericClassifier genericClassifier = GenericClassifierFactory.getClassifier(dataProperties);
		assertNull(genericClassifier);
	}

	@Test
	public void ifClassifierLibIsWekaThenReturnWekaGenericClassifier() {
		when(dataProperties.getValue(CLASSIFIER_LIB_LABEL)).thenReturn("weka");
		GenericClassifier genericClassifier = GenericClassifierFactory.getClassifier(dataProperties);
		assertEquals(genericClassifier.getClass(), WekaGenericClassifier.class);
	}

}
