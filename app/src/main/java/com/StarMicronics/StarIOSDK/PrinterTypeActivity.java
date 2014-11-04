package com.StarMicronics.StarIOSDK;

import com.starmicronics.stario.StarIOPort;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class PrinterTypeActivity extends Activity {
	/** Called when the activity is first created. */
	private static PrinterTypeActivity me;
	private String portName;
	private String portSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printertype);

		me = this;
	}

	private final int Menu1 = Menu.FIRST;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu1, Menu.NONE, "About");
		return super.onCreateOptionsMenu(menu);

	}

	private String versionName() {
		String versionName = "";

		PackageManager packageManager = this.getPackageManager();

		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu1:
			String message = "StarIO SDK Version : " + versionName() + "\n" + "StarIO Version  : " + StarIOPort.getStarIOVersion() + "\n\n" + "Copyright 2014 (C) StarMicronics Co., Ltd.";

			Builder dialog = new AlertDialog.Builder(this);
			dialog.setNegativeButton("Ok", null);
			AlertDialog alert = dialog.create();
			alert.setIcon(R.drawable.icon);
			alert.setTitle("Version Information");
			alert.setMessage(message);
			alert.setCancelable(false);
			alert.show();

			return true;
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	public void onClickPOSPrinterButton(View view) {
		if (!checkClick.isClickEvent())
			return;

		Intent myIntent = new Intent(this, CommandTypeActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void onClickImpactDotPrinterButton(View view) {
		if (!checkClick.isClickEvent())
			return;

		Intent myIntent = new Intent(this, StarIOSDKDotPOSPrinterLineModeActivity.class);
		startActivityFromChild(this, myIntent, 0);

	}

	public void onClickMobilePrinterButton(View view) {
		if (!checkClick.isClickEvent())
			return;

		Intent myIntent = new Intent(this, StarIOSDKMobilePrinterActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void onClickCashDrawerButton(View view) {
		if (!checkClick.isClickEvent())
			return;

		Intent myIntent = new Intent(this, DKAirCashActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public static String getPortName() {
		return me.portName;
	}

	public static String getPortSettings() {
		return me.portSettings;
	}

	public static void setPortName(String portName) {
		me.portName = portName;
	}

	public static void setPortSettings(String portSettings) {
		me.portSettings = portSettings;
	}

	public static String HTMLCSS() {
		String cssDefinition =
				"<html>" +
				"<head>" +
				"<style type=\"text/css\">" +
				"Code {color:blue;}\n" +
				"CodeDef {color:blue;font-weight:bold}\n" +
				"TitleBold {font-weight:bold}\n" +
				"It1 {font-style:italic; font-size:12}\n" +
				"LargeTitle{font-size:20px}\n" +
				"SectionHeader{font-size:17;font-weight:bold}\n" +
				"UnderlineTitle {text-decoration:underline}\n" +
				"div_cutParam {position:absolute; top:100; left:30; width:200px;font-style:italic;}\n" +
				"div_cutParam0 {position:absolute; top:130; left:30; font-style:italic;}\n" +
				"div_cutParam1 {position:absolute; top:145; left:30; font-style:italic;}\n" +
				"div_cutParam2 {position:absolute; top:160; left:30; font-style:italic;}\n" +
				"div_cutParam3 {position:absolute; top:175; left:30; font-style:italic;}\n" +
				".div-tableBarcodeWidth{display:table;}\n" +
				".div-table-rowBarcodeWidth{display:table-row;}\n" +
				".div-table-colBarcodeWidthHeader{display:table-cell;border:1px solid #000000;background: #800000;color:#ffffff}\n" +
				".div-table-colBarcodeWidthHeader2{display:table-cell;border:1px solid #000000;background: #800000;color:#ffffff}\n" +
				".div-table-colBarcodeWidth{display:table-cell;border:1px solid #000000;}\n" +
				"rightMov {position:absolute; left:30px; font-style:italic;}\n" +
				"rightMov_NOI {position:absolute; left:55px;}\n" +
				"rightMov_NOI2 {position:absolute; left:90px;}\n" +
				"StandardItalic {font-style:italic}" +
				".div-tableCut{display:table;}\n" +
				".div-table-rowCut{display:table-row;}\n" +
				".div-table-colFirstCut{display:table-cell;width:40px}\n" +
				".div-table-colCut{display:table-cell;width:20px;}\n" +
				".div-table-colCut2{display:table-cell;}\n" +
				".div-table-colRaster{display:table-cell; border:1px solid #000000;}\n" +
				"</style>" +
				"</head>";
		return cssDefinition;
	}
}