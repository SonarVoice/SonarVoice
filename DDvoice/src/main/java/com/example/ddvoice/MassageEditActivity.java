package com.example.ddvoice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.textservice.SpellCheckerService;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.client.HttpClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by owen_ on 2016-04-29.
 */
public class MassageEditActivity extends Activity {

    private DBManager dbManager;
    private Bundle bundle;
    private EditText contactName,phoneNumber,content;
    public List<String> phoneName = new ArrayList<String>();
    public List<String> phoneId = new ArrayList<String>();
    private String name = null;
    private String Massage = null;
    private String number = null;
    private Toast info;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);;
        setContentView(R.layout.massage_edit_activity);
        sharedpreferences = getSharedPreferences("data",MassageEditActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();



        info=Toast.makeText(this, "", Toast.LENGTH_SHORT);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.v("intent",bundle.toString());
        if(bundle.getSerializable("Name") != null)  {
             name = bundle.getSerializable("Name").toString();
             editor.putString("Name", name);
            editor.commit();
            ;
        }
        if(bundle.getSerializable("Content") != null)  {
            Massage = bundle.getSerializable("Content").toString();
            editor.putString("Content",Massage);
            editor.commit();
            Log.v("in", sharedpreferences.getString("Name", ""));
            Log.v("massage",Massage);
            if(sharedpreferences.getString("Name","").length() > 0){
                name = sharedpreferences.getString("Name","");
            }
        }

      //  Log.v("name", name);
        final OnlineSpeechAction vbutton = new OnlineSpeechAction(this);
        vbutton.initIflytek();
        vbutton.speechRecognition();

        dbManager = new DBManager(this);

        contactName = (EditText)findViewById(R.id.massageContactNameEditText);
        phoneNumber = (EditText)findViewById(R.id.massagePhoneNumberEditText);
        content = (EditText)findViewById(R.id.massageContentEditText);

        if(Massage == null && name != null){
            Log.v("1","1");
            contactName.setText(name);
            vbutton.speak("�����" + name + "˵Щʲô", false, this);
        }else if(Massage != null && name == null){
            Log.v("2","2");
            content.setText(Massage);
            vbutton.speak("���뷢���Ÿ�˭", false, this);
        }else if(Massage != null && name != null){
            Log.v("3", "3");
            if(contactName != null || content != null){
                contactName.setText(name);
                content.setText(Massage);
            }
        }else {
            Log.v("4","4");
            vbutton.speak("���뷢���Ÿ�˭", false, this);
        }

        Button returnButton = (Button)findViewById(R.id.massageEditReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MassageEditActivity.this, MassegeActivity.class));;
                MassageEditActivity.this.finish();
            }
        });

        Button btnSendMassage = (Button)findViewById(R.id.massageEditDeleteButton);
        btnSendMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneName = contactName.getText().toString();
                String Num = phoneNumber.getText().toString();
                Massage = content.getText().toString();
                if(Num != null){
                    //ֱ�Ӷ�ȡ���ݷ���Massage
                    if(Massage != null) {
                        SmsManager manager = SmsManager.getDefault();
                        ArrayList<String> list = manager.divideMessage(Massage);  //��Ϊһ���������������ƣ����Ҫ�������Ų��
                        for (String text : list) {
                            manager.sendTextMessage(Num, null, text, null, null);
                        }
                        showTip("���ͳɹ���");
                    }else{
                        vbutton.speak("��������ŵ�����", false, MassageEditActivity.this);
                    }

                }else if(PhoneName != null){
                    if(Massage != null) {
                        SendMassage(PhoneName, Massage, vbutton);
                    }else{
                        vbutton.speak("��������ŵ�����", false, MassageEditActivity.this);
                    }
                }else {
                    vbutton.speak("�����뷢�Ͷ��ŵ���ϵ�˻����", false, MassageEditActivity.this);
                }
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






    public void SendMassage(String PhoneName ,String MassageContent,OnlineSpeechAction vbutton){
        String GetPinYinName = getPinYin(PhoneName);
        char[] CharPinYin = PhoneName.toCharArray();
        FuzzySearch(String.valueOf(CharPinYin[0]), this);
        if (phoneName.size() > 0) {
            for (int i = 0; i < phoneName.size(); i++) {
                String GetFuzzyPinYin = getPinYin(phoneName.get(i));
                if (GetPinYinName.equals(GetFuzzyPinYin) && phoneId.size() > 0) {
                    Long PhoneNum = GainPhoneNum(phoneId.get(i), this);
                    number = String.valueOf(PhoneNum);
                    if (vbutton != null) {
                        Log.v("na",PhoneName);
                        if (number == null) {
                            vbutton.speak("û���ҵ�" + PhoneName + "�����ϵ��",false,this);
                        } else {
                            //打电�?
                            phoneNumber.setText(number);
                        }
                    }else{
                        if (number == null) {

                        } else {
                            //打电话直接注入号�?
                        }
                    }
                }
            }
        }
    }

    //转化成拼�?
    public  String getPinYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] input = inputString.trim().toCharArray();
        StringBuffer output = new StringBuffer("");

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                            input[i], format);
                    output.append(temp[0]);
                    output.append(" ");
                } else
                    output.append(Character.toString(input[i]));
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public void FuzzySearch(String key,Context context) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.PhoneLookup.DISPLAY_NAME + " LIKE" + " '%"
                        + key + "%'", null, null); // Sort order.
        String[] SearchName = new String[cursor.getCount()];
        if (cursor == null) {
//            vbutton.speak("û������������ϵ��", false,MassageEditActivity.this);
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String uname = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            phoneName.add(uname);
            String number = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            phoneId.add(number);
        }
        cursor.close();
    }


    public Long GainPhoneNum(String phoneNameId, Context context) {
        int i;
        Long phone = null;
        String phoneNumber = null;
		/*
		 * 查找该联系人的phone信息
		 */
        Cursor phones = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                        + phoneNameId, null, null);
        int phoneIndex = 0;
        if (phones.getCount() > 0) {
            phoneIndex = phones
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        }
        while (phones.moveToNext()) {
            phoneNumber = phones.getString(phoneIndex);
            Log.i("null", phoneNumber);
        }
        phone = PhoneToNum(phoneNumber);
        return phone;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public Long PhoneToNum(String Num) {
        String No = null;
        char[] temp = new char[Num.length()];
        temp = Num.toCharArray();
        char[] copyPhone = Arrays.copyOf(temp, temp.length);
        String[] copy = new String[copyPhone.length];
        for (int n = 0; n < copyPhone.length; n++) {
            copy[n] = String.valueOf(copyPhone[n]);

            if (!copy[n].endsWith(" ")) {
                if (!copy[n].equals("-")) {

                    if (No == null) {
                        No = copy[n];
                    } else {
                        No = No + copy[n];
                    }
                }
            }
        }
        return Long.parseLong(No.trim());
    }
    private void showTip(final String str) {
        info.setText(str);
        info.show();
    }

}
