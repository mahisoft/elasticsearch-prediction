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

package com.mahisoft.elasticsearchprediction.domain;

import java.util.ArrayList;

public class CrossDataSetResult {

	private Integer dataSetSize;

	private Integer numFolds;

	private String results;

	private ArrayList<String> resultPartitions;

	public CrossDataSetResult(Integer numFolds) {
		this.numFolds = numFolds;
		this.resultPartitions = new ArrayList<String>();
	}

	public void addResult(Integer foldIndex, String result) {
		this.resultPartitions.add(foldIndex, result);
	}

	public Integer getDataSetSize() {
		return dataSetSize;
	}

	public void setDataSetSize(Integer dataSetSize) {
		this.dataSetSize = dataSetSize;
	}

	public Integer getNumFolds() {
		return numFolds;
	}

	public void setNumFolds(Integer numFolds) {
		this.numFolds = numFolds;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public ArrayList<String> getResultPartitions() {
		return resultPartitions;
	}

	public void setResultPartitions(ArrayList<String> resultPartitions) {
		this.resultPartitions = resultPartitions;
	}

	@Override
	public String toString() {
		return "CrossDataSetResult: \ndataSetSize=" + dataSetSize + "\nnumFolds=" + numFolds + "\nresults=" + results
				+ "\nresultPartitions=" + resultPartitions;
	}
	
	

}
