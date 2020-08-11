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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.constant.ParameterKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.wso2.carbon.module.csv.constant.Constants.DEFAULT_EXPRESSION_SPLITTER;

/**
 * Transformer to transform JSON content to a CSV content.
 */
public class JsonToCsvTransformer extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        String[] header = getHeader(mc);

        mc.getJsonArrayStream()
                .map(JsonElement::getAsJsonObject)
                .map(obj -> {
                    List<String> csvEntry = new ArrayList<>();
                    Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
                    for (Map.Entry<String, JsonElement> elementEntry : entries) {
                        csvEntry.add(elementEntry.getValue().getAsString());
                    }

                    return csvEntry.toArray(new String[]{});
                })
                .collect(mc.collectToCsv(header));
    }

    /**
     * Returns the header to use as the CSV header.
     * @param mc SimpleMessageContext to use.
     * @return Header to use as the CSV header.
     */
    private String[] getHeader(SimpleMessageContext mc) {

        String[] header = null;

        String headerToAppend = (String) mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER);
        if (!StringUtils.isBlank(headerToAppend)) {
            header = headerToAppend.split(DEFAULT_EXPRESSION_SPLITTER);
        } else {
            JsonElement jsonPayload = mc.getJsonElement();
            if (jsonPayload.isJsonArray()) {
                JsonArray jsonArray = jsonPayload.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonElement firstElement = jsonArray.get(0);
                    if (firstElement.isJsonObject()) {
                        JsonObject firstJsonObject = firstElement.getAsJsonObject();
                        return firstJsonObject.keySet().toArray(new String[]{});
                    }
                }
            }

        }

        return header;
    }
}
