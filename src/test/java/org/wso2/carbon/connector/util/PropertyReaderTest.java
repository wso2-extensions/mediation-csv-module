package org.wso2.carbon.connector.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.util.ParameterKey;
import org.wso2.carbon.module.csv.util.PropertyReader;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyReaderTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testGetNumberOfLinesToSkip_correctValueForProperty_valueOfThePropertyShouldReturn() {

        final int linesToSkip = 2;
        when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn(Integer.toString(linesToSkip));
        final int result = PropertyReader.getNumberOfLinesToSkip(mc);
        Assertions.assertEquals(linesToSkip, result);
    }

    @Test
    void testGetNumberOfLinesToSkip_noValueGivenToProperty_defaultValueShouldReturn() {

        final int defaultValue = 0;
        when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn(null);
        final int result = PropertyReader.getNumberOfLinesToSkip(mc);
        Assertions.assertEquals(defaultValue, result);
    }
}