package com.StarMicronics.StarIOSDK;

import com.StarMicronics.StarIOSDK.PrinterFunctions.CutType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class cutDotPrinterActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cutdotprinter);
	}

	public void Cut(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		String portName = PrinterTypeActivity.getPortName();
		String portSettings = PrinterTypeActivity.getPortSettings();

		PrinterFunctions.performCut(this, portName, portSettings, CutType.PARTIAL_CUT);
	}

	public void CutWithFeed(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		String portName = PrinterTypeActivity.getPortName();
		String portSettings = PrinterTypeActivity.getPortSettings();

		PrinterFunctions.performCut(this, portName, portSettings, CutType.PARTIAL_CUT_FEED);
	}

	public void Help(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		String helpString =
				"<UnderlineTitle>CUT</UnderlineTitle><br/><br/>" +
				"<Code>ASCII:</Code> <CodeDef>ESC d <StandardItalic>n</StandardItalic></CodeDef><br/>" +
				"<Code>Hex:</Code> <CodeDef>1B 64 <StandardItalic>n</StandardItalic></CodeDef><br/><br/>" +
				"&nbsp;&nbsp;n = 0,1,2, or 3<br/>" +
				"&nbsp;&nbsp;0 = Full cut at current position.<br/>" +
				"&nbsp;&nbsp;1 = Partial cut at current position.<br/>" +
				"&nbsp;&nbsp;2 = Paper is fed to cutting position, then full cut.<br/>" +
				"&nbsp;&nbsp;3 = Paper is fed to cutting position, then partial cut.<br/><br/>" +
				"* n = 0,1 :TearBar model is ignored this command.<br/>" +
				"* n = 2,3 :TearBar model is fed to tearbar position.<br/><br/>";
		helpMessage.SetMessage(helpString);

		Intent myIntent = new Intent(this, helpMessage.class);
		startActivityFromChild(this, myIntent, 0);
	}
}
