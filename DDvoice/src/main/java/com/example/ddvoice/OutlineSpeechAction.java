package com.example.ddvoice;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ddvoice.util.ApkInstaller;
import com.example.ddvoice.util.FucUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class OutlineSpeechAction implements View.OnClickListener {
    private Context ctx;
    public OutlineSpeechAction(Context context){
        ctx = context;
        mToast=Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
    }
    private MediaPlayer player;
    private static String TAG = OutlineSpeechAction.class.getSimpleName();
    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    private Toast mToast;

    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    ApkInstaller mInstaller;





    public void initIflytek(){
        Activity activity = (Activity)ctx;

        activity.findViewById(R.id.voice_input).setOnClickListener(this);
        SpeechUtility.createUtility(ctx, SpeechConstant.APPID + "=564f2dfe");

        mEngineType = SpeechConstant.TYPE_LOCAL;
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            mInstaller.install();
        } else {
            String result = FucUtil.checkLocalResource();
            if (!TextUtils.isEmpty(result)) {
                showTip(result);
            }
        }
    }


    int ret = 0;


    @Override
    public void onClick(View view){

        startSpeenchRecognition();
    }

    public void startSpeenchRecognition() {
                player = MediaPlayer.create(ctx, R.raw.begin);
                player.start();
                FlowerCollector.onEvent(ctx, "iat_recognize");

                setParam();
                ret = mIat.startListening(mRecognizerListener);
                Log.v("????",String.valueOf(ret));
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
            showTip("成功");
        }

        }

    public void speechRecognition(){//初始化

        //1.创建SpeechRecognizer对象，第二个参数： 本地听写时传InitListener
        mIat= SpeechRecognizer.createRecognizer(ctx, mInitListener);
        Activity activity = (Activity)ctx;
        mInstaller = new ApkInstaller(activity);
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
            }
        }
    };




    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小为" + volume);
            Log.d(TAG, "音量" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            Log.v(">>>>>",resultJson.toString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

    }





    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }


    public void setParam() {
        mIat.setParameter(SpeechConstant.PARAMS, null);


        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");


        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");



        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");


        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");


        mIat.setParameter(SpeechConstant.ASR_PTT,"1");


        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

}
