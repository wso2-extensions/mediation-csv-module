package org.wso2.carbon.module.csv;

import com.google.gson.JsonElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.JsonArrayCollector;
import org.wso2.carbon.module.core.models.CsvPayloadInfo;
import org.wso2.carbon.module.csv.constants.Constants;
import org.wso2.carbon.module.csv.constants.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvToJsonTransformerTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testMediate_noConfigurations_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"key-1\":\"a\",\"key-2\":\"b\",\"key-3\":\"c\"},{\"key-1\":\"1\",\"key-2\":\"2\",\"key-3\":\"3\"},{\"key-1\":\"2\",\"key-2\":\"3\",\"key-3\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_headerPresent_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"a\",\"b\":\"b\",\"c\":\"c\"},{\"a\":\"1\",\"b\":\"2\",\"c\":\"3\"},{\"a\":\"2\",\"b\":\"3\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_headerAbsent_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Absent");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"key-1\":\"a\",\"key-2\":\"b\",\"key-3\":\"c\"},{\"key-1\":\"1\",\"key-2\":\"2\",\"key-3\":\"3\"},{\"key-1\":\"2\",\"key-2\":\"3\",\"key-3\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }


    @Test
    void testMediate_givenSeparator_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.VALUE_SEPARATOR)).thenReturn("#");
        when(mc.getCsvArrayStream(0, '#')).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"key-1\":\"a\",\"key-2\":\"b\",\"key-3\":\"c\"},{\"key-1\":\"1\",\"key-2\":\"2\",\"key-3\":\"3\"},{\"key-1\":\"2\",\"key-2\":\"3\",\"key-3\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_headerPresentAndSkipHeader_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"1\",\"b\":\"2\",\"c\":\"3\"},{\"a\":\"2\",\"b\":\"3\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_headerPresentAndSkipHeaderFalse_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("false");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"a\",\"b\":\"b\",\"c\":\"c\"},{\"a\":\"1\",\"b\":\"2\",\"c\":\"3\"},{\"a\":\"2\",\"b\":\"3\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipColumnGiven_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("1,2");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"key-1\":\"c\"},{\"key-1\":\"3\"},{\"key-1\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipColumnsWithHeaderWithHeaderNames_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("\"b\"");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);
        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"a\",\"c\":\"c\"},{\"a\":\"1\",\"c\":\"3\"},{\"a\":\"2\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipColumnsWithHeaderComplexQuery_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c", "d", "e", "f", "g", "h"});
        csvPayload.add(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        csvPayload.add(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("\"a\":\"d\",!2,\"g\"");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 8, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);
        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"b\":\"b\",\"e\":\"e\",\"f\":\"f\",\"h\":\"h\"},{\"b\":\"2\",\"e\":\"5\",\"f\":\"6\",\"h\":\"8\"},{\"b\":\"2\",\"e\":\"5\",\"f\":\"6\",\"h\":\"8\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipColumnsWithHeaderInvalidHeaderName_sameJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "3", "3"});
        csvPayload.add(new String[]{"2", "2", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("\"bb\"");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);
        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"a\",\"b\":\"b\",\"c\":\"c\"},{\"a\":\"1\",\"b\":\"3\",\"c\":\"3\"},{\"a\":\"2\",\"b\":\"2\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipColumnsWithHeaderComplexQueryInvalidColumnName_sameJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c", "d", "e", "f", "g", "h"});
        csvPayload.add(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        csvPayload.add(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("\"a\":\"d\",!2,\"gg\"");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 8, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);
        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"a\",\"b\":\"b\",\"c\":\"c\",\"d\":\"d\",\"e\":\"e\",\"f\":\"f\",\"g\":\"g\",\"h\":\"h\"},{\"a\":\"1\",\"b\":\"2\",\"c\":\"3\",\"d\":\"4\",\"e\":\"5\",\"f\":\"6\",\"g\":\"7\",\"h\":\"8\"},{\"a\":\"1\",\"b\":\"2\",\"c\":\"3\",\"d\":\"4\",\"e\":\"5\",\"f\":\"6\",\"g\":\"7\",\"h\":\"8\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipDataRowsGiven_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"key-1\":\"1\",\"key-2\":\"2\",\"key-3\":\"3\"},{\"key-1\":\"2\",\"key-2\":\"3\",\"key-3\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipDataRowsGivenWithSkipHeader_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(2, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(2, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"2\",\"b\":\"3\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_skipDataRowsGivenWithSkipHeaderFalse_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(2, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(2, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"a\",\"b\":\"b\",\"c\":\"c\"},{\"a\":\"2\",\"b\":\"3\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_emptyValueAsNull_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "", "3"});
        csvPayload.add(new String[]{"", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CSV_EMPTY_VALUES)).thenReturn("null");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"key-1\":\"a\",\"key-2\":\"b\",\"key-3\":\"c\"},{\"key-1\":\"1\",\"key-2\":null,\"key-3\":\"3\"},{\"key-1\":null,\"key-2\":\"3\",\"key-3\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_emptyValueAsEmpty_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "", "3"});
        csvPayload.add(new String[]{"", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CSV_EMPTY_VALUES)).thenReturn("empty");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"key-1\":\"a\",\"key-2\":\"b\",\"key-3\":\"c\"},{\"key-1\":\"1\",\"key-2\":\"\",\"key-3\":\"3\"},{\"key-1\":\"\",\"key-2\":\"3\",\"key-3\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_customJsonKeys_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "", "3"});
        csvPayload.add(new String[]{"", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.JSON_KEYS)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"p\":\"a\",\"q\":\"b\",\"r\":\"c\"},{\"p\":\"1\",\"q\":null,\"r\":\"3\"},{\"p\":null,\"q\":\"3\",\"r\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_customJsonKeysLongContent_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3", "4"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.JSON_KEYS)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"p\":\"a\",\"q\":\"b\",\"r\":\"c\"},{\"p\":\"1\",\"q\":\"2\",\"r\":\"3\",\"key-4\":\"4\"},{\"p\":\"2\",\"q\":\"3\",\"r\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_customJsonKeysHeaderPresent_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.JSON_KEYS)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"p\":\"a\",\"q\":\"b\",\"r\":\"c\"},{\"p\":\"1\",\"q\":\"2\",\"r\":\"3\"},{\"p\":\"2\",\"q\":\"3\",\"r\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_dataTypesGiven_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_TYPES)).thenReturn("integer,number,string");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":1,\"b\":2.0,\"c\":\"3\"},{\"a\":2,\"b\":3.0,\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_dataTypesGivenHeaderNotSkipping_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_TYPES)).thenReturn("integer,number,string");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"a\",\"b\":\"b\",\"c\":\"c\"},{\"a\":1,\"b\":2.0,\"c\":\"3\"},{\"a\":2,\"b\":3.0,\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_dataTypesGivenInvalidData_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "aaa", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_TYPES)).thenReturn("integer,number,string");
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":1,\"b\":\"aaa\",\"c\":\"3\"},{\"a\":2,\"b\":3.0,\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    void testMediate_rootObjectDefined_correctJsonShouldSet() {
        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, "root");

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_JSON_KEY)).thenReturn("root");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray("root")).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "{\"root\":[{\"key-1\":\"a\",\"key-2\":\"b\",\"key-3\":\"c\"},{\"key-1\":\"1\",\"key-2\":\"2\",\"key-3\":\"3\"},{\"key-1\":\"2\",\"key-2\":\"3\",\"key-3\":\"4\"}]}";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }
}