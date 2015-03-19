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

package com.mahisoft.elasticsearchprediction.classifier;

import java.io.File;

import com.mahisoft.elasticsearchprediction.domain.CrossDataSetResult;
import com.mahisoft.elasticsearchprediction.domain.SimpleDataSetResult;
import com.mahisoft.elasticsearchprediction.exception.DataSetException;
import com.mahisoft.elasticsearchprediction.exception.FileLoadException;
import com.mahisoft.elasticsearchprediction.exception.ModelException;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;

public interface GenericClassifier {

	public Boolean loadClassifier() throws ModelException;

	public Boolean loadData(File dataFile) throws FileLoadException;

	public Boolean splitDataSet(Integer trainingPercentage) throws DataSetException;

	public void trainModel() throws DataSetException, ModelException;

	public SimpleDataSetResult simpleValidation() throws DataSetException, ModelException;

	public CrossDataSetResult crossValidation(Integer numFolds) throws DataSetException, ModelException;

	public Boolean saveModel(String modelName) throws ModelException;

	public void setDataProperties(DataProperties dataProperties);

}
