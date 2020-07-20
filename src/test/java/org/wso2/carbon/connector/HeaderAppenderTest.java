package org.wso2.carbon.connector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.HeaderAppender;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderAppenderTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testMediate() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final String appendingHeader = "a,b,c";

        when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn(appendingHeader);
        when(mc.getCsvPayload(0)).thenReturn(csvPayload);

        ArgumentCaptor<List<String[]>> resultPayloadCaptor = ArgumentCaptor.forClass(List.class);

        HeaderAppender headerAppender = new HeaderAppender();
        headerAppender.mediate(mc);

        verify(mc).setCsvPayload(resultPayloadCaptor.capture());
        List<String[]> resultPayload = resultPayloadCaptor.getValue();

        final List<String[]> expectedPayload = new ArrayList<>();
        expectedPayload.add(new String[]{"a", "b", "c"});
        expectedPayload.add(new String[]{"1", "2", "3"});
        expectedPayload.add(new String[]{"2", "3", "4"});

        Assertions.assertEquals(expectedPayload.size(), resultPayload.size());
    }
}