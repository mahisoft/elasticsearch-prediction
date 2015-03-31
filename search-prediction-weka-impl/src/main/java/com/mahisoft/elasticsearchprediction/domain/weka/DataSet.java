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

package com.mahisoft.elasticsearchprediction.domain.weka;

import weka.core.Instances;

public class DataSet {

	private Instances trainDataSet;

	private Instances testDataSet;

	public DataSet() {
	}

	public DataSet(Instances trainDataSet, Instances testDataSet) {
		this.trainDataSet = trainDataSet;
		this.testDataSet = testDataSet;
	}

	public Instances getTrainDataSet() {
		return trainDataSet;
	}

	public void setTrainDataSet(Instances trainDataSet) {
		this.trainDataSet = trainDataSet;
	}

	public Instances getTestDataSet() {
		return testDataSet;
	}

	public void setTestDataSet(Instances testDataSet) {
		this.testDataSet = testDataSet;
	}

}
