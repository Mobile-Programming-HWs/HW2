package com.sharif.quizofkings;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameResponseParserTest {

    @Test
    public void parseQuestions_returnsQuestionsForSuccessfulResponse() {
        String json = "{"
                + "\"response_code\":0,"
                + "\"results\":[{"
                + "\"question\":\"Question?\","
                + "\"correct_answer\":\"Answer\","
                + "\"incorrect_answers\":[\"Wrong 1\",\"Wrong 2\",\"Wrong 3\"]"
                + "}]"
                + "}";

        ArrayList<Question> questions = GameResponseParser.parseQuestions(json);

        assertEquals(1, questions.size());
        assertEquals("Question?", questions.get(0).getQuestion());
        assertTrue(GameResponseParser.hasUsableQuestions(questions));
    }

    @Test
    public void parseQuestions_returnsEmptyListWhenApiHasNoResults() {
        String json = "{\"response_code\":1,\"results\":[]}";

        ArrayList<Question> questions = GameResponseParser.parseQuestions(json);

        assertTrue(questions.isEmpty());
        assertFalse(GameResponseParser.hasUsableQuestions(questions));
    }

    @Test
    public void hasUsableQuestions_rejectsQuestionsWithMissingAnswers() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Wrong 1");
        answers.add("Wrong 2");
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question("Question?", "Answer", answers));

        assertFalse(GameResponseParser.hasUsableQuestions(questions));
    }
}
