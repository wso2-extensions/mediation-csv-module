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
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.constants.Constants;
import org.wso2.carbon.module.csv.constants.ParameterKey;
import org.wso2.carbon.module.csv.enums.EmptyCsvValueType;
import org.wso2.carbon.module.csv.enums.JsonDataType;

import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.PropertyReader.getEnumParam;
import static org.wso2.carbon.module.csv.util.PropertyReader.getStringParam;

public class CsvToJsonTransformer extends AbstractCsvToAnyTransformer {

    @Override
    void mediate(SimpleMessageContext mc, Stream<String[]> csvArrayStream, String[] header) {
        final EmptyCsvValueType treatEmptyCsvValueAs = getEnumParam(mc, ParameterKey.CSV_EMPTY_VALUES, EmptyCsvValueType.class, EmptyCsvValueType.NULL);
        final Optional<String> jsonKeysQuery = getStringParam(mc, ParameterKey.JSON_KEYS);
        final Optional<String> dataTypesQuery = getStringParam(mc, ParameterKey.DATA_TYPES);
        final Optional<String> rootJsonKeyQuery = getStringParam(mc, ParameterKey.ROOT_JSON_KEY);


        String[] jsonKeys = generateObjectKeys(jsonKeysQuery.orElse(""), header);
        String[] dataTypes = getDataTypes(dataTypesQuery.orElse(""));

        csvArrayStream
                .map(row -> {
                    JsonObject jsonObject = new JsonObject();

                    for (int i = 0; i < row.length; i++) {
                        JsonPrimitive value = getCellValue(row, i, treatEmptyCsvValueAs, dataTypes);
                        String key = getObjectKey(jsonKeys, i);
                        jsonObject.add(key, value);
                    }

                    return jsonObject;
                })
                .collect(rootJsonKeyQuery.map(mc::collectToJsonArray).orElseGet(mc::collectToJsonArray));
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

