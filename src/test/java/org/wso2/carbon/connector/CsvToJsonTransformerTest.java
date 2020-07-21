/*
package org.wso2.carbon.connector;

import com.google.gson.JsonElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.JsonArrayCollector;
import org.wso2.carbon.module.csv.CsvToJsonTransformer;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvToJsonTransformerTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    public void testMediate_validCsvInputWithNoEmptyValues_correctJsonShouldSet() {

        final int linesToSkip = 1;

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final List<String[]> csvPayloadWithoutHeader = new ArrayList<>();
        csvPayloadWithoutHeader.add(new String[]{"1", "2", "3"});
        csvPayloadWithoutHeader.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CSV_EMPTY_VALUES)).thenReturn("false");
        when(mc.getCsvPayload(0)).thenReturn(csvPayload);
        when(mc.getCsvArrayStream(linesToSkip)).thenReturn(csvPayloadWithoutHeader.stream());
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
    public void testMediate_validCsvInputWithEmptyValuesSetEmptyAsNullParamOn_correctJsonShouldSet() {

        final int linesToSkip = 1;

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final List<String[]> csvPayloadWithoutHeader = new ArrayList<>();
        csvPayloadWithoutHeader.add(new String[]{"1", "", "3"});
        csvPayloadWithoutHeader.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CSV_EMPTY_VALUES)).thenReturn("true");
        when(mc.getCsvPayload(0)).thenReturn(csvPayload);
        when(mc.getCsvArrayStream(linesToSkip)).thenReturn(csvPayloadWithoutHeader.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":\"1\",\"b\":null,\"c\":\"3\"},{\"a\":\"2\",\"b\":\"3\",\"c\":\"4\"}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);

    }

    @Test
    public void testMediate_validCsvInputWithDataTypesDefined_correctJsonShouldSet() {

        final int linesToSkip = 1;

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "hello", "true"});
        csvPayload.add(new String[]{"2", "3", "false"});

        final List<String[]> csvPayloadWithoutHeader = new ArrayList<>();
        csvPayloadWithoutHeader.add(new String[]{"1", "hello", "true"});
        csvPayloadWithoutHeader.add(new String[]{"2", "3", "false"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CSV_EMPTY_VALUES)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_TYPES)).thenReturn("integer,nUmber,boolean");
        when(mc.getCsvPayload(0)).thenReturn(csvPayload);
        when(mc.getCsvArrayStream(linesToSkip)).thenReturn(csvPayloadWithoutHeader.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"a\":1,\"b\":\"hello\",\"c\":true},{\"a\":2,\"b\":3.0,\"c\":false}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);

    }

    @Test
    public void testMediate_validCsvInputWithCustomHeaders_correctJsonShouldSet() {
        final int linesToSkip = 0;

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "hello", "true"});
        csvPayload.add(new String[]{"2", "3", "false"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("p,q,r");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CSV_EMPTY_VALUES)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_TYPES)).thenReturn("integer,nUmber,boolean");
        when(mc.getCsvArrayStream(linesToSkip)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected = "[{\"p\":1,\"q\":\"hello\",\"r\":true},{\"p\":2,\"q\":3.0,\"r\":false}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

    @Test
    public void testMediate_validCsvInputWithCustomHeadersNoHeaderGiven_correctJsonShouldSet() {
        final int linesToSkip = 0;

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "hello", "true"});
        csvPayload.add(new String[]{"2", "3", "false"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CSV_EMPTY_VALUES)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_TYPES)).thenReturn("integer,nUmber,boolean");
        when(mc.getCsvPayload(linesToSkip)).thenReturn(csvPayload);
        when(mc.getCsvArrayStream(linesToSkip)).thenReturn(csvPayload.stream());
        when(mc.collectToJsonArray()).thenReturn(jsonArrayCollector);

        ArgumentCaptor<JsonElement> payloadSetArgumentCaptor = ArgumentCaptor.forClass(JsonElement.class);

        CsvToJsonTransformer csvToJsonTransformer = new CsvToJsonTransformer();
        csvToJsonTransformer.mediate(mc);

        verify(mc).setJsonPayload(payloadSetArgumentCaptor.capture());
        JsonElement jsonElement = payloadSetArgumentCaptor.getValue();

        final String expected =
                "[{\"key-1\":1,\"key-2\":\"hello\",\"key-3\":true},{\"key-1\":2,\"key-2\":3.0,\"key-3\":false}]";

        String resultJson = jsonElement.toString();

        Assertions.assertEquals(expected, resultJson);
    }

}*/
