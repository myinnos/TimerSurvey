package in.myinnos.timersurveylib.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.myinnos.timersurveylib.R;
import in.myinnos.timersurveylib.SurveyActivity;
import in.myinnos.timersurveylib.models.Question;
import in.myinnos.timersurveylib.widgets.AppSurveyConstants;
import in.myinnos.timersurveylib.widgets.SurveyHelper;

public class FragmentImageGrid extends Fragment {

    private Question q_data;
    private FragmentActivity mContext;
    private Button button_continue;
    private TextView textview_q_title;
    private ImageView image_1, image_2;
    private TextView textview_a_title_1, textview_a_title_2;
    private final ArrayList<RadioButton> allRb = new ArrayList<>();
    private boolean at_leaset_one_checked = false;
    private String questionId, questionVariableType;
    private String registeredBy;
    private LinearLayout liImg01, liImg02;
    private String question01, question02;
    private String answer01, answer02;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_image_grid, container, false);

        textview_a_title_1 = (TextView) rootView.findViewById(R.id.textview_a_title_1);
        textview_a_title_2 = (TextView) rootView.findViewById(R.id.textview_a_title_2);

        image_1 = (ImageView) rootView.findViewById(R.id.image_1);
        image_2 = (ImageView) rootView.findViewById(R.id.image_2);
        liImg01 = (LinearLayout) rootView.findViewById(R.id.liImg01);
        liImg02 = (LinearLayout) rootView.findViewById(R.id.liImg02);

        button_continue = (Button) rootView.findViewById(R.id.button_continue);
        textview_q_title = (TextView) rootView.findViewById(R.id.textview_q_title);

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SurveyActivity) mContext).go_to_next();
            }
        });

        return rootView;
    }

    private void collect_data(int value) {

        //----- collection & validation for is_required
        String the_choice = "";
        String the_choice_answers = "";
        at_leaset_one_checked = false;

        if (value == 0) {
            at_leaset_one_checked = true;
            the_choice = question01;
            the_choice_answers = answer01;
            textview_a_title_1.setTextColor(getActivity().getResources().getColor(R.color.survey_text_choosen));
            textview_a_title_2.setTextColor(getActivity().getResources().getColor(R.color.survey_text));

        } else if (value == 1) {
            at_leaset_one_checked = true;
            the_choice = question02;
            the_choice_answers = answer02;
            textview_a_title_2.setTextColor(getActivity().getResources().getColor(R.color.survey_text_choosen));
            textview_a_title_1.setTextColor(getActivity().getResources().getColor(R.color.survey_text));
        }

        if (the_choice.length() > 0) {
            //Answers.getInstance().put_answer(questionId, the_choice);
            SurveyHelper.putAnswer(textview_q_title.getText().toString().trim(), the_choice_answers,
                    questionVariableType, questionId, the_choice);

        }

        if (q_data.getRequired()) {
            if (at_leaset_one_checked) {
                button_continue.setVisibility(View.VISIBLE);
            } else {
                button_continue.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        q_data = (Question) getArguments().getSerializable("data");
        registeredBy = getArguments().getString(AppSurveyConstants.SUR_REGISTERED_BY);

        questionId = q_data.getQuestionId();
        questionVariableType = q_data.getQuestion_v_type();
        textview_q_title.setText(Html.fromHtml(q_data.getQuestionTitle()));

        final List<String> qq_data = new ArrayList<String>();
        final List<Object> qq_data_tag = new ArrayList<Object>();

        for (int i = 0; i < q_data.getChoicesListModelList().size(); i++) {
            qq_data.add(q_data.getChoicesListModelList().get(i).getName());
            qq_data_tag.add(q_data.getChoicesListModelList().get(i).getValue());
        }

        String[] splitted_1 = qq_data.get(0).split(":-");
        String[] splitted_2 = qq_data.get(1).split(":-");

        question01 = splitted_1[0];
        question02 = splitted_2[0];
        answer01 = qq_data_tag.get(0).toString();
        answer02 = qq_data_tag.get(1).toString();

        textview_a_title_1.setText(Html.fromHtml(splitted_1[0]));
        Log.d("asxasx", splitted_1[1]);
        Picasso.get()
                .load(splitted_1[1])
                .into(image_1);
        textview_a_title_2.setText(Html.fromHtml(splitted_2[0]));
        Picasso.get()
                .load(splitted_2[1])
                .into(image_2);

        /*if (q_data.getRandomChoices()) {
            Collections.shuffle(qq_data);
        }*/

        liImg01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                collect_data(0);
            }
        });

        liImg02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collect_data(1);
            }
        });


        if (q_data.getRequired()) {
            if (at_leaset_one_checked) {
                button_continue.setVisibility(View.VISIBLE);
            } else {
                button_continue.setVisibility(View.GONE);
            }
        }


    }


}