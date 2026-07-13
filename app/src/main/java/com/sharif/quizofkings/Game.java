package com.sharif.quizofkings;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "games")
public class Game implements Serializable {
    @PrimaryKey
    @NonNull
    private int id;

    @NonNull
    private int numberOfQuestions;

    @NonNull
    private String userEmail;

    @NonNull
    public String getUserEmail() {
        return userEmail;
    }

    @NonNull
    private ArrayList<Question> questions;

    public Game(int id, int numberOfQuestions, @NonNull String userEmail, @NonNull ArrayList<Question> questions) {
        this.id = id;
        this.numberOfQuestions = numberOfQuestions;
        this.userEmail = userEmail;
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public void setUserEmail(@NonNull String userEmail) {
        this.userEmail = userEmail;
    }

    public void setQuestions(@NonNull ArrayList<Question> questions) {
        this.questions = questions;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    @NonNull
    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
