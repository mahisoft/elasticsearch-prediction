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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DataProperties {

	private static final Logger logger = LogManager.getLogger(DataProperties.class);

	private Properties properties;

	public DataProperties(String configFile) throws IOException {
        FileInputStream fileInputStream = null;

        this.properties = new Properties();
        try {
            fileInputStream = new FileInputStream(new File(configFile));
            properties.clear();
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            logger.error("Problem loading properties file " + configFile, e);
        } finally {
            if (fileInputStream != null) fileInputStream.close();
        }
	}

	public String getValue(String key) {
		String value = properties.getProperty(key);

		if (value == null) {
			logger.warn("The key " + key + " is not in the configuration file");
		}
		return value;
	}

}
