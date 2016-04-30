package com.example.ddvoice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


import java.util.Map;

/**
 * Created by owen_ on 2016-04-29.
 */
public class MassageEditActivity extends Activity {

    private DBManager dbManager;
    private Bundle bundle;
    private EditText contactName,phoneNumber,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);;
        setContentView(R.layout.massage_edit_activity);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String name = bundle.getSerializable("Name").toString();
        Log.v("name",name);
        final OnlineSpeechAction vbutton = new OnlineSpeechAction(MassageEditActivity.this);
        vbutton.initIflytek();
        vbutton.speechRecognition();

        dbManager = new DBManager(this);

        contactName = (EditText)findViewById(R.id.massageContactNameEditText);
        phoneNumber = (EditText)findViewById(R.id.massagePhoneNumberEditText);
        content = (EditText)findViewById(R.id.massageContentEditText);

        Button returnButton = (Button)findViewById(R.id.massageEditReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MassageEditActivity.this, MassegeActivity.class));
                MassageEditActivity.this.finish();
            }
        });

        bundle = getIntent().getExtras();

//        loadPage(bundle.getInt("tag"));
    }

//    public void loadPage(int tag)
//    {
//        if(tag != -1)
//        {
//            Map<String,String> massageRecord = dbManager.queryOneMassageRecord(tag);
//            contactName.setText(massageRecord.get("contactName"));
//            phoneNumber.setText(massageRecord.get("phonenumber"));
//            content.setText(massageRecord.get("content"));
//        }
//    }


}
