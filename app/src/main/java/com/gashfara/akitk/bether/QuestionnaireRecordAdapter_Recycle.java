package com.gashfara.akitk.bether;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by akitk on 2016/05/23.
 */
public class QuestionnaireRecordAdapter_Recycle extends
        RecyclerView.Adapter<QuestionnaireRecordAdapter_Recycle.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qNum;
        public TextView qDetail;

        //2016June09 add した
        protected RadioGroup radioGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            qNum = (TextView) itemView.findViewById(R.id.q_number);
            qDetail = (TextView) itemView.findViewById(R.id.q_detail);

        }

    }

    private List<QuestionnaireRecord_Recycle> mQuestionnaireRecords;

    public QuestionnaireRecordAdapter_Recycle(List<QuestionnaireRecord_Recycle> QuestionnaireRecords) {
        mQuestionnaireRecords = QuestionnaireRecords;
    }

    //onCreateViewHolderのOverride
    @Override
    public QuestionnaireRecordAdapter_Recycle.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View questionnaireView = inflater.inflate(R.layout.questionnaire_item_recycle, parent, false);

        final ViewHolder viewHolder = new ViewHolder(questionnaireView);

        //2016June09 add した産業
        viewHolder.radioGroup = (RadioGroup) questionnaireView.findViewById(R.id.radioGroup);

        //2016June09 add
        viewHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int getPosition;
                Button btn;
                switch (checkedId) {
                    case R.id.radioFirst:
                        //getPosition = (Integer) group.getTag();
                        getPosition = viewHolder.getAdapterPosition();
                        mQuestionnaireRecords.get(getPosition).setRadioAnswer(1);
                        break;
                    case R.id.radioSecond:
                        //getPosition = (Integer) group.getTag();
                        getPosition = viewHolder.getAdapterPosition();
                        mQuestionnaireRecords.get(getPosition).setRadioAnswer(2);
                        break;
                    case R.id.radioThird:
                        //getPosition = (Integer) group.getTag();
                        getPosition = viewHolder.getAdapterPosition();
                        mQuestionnaireRecords.get(getPosition).setRadioAnswer(3);
                        break;
                    case R.id.radioFourth:
                        //getPosition = (Integer) group.getTag();
                        getPosition = viewHolder.getAdapterPosition();
                        mQuestionnaireRecords.get(getPosition).setRadioAnswer(4);
                        break;
                    case R.id.radioFifth:
                        //getPosition = (Integer) group.getTag();
                        getPosition = viewHolder.getAdapterPosition();
                        mQuestionnaireRecords.get(getPosition).setRadioAnswer(5);
                        break;
                }
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(QuestionnaireRecordAdapter_Recycle.ViewHolder viewHolder, int position) {
        QuestionnaireRecord_Recycle questionnaireRecord_recycle = mQuestionnaireRecords.get(position);

        TextView qNumTextView = viewHolder.qNum;
        qNumTextView.setText(questionnaireRecord_recycle.getQnumber());

        TextView qDetTextVIew = viewHolder.qDetail;
        qDetTextVIew.setText(questionnaireRecord_recycle.getQuestion());
    }

    @Override
    public int getItemCount() {
        return mQuestionnaireRecords.size();
    }

    public void setMessageRecords(List<QuestionnaireRecord_Recycle> objects) {
        for(QuestionnaireRecord_Recycle object : objects) {

        }
        notifyItemInserted(objects.size()-1);
    }
}
