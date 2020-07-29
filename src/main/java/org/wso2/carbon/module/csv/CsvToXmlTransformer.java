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
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.constant.Constants;
import org.wso2.carbon.module.csv.constant.ParameterKey;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.stream.Stream;

import static org.wso2.carbon.module.csv.util.PropertyReader.getStringParam;

public class CsvToXmlTransformer extends AbstractCsvToAnyTransformer {

    @Override
    void mediate(SimpleMessageContext mc, Stream<String[]> csvArrayStream, String[] header) {
        final Optional<String> tagNamesQuery = getStringParam(mc, ParameterKey.XML_TAG_NAMES);
        final Optional<String> rootElementTagNameQuery = getStringParam(mc, ParameterKey.ROOT_ELEMENT_TAG_NAME);
        final String rootElementTagName = rootElementTagNameQuery.orElse(Constants.DEFAULT_XML_ROOT_ELEMENT_NAME);
        final Optional<String> rootElementNamespaceQuery = getStringParam(mc, ParameterKey.ROOT_ELEMENT_NAMESPACE);
        final Optional<String> rootElementNamespaceUriQuery = getStringParam(mc, ParameterKey.ROOT_ELEMENT_NAMESPACE_URI);
        final Optional<String> groupElementTagNameQuery = getStringParam(mc, ParameterKey.GROUP_ELEMENT_TAG_NAME);
        final String groupElementTagName = groupElementTagNameQuery.orElse(Constants.DEFAULT_XML_GROUP_ELEMENT_NAME);
        final Optional<String> groupElementNamespaceQuery = getStringParam(mc, ParameterKey.GROUP_ELEMENT_NAMESPACE);
        final Optional<String> groupElementNamespaceUriQuery = getStringParam(mc, ParameterKey.GROUP_ELEMENT_NAMESPACE_URI);

        String[] tagNames = generateObjectKeys(tagNamesQuery.orElse(""), header);

        final OMFactory fac = OMAbstractFactory.getOMFactory();

        OMElement rootElement;
        if (!rootElementNamespaceUriQuery.isPresent()) {
            rootElement = fac.createOMElement(new QName(rootElementTagName));
        } else if (!rootElementNamespaceQuery.isPresent()) {
            rootElement = fac.createOMElement(new QName(rootElementNamespaceUriQuery.get(), rootElementTagName));
        } else {
            rootElement =
                    fac.createOMElement(new QName(rootElementNamespaceUriQuery.get(), rootElementTagName, rootElementNamespaceQuery.get()));
        }

        OMElement groupElement;
        if (!groupElementNamespaceUriQuery.isPresent()) {
            groupElement = fac.createOMElement(new QName(groupElementTagName));
        } else if (!groupElementNamespaceQuery.isPresent()) {
            groupElement = fac.createOMElement(new QName(groupElementNamespaceUriQuery.get(), groupElementTagName));
        } else {
            groupElement =
                    fac.createOMElement(new QName(groupElementNamespaceUriQuery.get(), groupElementTagName, groupElementNamespaceQuery.get()));
        }

        csvArrayStream
                .forEach(row -> {
                    OMElement childElement = groupElement.cloneOMElement();
                    for (int i = 0; i < row.length; i++) {
                        String tagName = getObjectKey(tagNames, i);
                        OMElement valueElement = fac.createOMElement(new QName(tagName));
                        valueElement.setText(row[i]);
                        childElement.addChild(valueElement);
                    }
                    rootElement.addChild(childElement);
                });

        mc.replaceRootXmlElement(rootElement);
    }

}

