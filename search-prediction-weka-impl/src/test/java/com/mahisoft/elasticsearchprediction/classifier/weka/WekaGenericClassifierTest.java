package com.mahisoft.elasticsearchprediction.classifier.weka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mahisoft.elasticsearchprediction.exception.FileLoadException;
import com.mahisoft.elasticsearchprediction.exception.ModelException;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;

public class WekaGenericClassifierTest {

	private static final Logger logger = LogManager.getLogger(WekaGenericClassifierTest.class);

	private static final String CLASSIFIER = "weka.classifiers.trees.RandomTree";

	private static final String PARAMS = "-K 0 -M 1.0 -V 0.001 -S 1 -U";

	private WekaGenericClassifier wekaGenericClassifier;

	@Mock
	private DataProperties dataProperties;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		wekaGenericClassifier = new WekaGenericClassifier();
		wekaGenericClassifier.setDataProperties(dataProperties);
	}

	@Test
	public void ifClassifierIsNullThenThrowsModelException() {
		try {
			when(dataProperties.getValue("weka.classifier.class")).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			fail("Expecting exception");
		} catch (ModelException e) {
			logger.error(e.getClass().getCanonicalName() + " " + e.getMessage());
		}
	}

	@Test
	public void ifClassifierIsNoValidThenThrowsModelException() {
		try {
			when(dataProperties.getValue("weka.classifier.class")).thenReturn("Clasificador");

			wekaGenericClassifier.loadClassifier();
			fail("Expecting exception");
		} catch (ModelException e) {
			logger.error(e.getClass().getCanonicalName() + " " + e.getMessage());
		}
	}

	@Test
	public void ifOptionsAreNullThenClassifierIsCreatedWithoutOptions() {
		try {
			when(dataProperties.getValue("weka.classifier.class")).thenReturn(CLASSIFIER);
			when(dataProperties.getValue("weka.classifier.options")).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			assertEquals(wekaGenericClassifier.getModel().getClass().getName(), CLASSIFIER);
			assertEquals(wekaGenericClassifier.getModel().getOptions().length, 8);
		} catch (ModelException e) {
			logger.error(e.getClass().getCanonicalName() + " " + e.getMessage());
		}
	}

	@Test
	public void ifOptionsAreNoValidThenClassifierIsCreatedWithoutOptions() {
		try {
			when(dataProperties.getValue("weka.classifier.class")).thenReturn(CLASSIFIER);
			when(dataProperties.getValue("weka.classifier.options")).thenReturn("-X 7 -Y 3 -Z 6");

			wekaGenericClassifier.loadClassifier();
			assertEquals(wekaGenericClassifier.getModel().getClass().getName(), CLASSIFIER);
			assertEquals(wekaGenericClassifier.getModel().getOptions().length, 8);
		} catch (ModelException e) {
			logger.error(e.getClass().getCanonicalName() + " " + e.getMessage());
		}
	}

	@Test
	public void classifierIsCreatedSuccess() {
		try {
			when(dataProperties.getValue("weka.classifier.class")).thenReturn(CLASSIFIER);
			when(dataProperties.getValue("weka.classifier.options")).thenReturn(PARAMS);

			wekaGenericClassifier.loadClassifier();
			assertEquals(wekaGenericClassifier.getModel().getClass().getName(), CLASSIFIER);
			assertEquals(wekaGenericClassifier.getModel().getOptions().length, 9);
		} catch (ModelException e) {
			logger.error(e.getClass().getCanonicalName() + " " + e.getMessage());
		}
	}

	@Test
	public void ifFileForLoadDataIsNullThenThrowsModelException() {
		try {
			wekaGenericClassifier.loadData(null);
			fail("Expecting exception");
		} catch (FileLoadException e) {
			logger.error(e.getClass().getCanonicalName() + " " + e.getMessage());
		}
	}

	@Ignore
	public void ifFlagArffIsNUllThenFileLoadIsSucess() {
		try {
			when(dataProperties.getValue("weka.data.saveArff")).thenReturn(null);
			
			wekaGenericClassifier.loadData(new File(""));
			fail("Expecting exception");
		} catch (FileLoadException e) {
			logger.error(e.getClass().getCanonicalName() + " " + e.getMessage());
		}
	}
	
	// TODO hacer test para weka Util
	// carga exitosa con arff null y con arff en s

	// splitDataSet si la data es null
	// el porcantaje es invalido> menor que 0, mayo que 100, null,
	// Probar valido con 100 y con 50

	// trainModel model null, dataset null correcto => 100
	// correct

	// simpleValidation dataset null, data test null, model null, evauacion
	// fallida
	// exitoso

	// evaluateModel model null, evauacion fallida

	// crossValidation dataset null, folds negativo o cero, model null

	// crossValidateModel model null

	// saveModel model null, model name malo
	// save exitoso
}
