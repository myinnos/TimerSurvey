package in.myinnos.timersurveylib.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import in.myinnos.timersurveylib.R;
import in.myinnos.timersurveylib.SurveyActivity;
import in.myinnos.timersurveylib.function.RealmObjectFlow;
import in.myinnos.timersurveylib.models.SurveyProperties;
import in.myinnos.timersurveylib.widgets.AppSurveyConstants;
import in.myinnos.timersurveylib.widgets.SurveyHelper;


public class FragmentStart extends Fragment {

    private FragmentActivity mContext;
    private TextView textView_start;
    private String registeredBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_start, container, false);

        // delete realm object
        RealmObjectFlow.clearObject();

        textView_start = (TextView) rootView.findViewById(R.id.textView_start);
        Button button_continue = (Button) rootView.findViewById(R.id.button_continue);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Answers.getInstance().put_answer(AppSurveyConstants.SUR_REGISTERED_BY, registeredBy);
                //Answers.getInstance().put_answer(AppSurveyConstants.SUR_CUSTOMER_ID, customerId);

                SurveyHelper.putAnswer(AppSurveyConstants.SUR_REGISTERED_BY, registeredBy,
                        "string", AppSurveyConstants.SUR_REGISTERED_BY, registeredBy);

                ((SurveyActivity) mContext).go_to_next();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        SurveyProperties survery_properties = (SurveyProperties) getArguments().getSerializable("survery_properties");
        registeredBy = getArguments().getString(AppSurveyConstants.SUR_REGISTERED_BY);

        assert survery_properties != null;
        textView_start.setText(Html.fromHtml(survery_properties.getIntroMessage()));

    }
}