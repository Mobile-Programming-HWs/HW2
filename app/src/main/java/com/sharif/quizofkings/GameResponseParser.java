package com.sharif.quizofkings;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

final class GameResponseParser {

    private static final Gson gson = new Gson();

    private GameResponseParser() {
    }

    static ArrayList<Question> parseQuestions(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            JsonObject object = JsonParser.parseString(json).getAsJsonObject();
            JsonElement responseCode = object.get("response_code");
            if (responseCode == null || responseCode.getAsInt() != 0) {
                return new ArrayList<>();
            }

            JsonArray results = object.getAsJsonArray("results");
            if (results == null || results.size() == 0) {
                return new ArrayList<>();
            }

            ArrayList<Question> questions = gson.fromJson(results, new TypeToken<ArrayList<Question>>() {
            }.getType());
            if (questions == null) {
                return new ArrayList<>();
            }
            return questions;
        } catch (RuntimeException e) {
            return new ArrayList<>();
        }
    }

    static boolean hasUsableQuestions(ArrayList<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return false;
        }

        for (Question question : questions) {
            if (question == null || isBlank(question.getQuestion()) || isBlank(question.getCorrect_answer())) {
                return false;
            }
            ArrayList<String> incorrectAnswers = question.getIncorrect_answers();
            if (incorrectAnswers == null || incorrectAnswers.size() < 3) {
                return false;
            }
            for (int i = 0; i < 3; i++) {
                if (isBlank(incorrectAnswers.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
