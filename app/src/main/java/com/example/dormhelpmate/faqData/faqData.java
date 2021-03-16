package com.example.dormhelpmate.faqData;

public class faqData {
    public String question, answer;

    public faqData() {

    }

    public faqData (String question, String answer) {
        this.question = question;
        this.answer = answer;
    }


    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
