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

package com.mahisoft.elasticsearchprediction.utils.weka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import weka.core.Instances;

import com.mahisoft.elasticsearchprediction.exception.FileLoadException;

public class WekaFileUtilTest {

	private static final Logger LOGGER = LogManager.getLogger(WekaFileUtilTest.class);

	private static final String TEST_CSV_FILE = "src/test/resources/adult_num.csv";

	private static final String TEST_ARFF_FILE = "src/test/resources/adult_num.arff";

	private static final String EXCEPTION_MSG = "Expecting exception";

	@Test
	public void ifFileIsNullThenLoadDataFromCSVThrowsFileLoadException() {
		try {
			WekaFileUtil.loadDataFromCSV(null);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileIsNotCSVFileThenLoadDataFromCSVThrowsFileLoadException() {
		try {
			WekaFileUtil.loadDataFromCSV(new File(TEST_ARFF_FILE));
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileIsCSVFileThenLoadDataFromCSVSucess() {
		try {
			Instances instances = WekaFileUtil.loadDataFromCSV(new File(TEST_CSV_FILE));
			assertNotNull(instances);
		} catch (FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifFileIsNullThenLoadDataFromArffThrowsFileLoadException() {
		try {
			WekaFileUtil.loadDataFromArff(null);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileIsNotArffFileThenLoadDataFromArffThrowsFileLoadException() {
		try {
			WekaFileUtil.loadDataFromArff(new File(TEST_CSV_FILE));
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileIsArffFileThenLoadDataFromArffSucess() {
		try {
			Instances instances = WekaFileUtil.loadDataFromArff(new File(TEST_ARFF_FILE));
			assertNotNull(instances);
		} catch (FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifFileIsNullThenLoadDataThrowsFileLoadException() {
		try {
			WekaFileUtil.loadData(null);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileIsNotArffOrCSVFileThenLoadDataThrowsFileLoadException() {
		try {
			WekaFileUtil.loadData(new File("file.txt"));
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileIsArffFileThenLoadDataSucess() {
		try {
			Instances instances = WekaFileUtil.loadData(new File(TEST_ARFF_FILE));
			assertNotNull(instances);
		} catch (FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifFileIsCSVFileThenLoadDataSucess() {
		try {
			Instances instances = WekaFileUtil.loadData(new File(TEST_CSV_FILE));
			assertNotNull(instances);
		} catch (FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataIsNullThenSaveArffFileThrowsFileLoadException() {
		try {
			WekaFileUtil.saveArffFile(null, null);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileIsNullThenSaveArffFileThrowsFileLoadException() {
		try {
			Instances data = WekaFileUtil.loadData(new File(TEST_ARFF_FILE));
			WekaFileUtil.saveArffFile(null, data);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		}
	}

	@Test
	public void ifFileAndInstancesAreCorrectThenSaveArffFileSucess() {
		try {
			Instances data = WekaFileUtil.loadData(new File(TEST_ARFF_FILE));
			WekaFileUtil.saveArffFile(new File("/tmp/file.arff"), data);
		} catch (FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataInIsNullThenCsv2ArffThrowsFileLoadException() {
		try {
			WekaFileUtil.csv2Arff(null, null);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		} catch (IOException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifDataOutIsNullThenCsv2ArffThrowsFileLoadException() {
		try {
			WekaFileUtil.csv2Arff(new File(TEST_CSV_FILE), null);
			fail(EXCEPTION_MSG);
		} catch (FileLoadException e) {
			LOGGER.info(e);
		} catch (IOException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifParametersAreCorrectThenCsv2ArffSucess() {
		try {
			WekaFileUtil.csv2Arff(new File(TEST_CSV_FILE), new File("/tmp/file.arff"));
		} catch (IOException | FileLoadException e) {
			fail(e.getMessage());
			LOGGER.error(e);
		}
	}

	@Test
	public void ifFileNameDoesNotHaveExtensionThenGetFileExtensionIsNull() {
		String result = WekaFileUtil.getFileExtension("nameFile");
		assertNull(result);
	}

	@Test
	public void ifFileNameHaveExtensionThenGetFileExtensionReturnString() {
		String result = WekaFileUtil.getFileExtension("nameFile.csv");
		assertEquals("csv", result);
	}
}
