package com.sharif.quizofkings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ScoreboardRows {

    private ScoreboardRows() {
    }

    static List<Row> topRows(List<Score> scores, int limit) {
        if (scores == null || limit <= 0) {
            return new ArrayList<>();
        }

        Map<String, Row> bestRowsByEmail = new HashMap<>();
        for (Score score : scores) {
            if (score == null || isBlank(score.getUserEmail())) {
                continue;
            }

            String email = score.getUserEmail().trim();
            Row current = bestRowsByEmail.get(email);
            if (current == null || score.getScore() > current.getScore()) {
                bestRowsByEmail.put(email, new Row(email, score.getScore()));
            }
        }

        ArrayList<Row> rows = new ArrayList<>(bestRowsByEmail.values());
        Collections.sort(rows, (left, right) -> Long.compare(right.getScore(), left.getScore()));
        if (rows.size() > limit) {
            return new ArrayList<>(rows.subList(0, limit));
        }
        return rows;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static final class Row {
        private final String userEmail;
        private final long score;

        Row(String userEmail, long score) {
            this.userEmail = userEmail;
            this.score = score;
        }

        String getUserEmail() {
            return userEmail;
        }

        long getScore() {
            return score;
        }
    }
}
