package org.wso2.carbon.module.csv.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;
import org.wso2.carbon.module.core.models.CsvPayloadInfo;
import org.wso2.carbon.module.csv.enums.HeaderAvailability;
import org.wso2.carbon.module.csv.util.parser.Parser;
import org.wso2.carbon.module.csv.util.parser.Tokenizer;
import org.wso2.carbon.module.csv.util.parser.model.Token;

import java.util.*;
import java.util.stream.Stream;

public class CsvTransformer {

    private static final Log log = LogFactory.getLog(CsvTransformer.class);

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

    public static Stream<String[]> skipColumns(int columnCount, String columnsToSkip, char separator, Stream<String[]> csvArrayStream) {
        Optional<int[]> skippingColumns = getSkippingColumns(columnCount, columnsToSkip, separator);
        if (skippingColumns.isPresent()) {
            csvArrayStream = csvArrayStream
                    .map(Arrays::asList)
                    .map(LinkedList::new)
                    .map(row -> {

                        return skipColumns(skippingColumns.get(), row);
                    });
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

    public static String[] skipColumnsSingleRow(int columnCount, String columnsToSkip, char separator, String[] row) {
        Optional<int[]> skippingColumns = getSkippingColumns(columnCount, columnsToSkip, separator);
        return skippingColumns.map(ints -> skipColumns(ints, new ArrayList<>(Arrays.asList(row)))).orElse(row);
    }

    private static Optional<int[]> getSkippingColumns(int columnCount, String columnsToSkip, char separator) {
        Optional<int[]> skippingColumns;

        if (StringUtils.isNotBlank(columnsToSkip)) {
            try {
                skippingColumns = Optional.of(parseExpression(columnsToSkip, columnCount));
            } catch (Exception e) {
                log.debug("Invalid Skipping Columns query", e);
                skippingColumns = Optional.empty();
            }
        } else {
            skippingColumns = Optional.empty();
        }

        return skippingColumns;

    }

    private static int[] parseExpression(String skippingColumnsString, int columnCount) {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();

        tokenizer.tokenize(skippingColumnsString.trim());
        LinkedList<Token> tokens = new LinkedList<>(tokenizer.getTokens());

        return parser.parseAndGetValues(tokens, columnCount).stream().mapToInt(Integer::intValue).toArray();
    }
}
