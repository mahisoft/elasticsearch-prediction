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

package com.mahisoft.elasticsearchprediction.classifier.weka;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;

import com.mahisoft.elasticsearchprediction.classifier.GenericClassifier;
import com.mahisoft.elasticsearchprediction.domain.CrossDataSetResult;
import com.mahisoft.elasticsearchprediction.domain.SimpleDataSetResult;
import com.mahisoft.elasticsearchprediction.domain.weka.DataSet;
import com.mahisoft.elasticsearchprediction.exception.DataSetException;
import com.mahisoft.elasticsearchprediction.exception.FileLoadException;
import com.mahisoft.elasticsearchprediction.exception.ModelException;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;
import com.mahisoft.elasticsearchprediction.utils.weka.WekaFileUtil;

public class WekaGenericClassifier implements GenericClassifier {

	private static final Logger LOGGER = LogManager.getLogger(WekaGenericClassifier.class);

	private static final String TRUE = "true";

	private static final String LOAD_FAIL_MESSAGE = "Failed to load classifier %s";

	private static final String OPTIONS_FAIL_MESSAGE = "Failed to set options for classifier %s - %s";

	private static final String SPLIT_FAIL_MESSAGE = "Failed to set split with training percentage %s";

	private static final String CROSS_VALIDATE_FAIL_MESSAGE = "Failed to validate model with num folds %s";

	private static final String NULL_CLASSIFIER_MESSAGE = "Classifier is null";

	private static final String NULL_DATASET_MESSAGE = "Data Set is null";

	private static final String NULL_DATA_TEST_MESSAGE = "Test Data Set is null";

	private static final String TRAIN_FAIL_MESSAGE = "Failed to train model for classifier %s";

	private static final String VALIDATE_FAIL_MESSAGE = "Failed to validate model for classifier %s";

	private static final String SAVE_FAIL_MESSAGE = "Failed to save model for classifier %s in file %s";

	private static final String VALIDATE_TEST_MESSAGE = "\nResults Evaluate Model %s using test dataset \n======\n";

	private static final String CROSSVALIDATE_MESSAGE = "\nResults Evaluate Model %s using CrossValidate with %s folds \n======\n";

	private DataProperties dataProperties;

	private String classifierClass;

	private AbstractClassifier model;

	private Instances data;

	private DataSet dataSet;

	public void loadClassifier(Class<?> classifierClass, String[] options) throws InstantiationException,
			IllegalAccessException {

		this.model = (AbstractClassifier) classifierClass.newInstance();
		this.classifierClass = classifierClass.getName();

		if (options != null) {
			try {
				this.model.setOptions(options);
			} catch (Exception e) {
				LOGGER.warn(format(OPTIONS_FAIL_MESSAGE, this.classifierClass, e.getMessage()), e);
			}
		}

	}

	@Override
	public Boolean loadClassifier() throws ModelException {

		this.classifierClass = dataProperties.getValue("weka.classifier.class");

		String options = dataProperties.getValue("weka.classifier.options");
		String[] optionsClassifier = null;

		if (options != null) {
			try {
				optionsClassifier = Utils.splitOptions(options);
			} catch (Exception e) {
				LOGGER.warn(format(OPTIONS_FAIL_MESSAGE, this.classifierClass, e.getMessage()), e);
			}
		}

		try {
			this.loadClassifier(Class.forName(this.classifierClass), optionsClassifier);
		} catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new ModelException(format(LOAD_FAIL_MESSAGE, this.classifierClass), e);
		}

		return true;
	}

	@Override
	public Boolean loadData(File dataFile) throws FileLoadException {
		this.data = WekaFileUtil.loadData(dataFile);

		String saveArff = dataProperties.getValue("weka.data.saveArff");

		if (saveArff != null && saveArff.equalsIgnoreCase(TRUE)) {
			try {
				WekaFileUtil.saveArffFile(new File(dataProperties.getValue("weka.data.fileArff")), data);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}

		return true;
	}

	@Override
	public Boolean splitDataSet(Integer trainingPercentage) throws DataSetException {

		if (data == null) {
			throw new DataSetException(NULL_DATASET_MESSAGE);
		}

		if (trainingPercentage < 1 || trainingPercentage > 100) {
			throw new DataSetException(format(SPLIT_FAIL_MESSAGE, trainingPercentage));
		}

		this.dataSet = new DataSet();
		this.data.setClassIndex(this.data.numAttributes() - 1);
		this.data.randomize(new java.util.Random(1));

		if (trainingPercentage == 100) {
			this.dataSet.setTrainDataSet(data);
			this.dataSet.setTestDataSet(null);
		} else {
			int trainSize = (int) Math.round(this.data.numInstances() * (trainingPercentage / 100d));
			int testSize = this.data.numInstances() - trainSize;

			this.dataSet.setTrainDataSet(new Instances(this.data, 0, trainSize));
			this.dataSet.setTestDataSet(new Instances(this.data, trainSize, testSize));
		}

		return true;
	}

	@Override
	public void trainModel() throws DataSetException, ModelException {
		LOGGER.info("Start the training");

		if (data == null) {
			throw new DataSetException(NULL_DATASET_MESSAGE);
		}

		if (this.dataSet == null) {
			this.splitDataSet(100);
		}

		if (this.model == null) {
			throw new ModelException(NULL_CLASSIFIER_MESSAGE);
		}

		try {
			this.model.buildClassifier(this.dataSet.getTrainDataSet());
		} catch (Exception e) {
			throw new ModelException(format(TRAIN_FAIL_MESSAGE, this.classifierClass), e);
		}

		LOGGER.info("End of the training");
	}

	@Override
	public SimpleDataSetResult simpleValidation() throws DataSetException, ModelException {

		if (this.dataSet == null || this.dataSet.getTestDataSet() == null) {
			throw new DataSetException(NULL_DATA_TEST_MESSAGE);
		}

		Evaluation evaluation = this.evaluateModel(this.dataSet.getTrainDataSet(), this.dataSet.getTestDataSet());

		SimpleDataSetResult result = new SimpleDataSetResult();
		result.setTrainDataSetSize(this.dataSet.getTrainDataSet().size());
		result.setTestDataSetSize(this.dataSet.getTestDataSet().size());
		result.setResults(evaluation.toSummaryString(format(VALIDATE_TEST_MESSAGE, this.classifierClass), false));

		return result;
	}

	private Evaluation evaluateModel(Instances trainDataSet, Instances testDataSet) throws ModelException {
		Evaluation eval = null;

		if (this.model == null) {
			throw new ModelException(NULL_CLASSIFIER_MESSAGE);
		}

		try {
			eval = new Evaluation(trainDataSet);
			eval.evaluateModel(this.model, testDataSet);
		} catch (Exception e) {
			throw new ModelException(format(VALIDATE_FAIL_MESSAGE, this.classifierClass), e);
		}

		return eval;
	}

	@Override
	public CrossDataSetResult crossValidation(Integer numFolds) throws DataSetException, ModelException {
		if (this.data == null) {
			throw new DataSetException(NULL_DATASET_MESSAGE);
		}

		if (this.model == null) {
			throw new ModelException(NULL_CLASSIFIER_MESSAGE);
		}

		if (numFolds <= 0) {
			throw new ModelException(format(CROSS_VALIDATE_FAIL_MESSAGE, numFolds));
		}

		return this.crossValidateModel(this.data, numFolds);
	}

	private CrossDataSetResult crossValidateModel(Instances data, int numFolds) throws ModelException {
		CrossDataSetResult result = new CrossDataSetResult(numFolds);
		result.setDataSetSize(data.size());

		data.setClassIndex(data.numAttributes() - 1);

		Instances randData = new Instances(data);
		randData.randomize(new Random(1));
		if (randData.classAttribute().isNominal())
			randData.stratify(numFolds);

		try {
			Evaluation evaluation = new Evaluation(randData);
			Evaluation eval = null;

			for (int index = 0; index < numFolds; index++) {
				eval = new Evaluation(randData);

				Instances train = randData.trainCV(numFolds, index);
				Instances test = randData.testCV(numFolds, index);

				// build and evaluate classifier
				Classifier clsCopy = AbstractClassifier.makeCopy(model);
				clsCopy.buildClassifier(train);

				evaluation.evaluateModel(clsCopy, test);
				eval.evaluateModel(clsCopy, test);

				result.addResult(index, eval.toSummaryString());
			}

			result.setResults(evaluation.toSummaryString(format(CROSSVALIDATE_MESSAGE, classifierClass, numFolds),
					false));
		} catch (Exception e) {
			throw new ModelException(format(VALIDATE_FAIL_MESSAGE, this.classifierClass), e);
		}

		return result;
	}

	@Override
	public Boolean saveModel(String modelName) throws ModelException {
		try {
			SerializationHelper.write(modelName, this.model);
		} catch (Exception e) {
			throw new ModelException(format(SAVE_FAIL_MESSAGE, this.classifierClass, modelName), e);
		}

		return true;
	}

	@Override
	public void setDataProperties(DataProperties dataProperties) {
		this.dataProperties = dataProperties;
	}

	public String getClassifierClass() {
		return classifierClass;
	}

	public AbstractClassifier getModel() {
		return model;
	}

	public Instances getData() {
		return data;
	}

	public void setData(Instances data) {
		this.data = data;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

}
