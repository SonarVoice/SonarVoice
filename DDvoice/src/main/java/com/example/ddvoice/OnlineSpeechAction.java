package com.example.ddvoice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

        private TextUnderstander mTextUnderstander;// �����������ı������壩��
        private ListView mListView;
        private ArrayList<SiriListItem> list;
        ChatMsgViewAdapter mAdapter;
        private MediaPlayer player;
        public static  String SRResult="";	//ʶ����
        private static String SAResult="";//����ʶ����
        private static String TAG = MainActivity.class.getSimpleName();
        //Toast��ʾ��Ϣ
        private Toast info;
        //�ı�����
        private TextView textView;
        //����ʶ��
        private SpeechRecognizer mIat;
        // ������дUI
        private RecognizerDialog mIatDialog;
        // ��HashMap�洢��д���
        private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
        // ��������
        private String mEngineType = SpeechConstant.TYPE_CLOUD;
        private String mEngineTypeTTS = SpeechConstant.TYPE_CLOUD;
        private SharedPreferences mSharedPreferences;
        private SharedPreferences mSharedPreferencesTTS;
        MainActivity mActivity = new MainActivity();
        //����ʶ�������
        private RecognizerListener recognizerListener = new RecognizerListener() {
            public void onBeginOfSpeech() {

            }
            public void onError(SpeechError error) {
                speak("û��������˵����", false);
                showTip(error.getPlainDescription(true));
            }
            public void onEndOfSpeech() {
                showTip("����˵��");

            }
            public void onResult(RecognizerResult results, boolean isLast) {
                //Log.d("dd", results.getResultString());
                printResult(results,isLast);
                if (isLast) {
                    // TODO ���Ľ��
                }
            }
            public void onVolumeChanged(int volume,byte[] data) {
               showTip("�������˷署��������Ϊ��"+volume);
                //info.makeText(getApplicationContext(), "��ǰ����˵����������С��" + volume, 100).show();
            }
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

            }
        };
        /**
         * ��ʼ����������
         */
        private InitListener mInitListener = new InitListener() {


            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                   showTip("��ʼ��ʧ�ܣ�������"+code);
                }
            }
        };

        //��ʼ�����������ı������壩��
        private InitListener textUnderstanderListener = new InitListener() {
            public void onInit(int code) {
                Log.d(TAG, "textUnderstanderListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    //showTip("��ʼ��ʧ��,�����룺"+code);
                    Log.d("dd","��ʼ��ʧ��,�����룺"+code);
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
                //����Ҫ��ʼ�����鲻Ȼ���еò����κν��������
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
            speak("����json����������",false);
            e.printStackTrace();
        }
            SonarReaction();
    }

        public void SonarReaction(){
            SRResult=null;//�ÿ�
            SAResult=null;
            //speak("service:"+service+" operation:"+operation,false);
            //speak("serviceFlag",serviceFlag);
            if(serviceFlag==false){//�������һ������вŽ��з�����ж�
                //speak("�жϷ�������",false);
                switch(service){


                    case "telephone":{//1 �绰��ط���

                        switch(operation){

                            case "CALL":{
                                CallAction callAction=new CallAction(name,code,ctx,this);
                                callAction.start();
                            }

                            case "VIEW":{	//�鿴�绰�����¼
                                //��Ҫ������
                                //��ѡ������δ�ӵ绰�����Ѳ��绰�����ѽӵ绰��
                             break;
                            }

                            default :break;

                        }

                        break;
                    }

                    case "message":{//2 ������ط���

                        switch(operation){

                            case "SEND":{//���Ͷ���
                                break;
                            }

                            case "VIEW":{//�鿴���Ͷ���ҳ��


                                break;
                            }



                            case "SENDCONTACTS":{//������Ƭ,Ŀǰֻ��ʶ�����ַ�������

                                break;
                            }
                            default :break;
                        }

                        break;
                    }

                    case "app":{//3 Ӧ����ط���

                        switch(operation){

                            case "LAUNCH":{//��Ӧ��

                                break;
                            }

                            case "QUERY":{//Ӧ����������Ӧ��

                                break;
                            }

                            default:break;

                        }
                        break;
                    }

                    case "website":{//4 ��վ��ط���

                        switch(operation){

                            case "OPEN":{//��ָ����ַ


                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "websearch":{//5 ������ط���

                        switch(operation){

                            case "QUERY":{//����


                                break;
                            }

                            default:break;

                        }


                        break;
                    }

                    case "faq":{//6 �����ʴ���ط���

                        switch(operation){

                            case "ANSWER":{//�����ʴ�



                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "chat":{//7 ������ط���

                        switch(operation){

                            case "ANSWER":{//����ģʽ



                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "openQA":{//8 �����ʴ���ط���

                        switch(operation){

                            case "ANSWER":{//�����ʴ�

                                OpenQA openQA = new OpenQA(text,this);
                                openQA.start();

                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "baike":{//9 �ٿ�֪ʶ��ط���

                        switch(operation){

                            case "ANSWER":{//�ٿ�



                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "schedule":{//10 �ճ���ط���

                        switch(operation){

                            case "CREATE":{//�����ճ�/����(ֱ����ת��Ӧ���ý���)



                                break;
                            }

                            case "VIEW":{//�鿴����/����(δʵ��)


                                break;
                            }


                            default:break;
                        }

                        break;
                    }

                    case "weather":{//11 ������ط���

                        switch(operation){

                            case "QUERY":{//��ѯ����



                                break;
                            }

                            default:break;

                        }

                        break;
                    }

                    case "translation":{//12 ������ط���

                        switch(operation){

                            case "TRANSLATION":{//����



                                break;
                            }

                            default:break;

                        }

                        break;
                    }

                    default:{
                        speak("��֪����Ҫ������������һ��ʱ���Ҿͻᶮ�ˡ�",false);
                        break;
                    }
                }
            }//����ĳ����������
        }
        public void initIflytek(){//��ʼѶ������
            Activity activity = (Activity)ctx;
            //�ҵ�Siri����


            activity.findViewById(R.id.voice_input).setOnClickListener(this);
            SpeechUtility.createUtility(ctx, SpeechConstant.APPID + "=564f2dfe");

    }

    public void initUI(){//��ʼ��UI�Ͳ���
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

    public void speechRecognition(){//��ʼ��
        //1.����SpeechRecognizer���󣬵ڶ��������� ������дʱ��InitListener
        mIat= SpeechRecognizer.createRecognizer(ctx, mInitListener);
        // ��ʼ����дDialog�����ֻʹ����UI��д���ܣ����贴��SpeechRecognizer
        mIatDialog = new RecognizerDialog(ctx, mInitListener);
        //���������ʼ��
        mTextUnderstander = TextUnderstander.createTextUnderstander(ctx, textUnderstanderListener);

        // ��ʼ���ϳɶ���
        mTts = SpeechSynthesizer.createSynthesizer(ctx, mTtsInitListener);
    }

    public void startSpeenchRecognition(){//����ʶ��
        player = MediaPlayer.create(ctx, R.raw.begin);
        player.start();
        // ��ʾ��д�Ի���
        mIatDialog.setListener(recognizerDialogListener);
        //mIatDialog.show();
        ret = mIat.startListening(recognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "" + ret);
            showTip("��дʧ�ܣ������룺" + ret);
            //info.makeText(getApplicationContext(), "��дʧ��,�����룺" + ret, 100).show();
        }

    }

    //����ʶ����������
    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results,isLast);//�õ�ʶ����
        }

        /**
         * ʶ��ص�����.
         */
        public void onError(SpeechError error) {
            speak(error.getPlainDescription(true),true);
            info.makeText(ctx.getApplicationContext(), "error.getPlainDescription(true)", 1000).show();
            //showTip(error.getPlainDescription(true));
        }

    };



    //��ʼ�������
    private void startAnalysis(){

        mTextUnderstander.setParameter(SpeechConstant.DOMAIN,  "iat");
        if(mTextUnderstander.isUnderstanding()){
            mTextUnderstander.cancel();
            //showTip("ȡ��");
            Log.d("dd","ȡ��");
        }else {
            //SRResult="�����������";
            ret = mTextUnderstander.understandText(SRResult, textListener);
            if(ret != 0)
            {
                //showTip("�������ʧ��,������:"+ ret);
                Log.d("dd","�������ʧ��,������:"+ ret);
            }
        }
    }
    //ʶ��ص�
    private TextUnderstanderListener textListener = new TextUnderstanderListener() {

        public void onResult(final UnderstanderResult result) {
            Activity activity = (Activity)ctx;
            activity.runOnUiThread(new Runnable() {

                public void run() {
                    if (null != result) {
                        // ��ʾ
                        //Log.d(TAG, "understander result��" + result.getResultString());
                        String text = result.getResultString();
                        SAResult = text;
                        Log.d("dd", "SAResult:" + SAResult);

                        if (TextUtils.isEmpty(text)) {
                            //Log.d("dd", "understander result:null");
                            //showTip("ʶ��������ȷ��");
                        }
                        //mainActivity.speak();
                        //speak(SAResult,false);
                        getJsonData();
                        //finish();
                    }
                }



					/*private void dialogueManagement(int mainServiceID,int branchServiceID) {//�Ի�������
						// TODO Auto-generated method stub
						if(mainServiceID==1){
							if(branchServiceID==1){//�����˴�绰���񣬱�Ҫ�����ǡ��绰���롿,��ѡ�����С���������ء�����Ӫ�̡����ŶΡ���β�š���
								//���ɶ����ѡ����ȷ����Ҫ����

							}
							if(branchServiceID==2){//�����˲鿴�绰���ż�¼

							}

						}
						if(mainServiceID==2){//�����˷����ŷ��񣬱�Ҫ�����ǵ绰����Ͷ�������

						}
					}*/
            });
        }

        public void onError(SpeechError error) {
            //showTip("onError Code��"	+ error.getErrorCode());
            Log.d("dd","onError Code��"	+ error.getErrorCode());
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data){//��дonActivityResult
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
        // ��ȡjson����е�sn�ֶ�
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
        if(isLast==true){
            speak(SRResult, true);
            //�������ݿ�

            startAnalysis();
		/*startSemanticAnalysis();*/
        }
    }

    int ret = 0; // �������÷���ֵ

    @SuppressWarnings("static-access")
    @Override
    public void onClick(View view) {//����ʶ�����


        startSpeenchRecognition();
    }

    public void setParam(){
        // ��ղ���
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // ������д����
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // ���÷��ؽ����ʽ
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // ��������
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // ��������
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // ������������
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }
        // ��������ǰ�˵�
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
        // ����������˵�
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        // ���ñ�����
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        // ������Ƶ����·��
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
                + "/iflytek/wavaudio.pcm");
        // ������д����Ƿ�����̬������Ϊ��1��������д�����ж�̬�����ط��ؽ��������ֻ����д����֮�󷵻����ս��
        // ע���ò�����ʱֻ��������д��Ч
        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }


    /**
     * ��ʼ��������
     */
    private InitListener mTtsInitListener = new InitListener() {

        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
               showTip("��ʼ��ʧ�ܣ������룺"+code);
            } else {
                // ��ʼ���ɹ���֮����Ե���startSpeaking����
                // ע���еĿ�������onCreate�����д�����ϳɶ���֮�����Ͼ͵���startSpeaking���кϳɣ�
                // ��ȷ�������ǽ�onCreate�е�startSpeaking������������
            }
        }
    };


    private void setParamTTS(){
        // ��ղ���
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //���úϳ�
        if(mEngineTypeTTS.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //���÷�����
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);
            //��������
            //mTts.setParameter(SpeechConstant.SPEED,mSharedPreferencesTTS.getString("speed_preference", "50"));
            //��������
            //mTts.setParameter(SpeechConstant.PITCH,mSharedPreferencesTTS.getString("pitch_preference", "50"));
            //��������
            //mTts.setParameter(SpeechConstant.VOLUME,mSharedPreferencesTTS.getString("volume_preference", "50"));
            //���ò�������Ƶ������
            //mTts.setParameter(SpeechConstant.STREAM_TYPE,mSharedPreferencesTTS.getString("stream_preference", "3"));
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //���÷����� voicerΪ��Ĭ��ͨ������+����ָ�������ˡ�
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
    }

    /**
     * �ϳɻص�������
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        public void onSpeakBegin() {
        }


        public void onSpeakPaused() {
            showTip("��ͣ����");
        }


        public void onSpeakResumed() {
            showTip("��������");
        }


        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // �ϳɽ���
            //mPercentForBuffering = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //	mPercentForBuffering, mPercentForPlaying));
        }


        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // ���Ž���
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

    private void textToSpeach(String text){//�����ϳ�

        // ���ò���
        setParamTTS();
        int code = mTts.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
           showTip("�����ϳ�ʧ�ܣ������룺"+code);
        }
    }


    //from SiriCN
    public void speak(String msg, boolean isSiri) {
        addToList(msg, isSiri);//��ӵ��Ի��б�
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
