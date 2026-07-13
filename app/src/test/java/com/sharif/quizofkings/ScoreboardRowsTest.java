package com.sharif.quizofkings;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScoreboardRowsTest {

    @Test
    public void topRows_keepsBestScoreForEachUserAndSortsDescending() {
        List<Score> scores = Arrays.asList(
                new Score("a@example.com", 10),
                new Score("b@example.com", 40),
                new Score("a@example.com", 60),
                new Score("c@example.com", 20)
        );

        List<ScoreboardRows.Row> rows = ScoreboardRows.topRows(scores, 5);

        assertEquals(3, rows.size());
        assertEquals("a@example.com", rows.get(0).getUserEmail());
        assertEquals(60, rows.get(0).getScore());
        assertEquals("b@example.com", rows.get(1).getUserEmail());
        assertEquals(40, rows.get(1).getScore());
        assertEquals("c@example.com", rows.get(2).getUserEmail());
        assertEquals(20, rows.get(2).getScore());
    }

    @Test
    public void topRows_ignoresBlankUsersAndHonorsLimit() {
        List<Score> scores = Arrays.asList(
                new Score("first@example.com", 50),
                new Score(" ", 90),
                new Score("second@example.com", 40),
                new Score("third@example.com", 30)
        );

        List<ScoreboardRows.Row> rows = ScoreboardRows.topRows(scores, 2);

        assertEquals(2, rows.size());
        assertEquals("first@example.com", rows.get(0).getUserEmail());
        assertEquals("second@example.com", rows.get(1).getUserEmail());
    }
}
