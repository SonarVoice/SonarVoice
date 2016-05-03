package com.example.ddvoice.util;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import com.iflytek.cloud.SpeechUtility;


/**
 * å¼¹å‡ºæç¤ºæ¡†ï¼Œä¸‹è½½æœåŠ¡ç»„ä»¶
 */
public class ApkInstaller {
	private Activity mActivity ;
	
	public ApkInstaller(Activity activity) {
		mActivity = activity;
	}

	public void install(){
		Builder builder = new Builder(mActivity);
		builder.setMessage("¼ì²âµ½ÄúÎ´°²×°ÀëÏß°ü£¬ÊÇ·ñÇ°Íù°²×°");
		builder.setTitle("ÏÂÔØÌáÊ¾");
		builder.setPositiveButton("È·ÈÏÇ°Íù", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String url = SpeechUtility.getUtility().getComponentUrl();
				String assetsApk="SpeechService.apk";
				processInstall(mActivity, url,assetsApk);
			}
		});
		builder.setNegativeButton("ÏÂÔØ", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
		return;
	}
	/**
	 * å¦‚æœæœåŠ¡ç»„ä»¶æ²¡æœ‰å®‰è£…æ‰“å¼€è¯­éŸ³æœåŠ¡ç»„ä»¶ä¸‹è½½é¡µé¢ï¼Œè¿›è¡Œä¸‹è½½åå®‰è£…ã€?
	 */
	private boolean processInstall(Context context ,String url,String assetsApk){
		//ç›´æ¥ä¸‹è½½æ–¹å¼
		Uri uri = Uri.parse(url);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(it);
		return true;		
	}
}
