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
import org.apache.synapse.SynapseException;
import org.wso2.carbon.module.core.SimpleMediator;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.csv.util.ParameterKey;

import static org.wso2.carbon.module.csv.util.PropertyReader.getNumberOfLinesToSkip;

public class CsvSorter extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {

        int linesToSkip = getNumberOfLinesToSkip(mc);
        int sortByColumnIndex = getSortByColumnIndex(mc);
        boolean inverseSort = getInverse(mc);

        mc.getCsvArrayStream(linesToSkip)
                .sorted((row1, row2) -> {
                    String val1 = getRowValue(row1, sortByColumnIndex);
                    String val2 = getRowValue(row2, sortByColumnIndex);
                    int comparisonResult = val1.compareTo(val2);
                    if (inverseSort) {
                        comparisonResult = -comparisonResult;
                    }
                    return comparisonResult;
                }).collect(mc.collectToCsv());

    }

    private String getRowValue(String[] row, int index) {

        final int rowLength = row.length;
        if (index >= rowLength) {
            return "";
        } else {
            return row[index];
        }
    }

    private boolean getInverse(SimpleMessageContext mc) {

        String inverseSortString = (String) mc.lookupTemplateParameter(ParameterKey.INVERSE_SORT);
        if (StringUtils.isNotBlank(inverseSortString)) {
            return Boolean.parseBoolean(inverseSortString.trim());
        } else {
            return false;
        }
    }

    private int getSortByColumnIndex(SimpleMessageContext mc) {

        String headerToAppend = (String) mc.lookupTemplateParameter(ParameterKey.SORT_BY);
        try {
            int index = Integer.parseInt(headerToAppend.trim()) - 1;
            if (index < 0) {
                throw new SynapseException("sort by column value should be greater than 0");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new SynapseException("Invalid value for sort by field");
        }
    }
}
