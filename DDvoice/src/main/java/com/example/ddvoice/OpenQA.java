package com.example.ddvoice;

import android.content.Context;

public class OpenQA {

	private String mText;
	OnlineSpeechAction onlineSpeechAction;
	Context ctx;
	public OpenQA(String text,Context context,OnlineSpeechAction activity){
		this.ctx = context;
		mText=text;
		onlineSpeechAction=activity;
	}
	
	public void start(){
		onlineSpeechAction.speak(mText, false,ctx);
	}
	
}
