package com.example.ddvoice.util;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class FucUtil {

	public static String readFile(Context mContext,String file,String code)
	{
		int len = 0;
		byte []buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);
			len  = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			
			result = new String(buf,code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

	public static String checkLocalResource(){
		Log.v(">??????","????:;;;");
		Log.v(">;.;.;.;.;", SpeechUtility.getUtility().toString());
			String resource = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_ASR);


		try {
			JSONObject result = new JSONObject(resource);
			int ret = result.getInt(SpeechUtility.TAG_RESOURCE_RET);
			switch (ret) {
			case ErrorCode.SUCCESS:
				JSONArray asrArray = result.getJSONObject("result").optJSONArray("asr");
				if (asrArray != null) {
					int i = 0;
					for (; i < asrArray.length(); i++) {
						if("iat".equals(asrArray.getJSONObject(i).get(SpeechConstant.DOMAIN))){
							break;
						}
					}
					if (i >= asrArray.length()) {
						
						SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
						return "û����д��Դ����ת����Դ����ҳ��";
					}
				}else {
					SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
					return "û����д��Դ����ת����Դ����ҳ��";
				}
				break;
			case ErrorCode.ERROR_VERSION_LOWER:
				return "��ǰ汾���ͣ�����º�ʹ�ñ��ع���";
			case ErrorCode.ERROR_INVALID_RESULT:
				SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
				return "��ȡ���������ת����Դ����ҳ��";
			case ErrorCode.ERROR_SYSTEM_PREINSTALL:
			default:
				break;
			}
		} catch (Exception e) {
			SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
			return "��ȡ���������ת����Դ����ҳ��";
		}
		return "";
	}
	

	public static byte[] readAudioFile(Context context, String filename) {
		try {
			InputStream ins = context.getAssets().open(filename);
			byte[] data = new byte[ins.available()];
			
			ins.read(data);
			ins.close();
			
			return data;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
