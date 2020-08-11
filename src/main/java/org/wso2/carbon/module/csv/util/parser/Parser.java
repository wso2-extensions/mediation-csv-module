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
import org.wso2.carbon.module.csv.util.parser.expressionnode.ComaExpressionNode;
import org.wso2.carbon.module.csv.util.parser.expressionnode.ConstantExpressionNode;
import org.wso2.carbon.module.csv.util.parser.expressionnode.ExpressionNode;
import org.wso2.carbon.module.csv.util.parser.expressionnode.NegationExpressionNode;
import org.wso2.carbon.module.csv.util.parser.expressionnode.RangeExpressionNode;
import org.wso2.carbon.module.csv.util.parser.expressionnode.WildcardRangeExpression;
import org.wso2.carbon.module.csv.util.parser.model.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Column selection expression parser.
 */
public class Parser {

    private LinkedList<Token> tokens;
    private Token lookahead;
    private int maxValue;

    /**
     * Parse and get indices.
     * @param tokens Tokens list.
     * @param maxValue Max column index to use.
     * @return List of column indices resolved.
     */
    public List<Integer> parseAndGetValues(List<Token> tokens, int maxValue) {

        ExpressionNode expressionNode = parse(tokens, maxValue);
        List<Integer> values = expressionNode.getValue();

        int size = values.size();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            int val = values.get(i);
            if (val > 0) {
                boolean isValid = true;
                for (int j = i + 1; j < size; j++) {
                    if (val == -values.get(j)) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    result.add(val);
                }
            }
        }

        return result.stream().sorted().distinct().collect(Collectors.toList());
    }

    private ExpressionNode parse(List<Token> tokens, int maxValue) {

        this.tokens = new LinkedList<>(tokens);
        this.maxValue = maxValue;

        lookahead = this.tokens.getFirst();

        ExpressionNode expression = expression();

        if (lookahead.tokenType != Token.EPSILON) {
            throw new ParserException(lookahead.sequence);
        }

        return expression;

    }

    private ExpressionNode expression() {

        ExpressionNode operation;
        if (lookahead.tokenType == Token.NUMBER) {
            operation = value();
        } else if (lookahead.tokenType == Token.EXCLAMATION) {
            operation = negation();
        } else {
            operation = statement();
        }
        return rangeOrExpression(operation);
    }

    private ExpressionNode value() {

        if (lookahead.tokenType == Token.NUMBER) {
            ConstantExpressionNode constantExpressionNode = new ConstantExpressionNode(lookahead.sequence);
            nextToken();
            return constantExpressionNode;
        } else {
            throw new ParserException(lookahead.sequence);
        }
    }

    private ExpressionNode negation() {

        nextToken();
        ExpressionNode value;
        if (lookahead.tokenType == Token.NUMBER) {
            value = value();
        } else {
            value = statement();
        }
        return new NegationExpressionNode(value);
    }

    private ExpressionNode statement() {

        if (lookahead.tokenType == Token.OPEN_BRACKET) {
            ExpressionNode resultNode;
            nextToken();
            ExpressionNode value = value();
            resultNode = rangeOrExpression(value);

            if (lookahead.tokenType != Token.CLOSE_BRACKET) {
                throw new ParserException("need closing bracket");
            }

            nextToken();
            return resultNode;
        } else {
            throw new ParserException(lookahead.sequence);
        }

    }

    private ExpressionNode rangeOrExpression(ExpressionNode value) {

        ExpressionNode resultNode;
        if (lookahead.tokenType == Token.COLON) {
            nextToken();
            resultNode = range(value);
        } else {
            resultNode = expressionNotRange(value);
        }

        return resultNode;
    }

    private ExpressionNode expressionNotRange(ExpressionNode value) {

        ExpressionNode resultNode;
        if (lookahead.tokenType == Token.COMA) {
            nextToken();
            ExpressionNode expressionNode = expression();
            resultNode = new ComaExpressionNode(value, expressionNode);

        } else if (lookahead.tokenType == Token.EPSILON || lookahead.tokenType == Token.CLOSE_BRACKET) {
            return value;
        } else {
            throw new ParserException(lookahead.sequence);
        }

        return resultNode;
    }

    private ExpressionNode range(ExpressionNode firstValue) {

        ExpressionNode rangeExpression;
        if (lookahead.tokenType == Token.NUMBER) {
            ExpressionNode secondValue = value();
            rangeExpression = new RangeExpressionNode(firstValue, secondValue);
        } else if (lookahead.tokenType == Token.WILDCARD) {
            nextToken();
            rangeExpression = new WildcardRangeExpression(firstValue, maxValue);
        } else {
            throw new ParserException("Expected a number after colon, but found : " + lookahead.sequence);
        }

        return expressionNotRange(rangeExpression);

    }

    private void nextToken() {

        tokens.pop();
        if (tokens.isEmpty())
            lookahead = new Token(Token.EPSILON, "");
        else
            lookahead = tokens.getFirst();
    }
}
