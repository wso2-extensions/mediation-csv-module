package org.wso2.carbon.module.csv.util.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wso2.carbon.module.csv.util.parser.exception.ParserException;
import org.wso2.carbon.module.csv.util.parser.model.Token;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ParserTest {

    @Test
    void testParse_singleValue_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 0);

        final List<Integer> expected = Collections.singletonList(1);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_withComas_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,3";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 0);

        final List<Integer> expected = Arrays.asList(1, 2, 3);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_withQuotes_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,3,(4,5,6)";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 0);

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_withRange_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,3,(4:6)";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 0);

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_withNegation_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,!2,3";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 0);

        final List<Integer> expected = Arrays.asList(1, 3);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_withRangeAndNegation_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,3,4,5,!(4:6)";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 0);

        final List<Integer> expected = Arrays.asList(1, 2, 3);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_withWildcardRange_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,3,(5:*)";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 10);

        final List<Integer> expected = Arrays.asList(1, 2, 3, 5, 6, 7, 8, 9, 10);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_withWildcardRangeAndNegation_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "!1,2,3,5,6,!(5:*)";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 10);

        final List<Integer> expected = Arrays.asList(2, 3);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParseAndGetValue_withWildcardRangeAndNegation_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "!1,2,3,!(5:*)";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 10);

        final List<Integer> expected = Arrays.asList(2, 3);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_clashingNumbers_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "(1:*),!5";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 10);

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9, 10);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_rangeWithoutBrackets_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,5:7";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 10);

        final List<Integer> expected = Arrays.asList(1, 2, 5, 6, 7);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_rangeWithoutBracketsWithWildCard_correctRangeShouldReturn() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,8:*,4";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();
        List<Integer> parseValue = parser.parseAndGetValues(tokens, 10);

        final List<Integer> expected = Arrays.asList(1, 2, 4, 8, 9, 10);

        Assertions.assertEquals(expected, parseValue);
    }

    @Test
    void testParse_invalidComaAtTheEnd_parserExceptionShouldThrow() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();

        Assertions.assertThrows(ParserException.class, () ->
                parser.parseAndGetValues(tokens, 10));

    }

    @Test
    void testParse_noCloseBracket_parserExceptionShouldThrow() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,(2,3";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();

        Assertions.assertThrows(ParserException.class, () ->
                parser.parseAndGetValues(tokens, 10));

    }

    @Test
    void testParse_invalidComa_parserExceptionShouldThrow() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,,3";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();

        Assertions.assertThrows(ParserException.class, () ->
                parser.parseAndGetValues(tokens, 10));

    }

    @Test
    void testParse_rangeExpression_parserExceptionShouldThrow() {

        Tokenizer tokenizer = new Tokenizer();

        final String input = "1,2,(5:)";

        tokenizer.tokenize(input);

        List<Token> tokens = tokenizer.getTokens();

        Parser parser = new Parser();

        Assertions.assertThrows(ParserException.class, () ->
                parser.parseAndGetValues(tokens, 10));

    }

}