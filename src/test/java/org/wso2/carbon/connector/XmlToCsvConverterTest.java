package org.wso2.carbon.connector;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.CsvCollector;
import org.wso2.carbon.module.csv.XmlToCsvConverter;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.stream.XMLStreamException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XmlToCsvConverterTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    public void testMediate_validXmlInput_correctCsvOutputShouldSet() throws XMLStreamException {

        final String payloadXml = "<root>\n" +
                "    <child>\n" +
                "        <id>1</id>\n" +
                "        <firstName>Denise</firstName>\n" +
                "        <lastName>Callum</lastName>\n" +
                "    </child>\n" +
                "    <child>\n" +
                "        <id>2</id>\n" +
                "        <firstName>Randee</firstName>\n" +
                "        <lastName>Abbot-Doyle</lastName>\n" +
                "    </child>\n" +
                "    <child>\n" +
                "        <id>3</id>\n" +
                "        <firstName>Sandland</firstName>\n" +
                "        <lastName>Latashia</lastName>\n" +
                "    </child>\n" +
                "</root>";

        final OMElement xmlPayload = AXIOMUtil.stringToOM(payloadXml);
        Iterable<OMElement> iterable = xmlPayload::getChildElements;
        Stream<OMElement> childElementStream = StreamSupport.stream(iterable.spliterator(), false);
        final CsvCollector csvCollector = new CsvCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn("");
        when(mc.getRootXmlElement()).thenReturn(xmlPayload.cloneOMElement());
        when(mc.getXmlChildElementsStream()).thenReturn(childElementStream);
        when(mc.collectToCsv(any())).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        XmlToCsvConverter xmlToCsvConverter = new XmlToCsvConverter();
        xmlToCsvConverter.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload =
                "1,Denise,Callum\n" +
                        "2,Randee,Abbot-Doyle\n" +
                        "3,Sandland,Latashia\n";

        Assertions.assertEquals(expectedPayload, setPayload);
    }

    @Test
    public void testMediate_validXmlInputWithHeaderProperty_correctCsvOutputWithHeaderShouldSet()
            throws XMLStreamException {

        final String payloadXml = "<root>\n" +
                "    <child>\n" +
                "        <id>1</id>\n" +
                "        <firstName>Denise</firstName>\n" +
                "        <lastName>Callum</lastName>\n" +
                "    </child>\n" +
                "    <child>\n" +
                "        <id>2</id>\n" +
                "        <firstName>Randee</firstName>\n" +
                "        <lastName>Abbot-Doyle</lastName>\n" +
                "    </child>\n" +
                "    <child>\n" +
                "        <id>3</id>\n" +
                "        <firstName>Sandland</firstName>\n" +
                "        <lastName>Latashia</lastName>\n" +
                "    </child>\n" +
                "</root>";

        final String header = "id,first,last";
        final String[] headerArray = new String[]{"id", "first", "last"};

        final OMElement xmlPayload = AXIOMUtil.stringToOM(payloadXml);
        Iterable<OMElement> iterable = xmlPayload::getChildElements;
        Stream<OMElement> childElementStream = StreamSupport.stream(iterable.spliterator(), false);
        final CsvCollector csvCollector = new CsvCollector(mc, headerArray);

        when(mc.lookupTemplateParameter(ParameterKey.CUSTOM_HEADER)).thenReturn(header);
        when(mc.getXmlChildElementsStream()).thenReturn(childElementStream);
        when(mc.collectToCsv(any())).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        XmlToCsvConverter xmlToCsvConverter = new XmlToCsvConverter();
        xmlToCsvConverter.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expectedPayload = "id,first,last\n" +
                "1,Denise,Callum\n" +
                "2,Randee,Abbot-Doyle\n" +
                "3,Sandland,Latashia\n";

        Assertions.assertEquals(expectedPayload, setPayload);
    }

}