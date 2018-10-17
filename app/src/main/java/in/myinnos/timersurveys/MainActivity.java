package in.myinnos.timersurveys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;

import in.myinnos.timersurveylib.SurveyActivity;
import in.myinnos.timersurveylib.widgets.AppSurveyConstants;
import in.myinnos.timersurveys.ApiInterface.ApiInterfaceSurveys;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int SURVEY_REQUEST = 1337;
    private Button btSurveyForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btSurveyForm = (Button) findViewById(R.id.btSurveyForm);

        btSurveyForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.myjson.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiInterfaceSurveys apiInterfaceSurveys = retrofit.create(ApiInterfaceSurveys.class);

                Call<JsonObject> jsonCall = apiInterfaceSurveys.timerSurveyForm();

                jsonCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        openSurvey(response.body().toString(), "BAA0006",
                                30000, "REMAINING TIME:",
                                true);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SURVEY_REQUEST) {
            if (resultCode == RESULT_OK) {

                Boolean answers_status = data.getExtras().getBoolean("answers_status");
                if (!answers_status) {
                    Toast.makeText(getApplicationContext(),
                            "failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String answers_json = data.getExtras().getString("answers");
                Log.d("****", "****************** WE HAVE ANSWERS ******************");
                Log.v("ANSWERS JSON", answers_json);
                Log.d("****", "*****************************************************");
                Toast.makeText(getApplicationContext(),
                        String.valueOf(answers_json), Toast.LENGTH_SHORT).show();
                //do whatever you want with them...
            }
        }
    }

    private void openSurvey(String json, String registered_by, long TIMER,
                            String TIMER_HEADER, Boolean HANDLING_BACK_BUTTON) {
        Intent i_survey = new Intent(MainActivity.this, SurveyActivity.class);
        //i_survey.putExtra("json_survey", loadSurveyJson("customer_survey.json"));
        i_survey.putExtra("json_survey", json);
        i_survey.putExtra(AppSurveyConstants.SUR_REGISTERED_BY, registered_by);
        i_survey.putExtra(AppSurveyConstants.TIMER_IN_MILLI_SECONDS, TIMER);
        i_survey.putExtra(AppSurveyConstants.TIMER_HEADER_STRING, TIMER_HEADER);
        i_survey.putExtra(AppSurveyConstants.HANDLING_BACK_BUTTON, HANDLING_BACK_BUTTON);
        startActivityForResult(i_survey, SURVEY_REQUEST);
    }
}
