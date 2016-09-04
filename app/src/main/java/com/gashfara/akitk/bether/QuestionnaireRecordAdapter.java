package com.gashfara.akitk.bether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akitk on 2016/04/03.
 */


public class QuestionnaireRecordAdapter extends ArrayAdapter<QuestionnaireRecord> {

    private static class ViewHolder {
        protected RadioGroup radioGroup;
    }

    private final ArrayList<QuestionnaireRecord> questionnaireRecords;

    public QuestionnaireRecordAdapter(Context context, ArrayList<QuestionnaireRecord> questionnaireRecords) {
        super(context,  R.layout.questionnaire_item, questionnaireRecords);
        this.questionnaireRecords = questionnaireRecords;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get the data item for this position
        QuestionnaireRecord textRecord = getItem(position);

        //
        ViewHolder viewHolder = null;

        // check if an existing view is being reused, otherwise inflate the view.
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.questionnaire_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.radioGroup);
            viewHolder.radioGroup.setTag(position);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lookup view for data population
        TextView textView1 = (TextView) convertView.findViewById(R.id.text1);
        TextView textView2 = (TextView) convertView.findViewById(R.id.text2);
        // populate the data into the template view using the data object
        textView1.setText(textRecord.getQnumber());
        textView2.setText(textRecord.getQuestion());


        viewHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int getPosition;
                Button btn;
                switch (checkedId) {
                    case R.id.radioFirst:
                        getPosition = (Integer) group.getTag();
                        questionnaireRecords.get(getPosition).setRadioAnswer(1);
                        break;
                    case R.id.radioSecond:
                        getPosition = (Integer) group.getTag();
                        questionnaireRecords.get(getPosition).setRadioAnswer(2);
                        break;
                    case R.id.radioThird:
                        getPosition = (Integer) group.getTag();
                        questionnaireRecords.get(getPosition).setRadioAnswer(3);
                        break;
                    case R.id.radioFourth:
                        getPosition = (Integer) group.getTag();
                        questionnaireRecords.get(getPosition).setRadioAnswer(4);
                        break;
                    case R.id.radioFifth:
                        getPosition = (Integer) group.getTag();
                        questionnaireRecords.get(getPosition).setRadioAnswer(5);
                        break;
                }
            }
        });

        return convertView;
    }

    //setMessageRecordsは、QuestionnaireActivity内で使用されている。
    public void setMessageRecords(List<QuestionnaireRecord> objects) {
        //ArrayAdapterを空にする。
        clear();
        //テータの数だけMessageRecordを追加
        for(QuestionnaireRecord object : objects) {
            add(object);
        }
        //データの変更を通知します。
        notifyDataSetChanged();
    }
}



