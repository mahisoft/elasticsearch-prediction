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

package com.mahisoft.elasticsearchprediction.engine;

import static java.lang.String.format;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.mahisoft.elasticsearchprediction.domain.DataType;
import com.mahisoft.elasticsearchprediction.domain.guesser.BaseGuesser;
import com.mahisoft.elasticsearchprediction.exception.ElasticsearchGenericIndexException;
import com.mahisoft.elasticsearchprediction.utils.Constants;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;

public class ElasticsearchGenericIndexEngine {

	private static final Logger LOGGER = LogManager.getLogger(ElasticsearchGenericIndexEngine.class);

	private static final String TYPE = "_default_";

	private DataProperties dataProperties;

	private BaseGuesser typeGuesser;

	public ElasticsearchGenericIndexEngine(DataProperties dataProperties, BaseGuesser typeGuesser) {
		this.dataProperties = dataProperties;
		this.typeGuesser = typeGuesser;
	}

	public void createIndex(File dataFile) throws IOException {
		Client client = null;

		try {
			String indexName = dataProperties.getValue("index.name");
			String clusterName = dataProperties.getValue("cluster.name");
			String nodeName = dataProperties.getValue("node.name");
			String host = dataProperties.getValue("host");
			String mappingFilename = dataProperties.getValue("mapping.filename");
			short port = Short.parseShort(dataProperties.getValue("port"));
			Settings settings = buildSettings(clusterName, nodeName);
			TransportClient transportClient = new TransportClient(settings);

			client = transportClient.addTransportAddress(new InetSocketTransportAddress(host, port));
			deleteIndex(client, indexName);
			loadData(dataFile, client, indexName, mappingFilename);
		} finally {
			if (client != null)
				client.close();
		}
	}

	private void loadData(File dataFile, Client client, String indexName, String mappingFilename) throws IOException {
		CSVParser parser = null;
		PrintWriter mappingFileWriter = null;
		List<String> headers = new ArrayList<String>();

		try {
			mappingFileWriter = new PrintWriter(mappingFilename, Constants.UTF8);
			parser = CSVParser.parse(dataFile, Charset.forName(Constants.UTF8), CSVFormat.RFC4180);

			for (CSVRecord csvRecord : parser) {
				if (csvRecord.getRecordNumber() == 1) {
					addHeaders(csvRecord, headers);
					continue;
				}

				if (csvRecord.getRecordNumber() == 2) {
					createIndex(client, indexName, mappingFileWriter, headers, csvRecord);
				}
				addValue(client, indexName, headers, csvRecord);
			}
		} finally {
			if (mappingFileWriter != null)
				mappingFileWriter.close();
			if (parser != null)
				parser.close();
		}
		
		LOGGER.info("Done!");
	}

	private void addHeaders(CSVRecord csvRecord, List<String> headers) {
		for (String header : csvRecord) {
			headers.add(header);
		}
	}

	private void addValue(Client client, String indexName, List<String> headers, CSVRecord csvRecord)
			throws IOException {
		XContentBuilder jsonBuilder = jsonBuilder().startObject();
		int i = 0;

		for (String value : csvRecord) {
			if (i == headers.size() - 1) {
				continue;
			}
			jsonBuilder.field(headers.get(i), value);
			i++;
		}
		jsonBuilder.endObject();

		IndexResponse response = client.prepareIndex(indexName, TYPE).setSource(jsonBuilder).execute().actionGet();

		if (!response.isCreated()) {
			LOGGER.warn(format("Problem adding document"));
		}
	}

	private void createIndex(Client client, String indexName, PrintWriter mappingFileWriter, List<String> headers,
			CSVRecord csvRecord) throws IOException {
		XContentBuilder jsonBuilder = jsonBuilder().startObject().startObject("properties");
		int i = 0;

		for (String value : csvRecord) {
			if (i == headers.size() - 1) {
				continue;
			}
			DataType dataType = typeGuesser.guess(value);

			mappingFileWriter.write(headers.get(i) + ":" + dataType + (i == headers.size() - 2 ? "" : ","));
			jsonBuilder.startObject(headers.get(i)).field("type", dataType).field("index", "not_analyzed").endObject();
			i++;
		}

		jsonBuilder.endObject().endObject();

		CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(indexName)
				.addMapping(TYPE, jsonBuilder).execute().actionGet();

		if (!createIndexResponse.isAcknowledged()) {
			String message = "Problem creating index";

			LOGGER.info(message);
			throw new ElasticsearchGenericIndexException(message);
		}
		LOGGER.info("Index created");
	}

	private void deleteIndex(Client client, String indexName) {
		if (client.admin().indices().prepareExists(indexName).execute().actionGet().isExists()) {
			client.admin().indices().prepareDelete(indexName).execute().actionGet();
		}
	}

	private Settings buildSettings(String clusterName, String nodeName) {
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();

		if (clusterName != null) {
			settingsBuilder.put("cluster.name", clusterName);
		}
		if (nodeName != null) {
			settingsBuilder.put("node.name", nodeName);
		}

		return settingsBuilder.build();
	}

}
