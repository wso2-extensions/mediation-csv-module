package org.wso2.carbon.module.csv;

import org.apache.commons.lang.StringUtils;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;
import org.wso2.carbon.module.csv.util.Constants;
import org.wso2.carbon.module.csv.util.ParameterKey;
import org.wso2.carbon.module.csv.util.parser.Parser;
import org.wso2.carbon.module.csv.util.parser.Tokenizer;
import org.wso2.carbon.module.csv.util.parser.model.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.PropertyReader.*;

public class CsvToCsvTransformer extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        final boolean isHeaderPresent = getBooleanParam(mc, ParameterKey.IS_HEADER_PRESENT); //todo:: change this to present/absent instead of boolean
        final Optional<Character> valueSeparator = getCharParam(mc, ParameterKey.VALUE_SEPARATOR);
        final boolean skipHeader = getBooleanParam(mc, ParameterKey.SKIP_HEADER);
        final Optional<Integer> dataRowsToSkip = getIntegerParam(mc, ParameterKey.DATA_ROWS_TO_SKIP);
        final Optional<String> columnsToSkip = getStringParam(mc, ParameterKey.COLUMNS_TO_SKIP);
        final Optional<Integer> orderByColumn = getIntegerParam(mc, ParameterKey.ORDER_BY_COLUMN); // todo :: add support for column names
        final Optional<String> customHeader = getStringParam(mc, ParameterKey.CUSTOM_HEADER);
        final Optional<Character> customValueSeparator = getCharParam(mc, ParameterKey.CUSTOM_VALUE_SEPARATOR);

        String[] header = getHeader(mc, isHeaderPresent, valueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR));
        int linesToSkip = getLinesToSkip(isHeaderPresent, skipHeader, dataRowsToSkip.orElse(0));

        Stream<String[]> csvArrayStream = mc.getCsvArrayStream(linesToSkip, valueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR));

        if (orderByColumn.isPresent()) {
            csvArrayStream = reorder(orderByColumn.get(), csvArrayStream);
        }
        if (columnsToSkip.isPresent()) {
            csvArrayStream = skipColumns(mc, columnsToSkip.get(), csvArrayStream);
        }

        String[] resultHeader;
        if (skipHeader && customHeader.isPresent()) {
            resultHeader = customHeader.get().split(Constants.DEFAULT_EXPRESSION_SPLITTER);
        } else {
            resultHeader = header;
        }
        csvArrayStream.collect(mc.collectToCsv(resultHeader, customValueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR)));

    }

    private Stream<String[]> skipColumns(SimpleMessageContext mc, String columnsToSkip, Stream<String[]> csvArrayStream) {
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

    private Stream<String[]> reorder(int orderByColumn, Stream<String[]> csvArrayStream) {
        int sortByColumnIndex = getOrderByColumnIndex(orderByColumn);
        csvArrayStream = csvArrayStream.sorted((row1, row2) -> {
            String val1 = getRowValue(row1, sortByColumnIndex);
            String val2 = getRowValue(row2, sortByColumnIndex);
            int comparisonResult = val1.compareTo(val2);
            // fixme
    /*        if (inverseSort) {
                comparisonResult = -comparisonResult;
            }*/
            return comparisonResult;
        });
        return csvArrayStream;
    }

    private String[] getHeader(SimpleMessageContext mc, boolean isHeaderPresent, char valueSeparator) {
        String[] header = null;

        if (isHeaderPresent) {
            List<String[]> csvPayload = mc.getCsvPayload(0, valueSeparator);
            if (!csvPayload.isEmpty()) {
                header = csvPayload.get(0);
            } else {
                throw new SimpleMessageContextException("Invalid csv content");
            }
        }

        return header;
    }

    private int getLinesToSkip(boolean isHeaderPresent, boolean skipHeader, int dateRowsToSkip) {
        int linesToSkip = dateRowsToSkip;
        if (isHeaderPresent && skipHeader) {
            linesToSkip++;
        }

        return linesToSkip;
    }


    private Optional<int[]> getSkippingColumns(SimpleMessageContext mc, String columnsToSkip) {
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

    private int[] parseExpression(String skippingColumnsString, int columnCount) {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();

        tokenizer.tokenize(skippingColumnsString.trim());
        LinkedList<Token> tokens = new LinkedList<>(tokenizer.getTokens());

        return parser.parseAndGetValues(tokens, columnCount).stream().mapToInt(Integer::intValue).toArray();
    }

    private int getOrderByColumnIndex(int orderByColumn) {
        int index = orderByColumn - 1;
        if (index < 0) {
            throw new SynapseException("sort by column value should be greater than 0");
        }
        return index;
    }

    private String getRowValue(String[] row, int index) {
        final int rowLength = row.length;
        if (index >= rowLength) {
            return "";
        } else {
            return row[index];
        }
    }
}
