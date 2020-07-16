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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;
import org.wso2.carbon.module.csv.util.ParameterKey;
import org.wso2.carbon.module.csv.util.PropertyReader;

import javax.xml.namespace.QName;

import static org.wso2.carbon.module.csv.util.PropertyReader.getBooleanParam;

public class CsvToXmlConverter extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {
        int linesToSkip = getLinesToSkip(mc);
        String[] header = generateXmlTagNames(mc);
        String rootElementName = getRootElementName(mc);
        String rootElementNamespace = getRootElementNamespace(mc);
        String rootElementNamespaceURI = getRootElementNamespaceURI(mc);
        String childElementName = getChildElementName(mc);

        final OMFactory fac = OMAbstractFactory.getOMFactory();

        OMElement rootElement =
                generateRootElement(rootElementName, rootElementNamespace, rootElementNamespaceURI, fac);

        mc.getCsvArrayStream(linesToSkip)
                .forEach(row -> {
                    OMElement childElement = fac.createOMElement(new QName(childElementName));
                    for (int i = 0; i < row.length; i++) {
                        String tagName;
                        try {
                            tagName = getXmlTagName(header, i);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new SynapseException("Header length not enough for the payload");
                        }
                        OMElement valueElement = fac.createOMElement(new QName(tagName));
                        valueElement.setText(row[i]);
                        childElement.addChild(valueElement);
                    }
                    rootElement.addChild(childElement);
                });

        mc.replaceRootXmlElement(rootElement);
    }

    private int getLinesToSkip(SimpleMessageContext mc) {
        boolean skipHeader = getBooleanParam(mc, ParameterKey.USE_HEADER_AS_KEYS);

        if (skipHeader) {
            return 1;
        } else {
            return 0;
        }
    }

    private String[] generateXmlTagNames(SimpleMessageContext mc) {
        boolean useHeaderAsKeys = getBooleanParam(mc, ParameterKey.USE_HEADER_AS_KEYS);
        return PropertyReader.getHeader(mc, useHeaderAsKeys);
    }

    private String getXmlTagName(String[] header, int index) {
        String headerValue;
        try {
            headerValue = header[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SynapseException("Header length not enough for the payload");
        }
        return headerValue;
    }

    private OMElement generateRootElement(String rootElementName, String rootElementNamespace,
                                          String rootElementNamespaceURI, OMFactory fac) {

        OMElement rootElement;
        if (StringUtils.isBlank(rootElementNamespaceURI)) {
            rootElement = fac.createOMElement(new QName(rootElementName));
        } else if (StringUtils.isBlank(rootElementNamespace)) {
            rootElement = fac.createOMElement(new QName(rootElementNamespaceURI, rootElementName));
        } else {
            rootElement =
                    fac.createOMElement(new QName(rootElementNamespaceURI, rootElementName, rootElementNamespace));
        }
        return rootElement;
    }

    private String getRootElementName(SimpleMessageContext mc) {

        String rootElementName = (String) mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAME);
        if (StringUtils.isBlank(rootElementName)) {
            throw new SimpleMessageContextException("Root element name can't be blank");
        }

        return rootElementName;
    }

    private String getRootElementNamespace(SimpleMessageContext mc) {

        return (String) mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE_PREFIX);

    }

    private String getRootElementNamespaceURI(SimpleMessageContext mc) {

        return (String) mc.lookupTemplateParameter(ParameterKey.ROOT_ELEMENT_NAMESPACE_URI);
    }

    private String getChildElementName(SimpleMessageContext mc) {

        String childElementName = (String) mc.lookupTemplateParameter(ParameterKey.CHILD_ELEMENT_NAME);
        if (StringUtils.isBlank(childElementName)) {
            throw new SimpleMessageContextException("Child element name can't be blank");
        }

        return childElementName;
    }

}

