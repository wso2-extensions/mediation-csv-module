package org.wso2.carbon.connector;

import org.apache.axiom.om.OMElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.CsvToXmlConverter;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CsvToXmlConverterTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testMediate_correctCsvInput_correctXmlShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final List<String[]> csvPayloadWithoutHeader = new ArrayList<>();
        csvPayloadWithoutHeader.add(new String[]{"1", "2", "3"});
        csvPayloadWithoutHeader.add(new String[]{"2", "3", "4"});

        final String rootElementName = "root";
        final String childElementNames = "child";

        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAME)).thenReturn(rootElementName);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CHILD_ELEMENT_NAME)).thenReturn(childElementNames);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn("1");
        when(mc.getCsvPayload(0)).thenReturn(csvPayload);
        when(mc.getCsvArrayStream(1)).thenReturn(csvPayloadWithoutHeader.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlConverter csvToXmlConverter = new CsvToXmlConverter();
        csvToXmlConverter.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String resultXml = resultPayload.toString();
        final String expectedXml =
                "<root><child><a>1</a><b>2</b><c>3</c></child><child><a>2</a><b>3</b><c>4</c></child></root>";

        Assertions.assertEquals(expectedXml, resultXml);

    }

    @Test
    void testMediate_correctCsvInputWithCustomHeaders_correctXmlShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final String rootElementName = "root";
        final String childElementNames = "child";

        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.HEADER_TO_APPEND)).thenReturn("a,b,c");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAME)).thenReturn(rootElementName);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CHILD_ELEMENT_NAME)).thenReturn(childElementNames);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(0)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlConverter csvToXmlConverter = new CsvToXmlConverter();
        csvToXmlConverter.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String resultXml = resultPayload.toString();
        final String expectedXml =
                "<root><child><a>1</a><b>2</b><c>3</c></child><child><a>2</a><b>3</b><c>4</c></child></root>";

        Assertions.assertEquals(expectedXml, resultXml);

    }

    @Test
    void testMediate_xmlNamespaceGiven_correctXmlShouldSetWithNamespace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final List<String[]> csvPayloadWithoutHeader = new ArrayList<>();
        csvPayloadWithoutHeader.add(new String[]{"1", "2", "3"});
        csvPayloadWithoutHeader.add(new String[]{"2", "3", "4"});

        final String rootElementName = "root";
        final String childElementNames = "child";
        final String rootElementNamespaceUri = "www.wso2.com";
        final String rootElementNamespacePrefix = "wso2";

        lenient().when(mc.lookupTemplateParameter(ParameterKey.USE_HEADER_AS_KEYS)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAME)).thenReturn(rootElementName);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE_PREFIX))
                .thenReturn(rootElementNamespacePrefix);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE_URI))
                .thenReturn(rootElementNamespaceUri);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.CHILD_ELEMENT_NAME)).thenReturn(childElementNames);
        lenient().when(mc.lookupTemplateParameter(ParameterKey.LINES_TO_SKIP)).thenReturn("1");
        when(mc.getCsvPayload(0)).thenReturn(csvPayload);
        when(mc.getCsvArrayStream(1)).thenReturn(csvPayloadWithoutHeader.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlConverter csvToXmlConverter = new CsvToXmlConverter();
        csvToXmlConverter.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String resultXml = resultPayload.toString();
        final String expectedXml =
                "<wso2:root xmlns:wso2=\"www.wso2.com\"><child><a>1</a><b>2</b><c>3</c></child><child><a>2</a><b>3</b><c>4</c></child></wso2:root>";

        Assertions.assertEquals(expectedXml, resultXml);

    }

}