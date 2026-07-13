package com.sharif.quizofkings;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuizLabelsTest {

    @Test
    public void categoryName_returnsReadableOpenTriviaNames() {
        assertEquals("Sports", QuizLabels.categoryName(21));
        assertEquals("Art", QuizLabels.categoryName(25));
        assertEquals("Animals", QuizLabels.categoryName(27));
    }

    @Test
    public void categoryName_returnsFallbackForUnknownCategory() {
        assertEquals("Category 99", QuizLabels.categoryName(99));
    }

    @Test
    public void difficultyName_formatsSavedApiValue() {
        assertEquals("Easy", QuizLabels.difficultyName("easy"));
        assertEquals("Medium", QuizLabels.difficultyName(" MEDIUM "));
    }

    @Test
    public void difficultyName_returnsUnknownForBlankValue() {
        assertEquals("Unknown", QuizLabels.difficultyName(""));
        assertEquals("Unknown", QuizLabels.difficultyName(null));
    }
}
