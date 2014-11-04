package com.StarMicronics.StarIOSDK;

import com.StarMicronics.StarIOSDK.MiniPrinterFunctions.BarcodeWidth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class pdf417miniActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mini_pdf417code);

		Spinner spinner_Width = (Spinner) findViewById(R.id.spinner_Width);
		ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "0.125 mm", "0.25 mm", "0.375 mm", "0.5 mm", "0.625 mm", "0.75 mm", "0.875 mm", "1.0 mm" });
		spinner_Width.setAdapter(ad);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner_ColumnNumber = (Spinner) findViewById(R.id.spinner_ColumnNumber);
		ad = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30" });
		spinner_ColumnNumber.setAdapter(ad);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner_Security = (Spinner) findViewById(R.id.spinner_SecurityLevel);
		ad = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "Level 0", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6", "Level 7", "Level 8" });
		spinner_Security.setAdapter(ad);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner_Ratio = (Spinner) findViewById(R.id.spinner_ratio);
		ad = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "2", "3", "4", "5" });
		spinner_Ratio.setAdapter(ad);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	public void PrintBarCode(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		String portName = PrinterTypeActivity.getPortName();
		String portSettings = PrinterTypeActivity.getPortSettings();

		MiniPrinterFunctions.BarcodeWidth width = BarcodeWidth._125;
		Spinner spinner_Width = (Spinner) findViewById(R.id.spinner_Width);
		switch (spinner_Width.getSelectedItemPosition()) {
		case 0:
			width = BarcodeWidth._125;
			break;
		case 1:
			width = BarcodeWidth._250;
			break;
		case 2:
			width = BarcodeWidth._375;
			break;
		case 3:
			width = BarcodeWidth._500;
			break;
		case 4:
			width = BarcodeWidth._625;
			break;
		case 5:
			width = BarcodeWidth._750;
			break;
		case 6:
			width = BarcodeWidth._875;
			break;
		case 7:
			width = BarcodeWidth._1_0;
			break;
		}

		Spinner spinner_ColumnWidth = (Spinner) findViewById(R.id.spinner_ColumnNumber);
		byte columnWidth = (byte) (spinner_ColumnWidth.getSelectedItemPosition() + 1);

		Spinner spinner_SecurityLevel = (Spinner) findViewById(R.id.spinner_SecurityLevel);
		byte securityLevel = (byte) spinner_SecurityLevel.getSelectedItemPosition();

		Spinner spinner_Ratio = (Spinner) findViewById(R.id.spinner_ratio);
		byte ratio = (byte) (spinner_Ratio.getSelectedItemPosition() + 2);

		EditText editText_BarcodeData = (EditText) findViewById(R.id.editText_Barcode_Data);
		byte[] barcodeData = editText_BarcodeData.getText().toString().getBytes();

		MiniPrinterFunctions.PrintPDF417(this, portName, portSettings, width, columnWidth, securityLevel, ratio, barcodeData);
	}

	public void Help(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		String helpString =
				"<UnderlineTitle>Set PDF417</UnderlineTitle><br/><br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>GS Z NUL</CodeDef><br/>" +
				"<Code>Hex:</Code> <CodeDef>1D 5A 00</CodeDef><br/><br/>" +
				"* Note: This code must be used before the other PDF417 command.  It sets the printer to use PDF417 code.<br/><br/>" +
				"<UnderlineTitle>Print PDF417 codes</UnderlineTitle><br/><br/>" +
				"<Code>ASCII:</Code> <CodeDef>ESC Z <StandardItalic>m n k dL dH d1 ... dk</StandardItalic></CodeDef><br/>" +
				"<Code>Hex:</Code> <CodeDef>1B 5A <StandardItalic>m n k dL dH d1 ... dk</StandardItalic></CodeDef><br/><br/>" +
				"<rightMov>m</rightMov> <rightMov_NOI>specifies column number (1&#8804;m&#8804;30).  See manual for more information.</rightMov_NOI><br/><br/></br/>" +
				"<rightMov>n</rightMov> <rightMov_NOI>specifies the security level (0&#8804;n&#8804;8).</rightMov_NOI><br/>" +
				"<rightMov>k</rightMov> <rightMov_NOI>defines the horizontal and vertical ratio (2&#8804;k&#8804;5).</rightMov_NOI><br/><br/>" +
				"<rightMov>dL</rightMov> <rightMov_NOI>Represents the lower byte describing the number of bytes in the PDF417 code.  Mathematically: dL = PDF417 Length &#37; 256.</rightMov_NOI><br/><br/><br/><br/>" +
				"<rightMov>dH</rightMov> <rightMov_NOI>Represents the higher byte describing the number of bytes in the PDF417 code.  Mathematically: dH = PDF417 Length / 256.</rightMov_NOI><br/><br/><br/><br/>" +
				"<rightMov>d1 ... dk</rightMov> <rightMov_NOI2>This is the text that will be placed in the PDF417 code.</rightMov_NOI2>" +
				"</body></html>";
		helpMessage.SetMessage(helpString);

		Intent myIntent = new Intent(this, helpMessage.class);
		startActivityFromChild(this, myIntent, 0);
	}
}
