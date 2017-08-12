package com.technowebx.feedplus;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.LinearLayout.LayoutParams;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
    private static final String TAG = "MainActivity";
    private LinearLayout questions_layout;
    private TextView[] dtv;
    private Spinner[] dsp;
    private int workshop_id,noofquestions,ans_type_parser=0,strongly_agree,agree,strongly_disagree,disagree;
    private String[] feedback_answers;
    private String user_email,user_phone,user_branch,user_college,instructor_name,suggestions_idea;
    private Button next1,next2,next3,next4,next5,next6;
    private Spinner ws_name,ws_date;
    private WorkShop data[];
    private int state=0;

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {


            String token = FirebaseInstanceId.getInstance().getToken();

            // Log and toast
            String msg = getString(R.string.msg_token_fmt, token);
            //MyFirebaseInstanceIDService.sendRegistrationToServer(token);
            MyFirebaseInstanceIDService.send_data sd = new MyFirebaseInstanceIDService.send_data(token);
            sd.execute();
            Log.d(TAG, msg);

        } else if (currentVersionCode > savedVersionCode) {


            String token = FirebaseInstanceId.getInstance().getToken();

            // Log and toast
            String msg = getString(R.string.msg_token_fmt, token);
            //MyFirebaseInstanceIDService.sendRegistrationToServer(token);
            MyFirebaseInstanceIDService.send_data sd = new MyFirebaseInstanceIDService.send_data(token);
            sd.execute();
            Log.d(TAG, msg);
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirstRun();

        questions_layout = (LinearLayout) findViewById(R.id.feedback_questions_here);

        ws_name = (Spinner) findViewById(R.id.ws_name);
        ws_date = (Spinner) findViewById(R.id.ws_date);

        next1 = (Button) findViewById(R.id.next_1st);
        next2 = (Button) findViewById(R.id.next_2nd);
        next3 = (Button) findViewById(R.id.next_3rd);
        next4 = (Button) findViewById(R.id.next_4th);
        next5 = (Button) findViewById(R.id.next_5th);
        next6 = (Button) findViewById(R.id.next_6th);
        next1.setOnClickListener(this);
        next2.setOnClickListener(this);
        next3.setOnClickListener(this);
        next4.setOnClickListener(this);
        next5.setOnClickListener(this);
        next6.setOnClickListener(this);

        ws_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> values = new ArrayList<>();
                //for (int i2=1;i2<=jsonArray.length();i2++) {
                //    String string1 = jsonArray.getJSONObject(String.valueOf(i2)).getString("name");
                //   if (string1.equals(string2))
                values.add(data[i].fromdate+" - "+data[i].todate);
                //}
                setSpinnerAdapter(ws_date,values);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        if (state==1) {
            super.onBackPressed();
        }
        else {
            Toast.makeText(this,"Press again to exit",Toast.LENGTH_LONG).show();
            state++;
        }
    }

    @Override
    public void onClick(View view) {
        if(view==next1) {
            user_email = getEditTextVal(R.id.email_user);
            user_phone = getEditTextVal(R.id.phone_user);
            user_branch = getSpinnerVal(R.id.branch_user);
            user_college = getSpinnerVal(R.id.college_user);
            if(user_email.isEmpty()||user_phone.isEmpty()||user_branch.equals("Choose Branch")||user_college.equals("Choose College")) {
                Toast.makeText(this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                return;
            }
            //Toast.makeText(this,user_email+user_phone+user_branch+user_college,Toast.LENGTH_LONG).show();
            WorkShop_Details wsd = new WorkShop_Details();
            wsd.execute();
        }
        if(view==next2) {
            instructor_name = getEditTextVal(R.id.name_instructor);
            workshop_id = data[getSpinnerPosVal(R.id.ws_name)].id;
            if(instructor_name.isEmpty()) {
                Toast.makeText(this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                return;
            }
            //Toast.makeText(this,instructor_name,Toast.LENGTH_LONG).show();
            questions_load ql = new questions_load();
            ql.execute();
        }
        if(view==next3) {
            if (ans_type_parser==10) {
                int sa=0,a=0,d=0,sd=0;
                for(int i=1;i<=10;i++) {
                    int resID = getResources().getIdentifier("qus_"+i+"_ans", "id", getPackageName());
                    if(getSpinnerPosVal(resID)==0) {
                        Toast.makeText(this, "Select any Option at " + i, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if (getSpinnerPosVal(resID)==1)
                        sa++;
                    else if (getSpinnerPosVal(resID)==2)
                        a++;
                    else if (getSpinnerPosVal(resID)==3)
                        d++;
                    else if (getSpinnerPosVal(resID)==4)
                        sd++;
                }
                sa *= 10;
                a *= 10;
                d *= 10;
                sd *= 10;
                strongly_agree = sa;
                agree = a;
                disagree = d;
                strongly_disagree = sd;
            }
            else {
                String[] answers = new String[10];
                for (int i = 1; i <= noofquestions; i++) {
                    int resID = getResources().getIdentifier("qus_" + i + "_ans", "id", getPackageName());
                    if (getSpinnerPosVal(resID) == 0) {
                        Toast.makeText(this, "Select any Option at " + i, Toast.LENGTH_LONG).show();
                        return;
                    } else
                        answers[i - 1] = ((Spinner) findViewById(resID)).getSelectedItem().toString();
                }
                feedback_answers = answers;
            }
            findViewById(R.id.feedback_questions).setVisibility(View.GONE);
            findViewById(R.id.idea_details).setVisibility(View.VISIBLE);
        }
        if (view==next4) {
            suggestions_idea = getEditTextVal(R.id.user_idea);
            if((suggestions_idea.trim()).isEmpty()) {
                Toast.makeText(this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                return;
            }
            Send_Feedback sf = new Send_Feedback();
            sf.execute();
        }
        if (view == next5) {
            EmptyEditText(R.id.email_user);
            EmptyEditText(R.id.phone_user);
            EmptyEditText(R.id.name_instructor);
            EmptyEditText(R.id.user_idea);
            EmptySpinner(R.id.branch_user);
            EmptySpinner(R.id.college_user);
            EmptySpinner(R.id.ws_name);
            noofquestions=0;
            ans_type_parser=0;
            int cnt = questions_layout.getChildCount();
            questions_layout.removeViews(1,cnt-1);
            findViewById(R.id.success_feedback).setVisibility(View.GONE);
            findViewById(R.id.user_details).setVisibility(View.VISIBLE);
        }
        if (view==next6) {
            EmptyEditText(R.id.email_user);
            EmptyEditText(R.id.phone_user);
            EmptyEditText(R.id.name_instructor);
            EmptyEditText(R.id.user_idea);
            EmptySpinner(R.id.branch_user);
            EmptySpinner(R.id.college_user);
            EmptySpinner(R.id.ws_name);
            noofquestions=0;
            ans_type_parser=0;
            int cnt = questions_layout.getChildCount();
            questions_layout.removeViews(1,cnt-1);
            findViewById(R.id.failed_feedback).setVisibility(View.GONE);
            findViewById(R.id.user_details).setVisibility(View.VISIBLE);
        }

    }

    public class Send_Feedback extends AsyncTask<Void,Void,String> {
        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this,"Sending Feedback","Please Wait...",false,false);
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHandler rh = new RequestHandler();
            HashMap<String,String> param = new HashMap<>();
            param.put("email",user_email);
            param.put("phone",user_phone);
            param.put("branch",user_branch);
            param.put("college",user_college);
            param.put("ws_id",String.valueOf(workshop_id));
            param.put("instructor_name",instructor_name);
            if (ans_type_parser==10) {
                param.put("type", String.valueOf(1));
                param.put("ques_sa",String.valueOf(strongly_agree));
                param.put("ques_a",String.valueOf(agree));
                param.put("ques_d",String.valueOf(disagree));
                param.put("ques_sd",String.valueOf(strongly_disagree));
            }
            else {
                param.put("type", String.valueOf(2));
                param.put("noofquestions",String.valueOf(noofquestions));
                for (int i=0;i<noofquestions;i++) {
                    param.put("qus_" + i + "_ans", feedback_answers[i]);
                }
            }

            param.put("suggestions_idea",suggestions_idea);
            result = rh.sendPostRequest("http://www.adityawebapps.com/feedback/sentfeedback.php",param);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.dismiss();
            //Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("1")) {
                findViewById(R.id.idea_details).setVisibility(View.GONE);
                findViewById(R.id.success_feedback).setVisibility(View.VISIBLE);
                //Toast.makeText(MainActivity.this,"Feedback Successfully Sent",Toast.LENGTH_LONG).show();
            }
            else {
                findViewById(R.id.idea_details).setVisibility(View.GONE);
                findViewById(R.id.failed_feedback).setVisibility(View.VISIBLE);
                //Toast.makeText(MainActivity.this,"Failed Sending Feedback"+result+" "+workshop_id,Toast.LENGTH_LONG).show();
            }
        }
    }

    public class WorkShop_Details extends AsyncTask<Void,Void,String> {
        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this,"Retrieving Workshop Details","Please Wait...",false,false);
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHandler rh = new RequestHandler();
            HashMap<String,String> param = new HashMap<>();
            result = rh.sendPostRequest("http://www.adityawebapps.com/feedback/getworkdet.php",param);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonArray = new JSONObject(result);
                data = new WorkShop[jsonArray.length()];
                ArrayList<String> values = new ArrayList<>();
                for (int i = 1; i<= jsonArray.length(); i++) {
                    int id = jsonArray.getJSONObject(String.valueOf(i)).getInt("id");
                    String name = jsonArray.getJSONObject(String.valueOf(i)).getString("name");
                    String fromdate = jsonArray.getJSONObject(String.valueOf(i)).getString("fromdate");
                    String todate = jsonArray.getJSONObject(String.valueOf(i)).getString("todate");
                    data[i-1] = new WorkShop(id,name,fromdate,todate);
                    values.add(name);
                }
                setSpinnerAdapter(ws_name,values);

                findViewById(R.id.user_details).setVisibility(View.GONE);
                findViewById(R.id.ws_details).setVisibility(View.VISIBLE);
                loading.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class questions_load extends AsyncTask<Void,Void,String> {
        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this,"Retrieving Questions","Please Wait...",false,false);
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHandler rh = new RequestHandler();
            HashMap<String,String> param = new HashMap<>();
            param.put("id", String.valueOf(workshop_id));
            result = rh.sendPostRequest("http://www.adityawebapps.com/feedback/getquestions.php",param);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonmain = new JSONObject(result);
                JSONObject jsonArray = jsonmain.getJSONObject("questions");
                noofquestions = jsonArray.length();
                dtv = new TextView[noofquestions];
                dsp = new Spinner[noofquestions];
                for (int i = 1; i<= jsonArray.length(); i++) {
                    dtv[i-1] = new TextView(MainActivity.this);
                    dsp[i-1] = new Spinner(MainActivity.this);
                    JSONObject jsr = jsonArray.getJSONObject(String.valueOf(i));
                    JSONArray answers_json = jsr.getJSONArray("answers_type");
                    if ((answers_json.toString()).equals("[\"Strongly Agree\",\"Agree\",\"Disagree\",\"Strongly Disagree\"]")) {
                        ans_type_parser++;
                    }
                    String[] answervalues = new String[answers_json.length()+1];
                    answervalues[0] = "Choose Option";
                    for (int j=1;j<=answers_json.length();j++) {
                        answervalues[j] = answers_json.getString(j-1);
                    }
                    //Toast.makeText(MainActivity.this,answers_json.length()+" "+answers_json.getString(1),Toast.LENGTH_LONG).show();
                    TextView tvquestion = new TextView(MainActivity.this);
                    Spinner spanswer = new Spinner(MainActivity.this);

                    String question = jsonArray.getJSONObject(String.valueOf(i)).getString("question");
                    //String answers_type = jsonArray.getJSONObject(String.valueOf(i)).getString("answers_type");

                    LayoutParams textviewlp=new LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    LayoutParams spinnerlp=new LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimensionPixelSize(R.dimen.spinner_height)));
                    spinnerlp.setMargins(0,(int) getResources().getDimensionPixelSize(R.dimen.spinner_margin_top),0,(int) getResources().getDimensionPixelSize(R.dimen.spinner_margin_bottom));
                    //Toast.makeText(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.spinner_margin_top)+" "+getResources().getDimensionPixelSize(R.dimen.spinner_margin_bottom),Toast.LENGTH_LONG).show();
                    int resID = getResources().getIdentifier("qus_"+i+"_ans", "id", getPackageName());
                    spanswer.setId(resID);
                    spanswer.setPaddingRelative((int) getResources().getDimension(R.dimen.spinner_margin_start),0,0,0);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                            MainActivity.this, android.R.layout.simple_spinner_item, answervalues);
                    spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                    spanswer.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    tvquestion.setLayoutParams(textviewlp);
                    spanswer.setLayoutParams(spinnerlp);
                    tvquestion.setTextColor(Color.parseColor("#ffffff"));
                    tvquestion.setText(question);

                    questions_layout.addView(tvquestion);
                    questions_layout.addView(spanswer);

                    dtv[i-1] = tvquestion;
                    dsp[i-1] = spanswer;

                    spanswer.setAdapter(new ArrayAdapter<String>(
                            MainActivity.this, android.R.layout.simple_spinner_dropdown_item, answervalues));

                }

                //Toast.makeText(MainActivity.this,""+ans_type_parser,Toast.LENGTH_LONG).show();
                findViewById(R.id.ws_details).setVisibility(View.GONE);
                findViewById(R.id.feedback_questions).setVisibility(View.VISIBLE);
                loading.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }
    }

    public void setSpinnerAdapter(Spinner spinner, ArrayList<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, values);
/*        Object[] st = values.toArray();
        for (Object s : st) {
            if (values.indexOf(s) != values.lastIndexOf(s)) {
                values.remove(values.lastIndexOf(s));
            }
        }*/
        spinner.setAdapter(adapter);
    }

    public String getEditTextVal(int id) {
        EditText field = (EditText) findViewById(id);
        return field.getText().toString();
    }

    public String getSpinnerVal(int id) {
        Spinner field = (Spinner) findViewById(id);
        return field.getSelectedItem().toString();
    }
    public int getSpinnerPosVal(int id) {
        Spinner field = (Spinner) findViewById(id);
        return field.getSelectedItemPosition();
    }
    public void EmptyEditText(int id) {
        EditText field = (EditText) findViewById(id);
        field.setText("");
    }

    public void EmptySpinner(int id) {
        Spinner field = (Spinner) findViewById(id);
        field.setSelection(0);
    }
}
