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
import org.wso2.carbon.module.csv.constant.EmptyCsvValueType;
import org.wso2.carbon.module.csv.constant.JsonDataType;
import org.wso2.carbon.module.csv.constant.ParameterKey;
import org.wso2.carbon.module.csv.model.JsonDataTypesSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.CsvTransformer.resolveColumnIndex;
import static org.wso2.carbon.module.csv.util.PropertyReader.getEnumParam;
import static org.wso2.carbon.module.csv.util.PropertyReader.getJsonArrayParam;
import static org.wso2.carbon.module.csv.util.PropertyReader.getStringParam;

public class CsvToJsonTransformer extends AbstractCsvToAnyTransformer {

    @Override
    void mediate(SimpleMessageContext mc, Stream<String[]> csvArrayStream, String[] header) {

        final EmptyCsvValueType treatEmptyCsvValueAs =
                getEnumParam(mc, ParameterKey.CSV_EMPTY_VALUES, EmptyCsvValueType.class, EmptyCsvValueType.NULL);
        final Optional<String> jsonKeysQuery = getStringParam(mc, ParameterKey.JSON_KEYS);
        final List<JsonDataTypesSchema> dataTypesSchemaList =
                getJsonArrayParam(mc, ParameterKey.DATA_TYPES, JsonDataTypesSchema.class);
        final Optional<String> rootJsonKeyQuery = getStringParam(mc, ParameterKey.ROOT_JSON_KEY);

        String[] jsonKeys = generateObjectKeys(jsonKeysQuery.orElse(""), header);
        Map<Integer, String> dataTypesMap = getDataTypes(dataTypesSchemaList, header);

        csvArrayStream
                .map(row -> {
                    JsonObject jsonObject = new JsonObject();

                    for (int i = 0; i < row.length; i++) {
                        String dataTypeString = dataTypesMap.getOrDefault(i, JsonDataType.STRING.toString());
                        JsonPrimitive value = getCellValue(row, i, treatEmptyCsvValueAs, dataTypeString);
                        String key = getObjectKey(jsonKeys, i);
                        jsonObject.add(key, value);
                    }

                    return jsonObject;
                })
                .collect(rootJsonKeyQuery.map(mc::collectToJsonArray).orElseGet(mc::collectToJsonArray));
    }

    private JsonPrimitive getCellValue(String[] row, int index, EmptyCsvValueType emptyCsvValueType,
                                       String dataTypeString) {

        JsonPrimitive cellValue = null;
        String cellValueString = row[index];

        if (StringUtils.isNotBlank(cellValueString)) {
            cellValue = convertCellType(cellValueString, dataTypeString);
        } else if (emptyCsvValueType == EmptyCsvValueType.EMPTY) {
            cellValue = new JsonPrimitive("");
        }

        return cellValue;
    }

    private JsonPrimitive convertCellType(String cellValueString, String dataTypeString) {

        if (dataTypeString != null) {
            try {
                JsonDataType dataType = JsonDataType.valueOf(dataTypeString.trim().toUpperCase());
                switch (dataType) {
                    case NUMBER:
                        return new JsonPrimitive(Double.parseDouble(cellValueString));
                    case INTEGER:
                        return new JsonPrimitive(Integer.parseInt(cellValueString));
                    case BOOLEAN:
                        return new JsonPrimitive(Boolean.parseBoolean(cellValueString));
                    case STRING:
                    default:
                        return new JsonPrimitive(cellValueString);
                }

            } catch (Exception e) {
                log.debug("Error converting csv data to given json type : ", e);
            }
        }

        return new JsonPrimitive(cellValueString);

    }

    private Map<Integer, String> getDataTypes(List<JsonDataTypesSchema> dataTypesSchemaList, String[] header) {

        Map<Integer, String> dataTypeMap = new HashMap<>();

        for (JsonDataTypesSchema jsonDataTypesSchema : dataTypesSchemaList) {
            String columnIdentifierQuery = jsonDataTypesSchema.getColumn();
            int columnIndex = resolveColumnIndex(columnIdentifierQuery, header);
            if (columnIndex >= 0) {
                String columnDataTypeValue = jsonDataTypesSchema.getType();
                dataTypeMap.put(columnIndex, columnDataTypeValue);
            }
        }

        return dataTypeMap;
    }
}

