package org.wso2.carbon.connector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.CsvCollector;
import org.wso2.carbon.module.csv.CsvSorter;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvSorterTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testMediate_correctCsvInputWithValidParam_correctSortedCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"0", "5", "5"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null);
        final String sortBy = "2";

        lenient().when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn("");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SORT_BY)).thenReturn(sortBy);
        when(mc.getCsvArrayStream(0)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv()).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvSorter csvSorter = new CsvSorter();
        csvSorter.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,2,3\n" +
                "2,3,4\n" +
                "0,5,5\n";

        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_inverseSort_correctSortedCsvShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"0", "5", "5"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null);
        final String sortBy = "2";
        final String inverseSort = "true";

        lenient().when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn("");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SORT_BY)).thenReturn(sortBy);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.INVERSE_SORT)).thenReturn(inverseSort);
        when(mc.getCsvArrayStream(0)).thenReturn(csvPayload.stream());
        when(mc.collectToCsv()).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvSorter csvSorter = new CsvSorter();
        csvSorter.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "0,5,5\n" +
                "2,3,4\n" +
                "1,2,3\n";

        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    void testMediate_withHeaderSkip_correctSortedCsvShouldSet() {

        final List<String[]> csvPayloadWithoutHeader = new ArrayList<>();
        csvPayloadWithoutHeader.add(new String[]{"0", "5", "5"});
        csvPayloadWithoutHeader.add(new String[]{"1", "2", "3"});
        csvPayloadWithoutHeader.add(new String[]{"2", "3", "4"});

        final CsvCollector csvCollector = new CsvCollector(mc, null);
        final String sortBy = "2";
        final String linesToSkip = "1";

        lenient().when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn("1");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SORT_BY)).thenReturn(sortBy);
        when(mc.getCsvArrayStream(1)).thenReturn(csvPayloadWithoutHeader.stream());
        when(mc.collectToCsv()).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvSorter csvSorter = new CsvSorter();
        csvSorter.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "1,2,3\n" +
                "2,3,4\n" +
                "0,5,5\n";

        Assertions.assertEquals(expectedPayload, setPayload);
    }
}