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

package org.wso2.carbon.module.csv.model;

/**
 * Schema of the JSON data type field used in the CSV to JSON transformer.
 */
public class JsonDataTypesSchema {

    private String columnNameOrIndex;
    private String isColumnName;
    private String dataType;

    public JsonDataTypesSchema() {

    }

    public JsonDataTypesSchema(String columnNameOrIndex, String isColumnName, String dataType) {

        this.columnNameOrIndex = columnNameOrIndex;
        this.isColumnName = isColumnName;
        this.dataType = dataType;
    }

    public String getColumnNameOrIndex() {

        return columnNameOrIndex;
    }

    public void setColumnNameOrIndex(String columnNameOrIndex) {

        this.columnNameOrIndex = columnNameOrIndex;
    }

    public String getDataType() {

        return dataType;
    }

    public void setDataType(String dataType) {

        this.dataType = dataType;
    }

    public String getIsColumnName() {

        return isColumnName;
    }

    public void setIsColumnName(String isColumnName) {

        this.isColumnName = isColumnName;
    }
}
