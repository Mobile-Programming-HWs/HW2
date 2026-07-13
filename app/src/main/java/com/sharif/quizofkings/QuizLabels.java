package com.sharif.quizofkings;

import java.util.Locale;

final class QuizLabels {

    private QuizLabels() {
    }

    static String categoryName(int category) {
        switch (category) {
            case 21: return "Sports";
            case 25: return "Art";
            case 26: return "Celebrities";
            case 27: return "Animals";
            case 28: return "Vehicles";
            default: return "Category " + category;
        }
    }

    static String difficultyName(String difficulty) {
        if (difficulty == null || difficulty.trim().isEmpty()) {
            return "Unknown";
        }
        String trimmed = difficulty.trim().toLowerCase(Locale.US);
        return trimmed.substring(0, 1).toUpperCase(Locale.US) + trimmed.substring(1);
    }
}
