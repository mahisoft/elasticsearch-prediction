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

package com.mahisoft.elasticsearchprediction.plugin.utils;

import com.mahisoft.elasticsearchprediction.domain.DataType;
import com.mahisoft.elasticsearchprediction.plugin.domain.IndexAttributeDefinition;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mahisoft.elasticsearchprediction.domain.DataType.DOUBLE;
import static com.mahisoft.elasticsearchprediction.domain.DataType.STRING;

public class PluginProperties {

    private static final String CONFIG_FILE = "/plugin.properties";

    private static final ESLogger LOGGER = Slf4jESLoggerFactory.getLogger(PluginProperties.class.getSimpleName());

    private static PluginProperties pluginProperties;

    private List<IndexAttributeDefinition> mapping;

    private String modelPath;

    private String classifier;

    public PluginProperties(String modelPath, String classifier, List<IndexAttributeDefinition> mapping) {
        this.modelPath = modelPath;
        this.mapping = mapping;
        this.classifier = classifier;
    }

    public static PluginProperties getInstance() throws IOException {
        if (pluginProperties == null) {
            Properties propertiesFile = new Properties();

            LOGGER.info("Loading properties");
            propertiesFile.load(PluginProperties.class.getResourceAsStream(CONFIG_FILE));

            String modelPath = propertiesFile.getProperty("modelPath");
            String mappingStr = propertiesFile.getProperty("mapping");
            String classifier = propertiesFile.getProperty("classifier.lib");
            List<IndexAttributeDefinition> mapping = new CopyOnWriteArrayList<IndexAttributeDefinition>();

            for (String definition : mappingStr.split(",")) {
                String[] tokens = definition.split(":");

                if (tokens.length != 2) {
                    throw new IOException("Problem reading configuration file");
                } else {
                    DataType type = "double".equals(tokens[1]) ? DOUBLE : STRING;
                    mapping.add(new IndexAttributeDefinition(tokens[0], type));
                }
            }

            LOGGER.info("Done loading properties");
            pluginProperties = new PluginProperties(modelPath, classifier, mapping);
        }
        return pluginProperties;
    }

    public static void clear() {
        pluginProperties = null;
    }

    public String getModelPath() {
        return modelPath;
    }
    
    public String getClassifier() {
		return classifier;
	}

	public List<IndexAttributeDefinition> getMapping() {
        return mapping;
    }

}
