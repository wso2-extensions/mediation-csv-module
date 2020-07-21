/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.module.csv;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.enums.EmptyCsvValueType;
import org.wso2.carbon.module.csv.enums.HeaderAvailability;
import org.wso2.carbon.module.csv.util.Constants;
import org.wso2.carbon.module.csv.util.CsvTransformer;
import org.wso2.carbon.module.csv.util.JsonDataType;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.CsvTransformer.skipColumns;
import static org.wso2.carbon.module.csv.util.PropertyReader.*;

public class CsvToJsonTransformer extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        final Optional<HeaderAvailability> headerAvailability = getEnumParam(mc, ParameterKey.IS_HEADER_PRESENT, HeaderAvailability.class);
        final Optional<Character> valueSeparator = getCharParam(mc, ParameterKey.VALUE_SEPARATOR);
        final boolean skipHeader = getBooleanParam(mc, ParameterKey.SKIP_HEADER);
        final Optional<Integer> dataRowsToSkip = getIntegerParam(mc, ParameterKey.DATA_ROWS_TO_SKIP);
        final Optional<String> columnsToSkip = getStringParam(mc, ParameterKey.COLUMNS_TO_SKIP);
        final Optional<EmptyCsvValueType> treatEmptyCsvValueAs = getEnumParam(mc, ParameterKey.CSV_EMPTY_VALUES, EmptyCsvValueType.class);
        final Optional<String> jsonKeysQuery = getStringParam(mc, ParameterKey.JSON_KEYS);
        final Optional<String> dataTypesQuery = getStringParam(mc, ParameterKey.DATA_TYPES);
        final Optional<String> rootJsonKey = getStringParam(mc, ParameterKey.ROOT_JSON_KEY); // todo:: implement this

        String[] header = CsvTransformer.getHeader(mc, headerAvailability.orElse(HeaderAvailability.ABSENT), valueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR));
        int linesToSkip = CsvTransformer.getLinesToSkip(headerAvailability.orElse(HeaderAvailability.ABSENT), skipHeader, dataRowsToSkip.orElse(0));
        String[] jsonKeys = generateJsonKeys(jsonKeysQuery.orElse(""), header);
        String[] dataTypes = getDataTypes(dataTypesQuery.orElse(""));

        Stream<String[]> csvArrayStream = mc.getCsvArrayStream(linesToSkip, valueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR));
        if (columnsToSkip.isPresent()) {
            csvArrayStream = skipColumns(mc, columnsToSkip.get(), csvArrayStream);
        }

        csvArrayStream
                .map(row -> {
                    JsonObject jsonObject = new JsonObject();

                    for (int i = 0; i < row.length; i++) {
                        JsonPrimitive value = getCellValue(row, i, treatEmptyCsvValueAs.orElse(EmptyCsvValueType.NULL), dataTypes);
                        String key = getJsonKey(jsonKeys, i);
                        jsonObject.add(key, value);
                    }

                    return jsonObject;
                })
                .collect(mc.collectToJsonArray());
    }

    private String[] generateJsonKeys(String jsonKeysQuery, String[] csvHeader) {
        String[] jsonKeys;

        if (StringUtils.isNotBlank(jsonKeysQuery)) {
            jsonKeys = jsonKeysQuery.split(Constants.DEFAULT_EXPRESSION_SPLITTER);
        } else {
            jsonKeys = csvHeader.clone();
        }

        return jsonKeys;
    }

    private String getJsonKey(String[] header, int index) {
        String headerValue;
        if (index < header.length) {
            headerValue = header[index];
        } else {
            headerValue = String.format("key-%d", index + 1);
        }
        return headerValue;
    }

    private JsonPrimitive getCellValue(String[] row, int index, EmptyCsvValueType emptyCsvValueType, String[] dataTypes) {

        JsonPrimitive cellValue = null;
        String cellValueString = row[index];

        if (StringUtils.isNotBlank(cellValueString)) {
            cellValue = convertCellType(cellValueString, dataTypes, index);
        } else if (emptyCsvValueType == EmptyCsvValueType.EMPTY) {
            cellValue = new JsonPrimitive("");
        }

        return cellValue;
    }

    private JsonPrimitive convertCellType(String cellValueString, String[] dataTypes, int index) {

        if (dataTypes != null) {
            try {
                String dataTypeString = dataTypes[index];
                JsonDataType dataType = JsonDataType.valueOf(dataTypeString.trim().toUpperCase());
                switch (dataType) {
                    case NUMBER:
                        return new JsonPrimitive(Double.parseDouble(cellValueString));
                    case INTEGER:
                        return new JsonPrimitive(Integer.parseInt(cellValueString));
                    case BOOLEAN:
                        return new JsonPrimitive(Boolean.parseBoolean(cellValueString));
                    default:
                        return new JsonPrimitive(cellValueString);
                }

            } catch (Exception e) {
                log.debug("Error converting csv data to given json type : ", e);
            }
        }

        return new JsonPrimitive(cellValueString);

    }

    private String[] getDataTypes(String dataTypesQuery) {

        String[] dataTypes = new String[]{};

        if (StringUtils.isNotBlank(dataTypesQuery)) {
            dataTypes = dataTypesQuery.split(Constants.DEFAULT_EXPRESSION_SPLITTER);
        }

        return dataTypes;
    }
}

