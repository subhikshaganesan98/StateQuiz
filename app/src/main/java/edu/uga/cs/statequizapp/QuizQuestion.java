package edu.uga.cs.statequizapp;

public class QuizQuestion {

    private String quizDate;
    private String time;
    private int question1Id;
    private int question2Id;
    private int question3Id;
    private int question4Id;
    private int question5Id;
    private int question6Id;
    private int correctAnswers;
    private int questionsAnswered;

    // Constructor with updated parameters
    public QuizQuestion(String quizDate, String time, int question1Id, int question2Id, int question3Id,
                        int question4Id, int question5Id, int question6Id, int correctAnswers, int questionsAnswered) {
        this.quizDate = quizDate;
        this.time = time;
        this.question1Id = question1Id;
        this.question2Id = question2Id;
        this.question3Id = question3Id;
        this.question4Id = question4Id;
        this.question5Id = question5Id;
        this.question6Id = question6Id;
        this.correctAnswers = correctAnswers;
        this.questionsAnswered = questionsAnswered;
    }

    // Getters and setters for the fields

    public String getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(String quizDate) {
        this.quizDate = quizDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getQuestion1Id() {
        return question1Id;
    }

    public void setQuestion1Id(int question1Id) {
        this.question1Id = question1Id;
    }

    public int getQuestion2Id() {
        return question2Id;
    }

    public void setQuestion2Id(int question2Id) {
        this.question2Id = question2Id;
    }

    public int getQuestion3Id() {
        return question3Id;
    }

    public void setQuestion3Id(int question3Id) {
        this.question3Id = question3Id;
    }

    public int getQuestion4Id() {
        return question4Id;
    }

    public void setQuestion4Id(int question4Id) {
        this.question4Id = question4Id;
    }

    public int getQuestion5Id() {
        return question5Id;
    }

    public void setQuestion5Id(int question5Id) {
        this.question5Id = question5Id;
    }

    public int getQuestion6Id() {
        return question6Id;
    }

    public void setQuestion6Id(int question6Id) {
        this.question6Id = question6Id;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public void setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
    }

}
