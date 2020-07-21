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

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class PropertyReader {

    private PropertyReader() {

    }

    public static int getNumberOfLinesToSkip(SimpleMessageContext mc, int defaultValue) {
        int linesToSkip = defaultValue;

        String linesToSkipString = (String) mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP);
        if (!StringUtils.isBlank(linesToSkipString)) {
            try {
                linesToSkip = Integer.parseInt(linesToSkipString);
            } catch (NumberFormatException e) {
                throw new SimpleMessageContextException("Invalid value for parameter: " + ParameterKey.LINES_TO_SKIP);
            }
        }

        return linesToSkip;
    }

    public static int getNumberOfLinesToSkip(SimpleMessageContext mc) {
        return getNumberOfLinesToSkip(mc, 0);
    }

    public static String[] getHeader(SimpleMessageContext mc, boolean useHeaderAsKey) {
        String[] header;

        if (useHeaderAsKey) {
            List<String[]> csvPayload = mc.getCsvPayload(0);
            if (!csvPayload.isEmpty()) {
                header = csvPayload.get(0);
            } else {
                throw new SimpleMessageContextException("Invalid csv content");
            }
        } else {
            String headerToAppend = (String) mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER);
            if (!StringUtils.isBlank(headerToAppend)) {
                header = headerToAppend.split(",");
            } else {
                List<String[]> csvPayload = mc.getCsvPayload(0);
                if (!csvPayload.isEmpty()) {
                    int numberOfColumns = csvPayload.get(0).length;
                    header = IntStream.range(1, numberOfColumns + 1)
                            .mapToObj(index -> "key-" + index)
                            .map(Object::toString)
                            .toArray(String[]::new);
                } else {
                    throw new SimpleMessageContextException("Invalid csv content");
                }
            }
        }

        return header;
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

    public static <E extends Enum<E>> Optional<E> getEnumParam(SimpleMessageContext mc, String parameterKey, Class<E> enumType) {
        Optional<String> parameterValue = getStringParam(mc, parameterKey);
        if (parameterValue.isPresent()) {
            try {
                return Optional.of(Enum.valueOf(enumType, parameterValue.get().toUpperCase()));
            } catch (Exception e) {
                throw new SimpleMessageContextException(String.format("Invalid parameter value for %s : %s", parameterKey, parameterValue.get()));
            }
        } else {
            return Optional.empty();
        }
    }

}
