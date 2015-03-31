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

package com.mahisoft.elasticsearchprediction.facade;

import static com.mahisoft.elasticsearchprediction.utils.Constants.ALL_COLUMNS;
import static com.mahisoft.elasticsearchprediction.utils.Constants.FAIL_LOAD_GENERIC_CLASSIFIER;
import static com.mahisoft.elasticsearchprediction.utils.Constants.INDEX_ENGINE_FAIL_MESSAGE;
import static com.mahisoft.elasticsearchprediction.utils.Constants.NO_VALID_MESSAGE;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.mahisoft.elasticsearchprediction.classifier.GenericClassifier;
import com.mahisoft.elasticsearchprediction.domain.guesser.BasicGuesser;
import com.mahisoft.elasticsearchprediction.engine.ElasticsearchGenericIndexEngine;
import com.mahisoft.elasticsearchprediction.engine.ModelGenericEngine;
import com.mahisoft.elasticsearchprediction.exception.DataSetException;
import com.mahisoft.elasticsearchprediction.exception.ElasticsearchGenericIndexException;
import com.mahisoft.elasticsearchprediction.exception.FileLoadException;
import com.mahisoft.elasticsearchprediction.exception.ModelException;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;
import com.mahisoft.elasticsearchprediction.utils.DataSetUtil;
import com.mahisoft.elasticsearchprediction.utils.FileUtil;

public class ElasticsearchClassifierFacade {

    private static final Logger LOGGER = LogManager.getLogger(ElasticsearchClassifierFacade.class);

	public Boolean start(String configFile) throws FileLoadException, ModelException, IOException, DataSetException {
		DataProperties dataProperties = new DataProperties(configFile);
		String dataFileName = dataProperties.getValue("data.filename");
		String fields = dataProperties.getValue("data.columns");
		File dataFile = new File(dataFileName);

		if (!FileUtil.isValid(dataFile)) {
			throw new FileLoadException(format(NO_VALID_MESSAGE, dataFileName));
		}

		if (!ALL_COLUMNS.equals(fields)) {
			String[] filter = fields.split(",");

			dataFile = DataSetUtil.filterInstances(filter, dataFile);
		}
		
		try {
			ElasticsearchGenericIndexEngine genericIndexService = new ElasticsearchGenericIndexEngine(dataProperties,
					new BasicGuesser());

			genericIndexService.createIndex(dataFile);
		} catch (IOException | ElasticsearchGenericIndexException e) {
			throw new ModelException(format(INDEX_ENGINE_FAIL_MESSAGE, e.getMessage()), e);
		}

		GenericClassifier genericClassifier = GenericClassifierFactory.getClassifier(dataProperties);

		if (genericClassifier == null) {
			throw new ModelException(FAIL_LOAD_GENERIC_CLASSIFIER);
		}

		System.out.println("DataFile " + dataFile.getAbsolutePath());
		ModelGenericEngine clientModelEngine = new ModelGenericEngine();

		clientModelEngine.setDataProperties(dataProperties);
		clientModelEngine.setGenericClassifier(genericClassifier);
		clientModelEngine.generateModel(dataFile);
		clientModelEngine.validateModel();

		return true;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			LOGGER.info("Usage:\n" + "java -jar jarfile.jar /path/to/configuration/file\n");
			return;
		}

		ElasticsearchClassifierFacade elasticSearchClassifier = new ElasticsearchClassifierFacade();

		try {
			elasticSearchClassifier.start(args[0]);
		} catch (FileLoadException | ModelException | IOException | DataSetException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
