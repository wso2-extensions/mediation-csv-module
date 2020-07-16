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
import org.wso2.carbon.module.csv.util.parser.Parser;
import org.wso2.carbon.module.csv.util.parser.Tokenizer;
import org.wso2.carbon.module.csv.util.parser.model.Token;

import java.util.Arrays;
import java.util.LinkedList;

public class CsvColumnSkipper extends SimpleMediator {

    @Override
    public void mediate(SimpleMessageContext mc) {
        int[] skippingColumns = getSkippingColumns(mc, mc.getCsvPayload().get(0).length);

        mc.getCsvArrayStream()
                .map(Arrays::asList)
                .map(LinkedList::new)
                .map(row -> {

                    int[] currentSkippingColumns = skippingColumns.clone();

                    for (int i = 0; i < currentSkippingColumns.length; i++) {

                        for (int j = i; j < currentSkippingColumns.length; j++) {
                            currentSkippingColumns[j]--;
                        }

                        int currentIndex = currentSkippingColumns[i];
                        if (currentIndex >= 0 && currentIndex < row.size()) {
                            row.remove(currentIndex);
                        } else {
                            log.debug("Invalid index to remove");
                        }
                    }

                    return row.toArray(new String[]{});
                })
                .collect(mc.collectToCsv());
    }

    private int[] getSkippingColumns(SimpleMessageContext mc, int columnCount) {
        int[] skippingColumns;

        String skippingColumnString = (String) mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP);
        if (StringUtils.isNotBlank(skippingColumnString)) {

            try {
                skippingColumns = parseExpression(skippingColumnString, columnCount);
            } catch (Exception e) {
                throw new SynapseException("Invalid Skipping Columns String", e);
            }

        } else {
            throw new SynapseException("Skipping Columns cannot be empty");
        }

        return skippingColumns;

    }

    private int[] parseExpression(String skippingColumnsString, int columnCount) {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();

        tokenizer.tokenize(skippingColumnsString.trim());
        LinkedList<Token> tokens = new LinkedList<>(tokenizer.getTokens());

        return parser.parseAndGetValues(tokens, columnCount).stream().mapToInt(Integer::intValue).toArray();
    }
}
