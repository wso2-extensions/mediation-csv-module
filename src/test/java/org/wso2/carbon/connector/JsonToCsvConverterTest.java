package org.wso2.carbon.connector;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.CsvCollector;
import org.wso2.carbon.module.csv.JsonToCsvConverter;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonToCsvConverterTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    public void testMediate_validJsonInput_correctCsvOutputShouldSet() {

        final String jsonPayload = "[\n" +
                "    {\n" +
                "        \"id\": \"1\",\n" +
                "        \"firstName\": \"Denise\",\n" +
                "        \"lastName\": \"Callum\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"2\",\n" +
                "        \"firstName\": \"Randee\",\n" +
                "        \"lastName\": \"Abbot-Doyle\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"3\",\n" +
                "        \"firstName\": \"Sandland\",\n" +
                "        \"lastName\": \"Latashia\"\n" +
                "    }\n" +
                "]";

        JsonParser parser = new JsonParser();
        final JsonElement payloadJsonElement = parser.parse(jsonPayload);
        final Stream<JsonElement> payloadJsonStream =
                StreamSupport.stream(payloadJsonElement.getAsJsonArray().spliterator(), false);
        final CsvCollector csvCollector = new CsvCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("");
        when(mc.getJsonElement()).thenReturn(payloadJsonElement);
        when(mc.getJsonArrayStream()).thenReturn(payloadJsonStream);
        when(mc.collectToCsv(any())).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        JsonToCsvConverter jsonToCsvConverter = new JsonToCsvConverter();
        jsonToCsvConverter.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,Denise,Callum\n" +
                "2,Randee,Abbot-Doyle\n" +
                "3,Sandland,Latashia\n";

        Assertions.assertEquals(expectedPayload, setPayload);

    }

    @Test
    public void testMediate_validJsonInputWithHeaderSet_correctCsvShouldReturnWithHeader() {

        final String jsonPayload = "[\n" +
                "    {\n" +
                "        \"id\": \"1\",\n" +
                "        \"firstName\": \"Denise\",\n" +
                "        \"lastName\": \"Callum\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"2\",\n" +
                "        \"firstName\": \"Randee\",\n" +
                "        \"lastName\": \"Abbot-Doyle\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"3\",\n" +
                "        \"firstName\": \"Sandland\",\n" +
                "        \"lastName\": \"Latashia\"\n" +
                "    }\n" +
                "]";

        final String header = "id,first,last";
        final String[] headerArray = new String[]{"id", "first", "last"};

        JsonParser parser = new JsonParser();
        final JsonElement payloadJsonElement = parser.parse(jsonPayload);
        final Stream<JsonElement> payloadJsonStream =
                StreamSupport.stream(payloadJsonElement.getAsJsonArray().spliterator(), false);
        final CsvCollector csvCollector = new CsvCollector(mc, headerArray);

        when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn(header);
        when(mc.getJsonArrayStream()).thenReturn(payloadJsonStream);
        when(mc.collectToCsv(headerArray)).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        JsonToCsvConverter jsonToCsvConverter = new JsonToCsvConverter();
        jsonToCsvConverter.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "id,first,last\n" +
                "1,Denise,Callum\n" +
                "2,Randee,Abbot-Doyle\n" +
                "3,Sandland,Latashia\n";

        Assertions.assertEquals(expectedPayload, setPayload);

    }
}