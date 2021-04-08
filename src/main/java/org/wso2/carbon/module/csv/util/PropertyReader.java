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

package org.wso2.carbon.module.csv.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Common methods to use to read properties.
 */
public class PropertyReader {

    private static final Gson gson =
            new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES)
                    .setPrettyPrinting()
                    .create();

    private PropertyReader() {

    }

    /**
     * Read a String parameter
     * @param mc SimpleMessageContext.
     * @param parameterKey Key of the parameter.
     * @return Optional String of the parameter value.
     */
    public static Optional<String> getStringParam(SimpleMessageContext mc, String parameterKey) {

        String parameter = (String) mc.lookupTemplateParameter(parameterKey);
        if (StringUtils.isNotBlank(parameter)) {
            return Optional.of(parameter);
        }
        return Optional.empty();
    }

    /**
     * Read a Character parameter.
     * @param mc SimpleMessageContext.
     * @param parameterKey Key of the parameter.
     * @return Optional Character of the parameter value.
     */
    public static Optional<Character> getCharParam(SimpleMessageContext mc, String parameterKey) {

        Optional<String> parameterValue = getStringParam(mc, parameterKey);
        return parameterValue.map(PropertyReader::replaceWhitespaces);
    }

    private static Character replaceWhitespaces(String text) {

        if (text.equals("tab")) {
            return 9;
        } else if (text.equals("space")) {
            return 32;
        } else {
            return text.charAt(0);
        }
    }

    /**
     * Read an Integer parameter.
     * @param mc SimpleMessageContext.
     * @param parameterKey Key of the parameter.
     * @return Optional Integer of the parameter value.
     */
    public static Optional<Integer> getIntegerParam(SimpleMessageContext mc, String parameterKey) {

        Optional<String> parameterValue = getStringParam(mc, parameterKey);
        return parameterValue.map(s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }
        });
    }

    /**
     * Read a boolean parameter.
     * @param mc SimpleMessageContext.
     * @param parameterKey Key of the parameter.
     * @return Boolean of the parameter value.
     */
    public static boolean getBooleanParam(SimpleMessageContext mc, String parameterKey) {

        boolean skipHeader = false;
        String parameter = (String) mc.lookupTemplateParameter(parameterKey);
        if (StringUtils.isNotBlank(parameter)) {
            skipHeader = Boolean.parseBoolean(parameter);
        }
        return skipHeader;
    }

    /**
     * Read an Enum parameter.
     * @param mc SimpleMessageContext.
     * @param parameterKey Key of the parameter.
     * @param enumType Type of the enum.
     * @param defaultValue Default value of the enum.
     * @param <E> Enum type.
     * @return Enum value for the parameter value.
     */
    public static <E extends Enum<E>> E getEnumParam(SimpleMessageContext mc, String parameterKey, Class<E> enumType,
                                                     E defaultValue) {

        Optional<String> parameterValue = getStringParam(mc, parameterKey);
        if (parameterValue.isPresent()) {
            try {
                return Enum.valueOf(enumType, parameterValue.get().toUpperCase());
            } catch (Exception e) {
                throw new SimpleMessageContextException(
                        String.format("Invalid parameter value for %s : %s", parameterKey, parameterValue.get()));
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Read JSON array parameter.
     * @param mc SimpleMessageContext.
     * @param parameterKey Key of the parameter.
     * @param type JSON object type.
     * @param <E> JSON Object type.
     * @return List of JSON Objects of the parameter value.
     */
    public static <E> List<E> getJsonArrayParam(SimpleMessageContext mc, String parameterKey, Class<E> type) {

        List<E> result;
        Optional<String> stringParamOptional = getStringParam(mc, parameterKey);
        Type resultListType = TypeToken.getParameterized(List.class, type).getType();
        if (stringParamOptional.isPresent()) {
            String paramString = stringParamOptional.get();
            try {
                result = gson.fromJson(paramString, resultListType);
            } catch (JsonSyntaxException e) {
                throw new SimpleMessageContextException(String.format("Invalid parameter value for %s", parameterKey),
                        e);
            }
        } else {
            result = Collections.emptyList();
        }
        return result;
    }
}
