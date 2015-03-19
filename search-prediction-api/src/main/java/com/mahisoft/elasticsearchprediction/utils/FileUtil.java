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

import static com.mahisoft.elasticsearchprediction.utils.Constants.CSV_EXTENSION;

import java.io.File;

public class FileUtil {

	public static Boolean isValid(File dataFile) {

		if (dataFile == null || !getFileExtension(dataFile.getName()).equals(CSV_EXTENSION)) {
			return false;
		}

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
