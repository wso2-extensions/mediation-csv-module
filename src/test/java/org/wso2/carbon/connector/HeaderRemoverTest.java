package org.wso2.carbon.connector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.HeaderRemover;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderRemoverTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testMediate() {

        final int linesToSkip = 1;
        final List<String[]> resultPayload = new ArrayList<>();
        resultPayload.add(new String[]{"1", "2", "3"});
        resultPayload.add(new String[]{"2", "3", "4"});

        when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn(Integer.toString(linesToSkip));
        when(mc.getCsvPayload(linesToSkip)).thenReturn(resultPayload);

        ArgumentCaptor<List<String[]>> resultPayloadCaptor = ArgumentCaptor.forClass(List.class);

        HeaderRemover headerRemover = new HeaderRemover();
        headerRemover.mediate(mc);

        verify(mc).setCsvPayload(resultPayloadCaptor.capture());
        List<String[]> setPayload = resultPayloadCaptor.getValue();

        assertThat(resultPayload, is(setPayload));
    }
}