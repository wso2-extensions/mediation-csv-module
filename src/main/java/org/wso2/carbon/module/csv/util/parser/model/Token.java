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

package org.wso2.carbon.module.csv.util.parser.model;

public class Token {

    public static final int EPSILON = 0;
    public static final int OPEN_BRACKET = 1;
    public static final int CLOSE_BRACKET = 2;
    public static final int COMA = 3;
    public static final int NUMBER = 4;
    public static final int COLON = 5;
    public static final int EXCLAMATION = 6;
    public static final int WILDCARD = 7;

    public final int tokenType;
    public final String sequence;

    public Token(int tokenType, String sequence) {
        super();
        this.tokenType = tokenType;
        this.sequence = sequence;
    }
}