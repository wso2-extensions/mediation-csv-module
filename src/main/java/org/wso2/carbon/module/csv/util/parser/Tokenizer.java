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

package org.wso2.carbon.module.csv.util.parser;

import org.wso2.carbon.module.csv.util.parser.exception.ParserException;
import org.wso2.carbon.module.csv.util.parser.model.Token;
import org.wso2.carbon.module.csv.util.parser.model.TokenInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private final LinkedList<TokenInfo> tokenInfos;
    private final LinkedList<Token> tokens;

    public Tokenizer() {

        tokenInfos = new LinkedList<>();
        tokens = new LinkedList<>();

        initTokens();
    }

    private void initTokens() {
        addTokenInfo("\\(", Token.OPEN_BRACKET);
        addTokenInfo("\\)", Token.CLOSE_BRACKET);
        addTokenInfo("\\,", Token.COMA);
        addTokenInfo("\\d+", Token.NUMBER);
        addTokenInfo("\\:", Token.COLON);
        addTokenInfo("\\!", Token.EXCLAMATION);
        addTokenInfo("\\*", Token.WILDCARD);
    }

    public void addTokenInfo(String regex, int token) {
        tokenInfos.add(
                new TokenInfo(
                        Pattern.compile("^(" + regex + ")"), token));
    }

    public void tokenize(String str) {
        String s = str.trim();
        tokens.clear();

        while (!s.equals("")) {
            boolean match = false;

            for (TokenInfo info : tokenInfos) {
                Matcher m = info.regex.matcher(s);
                if (m.find()) {
                    match = true;

                    String tok = m.group().trim();
                    tokens.add(new Token(info.token, tok));

                    s = m.replaceFirst("");
                    break;
                }
            }

            if (!match) {
                throw new ParserException(s);
            }
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

}
