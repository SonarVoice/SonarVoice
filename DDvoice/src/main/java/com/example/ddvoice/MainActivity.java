package com.example.ddvoice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public class MainActivity extends Activity {

	    private MediaPlayer player;//ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	    //Toastï¿½ï¿½Ê¾ï¿½ï¿½Ï¢
	    private Toast info;


	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		info=Toast.makeText(this, "", Toast.LENGTH_SHORT);

		    OnlineSpeechAction vbutton = new OnlineSpeechAction(this);
			/*mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("ï¿½ï¿½ï¿½Ú³ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ôºò¡­¡ï¿½ ^_^");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();*/
			showTip("³õÊ¼»¯ÖÐ...");
			//info.makeText(getApplicationContext(), "ï¿½ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½...", 5).show();
	       // showTip("hai");



			vbutton.initIflytek();
			vbutton.initUI();
			vbutton.speechRecognition();
			//mProgressDialog.dismiss();
		    showTip("³õÊ¼»¯Íê±Ï");
			//info.makeText(getApplicationContext(), "ï¿½ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½ï¿?, 5).show();

			player = MediaPlayer.create(MainActivity.this, R.raw.lock);
			player.start();
			vbutton.beginSpeak("ÄãºÃ£¬ÎÒÊÇSonar£¬ÄúµÄÖÇÄÜÓïÒôÖúÊÖ¡£", false);


    }

	private void showTip(final String str) {
		info.setText(str);
		info.show();
	}


}
