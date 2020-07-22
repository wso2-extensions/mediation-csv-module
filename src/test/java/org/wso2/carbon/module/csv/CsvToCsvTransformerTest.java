package org.wso2.carbon.module.csv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.CsvCollector;
import org.wso2.carbon.module.core.models.CsvPayloadInfo;
import org.wso2.carbon.module.csv.util.Constants;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvToCsvTransformerTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testMediate_noConfigurations_sameCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a,b,c\n" +
                "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_headerPresent_sameCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, csvPayload.get(0), Constants.DEFAULT_CSV_SEPARATOR);

        when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(csvPayload.get(0), Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a,b,c\n" +
                "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_headerPresentGivenCustomHeader_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "p,q,r\n" +
                "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_headerPresentSkipHeader_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_headerPresentSkipHeaderCustomHeaderGiven_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "p,q,r\n" +
                "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_headerAbsentCustomHeaderGiven_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Absent");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "p,q,r\n" +
                "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_headerAbsentCustomHeaderNotGiven_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Absent");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_OrderByColumn_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ORDER_BY_COLUMN)).thenReturn("2");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "2,2,4\n" +
                "1,3,3\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_OrderByColumnOrderAscending_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ORDER_BY_COLUMN)).thenReturn("2");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SORT_COLUMNS_BY_ORDERING)).thenReturn("Ascending");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "2,2,4\n" +
                "1,3,3\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_OrderByColumnOrderDescending_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ORDER_BY_COLUMN)).thenReturn("2");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SORT_COLUMNS_BY_ORDERING)).thenReturn("Descending");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,3,3\n" +
                "2,2,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_OrderByColumnWithHeader_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, csvPayload.get(0), Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ORDER_BY_COLUMN)).thenReturn("2");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(csvPayload.get(0), Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a,b,c\n" +
                "2,2,4\n" +
                "1,3,3\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_OrderByColumnWithHeaderWithSkipHeader_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ORDER_BY_COLUMN)).thenReturn("2");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "2,2,4\n" +
                "1,3,3\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_skipDataRows_correctCsvShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 2).stream());
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_skipDataRowsHeaderPresent_correctCsvShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, csvPayload.get(0), Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(2, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(2, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(csvPayload.get(0), Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a,b,c\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_skipDataRowsHeaderPresentSkipHeader_correctCsvShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(2, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(2, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_skipDataRowsHeaderPresentSkipHeaderCustomHeader_correctCsvShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("p,q,r");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(2, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(2, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(new String[]{"p", "q", "r"}, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "p,q,r\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_SeparatorDefined_sameCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, ',');

        lenient().when(mc.lookupTemplateParameter(ParameterKey.VALUE_SEPARATOR)).thenReturn("#");
        when(mc.getCsvArrayStream(0, '#')).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, ',')).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a,b,c\n" +
                "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_SeparatorDefinedWithHeader_sameCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, csvPayload.get(0), ',');

        lenient().when(mc.lookupTemplateParameter(ParameterKey.VALUE_SEPARATOR)).thenReturn("#");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        when(mc.getCsvArrayStream(1, '#')).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo('#')).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(csvPayload.get(0), ',')).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a,b,c\n" +
                "1,2,3\n" +
                "2,3,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_skipColumns_correctCsvShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("2");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,3\n" +
                "2,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_skipColumnsWithHeader_correctCsvShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, new String[]{"a", "c"}, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("2");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(new String[]{"a", "c"}, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a,c\n" +
                "1,3\n" +
                "2,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_skipColumnsWithHeaderWithSkipHeader_correctCsvShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, Constants.DEFAULT_CSV_SEPARATOR);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("2");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToCsv(null, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,3\n" +
                "2,4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_customSeparator_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null, '#');

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_VALUE_SEPARATOR)).thenReturn("#");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv(null, '#')).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a#b#c\n" +
                "1#2#3\n" +
                "2#3#4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_customSeparatorWithHeader_correctCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, csvPayload.get(0), '#');

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_VALUE_SEPARATOR)).thenReturn("#");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));

        when(mc.collectToCsv(csvPayload.get(0), '#')).thenReturn(csvCollector);
        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvToCsvTransformer csvToCsvTransformer = new CsvToCsvTransformer();
        csvToCsvTransformer.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "a#b#c\n" +
                "1#2#3\n" +
                "2#3#4\n";
        Assertions.assertEquals(expectedPayload, setPayload);
    }


}