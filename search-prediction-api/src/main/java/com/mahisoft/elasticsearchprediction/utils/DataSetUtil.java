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

package com.mahisoft.elasticsearchprediction.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataSetUtil {

	private DataSetUtil() {
	}

	public static File filterInstances(String[] filter, File dataFile) throws IOException {

		String line, filterLine;
		String filteredFileName = getfilterFileName(dataFile.getAbsolutePath());
		File filteredFile = new File(filteredFileName);

		BufferedWriter bw = null;
		BufferedReader br = null;

		try {
			bw = new BufferedWriter(new FileWriter(filteredFile));
			br = new BufferedReader(new FileReader(dataFile));

			line = br.readLine();
			Integer[] index = getFilterIndex(filter, line);

			do {
				filterLine = getFilterLine(index, line);
				bw.write(filterLine);
				bw.newLine();

			} while ((line = br.readLine()) != null && line.length() > 0);

			bw.flush();
		} finally {
			if (bw != null)
				bw.close();

			if (br != null)
				br.close();
		}

		return filteredFile;
	}

	private static String getFilterLine(Integer[] index, String line) {
		String newLine = "";
		String[] splitLine = line.split(",");

		for (int i = 0; i < index.length; i++) {
			if (index[i] != null) {
				newLine += "," + splitLine[index[i]];
			}
		}

		return newLine.replaceFirst(",", "");
	}

	private static Integer[] getFilterIndex(String[] filter, String line) {

		int j = 0;
		Integer[] index = new Integer[filter.length];
		List<String> array = Arrays.asList(line.split(","));

		for (int i = 0; i < filter.length; i++) {
			if (array.contains(filter[i])) {
				index[j++] = array.indexOf(filter[i]);
			}
		}

		return index;
	}

	private static String getfilterFileName(String filename) {
		String name = null;
		int periodIndex = filename.lastIndexOf(".");

		if (periodIndex != -1) {
			name = filename.substring(0, periodIndex) + "_filter" + filename.substring(periodIndex, filename.length());
		}

		return name;
	}

}
