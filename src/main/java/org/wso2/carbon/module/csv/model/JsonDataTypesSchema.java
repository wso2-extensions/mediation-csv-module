package org.wso2.carbon.module.csv.model;

import com.google.gson.annotations.SerializedName;

public class JsonDataTypesSchema {
    @SerializedName("key")
    private String column;
    @SerializedName("value")
    private String dataType;

    public JsonDataTypesSchema() {
    }

    public JsonDataTypesSchema(String column, String dataType) {
        this.column = column;
        this.dataType = dataType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
