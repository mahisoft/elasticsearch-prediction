/*====================================================================================
    Copyright 2015 Mahisoft Inc. (Yessika Labrador, Federico Ponte and Joaquin Delgado)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
======================================================================================*/

package com.mahisoft.elasticsearchprediction.engine;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.mahisoft.elasticsearchprediction.classifier.GenericClassifier;
import com.mahisoft.elasticsearchprediction.domain.CrossDataSetResult;
import com.mahisoft.elasticsearchprediction.domain.SimpleDataSetResult;
import com.mahisoft.elasticsearchprediction.exception.DataSetException;
import com.mahisoft.elasticsearchprediction.exception.FileLoadException;
import com.mahisoft.elasticsearchprediction.exception.ModelException;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;

public class ModelGenericEngine {

	private static final Logger LOGGER = LogManager.getLogger(ModelGenericEngine.class);

	private GenericClassifier genericClassifier;

	private DataProperties dataProperties;

	public void generateModel(File dataFile) throws ModelException, DataSetException, FileLoadException {
		int trainingPercentage = Integer.parseInt(this.dataProperties.getValue("train.percentage"));

		this.genericClassifier.setDataProperties(this.dataProperties);
		this.genericClassifier.loadClassifier();
		this.genericClassifier.loadData(dataFile);
		this.genericClassifier.splitDataSet(trainingPercentage);
		this.genericClassifier.trainModel();
		this.genericClassifier.saveModel(this.dataProperties.getValue("model.filename"));
	}

	public void validateModel() throws DataSetException, ModelException {
		String options = dataProperties.getValue("validate.options");

		if (options == null) {
			SimpleDataSetResult result = this.genericClassifier.simpleValidation();

			LOGGER.info(result);
		} else {
			if (options.contains("Sd")) {
				SimpleDataSetResult result = this.genericClassifier.simpleValidation();

				LOGGER.info(result);
			}
			if (options.contains("Cv")) {
				CrossDataSetResult result = this.genericClassifier.crossValidation(Integer.parseInt(dataProperties
						.getValue("validate.numFolds")));

				LOGGER.info(result);
			}
		}
	}

	public GenericClassifier getGenericClassifier() {
		return genericClassifier;
	}

	public void setGenericClassifier(GenericClassifier genericClassifier) {
		this.genericClassifier = genericClassifier;
	}

	public void setDataProperties(DataProperties dataProperties) {
		this.dataProperties = dataProperties;
	}

}
