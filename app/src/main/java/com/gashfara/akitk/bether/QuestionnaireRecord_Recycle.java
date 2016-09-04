package com.gashfara.akitk.bether;

/**
 * Created by akitk on 2016/05/23.
 */
public class QuestionnaireRecord_Recycle {
    private String qnumber;
    private String question;
    private Integer radioAnswer;

    public QuestionnaireRecord_Recycle(String qnumber, String question) {
        this.qnumber = qnumber;
        this.question = question;
    }

    public void setRadioAnswer(Integer position){
        this.radioAnswer = position;
    }

    //Accessor
    public String getQnumber() {
        return qnumber;
    }
    public String getQuestion() {
        return question;
    }

    public Integer getRadioAnswer() {
        return radioAnswer;
    }

    @Override
    public String toString(){
        Integer value = radioAnswer;
        return value+"";
    }

}
