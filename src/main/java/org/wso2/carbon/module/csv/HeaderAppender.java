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

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.exceptions.SimpleMessageContextException;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.List;

public class HeaderAppender extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {
        List<String[]> csvPayload = mc.getCsvPayload(0);
        String[] headerToAppend = getHeaderToAppend(mc);
        csvPayload.add(0, headerToAppend);
        mc.setCsvPayload(csvPayload);
    }

    private String[] getHeaderToAppend(SimpleMessageContext mc) {
        String[] header;

        String headerToAppend = (String) mc.lookupTemplateParameter(ParameterKey.HEADER_TO_APPEND);
        if (!StringUtils.isBlank(headerToAppend)) {
            header = headerToAppend.split(",");
        } else {
            throw new SimpleMessageContextException("parameter : " + ParameterKey.HEADER_TO_APPEND + " is required");
        }

        return header;
    }
}