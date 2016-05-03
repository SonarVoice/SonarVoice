package com.example.ddvoice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CheckBox;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import android.content.Context;
import com.example.ddvoice.MainActivity;
/**
 * Created by jf on 4/18/2016.
 */
public class OnlineSpeechAction implements AdapterView.OnItemClickListener,View.OnClickListener {
        private Context ctx;
        public OnlineSpeechAction(Context context){
            ctx = context;
            info=Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        }
        private SpeechSynthesizer mTts;
        private String voicer="xiaoyan";

        public static boolean serviceFlag=false;
        public static JSONObject semantic = null,slots =null,answer=null,datetime=null,location=null,data=null;public static String operation = null,service=null;
        public static JSONArray result=null;
        public static String receiver=null, name = null,price=null,code=null,song = null,keywords=null,content=null,
                url=null,text=null,time=null,date=null,city=null,sourceName=null,target=null,source=null;
        public static String[] weatherDate=null,weather=null,tempRange=null,airQuality=null,wind=null,humidity=null,windLevel=null;

        private TextUnderstander mTextUnderstander;// ??????????????????�??
        private ListView mListView;
        private ArrayList<SiriListItem> list;
        ChatMsgViewAdapter mAdapter;
        private MediaPlayer player;
        public static  String SaveResult="";
        public static  String SRResult="";	//?????
        private static String SAResult="";//?????????
        private static String TAG = MainActivity.class.getSimpleName();
        //Toast??????
        private Toast info;
        //???????
        private TextView textView;
        //???????
        private SpeechRecognizer mIat;
        // ??????дUI
        private RecognizerDialog mIatDialog;
        // ??HashMap?�??д???
        private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
        // ????????
        private String mEngineType = SpeechConstant.TYPE_CLOUD;
        private String mEngineTypeTTS = SpeechConstant.TYPE_CLOUD;
        private SharedPreferences mSharedPreferences;
        private SharedPreferences mSharedPreferencesTTS;
        MainActivity mActivity = new MainActivity();
        GetNameAction getNameAction = new GetNameAction();

        //????????????
        private RecognizerListener recognizerListener = new RecognizerListener() {
            public void onBeginOfSpeech() {

            }
            public void onError(SpeechError error) {
                speak("????????????", false,ctx);
                showTip(error.getPlainDescription(true));
            }
            public void onEndOfSpeech() {
                showTip("???????");

            }
            public void onResult(RecognizerResult results, boolean isLast) {
                //Log.d("dd", results.getResultString());
                printResult(results,isLast);
                if (isLast) {
                    // TODO ??????
                }
            }
            public void onVolumeChanged(int volume,byte[] data) {
               showTip("????????�???????????"+volume);
                //info.makeText(getApplicationContext(), "??????????????????С??" + volume, 100).show();
            }
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

            }
        };
        /**
         * ?????????????
         */
        private InitListener mInitListener = new InitListener() {


            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                   showTip("???????????????"+code);
                }
            }
        };

        //?????????????????????�??
        private InitListener textUnderstanderListener = new InitListener() {
            public void onInit(int code) {
                Log.d(TAG, "textUnderstanderListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    //showTip("????????,??????"+code);
                    Log.d("dd","????????,??????"+code);
                }
            }
        };

        public void getJsonData() {
        //speak("here",false);
        try {
            JSONObject SAResultJson = new JSONObject(SAResult);

            operation=SAResultJson.optString("operation");
            service=SAResultJson.optString("service");
            semantic=SAResultJson.optJSONObject("semantic");
            answer=SAResultJson.optJSONObject("answer");
            data=SAResultJson.optJSONObject("data");

            if(data==null){
            }else result=data.optJSONArray("result");

            if(result==null){
            }else{
                //?????????????�????еò????κν???????
                airQuality=new String[10];
                weatherDate=new String[10];
                wind=new String[10];
                humidity=new String[10];
                windLevel=new String[10];
                weather=new String[10];
                tempRange=new String[10];
                for(int i=1;i<7;i++){
                    airQuality[i-1]=result.getJSONObject(i).optString("airQuality");
                    weatherDate[i-1]=result.getJSONObject(i).optString("date");
                    wind[i-1]=result.getJSONObject(i).optString("wind");
                    humidity[i-1]=result.getJSONObject(i).optString("humidity");
                    windLevel[i-1]=result.getJSONObject(i).optString("windLevel");
                    weather[i-1]=result.getJSONObject(i).optString("weather");
                    tempRange[i-1]=result.getJSONObject(i).optString("tempRange");
                    sourceName=result.getJSONObject(i).optString("sourceName");
                }

            }

            if(answer==null){
            }else text=answer.optString("text");

            if(semantic==null){
            }else slots=semantic.optJSONObject("slots");

            if(slots==null){
            }else{
                receiver=slots.optString("receiver");
                location=slots.optJSONObject("location");
                name = slots.optString("name");
                price= slots.optString("price");
                code = slots.optString("code");
                song = slots.optString("song");
                keywords=slots.optString("keywords");
                content=slots.optString("content");
                url=slots.optString("url");
                target=slots.optString("target");
                source=slots.optString("source");
            }

            if(location==null){
            }else{
                city=location.optString("city");
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            speak("????json?????????",false,ctx);
            e.printStackTrace();
        }
            SonarReaction();
    }

        public void SonarReaction(){
            SRResult=null;//???
            SAResult=null;
            //speak("service:"+service+" operation:"+operation,false);
            //speak("serviceFlag",serviceFlag);
            if(serviceFlag==false) {//????????????в???з?????ж?
                //speak("?ж????????",false);
                switch (service) {


                    case "telephone": {//1 ?�??????

                        switch (operation) {

                            case "CALL": {
                                CallAction callAction = new CallAction(name, code, ctx, this);
                                callAction.start();
                            }

                            case "VIEW": {    //???�??????
                                //?????????
                                //?????????δ??�????????�???????�??
                                break;
                            }

                            default:
                                break;

                        }

                        break;
                    }

                    case "message": {//2 ??????????

                        switch (operation) {

                            case "SEND": {//???????
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Name",name);
                                Intent intent = new Intent(ctx,MassageEditActivity.class);
                                intent.putExtras(bundle);
                                ctx.startActivity(intent);
                                break;
                            }

                            case "VIEW": {//????????????


                                break;
                            }


                            case "SENDCONTACTS": {//???????,???????????????????

                                break;
                            }
                            default:
                                break;
                        }

                        break;
                    }

                    case "app": {//3 ?????????

                        switch (operation) {

                            case "LAUNCH": {//?????

                                break;
                            }

                            case "QUERY": {//??????????????

                                break;
                            }

                            default:
                                break;

                        }
                        break;
                    }

                    case "website": {//4 ?????????

                        switch (operation) {

                            case "OPEN": {//????????


                                break;
                            }

                            default:
                                break;
                        }

                        break;
                    }

                    case "websearch": {//5 ??????????

                        switch (operation) {

                            case "QUERY": {//????


                                break;
                            }

                            default:
                                break;

                        }


                        break;
                    }

                    case "faq": {//6 ?????????????

                        switch (operation) {

                            case "ANSWER": {//???????


                                break;
                            }

                            default:
                                break;
                        }

                        break;
                    }

                    case "chat": {//7 ??????????

                        switch (operation) {

                            case "ANSWER": {//??????
                                Log.v("wifi", text);
                                String[] Function = getNameAction.getParagraph(SaveResult);
                                WifiManager wifiManager = (WifiManager) ctx
                                        .getSystemService(Context.WIFI_SERVICE);
                                for (int i = 0; i < Function.length; i++) {
                                    if ((i + 1) < Function.length) {
                                        String GetNews = Function[i] + Function[i + 1];
                                        switch (GetNews) {
                                            case "��":
                                                for (int n = 0; n < Function.length; n++) {
                                                    if ((n + 3) < Function.length) {
                                                        String keyword = Function[n] + Function[n + 1] + Function[n + 2] + Function[n + 3];
                                                        if (keyword.equals("wifi")) {
                                                            Log.v("wifiTest", "wifiIn");
                                                            wifiManager.setWifiEnabled(true);
                                                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                                            ctx.startActivity(intent);
                                                        }
                                                    }
                                                }
                                                break;
                                            case "�ر�":
                                                for (int n = 0; n < Function.length; n++) {
                                                    if ((n + 3) < Function.length) {
                                                        String keyword = Function[n] + Function[n + 1] + Function[n + 2] + Function[n + 3];
                                                        if (keyword.equals("wifi")) {
                                                            Log.v("keyword", "in");
                                                            wifiManager.setWifiEnabled(false);
                                                        }
                                                    }
                                                }
                                                break;

                                        }
                                    }
                                }

                                        break;
                                    }

                                    default:
                                        break;
                                }

                                break;
                            }

                            case "openQA": {//8 ?????????????

                                switch (operation) {

                                    case "ANSWER": {//???????

                                        OpenQA openQA = new OpenQA(text,ctx,this);
                                        openQA.start();

                                        break;
                                    }

                                    default:
                                        break;
                                }

                                break;
                            }

                            case "baike": {//9 ???????????

                                switch (operation) {

                                    case "ANSWER": {//???


                                        break;
                                    }

                                    default:
                                        break;
                                }

                                break;
                            }

                            case "schedule": {//10 ?????????

                                switch (operation) {

                                    case "CREATE": {//???????/????(????????????????)


                                        break;
                                    }

                                    case "VIEW": {//??????/????(δ???)


                                        break;
                                    }


                                    default:
                                        break;
                                }

                                break;
                            }

                            case "weather": {//11 ??????????

                                switch (operation) {

                                    case "QUERY": {//???????


                                        break;
                                    }

                                    default:
                                        break;

                                }

                                break;
                            }

                            case "translation": {//12 ??????????

                                switch (operation) {

                                    case "TRANSLATION": {//????


                                        break;
                                    }

                                    default:
                                        break;

                                }

                                break;
                            }

                            default: {
                                String VoiceTag = "null";
                                String paragraph = null;
                                try {
                                    JSONObject JSONNews = new JSONObject(SaveResult);
                                    String FunctionText = JSONNews.getString("text");
                                    String[] Function = getNameAction.getParagraph(FunctionText);
                                    WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                                    Activity activityContext = (Activity) ctx;
                                        for (int i = 0; i < Function.length; i++) {
                                            Log.v("i", String.valueOf(i));
                                            if ((i + 1) < Function.length) {
                                                String GetNews = Function[i] + Function[i + 1];
                                                switch (GetNews) {
                                                    case "����":
                                                        for (int n = 0; n < Function.length; n++) {
                                                            if ((n + 1) < Function.length) {
                                                                String keyword = Function[n] + Function[n + 1];
                                                                VoiceTag = "N";
                                                                switch (keyword) {
                                                                    case "���":
                                                                        VoiceTag = "N1";
                                                                        Jump("http://apis.baidu.com/txapi/social/social");
                                                                        break;
                                                                    case "����":
                                                                        VoiceTag = "N2";
                                                                        Jump("http://apis.baidu.com/txapi/world/world");
                                                                        break;
                                                                    case "����":
                                                                        VoiceTag = "N3";
                                                                        Jump("http://apis.baidu.com/txapi/tiyu/tiyu");
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                            }
                                                        }
                                                        break;

                                                    case "����":
                                                        for (int n = 0; n < Function.length; n++) {
                                                            if ((n + 1) < Function.length) {
                                                                BrightnessAction brightnessAction = new BrightnessAction(ctx);
                                                                String keyword = Function[n] + Function[n + 1];
                                                                int BrightnessNow = brightnessAction.screenBrightness_check();
                                                                switch (keyword) {
                                                                    case "����":
                                                                        VoiceTag = "B1";
                                                                        int increase = BrightnessNow + 52;
                                                                        brightnessAction.setScreenBritness(increase);
                                                                        break;
                                                                    case "����":
                                                                        VoiceTag = "B2";
                                                                        int lower = BrightnessNow - 52;
                                                                        brightnessAction.setScreenBritness(lower);
                                                                        break;
                                                                }
                                                            }
                                                        }
                                                        break;

                                                    case "����":
                                                        for (int n = 0; n < Function.length; n++) {
                                                            if ((n + 1) < Function.length) {
                                                                String keyword = Function[n] + Function[n + 1];
                                                                switch (keyword) {
                                                                    case "ý��":
                                                                        for (int m = 0; m < Function.length; m++) {

                                                                            if ((m + 1) < Function.length) {
                                                                                String keywordType = Function[m] + Function[m + 1];
                                                                                if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S1";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_MUSIC,
                                                                                            AudioManager.ADJUST_RAISE,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                } else if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S2";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_MUSIC,
                                                                                            AudioManager.ADJUST_LOWER,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                }
                                                                            }
                                                                        }
                                                                        break;
                                                                    case "��ʾ":
                                                                        for (int m = 0; m < Function.length; m++) {
                                                                            if ((m + 1) < Function.length) {
                                                                                String keywordType = Function[m] + Function[m + 1];
                                                                                if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S3";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_ALARM,
                                                                                            AudioManager.ADJUST_RAISE,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                } else if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S4";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_ALARM,
                                                                                            AudioManager.ADJUST_LOWER,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                }
                                                                            }
                                                                        }

                                                                        break;
                                                                    case "����":
                                                                        for (int m = 0; m < Function.length; m++) {
                                                                            if ((m + 1) < Function.length) {
                                                                                String keywordType = Function[m] + Function[m + 1];
                                                                                if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S5";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_RING,
                                                                                            AudioManager.ADJUST_RAISE,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                } else if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S6";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_RING,
                                                                                            AudioManager.ADJUST_LOWER,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                }
                                                                            }
                                                                        }
                                                                        break;
                                                                    case "ͨ��":
                                                                        for (int m = 0; m < Function.length; m++) {
                                                                            if ((m + 1) < Function.length) {
                                                                                String keywordType = Function[m] + Function[m + 1];
                                                                                if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S7";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_VOICE_CALL,
                                                                                            AudioManager.ADJUST_RAISE,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                } else if (keywordType.equals("����")) {
                                                                                    VoiceTag = "S8";
                                                                                    ChangeVolume(
                                                                                            AudioManager.STREAM_VOICE_CALL,
                                                                                            AudioManager.ADJUST_LOWER,
                                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                                }
                                                                            }
                                                                        }
                                                                        break;
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case "����":
                                                        if (activityContext instanceof MassageEditActivity) {
                                                            VoiceTag = "M1";
                                                        //�ڱ༭����ҳ����
                                                            for (int n = i + 2; n < Function.length; n++) {
                                                                if (paragraph == null) {
                                                                    paragraph = Function[n];
                                                                }else if(Function[n].equals("��")){
                                                                    continue;
                                                                }
                                                                else {
                                                                    paragraph = paragraph + Function[n];
                                                                }
                                                                Log.v("name",paragraph);
                                                                Log.v("n",String.valueOf(n));
                                                            }
                                                            if(paragraph != null) {
                                                                String MassageName = getNameAction.GetChineseName(paragraph.trim());
                                                                Bundle bundle = new Bundle();
                                                                bundle.putSerializable("Name",MassageName);
                                                                Intent intent = new Intent(ctx,MassageEditActivity.class);
                                                                intent.putExtras(bundle);
                                                                ctx.startActivity(intent);
                                                                ((Activity) ctx).finish();

                                                            }
                                                        }
                                                        break;
                                                    case "����":
                                                        if (activityContext instanceof MassageEditActivity) {
                                                            VoiceTag = "M2";
                                                            //�ڱ༭����ҳ����
                                                            for (int n = i + 3; n < Function.length; n++) {
                                                                if (paragraph == null) {
                                                                    paragraph = Function[n];
                                                                } else {
                                                                    paragraph = paragraph + Function[n];
                                                                }
                                                            }
                                                            if(paragraph != null) {
                                                                Bundle bundle = new Bundle();
                                                                bundle.putSerializable("Content",paragraph);
                                                                Intent intent = new Intent(ctx,MassageEditActivity.class);
                                                                intent.putExtras(bundle);
                                                                ctx.startActivity(intent);
                                                                ((Activity) ctx).finish();

                                                            }

                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            } else {
                                                Log.v("ininini", VoiceTag);
                                                Log.v("i + 1", String.valueOf(i + 1));
                                                Log.v("Function.length", String.valueOf(Function.length));
                                                if (VoiceTag.equals("null") && (i + 1) == Function.length) {
                                                    Log.v("ininini", "in");
                                                    speak("��֪����Ҫ������������һ��ʱ���Ҿͻᶮ�ˡ�", false, ctx);
                                                    VoiceTag = "";

                                                }
                                            }
                                        }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }//??????????????
                }






    public void ChangeVolume(int streamType,int direction,int flages){
        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.adjustStreamVolume(streamType, direction, flages);
    }



    public  void Jump(String Url){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Url",Url);
        Intent intent = new Intent(ctx ,NewsActivity.class);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }



        public void initIflytek(){//??????????
            Activity activity = (Activity)ctx;
            //???Siri????


            activity.findViewById(R.id.voice_input).setOnClickListener(this);
            SpeechUtility.createUtility(ctx, SpeechConstant.APPID + "=564f2dfe");

    }

    public void initUI(){//?????UI?????
        SRResult="";
        list = new ArrayList<SiriListItem>();
        if(ctx instanceof MainActivity) {
            list.add(new SiriListItem("text", true));
        }
        mAdapter = new ChatMsgViewAdapter(ctx, list);
        Activity activity = (Activity)ctx;
        mListView = (ListView) activity.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        activity.registerForContextMenu(mListView);
    }

    public void speechRecognition(){//?????
        //1.????SpeechRecognizer???????????? ??????д???InitListener
        mIat= SpeechRecognizer.createRecognizer(ctx, mInitListener);
        // ???????дDialog???????????UI??д????????�??SpeechRecognizer
        mIatDialog = new RecognizerDialog(ctx, mInitListener);
        //????????????
        mTextUnderstander = TextUnderstander.createTextUnderstander(ctx, textUnderstanderListener);

        // ???????????
        mTts = SpeechSynthesizer.createSynthesizer(ctx, mTtsInitListener);
    }

    public void startSpeenchRecognition(){//???????
        player = MediaPlayer.create(ctx, R.raw.begin);
        player.start();
        // ?????д?????
        mIatDialog.setListener(recognizerDialogListener);
        //mIatDialog.show();
        ret = mIat.startListening(recognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "" + ret);
            showTip("??д??????????" + ret);
            //info.makeText(getApplicationContext(), "??д???,??????" + ret, 100).show();
        }

    }

    //??????????????
    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results,isLast);//????????
        }

        /**
         * ?????????.
         */
        public void onError(SpeechError error) {
            speak(error.getPlainDescription(true),true,ctx);
            info.makeText(ctx.getApplicationContext(), "error.getPlainDescription(true)", 1000).show();
            //showTip(error.getPlainDescription(true));
        }

    };



    //??????????
    private void startAnalysis(){

        mTextUnderstander.setParameter(SpeechConstant.DOMAIN,  "iat");
        if(mTextUnderstander.isUnderstanding()){
            mTextUnderstander.cancel();
            //showTip("???");
            Log.d("dd","???");
        }else {
            //SRResult="???????????";
            ret = mTextUnderstander.understandText(SRResult, textListener);
            if(ret != 0)
            {
                //showTip("??????????,??????:"+ ret);
                Log.d("dd","??????????,??????:"+ ret);
            }
        }
    }
    //?????
    private TextUnderstanderListener textListener = new TextUnderstanderListener() {

        public void onResult(final UnderstanderResult result) {
            Activity activity = (Activity)ctx;
            activity.runOnUiThread(new Runnable() {

                public void run() {
                    if (null != result) {
                        // ???
                        //Log.d(TAG, "understander result??" + result.getResultString());
                        String text = result.getResultString();
                        SAResult = text;
                        SaveResult = SAResult;
                        Log.d("dd", "SAResult:" + SAResult);

                        if (TextUtils.isEmpty(text)) {
                            //Log.d("dd", "understander result:null");
                            //showTip("??????????");
                        }
                        //mainActivity.speak();
                        //speak(SAResult,false);
                        getJsonData();
                        //finish();
                    }
                }



					/*private void dialogueManagement(int mainServiceID,int branchServiceID) {//?????????
						// TODO Auto-generated method stub
						if(mainServiceID==1){
							if(branchServiceID==1){//???????�??????????????�????,????????С???????????????????????Ρ???β?????
								//???????????????????????

							}
							if(branchServiceID==2){//????????�??????

							}

						}
						if(mainServiceID==2){//??????????????????????�????????????

						}
					}*/
            });
        }

        public void onError(SpeechError error) {
            //showTip("onError Code??"	+ error.getErrorCode());
            Log.d("dd","onError Code??"	+ error.getErrorCode());
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data){//??дonActivityResult
        if(requestCode == 0){
            //System.out.println("REQUESTCODE equal");
            if(resultCode == 0){
                //    System.out.println("RESULTCODE equal");
                SAResult = data.getStringExtra("SRResult");
            }
        }
    }



    private void printResult(RecognizerResult results,boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());

        //Log.d("dd","text:"+text);
        String sn = null;
        // ???json????е?sn???
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            Log.d("dd","json:"+results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        SRResult=resultBuffer.toString();
        Activity activityContext = (Activity)ctx;
        if(isLast==true){
            speak(SRResult, true,ctx);
            //????????

            startAnalysis();
		/*startSemanticAnalysis();*/
        }
    }

    int ret = 0; // ??????÷????

    @SuppressWarnings("static-access")
    @Override
    public void onClick(View view) {//?????????


        startSpeenchRecognition();
    }

    public void setParam(){
        // ??????
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // ??????д????
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // ???÷???????
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // ????????
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // ????????
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // ????????????
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }
        // ????????????
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
        // ????????????
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        // ????????
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        // ???????????·??
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
                + "/iflytek/wavaudio.pcm");
        // ??????д????????????????1????????д????ж?????????????????????д??????????????
        // ????ò???????????????д??Ч
        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }


    /**
     * ?????????
     */
    private InitListener mTtsInitListener = new InitListener() {

        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
               showTip("???????????????"+code);
            } else {
                // ???????????????????startSpeaking????
                // ????е????????onCreate?????д?????????????????????startSpeaking???к???
                // ????????????onCreate?е?startSpeaking????????????
            }
        }
    };


    private void setParamTTS(){
        // ??????
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //???ú??
        if(mEngineTypeTTS.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //???÷?????
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);
            //????????
            //mTts.setParameter(SpeechConstant.SPEED,mSharedPreferencesTTS.getString("speed_preference", "50"));
            //????????
            //mTts.setParameter(SpeechConstant.PITCH,mSharedPreferencesTTS.getString("pitch_preference", "50"));
            //????????
            //mTts.setParameter(SpeechConstant.VOLUME,mSharedPreferencesTTS.getString("volume_preference", "50"));
            //???ò??????????????
            //mTts.setParameter(SpeechConstant.STREAM_TYPE,mSharedPreferencesTTS.getString("stream_preference", "3"));
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //???÷????? voicer?????????????+??????????????
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
    }

    /**
     * ?????????
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        public void onSpeakBegin() {
        }


        public void onSpeakPaused() {
            showTip("???????");
        }


        public void onSpeakResumed() {
            showTip("?????");
        }


        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // ?????
            //mPercentForBuffering = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //	mPercentForBuffering, mPercentForPlaying));
        }


        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // ??????
            //mPercentForPlaying = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //	mPercentForBuffering, mPercentForPlaying));
        }


        public void onCompleted(SpeechError error) {
            if (error == null) {
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }


        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private void textToSpeach(String text){//???????

        // ???ò???
        setParamTTS();
        int code = mTts.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
           showTip("?????????????????"+code);
        }
    }


    //from SiriCN
    public void speak(String msg, boolean isSiri,Context context) {
        Activity activityContext = (Activity)context;
        if(!(activityContext instanceof PhoneCallActivity) && !(activityContext instanceof MassageEditActivity)) {
            Log.v("INININININI","AIAIAIA");
            addToList(msg, isSiri);//????????б?
        }
        if(isSiri==false){
            textToSpeach(msg);
        }
    }

    public void beginSpeak(String msg, boolean isSiri){
        if(isSiri==false){
            textToSpeach(msg);
        }
    }

    private void addToList(String msg, boolean isSiri) {
        //
        Log.v("msg",String.valueOf(isSiri));
        list.add(new SiriListItem(msg, isSiri));
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size() - 1);
    }

    public class SiriListItem {
        String message;
        boolean isSiri;

        public SiriListItem(String msg, boolean siri) {
            message = msg;
            isSiri = siri;
        }
    }

    private void showTip(final String str) {
        info.setText(str);
        info.show();
    }



}
