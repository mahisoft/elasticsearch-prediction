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

import static com.mahisoft.elasticsearchprediction.utils.Constants.ARFF_EXTENSION;
import static com.mahisoft.elasticsearchprediction.utils.Constants.CSV_EXTENSION;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import com.mahisoft.elasticsearchprediction.exception.FileLoadException;

public class WekaFileUtil {

	private static final Logger logger = LogManager.getLogger(WekaFileUtil.class);

	private static final String LOAD_ERROR_MESSAGE = "Failed to load data from %s";

	private static final String EXTENSION_ERROR_MESSAGE = "Failed to load data from %s, error in the type of extesion";

	private static final String SAVE_ERROR_MESSAGE = "Failed to save data to file %s";

	public static Instances loadDataFromCSV(File file) throws FileLoadException {
		CSVLoader loader = new CSVLoader();

		try {
			loader.setSource(file);

			return loader.getDataSet();
		} catch (IOException e) {
			throw new FileLoadException(format(LOAD_ERROR_MESSAGE, file), e);
		}
	}

	public static Instances loadDataFromArff(File file) throws FileLoadException {
		BufferedReader reader = null;
		Instances data = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			data = new Instances(reader);
			reader.close();
		} catch (IOException e) {
			throw new FileLoadException(format(LOAD_ERROR_MESSAGE, file), e);
		}

		return data;
	}

	public static Instances loadData(File file) throws FileLoadException {
		if (file == null) {
			throw new FileLoadException(format(LOAD_ERROR_MESSAGE, file));
		}

		String extension = getFileExtension(file.getName());

		switch (extension) {
		case ARFF_EXTENSION:
			return loadDataFromArff(file);
		case CSV_EXTENSION:
			return loadDataFromCSV(file);
		default:
			throw new FileLoadException(format(EXTENSION_ERROR_MESSAGE, file));
		}
	}

	public static Boolean saveArffFile(File file, Instances data) throws IOException {
		ArffSaver saver = new ArffSaver();

		try {
			saver.setInstances(data);
			saver.setFile(file);
			saver.setDestination(file);
			saver.writeBatch();
		} catch (IOException e) {
			logger.error(format(SAVE_ERROR_MESSAGE, file), e);
			throw e;
		}

		return true;
	}

	public static Boolean csv2Arff(File dataIn, File dataOut) throws FileLoadException, IOException {
		Instances data = loadDataFromCSV(dataIn);
		saveArffFile(dataOut, data);

		return true;
	}

	public static String getFileExtension(String filename) {
		int periodIndex = filename.lastIndexOf(".");

		if (periodIndex != -1) {
			return filename.substring(periodIndex + 1, filename.length()).toLowerCase();
		} else {
			return null;
		}
	}

}
