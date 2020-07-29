package org.wso2.carbon.module.csv;

import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.models.CsvPayloadInfo;
import org.wso2.carbon.module.csv.constant.Constants;
import org.wso2.carbon.module.csv.constant.HeaderAvailability;
import org.wso2.carbon.module.csv.constant.OrderingType;
import org.wso2.carbon.module.csv.constant.ParameterKey;
import org.wso2.carbon.module.csv.util.CsvTransformer;

import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.CsvTransformer.resolveColumnIndex;
import static org.wso2.carbon.module.csv.util.CsvTransformer.skipColumnsSingleRow;
import static org.wso2.carbon.module.csv.util.PropertyReader.*;

public class CsvToCsvTransformer extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        final HeaderAvailability headerAvailability = getEnumParam(mc, ParameterKey.IS_HEADER_PRESENT, HeaderAvailability.class, HeaderAvailability.ABSENT);
        final Optional<Character> valueSeparatorOptional = getCharParam(mc, ParameterKey.VALUE_SEPARATOR);
        final char valueSeparator = valueSeparatorOptional.orElse(Constants.DEFAULT_CSV_SEPARATOR);
        final boolean skipHeader = getBooleanParam(mc, ParameterKey.SKIP_HEADER);
        final Optional<Integer> dataRowsToSkip = getIntegerParam(mc, ParameterKey.DATA_ROWS_TO_SKIP);
        final Optional<String> columnsToSkipQuery = getStringParam(mc, ParameterKey.COLUMNS_TO_SKIP);
        final Optional<String> orderByColumnQuery = getStringParam(mc, ParameterKey.ORDER_BY_COLUMN);
        final OrderingType columnOrdering = getEnumParam(mc, ParameterKey.SORT_COLUMNS_BY_ORDERING, OrderingType.class, OrderingType.ASCENDING);
        final Optional<String> customHeader = getStringParam(mc, ParameterKey.CUSTOM_HEADER);
        final Optional<Character> customValueSeparator = getCharParam(mc, ParameterKey.CUSTOM_VALUE_SEPARATOR);

        CsvPayloadInfo payloadInfo = new CsvPayloadInfo();
        if (headerAvailability == HeaderAvailability.PRESENT || customHeader.isPresent()) {
            payloadInfo = mc.getCsvPayloadInfo(valueSeparator);
        }
        String[] header = CsvTransformer.getHeader(payloadInfo, headerAvailability);
        int linesToSkip = CsvTransformer.getLinesToSkip(headerAvailability, dataRowsToSkip.orElse(0));

        Stream<String[]> csvArrayStream = mc.getCsvArrayStream(linesToSkip, valueSeparator);

        if (orderByColumnQuery.isPresent()) {
            csvArrayStream = reorder(resolveColumnIndex(orderByColumnQuery.get(), header), csvArrayStream, columnOrdering);
        }
        if (columnsToSkipQuery.isPresent()) {
            csvArrayStream = CsvTransformer.skipColumns(payloadInfo.getNumberOfColumns(), columnsToSkipQuery.get(), csvArrayStream, header);
        }

        String[] resultHeader = null;
        if (headerAvailability == HeaderAvailability.PRESENT) {
            if (customHeader.isPresent()) {
                resultHeader = getCustomHeader(customHeader.get());
            } else if (!skipHeader) {
                resultHeader = header;
            }
        } else if (customHeader.isPresent()) {
            resultHeader = getCustomHeader(customHeader.get());
        }
        if (columnsToSkipQuery.isPresent() && resultHeader != null) {
            resultHeader = skipColumnsSingleRow(payloadInfo.getNumberOfColumns(), columnsToSkipQuery.get(), payloadInfo.getFirstRow(), header);
        }

        csvArrayStream.collect(mc.collectToCsv(resultHeader, customValueSeparator.orElse(Constants.DEFAULT_CSV_SEPARATOR)));

    }

    private String[] getCustomHeader(String customHeader) {
        return customHeader.split(Constants.DEFAULT_EXPRESSION_SPLITTER);
    }

    private Stream<String[]> reorder(int orderByColumn, Stream<String[]> csvArrayStream, OrderingType orderingType) {
        if (orderByColumn >= 0) {
            csvArrayStream = csvArrayStream.sorted((row1, row2) -> {
                String val1 = getRowValue(row1, orderByColumn);
                String val2 = getRowValue(row2, orderByColumn);
                int comparisonResult = val1.compareTo(val2);
                if (orderingType == OrderingType.DESCENDING) {
                    comparisonResult = -comparisonResult;
                }
                return comparisonResult;
            });
        }
        return csvArrayStream;
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
