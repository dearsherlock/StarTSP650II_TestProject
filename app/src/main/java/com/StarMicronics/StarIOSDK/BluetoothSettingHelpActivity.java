package com.StarMicronics.StarIOSDK;

import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;

public class BluetoothSettingHelpActivity extends Activity {
	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetoothsettinghelp);

		setTitle(getString(R.string.help));

		findViews();

		webView.loadUrl("file:///android_asset/Help_Bluetooth_En.html");
	}

	private void findViews() {
		webView = (WebView)findViewById(R.id.webView_help);
	}
}
