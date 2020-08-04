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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CsvTransformer {

    private static final Log log = LogFactory.getLog(CsvTransformer.class);
    private static final Pattern doubleQuotedTextPattern = Pattern.compile("\"([^\"]+)\"");

    private CsvTransformer() {
    }

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

    public static int getLinesToSkip(HeaderAvailability headerAvailability, int dateRowsToSkip) {
        int linesToSkip = dateRowsToSkip;
        if (headerAvailability == HeaderAvailability.PRESENT) {
            linesToSkip++;
        }

        return linesToSkip;
    }

    public static Stream<String[]> skipColumns(int columnCount, String skipColumnsQuery, Stream<String[]> csvArrayStream, String[] header) {
        Optional<int[]> skippingColumns = getSkippingColumns(columnCount, skipColumnsQuery, header);
        if (skippingColumns.isPresent()) {
            csvArrayStream = csvArrayStream
                    .map(Arrays::asList)
                    .map(LinkedList::new)
                    .map(row -> skipColumns(skippingColumns.get(), row));
        }
        return csvArrayStream;
    }

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

    public static String[] skipColumnsSingleRow(int columnCount, String columnsToSkip, String[] row, String[] header) {
        Optional<int[]> skippingColumns = getSkippingColumns(columnCount, columnsToSkip, header);
        return skippingColumns.map(ints -> skipColumns(ints, new ArrayList<>(Arrays.asList(row)))).orElse(row);
    }

    private static Optional<int[]> getSkippingColumns(int columnCount, String skipColumnsQuery, String[] header) {
        Optional<int[]> skippingColumns;

        if (StringUtils.isNotBlank(skipColumnsQuery)) {
            try {
                skippingColumns = Optional.of(parseExpression(skipColumnsQuery, columnCount, header));
            } catch (Exception e) {
                log.debug("Invalid Skipping Columns query (no columns would be skipped)", e);
                skippingColumns = Optional.empty();
            }
        } else {
            skippingColumns = Optional.empty();
        }

        return skippingColumns;

    }

    private static int[] parseExpression(String skippingColumnsQuery, int columnCount, String[] header) {
        skippingColumnsQuery = preProcessExpression(skippingColumnsQuery, header);
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();

        tokenizer.tokenize(skippingColumnsQuery);
        LinkedList<Token> tokens = new LinkedList<>(tokenizer.getTokens());

        return parser.parseAndGetValues(tokens, columnCount).stream().mapToInt(Integer::intValue).toArray();
    }

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

    private static int extractColumnIndex(String intQuery) {
        try {
            return Integer.parseInt(intQuery);
        } catch (NumberFormatException e) {
            log.error("Invalid query to select columns: " + intQuery);
            return -1;
        }
    }
}
