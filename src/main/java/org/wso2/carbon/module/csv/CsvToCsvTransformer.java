package org.wso2.carbon.module.csv;

import org.apache.synapse.SynapseException;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.enums.HeaderAvailability;
import org.wso2.carbon.module.csv.enums.OrderingType;
import org.wso2.carbon.module.csv.util.Constants;
import org.wso2.carbon.module.csv.util.CsvTransformer;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.PropertyReader.*;

public class CsvToCsvTransformer extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        final Optional<HeaderAvailability> headerAvailability = getEnumParam(mc, ParameterKey.IS_HEADER_PRESENT, HeaderAvailability.class);
        final Optional<Character> valueSeparator = getCharParam(mc, ParameterKey.VALUE_SEPARATOR);
        final boolean skipHeader = getBooleanParam(mc, ParameterKey.SKIP_HEADER);
        final Optional<Integer> dataRowsToSkip = getIntegerParam(mc, ParameterKey.DATA_ROWS_TO_SKIP);
        final Optional<String> columnsToSkip = getStringParam(mc, ParameterKey.COLUMNS_TO_SKIP);
        final Optional<Integer> orderByColumn = getIntegerParam(mc, ParameterKey.ORDER_BY_COLUMN); // todo :: add support for column names
        final Optional<OrderingType> columnOrdering = getEnumParam(mc, ParameterKey.SORT_COLUMNS_BY_ORDERING, OrderingType.class);
        final Optional<String> customHeader = getStringParam(mc, ParameterKey.CUSTOM_HEADER);
        final Optional<Character> customValueSeparator = getCharParam(mc, ParameterKey.CUSTOM_VALUE_SEPARATOR);

        String[] header = CsvTransformer.getHeader(mc, headerAvailability.orElse(HeaderAvailability.ABSENT), valueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR));
        int linesToSkip = CsvTransformer.getLinesToSkip(headerAvailability.orElse(HeaderAvailability.ABSENT), skipHeader, dataRowsToSkip.orElse(0));

        Stream<String[]> csvArrayStream = mc.getCsvArrayStream(linesToSkip, valueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR));

        if (orderByColumn.isPresent()) {
            csvArrayStream = reorder(orderByColumn.get(), csvArrayStream, columnOrdering.orElse(OrderingType.ASCENDING));
        }

        if (columnsToSkip.isPresent()) {
            csvArrayStream = CsvTransformer.skipColumns(mc, columnsToSkip.get(), csvArrayStream);
        }

        String[] resultHeader;
        if (skipHeader && customHeader.isPresent()) {
            resultHeader = customHeader.get().split(Constants.DEFAULT_EXPRESSION_SPLITTER);
        } else {
            resultHeader = header;
        }
        csvArrayStream.collect(mc.collectToCsv(resultHeader, customValueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR)));

    }

    private Stream<String[]> reorder(int orderByColumn, Stream<String[]> csvArrayStream, OrderingType orderingType) {
        int sortByColumnIndex = getOrderByColumnIndex(orderByColumn);
        csvArrayStream = csvArrayStream.sorted((row1, row2) -> {
            String val1 = getRowValue(row1, sortByColumnIndex);
            String val2 = getRowValue(row2, sortByColumnIndex);
            int comparisonResult = val1.compareTo(val2);
            if (orderingType == OrderingType.DESCENDING) {
                comparisonResult = -comparisonResult;
            }
            return comparisonResult;
        });
        return csvArrayStream;
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
