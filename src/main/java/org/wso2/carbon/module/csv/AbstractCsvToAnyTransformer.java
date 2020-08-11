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

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.models.CsvPayloadInfo;
import org.wso2.carbon.module.csv.constant.Constants;
import org.wso2.carbon.module.csv.constant.HeaderAvailability;
import org.wso2.carbon.module.csv.constant.ParameterKey;
import org.wso2.carbon.module.csv.util.CsvTransformer;

import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.CsvTransformer.skipColumns;
import static org.wso2.carbon.module.csv.util.CsvTransformer.skipColumnsSingleRow;
import static org.wso2.carbon.module.csv.util.PropertyReader.getBooleanParam;
import static org.wso2.carbon.module.csv.util.PropertyReader.getCharParam;
import static org.wso2.carbon.module.csv.util.PropertyReader.getEnumParam;
import static org.wso2.carbon.module.csv.util.PropertyReader.getIntegerParam;
import static org.wso2.carbon.module.csv.util.PropertyReader.getStringParam;

/**
 * Abstract transformer with basic CSV transformation logics. Any transformer used to transform CSV to another data
 * type can use this to get the basic CSV transformation functionalities.
 */
abstract class AbstractCsvToAnyTransformer extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        final HeaderAvailability headerAvailability =
                getEnumParam(mc, ParameterKey.IS_HEADER_PRESENT, HeaderAvailability.class, HeaderAvailability.ABSENT);
        final Optional<Character> valueSeparatorOptional = getCharParam(mc, ParameterKey.VALUE_SEPARATOR);
        final char valueSeparator = valueSeparatorOptional.orElse(Constants.DEFAULT_CSV_SEPARATOR);
        final boolean skipHeader = getBooleanParam(mc, ParameterKey.SKIP_HEADER);
        final Optional<Integer> dataRowsToSkip = getIntegerParam(mc, ParameterKey.DATA_ROWS_TO_SKIP);
        final Optional<String> columnsToSkipQuery = getStringParam(mc, ParameterKey.COLUMNS_TO_SKIP);

        CsvPayloadInfo csvPayloadInfo = new CsvPayloadInfo();
        if (headerAvailability == HeaderAvailability.PRESENT) {
            csvPayloadInfo = mc.getCsvPayloadInfo(valueSeparator);
        }
        int linesToSkip = CsvTransformer.getLinesToSkip(headerAvailability, dataRowsToSkip.orElse(0));
        String[] header = CsvTransformer.getHeader(csvPayloadInfo, headerAvailability);
        Stream<String[]> csvArrayStream = mc.getCsvArrayStream(linesToSkip, valueSeparator);
        if (columnsToSkipQuery.isPresent()) {
            csvArrayStream =
                    skipColumns(csvPayloadInfo.getNumberOfColumns(), columnsToSkipQuery.get(), csvArrayStream, header);
            if (headerAvailability == HeaderAvailability.PRESENT) {
                header = skipColumnsSingleRow(csvPayloadInfo.getNumberOfColumns(), columnsToSkipQuery.get(),
                        csvPayloadInfo.getFirstRow(), header);
            }
        }
        if (headerAvailability == HeaderAvailability.PRESENT && !skipHeader) {
            csvArrayStream = Stream.concat(Stream.<String[]>of(header), csvArrayStream);
        }

        mediate(mc, csvArrayStream, header);
    }

    /**
     * Generate final header to use by considering the CSV header and the header query if present
     * @param objectKeysQuery @Nullable String representing the header to replace. This String should be split
     *                        using the default splitter.
     * @param csvHeader Header of the CSV content
     * @return Final result of the header that need to use in transformation
     */
    String[] generateObjectKeys(String objectKeysQuery, String[] csvHeader) {

        String[] objectKeys;
        if (StringUtils.isNotBlank(objectKeysQuery)) {
            objectKeys = objectKeysQuery.split(Constants.DEFAULT_EXPRESSION_SPLITTER);
        } else {
            objectKeys = csvHeader.clone();
        }
        return objectKeys;
    }

    /**
     * Return the key to use for the element represents by the given index. If no appropriate value found in the
     * given header then, returns an auto generated value.
     * @param header CSV header values
     * @param index Index of the value in CSV row array.
     * @return Key value to use for the given value.
     */
    String getObjectKey(String[] header, int index) {

        String headerValue;
        if (index < header.length) {
            headerValue = header[index];
        } else {
            headerValue = String.format("%s-%d", Constants.DEFAULT_OBJECT_KEY_PREFIX, index + 1);
        }
        return headerValue;
    }

    /**
     * mediate method to be extended by child classes to get the pre-processed CSV content.
     * @param mc SimpleMessageContext
     * @param csvArrayStream Pre-processed CSV content as Stream of CSV content.
     * @param header Header for the CSV payload. If no header specified, then gives an empty array.
     */
    abstract void mediate(SimpleMessageContext mc, Stream<String[]> csvArrayStream, String[] header);
}
