package com.gashfara.akitk.bether;

/**
 * Created by akitk on 2016/04/03.
 */
public class QuestionnaireRecord {
    //保存するデータ全てを変数で定義します。
    private String qnumber;
    private String question;
    private Integer radioAnswer;

    //データを１つ作成する関数。項目が増えたら増やす。
    public QuestionnaireRecord(String qnumber, String question) {
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
