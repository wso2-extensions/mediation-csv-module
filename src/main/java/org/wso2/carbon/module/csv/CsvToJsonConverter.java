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
import org.apache.synapse.SynapseException;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.util.JsonDataType;
import org.wso2.carbon.module.csv.util.ParameterKey;
import org.wso2.carbon.module.csv.util.PropertyReader;

import static org.wso2.carbon.module.csv.util.PropertyReader.getBooleanParam;

public class CsvToJsonConverter extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {
        int linesToSkip = getLinesToSkip(mc);
        String[] jsonKeys = generateJsonKeys(mc);
        String[] dataTypes = getDataTypes(mc);
        boolean treatEmptyValueAsNull = getEmptyValueAsNullValue(mc);

        mc.getCsvArrayStream(linesToSkip)
                .map(row -> {
                    JsonObject jsonObject = new JsonObject();

                    for (int i = 0; i < row.length; i++) {
                        JsonPrimitive value = getCellValue(row, i, treatEmptyValueAsNull, dataTypes);
                        String key = getJsonKey(jsonKeys, i);
                        jsonObject.add(key, value);
                    }

                    return jsonObject;
                })
                .collect(mc.collectToJsonArray());
    }

    private String[] generateJsonKeys(SimpleMessageContext mc) {
        boolean useHeaderAsKeys = getBooleanParam(mc, ParameterKey.USE_HEADER_AS_KEYS);
        return PropertyReader.getHeader(mc, useHeaderAsKeys);
    }

    private String getJsonKey(String[] header, int index) {
        String headerValue;
        try {
            headerValue = header[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SynapseException("Header length not enough for the payload");
        }
        return headerValue;
    }

    private int getLinesToSkip(SimpleMessageContext mc) {
        boolean skipHeader = getBooleanParam(mc, ParameterKey.USE_HEADER_AS_KEYS);

        if (skipHeader) {
            return 1;
        } else {
            return 0;
        }
    }

    private JsonPrimitive getCellValue(String[] row, int index, boolean treatEmptyValueAsNull,
                                       String[] dataTypes) {

        JsonPrimitive cellValue = null;

        String cellValueString = row[index];

        if (StringUtils.isNotBlank(cellValueString)) {
            cellValue = convertCellType(cellValueString, dataTypes, index);
        } else if (!treatEmptyValueAsNull) {
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

    private boolean getEmptyValueAsNullValue(SimpleMessageContext mc) {
        boolean treatEmptyValueAsNull = getBooleanParam(mc, ParameterKey.EMPTY_VALUE_AS_NULL);
        return treatEmptyValueAsNull;
    }

    private String[] getDataTypes(SimpleMessageContext mc) {

        String[] dataTypes = null;

        String dataTypesString = (String) mc.lookupTemplateParameter(ParameterKey.DATA_TYPES);
        if (StringUtils.isNotBlank(dataTypesString)) {
            dataTypes = dataTypesString.split(",");
        }

        return dataTypes;
    }
}

