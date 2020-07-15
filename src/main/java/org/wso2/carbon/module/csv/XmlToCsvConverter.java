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
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlToCsvConverter extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        String[] header = getHeader(mc);

        mc.getXmlChildElementsStream()
                .map(omElement -> {
                    List<String> csvEntry = new ArrayList<>();

                    Iterator<OMElement> childElementsIterator = omElement.getChildElements();
                    while (childElementsIterator.hasNext()) {
                        OMElement childElement = childElementsIterator.next();
                        String childText = childElement.getText();
                        csvEntry.add(childText);
                    }

                    return csvEntry.toArray(new String[0]);
                }).collect(mc.collectToCsv(header));
    }

    private String[] getHeader(SimpleMessageContext mc) {

        String[] header = null;

        String headerToAppend = (String) mc.lookupTemplateParameter(ParameterKey.HEADER_TO_APPEND);
        if (!StringUtils.isBlank(headerToAppend)) {
            header = headerToAppend.split(",");
        } else {

            OMElement rootXmlElement = mc.getRootXmlElement();
            OMElement firstElement = rootXmlElement.getFirstElement();
            Iterator<OMElement> childElementsIterator = firstElement.getChildElements();

            List<String> headerList = new ArrayList<>();
            while (childElementsIterator.hasNext()) {
                OMElement childElement = childElementsIterator.next();
                String keyName = childElement.getQName().getLocalPart();
                headerList.add(keyName);
            }

            header = headerList.toArray(new String[0]);
        }

        return header;
    }
}
