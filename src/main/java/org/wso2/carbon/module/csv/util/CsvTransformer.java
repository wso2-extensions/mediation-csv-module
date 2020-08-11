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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;
import org.wso2.carbon.module.core.models.CsvPayloadInfo;
import org.wso2.carbon.module.csv.constant.HeaderAvailability;
import org.wso2.carbon.module.csv.util.parser.Parser;
import org.wso2.carbon.module.csv.util.parser.Tokenizer;
import org.wso2.carbon.module.csv.util.parser.model.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Common transformation methods for CSV payload.
 */
public class CsvTransformer {

    private static final Log log = LogFactory.getLog(CsvTransformer.class);
    private static final Pattern doubleQuotedTextPattern = Pattern.compile("\"([^\"]+)\"");

    private CsvTransformer() {

    }

    /**
     * Get CSV header.
     * @param payloadInfo CSV payload info object.
     * @param headerAvailability Header availability.
     * @return Generated CSV header.
     */
    public static String[] getHeader(CsvPayloadInfo payloadInfo, HeaderAvailability headerAvailability) {

        String[] header = new String[]{};

        if (headerAvailability == HeaderAvailability.PRESENT) {
            if (payloadInfo.getNumberOfColumns() > 0) {
                header = payloadInfo.getFirstRow();
            } else {
                throw new SimpleMessageContextException("Invalid csv content");
            }
        }

        return header;
    }

    /**
     * Return lines to skip from CSV content.
     * @param headerAvailability Header availability.
     * @param dataRowsToSkip Data rows to skip.
     * @return Number of rows to skip from CSV content.
     */
    public static int getLinesToSkip(HeaderAvailability headerAvailability, int dataRowsToSkip) {

        int linesToSkip = dataRowsToSkip;
        if (headerAvailability == HeaderAvailability.PRESENT) {
            linesToSkip++;
        }

        return linesToSkip;
    }

    /**
     * Skip columns from CSV content.
     * @param columnCount Number of columns in the CSV content.
     * @param skipColumnsQuery Skip columns query.
     * @param csvArrayStream CSV array stream.
     * @param header CSV header.
     * @return CSV stream with skipped columns.
     */
    public static Stream<String[]> skipColumns(int columnCount, String skipColumnsQuery,
                                               Stream<String[]> csvArrayStream, String[] header) {

        Optional<int[]> skippingColumns = getSkippingColumns(columnCount, skipColumnsQuery, header);
        if (skippingColumns.isPresent()) {
            csvArrayStream = csvArrayStream
                    .map(Arrays::asList)
                    .map(LinkedList::new)
                    .map(row -> skipColumns(skippingColumns.get(), row));
        }
        return csvArrayStream;
    }

    /**
     * Skip columns from the given CSV row.
     * @param skippingColumns Columns to skip.
     * @param row List of CSV values in the row.
     * @return Row of skipped columns.
     */
    private static String[] skipColumns(int[] skippingColumns, List<String> row) {

        int[] currentSkippingColumns = skippingColumns.clone();
        for (int i = 0; i < currentSkippingColumns.length; i++) {

            for (int j = i; j < currentSkippingColumns.length; j++) {
                currentSkippingColumns[j]--;
            }
            int currentIndex = currentSkippingColumns[i];
            if (currentIndex >= 0 && currentIndex < row.size()) {
                row.remove(currentIndex);
            } else {
                log.debug("Invalid index to remove");
            }
        }
        return row.toArray(new String[]{});
    }

    /**
     * Skip columns from the given CSV row.
     * @param columnCount Number of columns in the CSV.
     * @param columnsToSkip Columns to skip.
     * @param row CSV row.
     * @param header CSV header.
     * @return CSV row of skipped columns.
     */
    public static String[] skipColumnsSingleRow(int columnCount, String columnsToSkip, String[] row, String[] header) {

        Optional<int[]> skippingColumns = getSkippingColumns(columnCount, columnsToSkip, header);
        return skippingColumns.map(ints -> skipColumns(ints, new ArrayList<>(Arrays.asList(row)))).orElse(row);
    }

    /**
     * Return columns to skip.
     * @param columnCount Number of columns in the CSV.
     * @param skipColumnsQuery Skip columns query.
     * @param header CSV header.
     * @return Columns to skip. If no need to skip colomns, then returns an empty optional.
     */
    private static Optional<int[]> getSkippingColumns(int columnCount, String skipColumnsQuery, String[] header) {

        Optional<int[]> skippingColumns;

        if (StringUtils.isNotBlank(skipColumnsQuery)) {
            try {
                skippingColumns = Optional.of(parseColumnExpression(skipColumnsQuery, columnCount, header));
            } catch (Exception e) {
                log.debug("Invalid Skipping Columns query (no columns would be skipped)", e);
                skippingColumns = Optional.empty();
            }
        } else {
            skippingColumns = Optional.empty();
        }

        return skippingColumns;

    }

    /**
     * Parse a column expression.
     * @param skippingColumnsQuery Query to parse.
     * @param columnCount Number of columns in CSV.
     * @param header CSV header.
     * @return Parsed column indices.
     */
    private static int[] parseColumnExpression(String skippingColumnsQuery, int columnCount, String[] header) {

        skippingColumnsQuery = preProcessExpression(skippingColumnsQuery, header);
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();

        tokenizer.tokenize(skippingColumnsQuery);
        LinkedList<Token> tokens = new LinkedList<>(tokenizer.getTokens());

        return parser.parseAndGetValues(tokens, columnCount).stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Pre-process a column expression.
     * @param query Column expression.
     * @param header CSV header.
     * @return Pre-processed column expression.
     */
    private static String preProcessExpression(String query, String[] header) {

        String queryClone = query;
        Matcher matcher = doubleQuotedTextPattern.matcher(query);
        while (matcher.find()) {
            String match = matcher.group();
            int matchingColumnIndex = getMatchingColumnIndex(header, match);
            if (matchingColumnIndex >= 0) {
                matchingColumnIndex++;
            } else {
                log.error("Invalid column skipping query (no columns would be skipped) : " + query);
                return queryClone;
            }
            query = query.replaceFirst(match, String.valueOf(matchingColumnIndex));

        }
        return query;
    }

    /**
     * Get column index by the given query.
     * @param query Column query.
     * @param header CSV header.
     * @return Index resolved by the given query. -1 if no column is resolved.
     */
    public static int resolveColumnIndex(String query, String[] header) {

        int columnIndex = -1;
        if (header.length == 0) {
            columnIndex = extractColumnIndex(query) - 1;
        } else {
            Matcher matcher = doubleQuotedTextPattern.matcher(query);
            if (matcher.find()) {
                columnIndex = getMatchingColumnIndex(header, matcher.group());
            } else {
                columnIndex = extractColumnIndex(query) - 1;
            }
        }
        return columnIndex;
    }

    /**
     * Get column index for the given match from column query.
     * @param header CSV header.
     * @param match Match from the column query.
     * @return Index of the given match.
     */
    private static int getMatchingColumnIndex(String[] header, String match) {

        int columnIndex = -1;
        String columnName = match.replaceAll("\"", "");
        for (int i = 0; i < header.length; i++) {
            if (header[i].equalsIgnoreCase(columnName)) {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
    }

    /**
     * Extract column index from integer string.
     * @param intQuery Integer string.
     * @return Extract column index from integer string. If error, then returns -1.
     */
    private static int extractColumnIndex(String intQuery) {

        try {
            return Integer.parseInt(intQuery);
        } catch (NumberFormatException e) {
            log.error("Invalid query to select columns: " + intQuery);
            return -1;
        }
    }
}
