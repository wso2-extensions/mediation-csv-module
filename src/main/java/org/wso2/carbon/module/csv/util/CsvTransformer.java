package org.wso2.carbon.module.csv.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;
import org.wso2.carbon.module.csv.enums.HeaderAvailability;
import org.wso2.carbon.module.csv.util.parser.Parser;
import org.wso2.carbon.module.csv.util.parser.Tokenizer;
import org.wso2.carbon.module.csv.util.parser.model.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CsvTransformer {

    private static final Log log = LogFactory.getLog(CsvTransformer.class);

    private CsvTransformer() {
    }

    public static String[] getHeader(SimpleMessageContext mc, HeaderAvailability headerAvailability, char valueSeparator) {
        String[] header = new String[]{};

        if (headerAvailability == HeaderAvailability.PRESENT) {
            List<String[]> csvPayload = mc.getCsvPayload(0, valueSeparator);
            if (!csvPayload.isEmpty()) {
                header = csvPayload.get(0);
            } else {
                throw new SimpleMessageContextException("Invalid csv content");
            }
        }

        return header;
    }

    public static int getLinesToSkip(HeaderAvailability headerAvailability, boolean skipHeader, int dateRowsToSkip) {
        int linesToSkip = dateRowsToSkip;
        if (headerAvailability == HeaderAvailability.PRESENT && skipHeader) {
            linesToSkip++;
        }

        return linesToSkip;
    }

    public static Stream<String[]> skipColumns(SimpleMessageContext mc, String columnsToSkip, Stream<String[]> csvArrayStream) {
        Optional<int[]> skippingColumns = getSkippingColumns(mc, columnsToSkip);
        if (skippingColumns.isPresent()) {
            csvArrayStream = csvArrayStream
                    .map(Arrays::asList)
                    .map(LinkedList::new)
                    .map(row -> {

                        int[] currentSkippingColumns = skippingColumns.get().clone();

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
                    });
        }
        return csvArrayStream;
    }

    private static Optional<int[]> getSkippingColumns(SimpleMessageContext mc, String columnsToSkip) {
        int columnCount = mc.getCsvPayload().get(0).length;
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
