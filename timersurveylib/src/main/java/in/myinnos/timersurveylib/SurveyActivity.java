package in.myinnos.timersurveylib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import in.myinnos.timersurveylib.adapters.AdapterFragmentQ;
import in.myinnos.timersurveylib.fragment.FragmentEnd;
import in.myinnos.timersurveylib.fragment.FragmentImageGrid;
import in.myinnos.timersurveylib.fragment.FragmentStart;
import in.myinnos.timersurveylib.fragment.FragmentTextSimple;
import in.myinnos.timersurveylib.models.Question;
import in.myinnos.timersurveylib.models.SurveyPojo;
import in.myinnos.timersurveylib.widgets.AppSurveyConstants;
import in.myinnos.timersurveylib.widgets.bottomview.BottomDialog;
import io.realm.Realm;

public class SurveyActivity extends AppCompatActivity {

    private SurveyPojo mSurveyPojo;
    private ViewPager mPager;
    private String style_string = null;
    private String registered_by;
    private long TIMER_IN_MILLI_SECONDS = 0;
    private LinearLayout liProgress;
    private TextView txTimer;
    private String TIMER_HEADER_STRING = "";
    private Boolean HANDLING_BACK_BUTTON = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_survey);

        // Initialize Realm
        Realm.init(this);

        liProgress = (LinearLayout) findViewById(R.id.liProgress);
        liProgress.setVisibility(View.GONE);
        txTimer = (TextView) findViewById(R.id.txTimer);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mSurveyPojo = new Gson().fromJson(bundle.getString("json_survey"), SurveyPojo.class);
            /// registered_by user
            registered_by = bundle.getString(AppSurveyConstants.SUR_REGISTERED_BY);
            TIMER_IN_MILLI_SECONDS = bundle.getLong(AppSurveyConstants.TIMER_IN_MILLI_SECONDS);
            TIMER_HEADER_STRING = bundle.getString(AppSurveyConstants.TIMER_HEADER_STRING);
            HANDLING_BACK_BUTTON = bundle.getBoolean(AppSurveyConstants.HANDLING_BACK_BUTTON);
            //
            if (bundle.containsKey("style")) {
                style_string = bundle.getString("style");
            }
        }


        Log.i("json Object = ", String.valueOf(mSurveyPojo.getQuestions()));

        final ArrayList<Fragment> arraylist_fragments = new ArrayList<>();

        //- START -
        if (!mSurveyPojo.getSurveyProperties().getSkipIntro()) {
            FragmentStart frag_start = new FragmentStart();
            Bundle sBundle = new Bundle();
            sBundle.putSerializable("survery_properties", mSurveyPojo.getSurveyProperties());
            sBundle.putString("style", style_string);
            sBundle.putString(AppSurveyConstants.SUR_REGISTERED_BY, registered_by);
            frag_start.setArguments(sBundle);
            arraylist_fragments.add(frag_start);
        }

        //- FILL -
        for (Question mQuestion : mSurveyPojo.getQuestions()) {

            if (mQuestion.getQuestionType().equals("String")) {
                FragmentTextSimple frag = new FragmentTextSimple();
                Bundle xBundle = new Bundle();
                xBundle.putSerializable("data", mQuestion);
                xBundle.putString("style", style_string);
                xBundle.putString(AppSurveyConstants.SUR_REGISTERED_BY, registered_by);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);
            }

            if (mQuestion.getQuestionType().equals("ImageGrid")) {
                FragmentImageGrid frag = new FragmentImageGrid();
                Bundle xBundle = new Bundle();
                xBundle.putSerializable("data", mQuestion);
                xBundle.putString("style", style_string);
                xBundle.putString(AppSurveyConstants.SUR_REGISTERED_BY, registered_by);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);
            }

        }

        //- END -
        FragmentEnd frag_end = new FragmentEnd();
        Bundle eBundle = new Bundle();
        eBundle.putSerializable("survery_properties", mSurveyPojo.getSurveyProperties());
        eBundle.putString("style", style_string);
        eBundle.putString(AppSurveyConstants.SUR_REGISTERED_BY, registered_by);
        frag_end.setArguments(eBundle);
        arraylist_fragments.add(frag_end);

        mPager = (ViewPager) findViewById(R.id.pager);
        AdapterFragmentQ mPagerAdapter = new AdapterFragmentQ(getSupportFragmentManager(), arraylist_fragments);
        mPager.setAdapter(mPagerAdapter);

        new CountDownTimer(TIMER_IN_MILLI_SECONDS, 1000) {

            public void onTick(long millisUntilFinished) {
                txTimer.setText(TIMER_HEADER_STRING + " " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
                if ((millisUntilFinished / 1000) > 5) {
                    txTimer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    txTimer.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }

            public void onFinish() {
                txTimer.setText("TIME-UP");
                event_survey_completed(Answers.getInstance(), false);
            }

        }.start();
    }

    public void go_to_next() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            //super.onBackPressed();

            new BottomDialog.Builder(this)
                    .setTitle(R.string.close_popup_title)
                    .setContent(R.string.close_popup_message)
                    .setPositiveText(R.string.close_popup_ok)
                    .setNegativeText(R.string.close_popup_no)
                    .setPositiveBackgroundColorResource(R.color.colorPrimary)
                    //.setPositiveBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)
                    .setPositiveTextColorResource(android.R.color.white)
                    .setNegativeTextColorResource(R.color.colorAccent)
                    //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(BottomDialog dialog) {
                            SurveyActivity.this.finish();
                        }
                    }).show();

        } else {
            if (!HANDLING_BACK_BUTTON) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return;
            }
            Snackbar snackbar = Snackbar
                    .make(mPager, "Sorry, You cannot go back", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            snackbar.show();
        }
    }

    public void event_survey_completed(Answers instance, Boolean status) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("answers", instance.get_json_object());
        returnIntent.putExtra("answers_status", status);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
