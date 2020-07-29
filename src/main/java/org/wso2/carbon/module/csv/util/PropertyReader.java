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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PropertyReader {

    private static final Gson gson = new Gson();

    private PropertyReader() {
    }

    public static Optional<String> getStringParam(SimpleMessageContext mc, String parameterKey) {
        String parameter = (String) mc.lookupTemplateParameter(parameterKey);
        if (StringUtils.isNotBlank(parameter)) {
            return Optional.of(parameter);
        }

        return Optional.empty();
    }

    public static Optional<Character> getCharParam(SimpleMessageContext mc, String parameterKey) {
        Optional<String> parameterValue = getStringParam(mc, parameterKey);
        return parameterValue.map(s -> s.charAt(0));
    }

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

    public static boolean getBooleanParam(SimpleMessageContext mc, String parameterKey) {
        boolean skipHeader = false;

        String parameter = (String) mc.lookupTemplateParameter(parameterKey);
        if (StringUtils.isNotBlank(parameter)) {
            skipHeader = Boolean.parseBoolean(parameter);
        }
        return skipHeader;
    }

    public static <E extends Enum<E>> E getEnumParam(SimpleMessageContext mc, String parameterKey, Class<E> enumType, E defaultValue) {
        Optional<String> parameterValue = getStringParam(mc, parameterKey);
        if (parameterValue.isPresent()) {
            try {
                return Enum.valueOf(enumType, parameterValue.get().toUpperCase());
            } catch (Exception e) {
                throw new SimpleMessageContextException(String.format("Invalid parameter value for %s : %s", parameterKey, parameterValue.get()));
            }
        } else {
            return defaultValue;
        }
    }

    public static <E> List<E> getJsonArrayParam(SimpleMessageContext mc, String parameterKey, Class<E> type) {
        List<E> result;

        Optional<String> stringParamOptional = getStringParam(mc, parameterKey);
        Type resultListType = TypeToken.getParameterized(List.class, type).getType();
        if (stringParamOptional.isPresent()) {
            String paramString = stringParamOptional.get();
            try {
                result = gson.fromJson(paramString, resultListType);
            } catch (JsonSyntaxException e) {
                throw new SimpleMessageContextException(String.format("Invalid parameter value for %s", parameterKey), e);
            }
        } else {
            result = Collections.emptyList();
        }

        return result;
    }
}
