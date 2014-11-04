package com.StarMicronics.StarIOSDK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class textFormattingSelectLanguageActivity extends Activity {

	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.language);

		intent = getIntent();

		if (intent.getIntExtra("PRINTERTYPE", 1) == 2) { // RasterMode
			TextView titleSBCSText = (TextView) findViewById(R.id.text_SBCS);
			TextView titleDBCSText = (TextView) findViewById(R.id.text_DBSC);
			TextView titleDBCSDescriptionText = (TextView) findViewById(R.id.text_DBSC_Description);
			titleSBCSText.setVisibility(View.GONE);
			titleDBCSText.setVisibility(View.GONE);
			titleDBCSDescriptionText.setVisibility(View.GONE);

			setTitle("StarIOSDK - Raster Mode Samples");
		} else if ((intent.getIntExtra("PRINTERTYPE", 1) == 1)) { // Thermal LineMode
			TextView rasterDescriptionText = (TextView) findViewById(R.id.text_Raster_Description);
			rasterDescriptionText.setVisibility(View.GONE);

			setTitle("StarIOSDK - Line Mode Samples");
		} else if (intent.getIntExtra("PRINTERTYPE", 1) == 0) { // DotPrinter
			TextView rasterDescriptionText = (TextView) findViewById(R.id.text_Raster_Description);
			rasterDescriptionText.setVisibility(View.GONE);

			setTitle("StarIOSDK - Impact Dot Matrix Printer Samples");
		}
	}

	public void English(View view) {
		if (!checkClick.isClickEvent())
			return;

		switch (intent.getIntExtra("TEXTFORMATTING", 1)) {
		case 0:// Dot Printer
			Intent dotPrinterEnglishIntent = new Intent(this, textFormatingDotPrinterActivity.class);
			startActivityFromChild(this, dotPrinterEnglishIntent, 0);

			break;

		case 1:// Thermal Line Mode
		default:
			Intent thermalLineModeIntent = new Intent(this, textFormatingActivity.class);
			startActivityFromChild(this, thermalLineModeIntent, 0);
			break;

		}

	}

	public void Japanese(View view) {

		if (!checkClick.isClickEvent()) {
			return;
		}

		switch (intent.getIntExtra("TEXTFORMATTING", 1)) {
		case 0:// Dot Printer
			Intent dotPrinterEnglishIntent = new Intent(this, kanjiTextFormatingDotPrinterActivity.class);
			startActivityFromChild(this, dotPrinterEnglishIntent, 0);

			break;

		case 1:// Thermal Line Mode
		default:
			Intent thermalLineModeIntent = new Intent(this, kanjiTextFormatingActivity.class);
			startActivityFromChild(this, thermalLineModeIntent, 0);
			break;

		}

	}

	public void SimplifiedChinese(View view) {

		if (!checkClick.isClickEvent()) {
			return;
		}

		switch (intent.getIntExtra("TEXTFORMATTING", 1)) {
		case 0:// Dot Printer
			Intent dotPrinterEnglishIntent = new Intent(this, simplifiedchineseTextFormatingDotPrinterActivity.class);
			startActivityFromChild(this, dotPrinterEnglishIntent, 0);

			break;

		case 1:// Thermal Line Mode
		default:
			Intent thermalLineModeIntent = new Intent(this, simplifiedchineseTextFormatingActivity.class);
			startActivityFromChild(this, thermalLineModeIntent, 0);
			break;

		}

	}

	public void TraditionalChinese(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		switch (intent.getIntExtra("TEXTFORMATTING", 1)) {
		case 0:// Dot Printer
			Intent dotPrinterEnglishIntent = new Intent(this, traditionalchineseTextFormatingDotPrinterActivity.class);
			startActivityFromChild(this, dotPrinterEnglishIntent, 0);

			break;

		case 1:// Thermal Line Mode
		default:
			Intent thermalLineModeIntent = new Intent(this, traditionalchineseTextFormatingActivity.class);
			startActivityFromChild(this, thermalLineModeIntent, 0);
			break;
		}
	}

}