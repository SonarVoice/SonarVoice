package com.example.ddvoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by owen_ on 2016-04-22.
 */
public class AlarmActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();


        new AlertDialog.Builder(AlarmActivity.this).
                setTitle("ÄÖÖÓ").
                setMessage(bundle.getString("memorandum")).
                setPositiveButton("ÖªµÀÁË", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                AlarmActivity.this.finish();//å…³é—­Activity
            }
        }).create().show();


    }
}
