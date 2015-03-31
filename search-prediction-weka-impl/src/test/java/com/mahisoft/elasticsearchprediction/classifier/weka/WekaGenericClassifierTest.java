package com.mahisoft.elasticsearchprediction.classifier.weka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

import com.mahisoft.elasticsearchprediction.domain.CrossDataSetResult;
import com.mahisoft.elasticsearchprediction.domain.SimpleDataSetResult;
import com.mahisoft.elasticsearchprediction.exception.DataSetException;
import com.mahisoft.elasticsearchprediction.exception.FileLoadException;
import com.mahisoft.elasticsearchprediction.exception.ModelException;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;

public class WekaGenericClassifierTest {

	private static final Logger LOGGER = LogManager.getLogger(WekaGenericClassifierTest.class);

	private static final int SPLIT = 50;

	private static final int FOLDS = 5;

	private static final String CLASSIFIER = "weka.classifiers.trees.RandomTree";

	private static final String CLASS_LABEL = "weka.classifier.class";

	private static final String OPTIONS_LABEL = "weka.classifier.options";

	private static final String SAVE_ARFF_LABEL = "weka.data.saveArff";

	private static final String TEST_FILE = "src/test/resources/adult_num.csv";

	private static final String EXCEPTION_MSG = "Expecting exception";

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
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifClassifierIsNoValidThenThrowsModelException() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn("Clasificador");

			wekaGenericClassifier.loadClassifier();
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifOptionsAreNullThenClassifierIsCreatedWithoutOptions() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(OPTIONS_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			assertEquals(wekaGenericClassifier.getModel().getClass().getName(), CLASSIFIER);
			assertEquals(wekaGenericClassifier.getModel().getOptions().length, 8);
		} catch (ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifOptionsAreNoValidThenClassifierIsCreatedWithoutOptions() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(OPTIONS_LABEL)).thenReturn("-X 7 -Y 3 -Z 6");

			wekaGenericClassifier.loadClassifier();
			assertEquals(wekaGenericClassifier.getModel().getClass().getName(), CLASSIFIER);
			assertEquals(wekaGenericClassifier.getModel().getOptions().length, 8);
		} catch (ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void classifierIsCreatedSuccess() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(OPTIONS_LABEL)).thenReturn(PARAMS);

			wekaGenericClassifier.loadClassifier();
			assertEquals(wekaGenericClassifier.getModel().getClass().getName(), CLASSIFIER);
			assertEquals(wekaGenericClassifier.getModel().getOptions().length, 9);
		} catch (ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifFileForLoadDataIsNullThenThrowsModelException() {
		try {
			wekaGenericClassifier.loadData(null);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Ignore
	public void ifFlagArffIsNullThenFileLoadIsSucess() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadData(new File(""));
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifDataIsNullThenThrowsDataSetException() {
		try {
			wekaGenericClassifier.splitDataSet(0);
			fail(EXCEPTION_MSG);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifTrainingPercentageIsNegativeThenThrowsDataSetException() {
		try {
			wekaGenericClassifier.splitDataSet(-1);
			fail(EXCEPTION_MSG);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifTrainingPercentageIsGreaterThanOneHundredThenThrowsDataSetException() {
		try {
			wekaGenericClassifier.splitDataSet(101);
			fail(EXCEPTION_MSG);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifTrainingPercentageIsZeroThenThrowsDataSetException() {
		try {
			wekaGenericClassifier.splitDataSet(0);
			fail(EXCEPTION_MSG);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifTrainingPercentageIsOneHundredThenTestDataSetIsNull() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);
			wekaGenericClassifier.loadData(new File(TEST_FILE));

			wekaGenericClassifier.splitDataSet(100);

			int sizeTrain = wekaGenericClassifier.getData().size();

			assertNull(wekaGenericClassifier.getDataSet().getTestDataSet());
			assertNotNull(wekaGenericClassifier.getDataSet().getTrainDataSet());
			assertEquals(sizeTrain, wekaGenericClassifier.getDataSet().getTrainDataSet().size());
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifTrainingPercentageIsBetweenOneAndOneHundredThenTrainAndTestDataSetAreCreated() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);
			wekaGenericClassifier.loadData(new File(TEST_FILE));

			wekaGenericClassifier.splitDataSet(SPLIT);

			int sizeTrain = wekaGenericClassifier.getData().size() / 2 + wekaGenericClassifier.getData().size() % 2;
			int sizeTest = wekaGenericClassifier.getData().size() / 2;

			assertNotNull(wekaGenericClassifier.getDataSet().getTrainDataSet());
			assertEquals(sizeTrain, wekaGenericClassifier.getDataSet().getTrainDataSet().size());
			assertNotNull(wekaGenericClassifier.getDataSet().getTestDataSet());
			assertEquals(sizeTest, wekaGenericClassifier.getDataSet().getTestDataSet().size());
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataIsNullThenTrainModelThrowsDataSetException() {
		try {
			wekaGenericClassifier.trainModel();
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifClassifierIsNullThenTrainModelThrowsModelException() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);
			wekaGenericClassifier.loadData(new File(TEST_FILE));

			wekaGenericClassifier.trainModel();
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataSetIsNullThenTrainModelWithAllData() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.trainModel();

			int sizeTrain = wekaGenericClassifier.getData().size();

			assertNotNull(wekaGenericClassifier.getDataSet().getTrainDataSet());
			assertEquals(sizeTrain, wekaGenericClassifier.getDataSet().getTrainDataSet().size());
			assertNull(wekaGenericClassifier.getDataSet().getTestDataSet());
		} catch (DataSetException | FileLoadException | ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataSetIsNotNullThenTrainModelWithTrainData() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.splitDataSet(SPLIT);
			wekaGenericClassifier.trainModel();

			assertNotNull(wekaGenericClassifier.getDataSet().getTrainDataSet());
			assertNotNull(wekaGenericClassifier.getDataSet().getTestDataSet());
		} catch (DataSetException | FileLoadException | ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataIsNullThenSimpleValidationThrowsDataSetException() {
		try {
			wekaGenericClassifier.simpleValidation();
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifDataSetIsNullThenSimpleValidationThrowsDataSetException() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.simpleValidation();

			fail(EXCEPTION_MSG);
		} catch (ModelException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifTestDataSetIsNullThenSimpleValidationThrowsDataSetException() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.splitDataSet(100);
			wekaGenericClassifier.simpleValidation();

			fail(EXCEPTION_MSG);
		} catch (ModelException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifClassifierIsNullThenSimpleValidationThrowsModelException() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.splitDataSet(80);
			wekaGenericClassifier.simpleValidation();
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifThereIsNotAModelThenSimpleValidationThrowsModelException() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.splitDataSet(SPLIT);
			wekaGenericClassifier.simpleValidation();
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifTrainAndDataSetAreValidThenEvaluateModelWithSimpleValidation() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.splitDataSet(SPLIT);
			wekaGenericClassifier.trainModel();

			SimpleDataSetResult result = wekaGenericClassifier.simpleValidation();
			int sizeTrain = wekaGenericClassifier.getData().size() / 2 + wekaGenericClassifier.getData().size() % 2;
			int sizeTest = wekaGenericClassifier.getData().size() / 2;

			assertNotNull(result);
			assertNotNull(result.getResults());
			assertEquals(sizeTrain, result.getTrainDataSetSize().intValue());
			assertEquals(sizeTest, result.getTestDataSetSize().intValue());

		} catch (DataSetException | FileLoadException | ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataIsNullThenCrossValidationThrowsDataSetException() {
		try {
			wekaGenericClassifier.crossValidation(FOLDS);
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		} catch (DataSetException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifNumFoldIsNegativeThenCrossValidationThrowsModelException() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.crossValidation(-1);

			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifNumFoldIsZeroThenCrossValidationThrowsModelException() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.crossValidation(0);

			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifClassifierIsNullThenCrossValidationThrowsModelException() {
		try {
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.crossValidation(FOLDS);
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifTrainAndDataSetAreValidThenEvaluateModelWithCrossValidation() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.trainModel();

			CrossDataSetResult result = wekaGenericClassifier.crossValidation(FOLDS);
			assertNotNull(result);
			assertNotNull(result.getResults());
			assertEquals(5, result.getNumFolds().intValue());
		} catch (DataSetException | FileLoadException | ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifModelIsNullThenSaveModelThrowsModelException() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.saveModel("");
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifNameOfModelIsNullThenSaveModelThrowsModelException() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.trainModel();
			wekaGenericClassifier.saveModel(null);
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifNameOfModelIsEmptyThenSaveModelThrowsModelException() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.trainModel();
			wekaGenericClassifier.saveModel("");
			fail(EXCEPTION_MSG);
		} catch (ModelException e) {
			LOGGER.info(e);
		} catch (DataSetException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifThereIsAModelAndTheNameOfModelIsCorrectThenSaveModel() {
		try {
			when(dataProperties.getValue(CLASS_LABEL)).thenReturn(CLASSIFIER);
			when(dataProperties.getValue(SAVE_ARFF_LABEL)).thenReturn(null);

			wekaGenericClassifier.loadClassifier();
			wekaGenericClassifier.loadData(new File(TEST_FILE));
			wekaGenericClassifier.trainModel();
			wekaGenericClassifier.saveModel("/tmp/test.model");
		} catch (DataSetException | FileLoadException | ModelException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}
}
