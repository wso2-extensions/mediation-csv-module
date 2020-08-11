/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.module.csv;

import org.apache.axiom.om.OMElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.JsonArrayCollector;
import org.wso2.carbon.module.core.models.CsvPayloadInfo;
import org.wso2.carbon.module.csv.constant.Constants;
import org.wso2.carbon.module.csv.constant.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvToXmlTransformerTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    void testMediate_noConfigurations_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());
        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></group><group><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></group><group><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_headerPresent_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR))
                .thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><a>a</a><b>b</b><c>c</c></group><group><a>1</a><b>2</b><c>3</c></group><group><a>2</a><b>3</b><c>4</c></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_headerAbsent_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Absent");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></group><group><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></group><group><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_givenSeparator_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.VALUE_SEPARATOR)).thenReturn("#");
        when(mc.getCsvArrayStream(0, '#')).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></group><group><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></group><group><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_headerPresentAndSkipHeader_correctXmlShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR))
                .thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><a>1</a><b>2</b><c>3</c></group><group><a>2</a><b>3</b><c>4</c></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_headerPresentAndSkipHeaderFalse_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("false");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR))
                .thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><a>a</a><b>b</b><c>c</c></group><group><a>1</a><b>2</b><c>3</c></group><group><a>2</a><b>3</b><c>4</c></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_skipColumnGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn("1,2");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><key-1>c</key-1></group><group><key-1>3</key-1></group><group><key-1>4</key-1></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_skipDataRowsGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        final JsonArrayCollector jsonArrayCollector = new JsonArrayCollector(mc, null);

        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></group><group><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_skipDataRowsGivenWithSkipHeader_correctJsonShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("true");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(2, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(2, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR))
                .thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><a>2</a><b>3</b><c>4</c></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_skipDataRowsGivenWithSkipHeaderFalse_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.DATA_ROWS_TO_SKIP)).thenReturn("1");
        when(mc.getCsvArrayStream(2, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(2, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR))
                .thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><a>a</a><b>b</b><c>c</c></group><group><a>2</a><b>3</b><c>4</c></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_customResultTags_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.XML_TAG_NAMES)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><p>a</p><q>b</q><r>c</r></group><group><p>1</p><q>2</q><r>3</r></group><group><p>2</p><q>3</q><r>4</r></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_customResultTagsLongContent_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3", "4"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.XML_TAG_NAMES)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><p>a</p><q>b</q><r>c</r></group><group><p>1</p><q>2</q><r>3</r><key-4>4</key-4></group><group><p>2</p><q>3</q><r>4</r></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_customResultTagsHeaderPresent_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.IS_HEADER_PRESENT)).thenReturn("Present");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.SKIP_HEADER)).thenReturn("false");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.XML_TAG_NAMES)).thenReturn("p,q,r");
        when(mc.getCsvArrayStream(1, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.subList(1, 3).stream());
        when(mc.getCsvPayloadInfo(Constants.DEFAULT_CSV_SEPARATOR))
                .thenReturn(new CsvPayloadInfo(csvPayload.get(0), 3, csvPayload));

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><group><p>a</p><q>b</q><r>c</r></group><group><p>1</p><q>2</q><r>3</r></group><group><p>2</p><q>3</q><r>4</r></group></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_rootElementNameGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_TAG_NAME)).thenReturn("rootTag");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<rootTag><group><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></group><group><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></group><group><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></group></rootTag>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_rootElementNameAndNamespaceGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_TAG_NAME)).thenReturn("rootTag");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE)).thenReturn("nm");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE_URI))
                .thenReturn("www.google.com");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<nm:rootTag xmlns:nm=\"www.google.com\"><group><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></group><group><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></group><group><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></group></nm:rootTag>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_groupElementNameGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_TAG_NAME)).thenReturn("gorupTag");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><gorupTag><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></gorupTag><gorupTag><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></gorupTag><gorupTag><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></gorupTag></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_groupElementNameAndNamespaceGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_TAG_NAME)).thenReturn("groupTag");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_NAMESPACE)).thenReturn("nm");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_NAMESPACE_URI))
                .thenReturn("www.google.com");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<root><nm:groupTag xmlns:nm=\"www.google.com\"><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></nm:groupTag><nm:groupTag xmlns:nm=\"www.google.com\"><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></nm:groupTag><nm:groupTag xmlns:nm=\"www.google.com\"><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></nm:groupTag></root>";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_RootAndGroupElementNameAndNamespaceGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_TAG_NAME)).thenReturn("rootTag");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE)).thenReturn("rt");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE_URI))
                .thenReturn("www.google.com");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_TAG_NAME)).thenReturn("groupTag");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_NAMESPACE)).thenReturn("gt");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_NAMESPACE_URI))
                .thenReturn("www.google2.com");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<rt:rootTag xmlns:rt=\"www.google.com\"><gt:groupTag xmlns:gt=\"www.google2.com\"><key-1>a</key-1><key-2>b</key-2><key-3>c</key-3></gt:groupTag><gt:groupTag xmlns:gt=\"www.google2.com\"><key-1>1</key-1><key-2>2</key-2><key-3>3</key-3></gt:groupTag><gt:groupTag xmlns:gt=\"www.google2.com\"><key-1>2</key-1><key-2>3</key-2><key-3>4</key-3></gt:groupTag></rt:rootTag>";
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMediate_RootAndGroupAndResultElementGiven_correctXmlShouldReplace() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c"});
        csvPayload.add(new String[]{"1", "2", "3"});
        csvPayload.add(new String[]{"2", "3", "4"});

        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_TAG_NAME)).thenReturn("rootTag");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE)).thenReturn("rt");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE_URI))
                .thenReturn("www.google.com");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_TAG_NAME)).thenReturn("groupTag");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_NAMESPACE)).thenReturn("gt");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.GROUP_ELEMENT_NAMESPACE_URI))
                .thenReturn("www.google2.com");
        lenient().when(mc.lookupTemplateParameter(ParameterKey.XML_TAG_NAMES)).thenReturn("t1,t2,t3");
        when(mc.getCsvArrayStream(0, Constants.DEFAULT_CSV_SEPARATOR)).thenReturn(csvPayload.stream());

        ArgumentCaptor<OMElement> resultPayloadCaptor = ArgumentCaptor.forClass(OMElement.class);

        CsvToXmlTransformer csvToXmlTransformer = new CsvToXmlTransformer();
        csvToXmlTransformer.mediate(mc);

        verify(mc).replaceRootXmlElement(resultPayloadCaptor.capture());
        OMElement resultPayload = resultPayloadCaptor.getValue();

        final String result = resultPayload.toString();
        final String expected =
                "<rt:rootTag xmlns:rt=\"www.google.com\"><gt:groupTag xmlns:gt=\"www.google2.com\"><t1>a</t1><t2>b</t2><t3>c</t3></gt:groupTag><gt:groupTag xmlns:gt=\"www.google2.com\"><t1>1</t1><t2>2</t2><t3>3</t3></gt:groupTag><gt:groupTag xmlns:gt=\"www.google2.com\"><t1>2</t1><t2>3</t2><t3>4</t3></gt:groupTag></rt:rootTag>";
        Assertions.assertEquals(expected, result);
    }

}
