package com.StarMicronics.StarIOSDK;

import com.StarMicronics.StarIOSDK.PrinterFunctions.Alignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class simplifiedchineseTextFormatingDotPrinterActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simplifiedchinesetextformating);

		Spinner spinner_Alignment = (Spinner) findViewById(R.id.spinner_Alignment);
		ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "Left", "Center", "Right" });
		spinner_Alignment.setAdapter(ad);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		InitializeComponent();
	}

	private void InitializeComponent() {

		Spinner spinner_Height = (Spinner) findViewById(R.id.spinner_Height);
		spinner_Height.setVisibility(View.GONE);

		Spinner spinner_Width = (Spinner) findViewById(R.id.spinner_Width);
		spinner_Width.setVisibility(View.GONE);

		TextView text_Height = (TextView) findViewById(R.id.textView_Height);
		text_Height.setVisibility(View.GONE);

		TextView text_Width = (TextView) findViewById(R.id.textView_Width);
		text_Width.setVisibility(View.GONE);

		CheckBox checkbox_invertcolor = (CheckBox) findViewById(R.id.checkBox_InvertColor);
		checkbox_invertcolor.setVisibility(View.GONE);

		CheckBox checkbox_Height = (CheckBox) findViewById(R.id.checkBox_Height);
		checkbox_Height.setVisibility(View.GONE);
	}

	public void PrintText(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		String portName = PrinterTypeActivity.getPortName();
		String portSettings = PrinterTypeActivity.getPortSettings();

		CheckBox checkbox_underline = (CheckBox) findViewById(R.id.checkBox_Underline);
		boolean underline = checkbox_underline.isChecked();

		CheckBox checkbox_twoColor = (CheckBox) findViewById(R.id.checkBox_TwoColor);
		boolean twoColor = checkbox_twoColor.isChecked();

		CheckBox checkbox_emphasized = (CheckBox) findViewById(R.id.checkBox_Emphasized);
		boolean emphasized = checkbox_emphasized.isChecked();

		CheckBox checkbox_upperline = (CheckBox) findViewById(R.id.checkBox_Upperline);
		boolean upperline = checkbox_upperline.isChecked();

		CheckBox checkbox_upsideDown = (CheckBox) findViewById(R.id.checkBox_UpsideDown);
		boolean upsideDown = checkbox_upsideDown.isChecked();

		CheckBox checkbox_Width = (CheckBox) findViewById(R.id.checkBox_Width);
		boolean widthExpansion = checkbox_Width.isChecked();

		EditText editText_margin = (EditText) findViewById(R.id.editText_LeftMargin);
		byte leftMargin = 0;
		try {
			leftMargin = (byte) Integer.parseInt(editText_margin.getText().toString());
		} catch (Exception e) {
			leftMargin = 0;
		}

		Spinner spinner_Alignment = (Spinner) findViewById(R.id.spinner_Alignment);
		PrinterFunctions.Alignment alignment = Alignment.Left;
		switch (spinner_Alignment.getSelectedItemPosition()) {
		case 0:
			alignment = Alignment.Left;
			break;
		case 1:
			alignment = Alignment.Center;
			break;
		case 2:
			alignment = Alignment.Right;
			break;
		}

		EditText editText_TextToPrint = (EditText) findViewById(R.id.editText_TextToPrint);
		byte[] textToPrint = editText_TextToPrint.getText().toString().getBytes();
		PrinterFunctions.PrintTextbyDotPrinter(this, portName, portSettings, false, underline, twoColor, emphasized, upperline, upsideDown, false, widthExpansion, leftMargin, alignment, textToPrint, "GB2312");
	}

	public void Help(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		String helpString =
				"<UnderlineTitle>Character Expansion Settings</UnderlineTitle><br/><br/>\n" +
				"Width Expansion<br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC W <StandardItalic>n</StandardItalic></CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1B 57 <StandardItalic>n</StandardItalic></CodeDef><br/><br/>\n" +
				"<rightMov>n = 1 or 0 (on or off)</rightMov><br/><br/>\n" +
				"See the Impact Dot Matrix manual for more information.<br/><br/>\n" +
				"<UnderlineTitle>Emphasized Printing (Bold)</UnderlineTitle><br/><br/>\n" +
				"Start Bold Text<br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC E\n</CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1B 45\n</CodeDef><br/><br/>\n" +
				"Stop Bold Text<br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC F</CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1B 46</CodeDef><br/><br/>\n" +
				"See the Impact Dot Matrix manual for more information.<br/><br/>\n" +
				"<UnderlineTitle>Underline Mode</UnderlineTitle><br/><br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC - <StandardItalic>n</StandardItalic></CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1B 2D <StandardItalic>n</StandardItalic></CodeDef><br/><br/>\n" +
				"<rightMov>n = 1 or 0 (on or off)</rightMov><br/><br/>\n" +
				"<UnderlineTitle>Upperline Mode</UnderlineTitle><br/><br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC _ <StandardItalic>n</StandardItalic></CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1B 5F <StandardItalic>n</StandardItalic></CodeDef><br/><br/>\n" +
				"<rightMov>n = 1 or 0 (on or off)</rightMov><br/><br/>\n" +
				"<UnderlineTitle>Two Color Mode</UnderlineTitle><br/><br/>\n" +
				"Start Two Color<br/>" +
				"<Code>ASCII:</Code> <CodeDef>ESC 4</CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1B 34</CodeDef><br/><br/>\n" +
				"Stop Two Color<br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC 5</CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1b 35</CodeDef><br/><br/>\n" +
				"<UnderlineTitle>Upside-Down Printing</UnderlineTitle><br/><br/>\n" +
				"Start Upside-Down<br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>SI</CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>0F</CodeDef><br/><br/>\n" +
				"Stop Upside-Down<br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>DC2</CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>12</CodeDef><br/><br/>\n" +
				"Note: When using the command, only use it at the start of a new line. Rightside-up and Upside-Down text cannot be on the same line.<br/><br/>" +
				"<UnderlineTitle>Set Left Margin</UnderlineTitle><br/><br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC l <StandardItalic>n</StandardItalic></CodeDef><br/>\n" +
				"<Code>Hex:</Code> <CodeDef>1B 6C <StandardItalic>n</StandardItalic></CodeDef><br/><br/>\n" +
				"<rightMov>n = 0 through 255</rightMov><br/><br/>\n" +
				"See the Impact Dot Matrix manual for more information.<br/><br/>\n" +
				"<UnderlineTitle>Set Text Alignment</UnderlineTitle><br/><br/>\n" +
				"<Code>ASCII:</Code> <CodeDef>ESC GS a <StandardItalic>n</StandardItalic></CodeDef><br/>" +
				"<Code>Hex:</Code> <CodeDef>1B 1D 61 <StandardItalic>n</StandardItalic></CodeDef><br/><br/>" +
				"<rightMov>n = 0 (left) or 1 (center) or 2 (right)</rightMov>" +
				"</body></html>";
		helpMessage.SetMessage(helpString);

		Intent myIntent = new Intent(this, helpMessage.class);
		startActivityFromChild(this, myIntent, 0);
	}
}
