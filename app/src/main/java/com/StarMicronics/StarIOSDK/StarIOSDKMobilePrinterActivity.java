package com.StarMicronics.StarIOSDK;

import java.util.ArrayList;
import java.util.List;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class StarIOSDKMobilePrinterActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.main);

		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		portNameField.setText(pref.getString("m_portName", "TCP:192.168.192.45"));

		InitializeComponent();
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void Help(View view) {
		if (!checkClick.isClickEvent())
			return;
		Intent myIntent = new Intent(this, helpActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void OpenCashDrawer(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		String portName = portNameField.getText().toString();
		String portSettings = getBluetoothRetrySettings();

		MiniPrinterFunctions.OpenCashDrawer(this, portName, portSettings);

	}

	public void GetStatus(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		String portName = portNameField.getText().toString();
		String portSettings = getBluetoothRetrySettings();

		// The portable printer and non portable printer have the same
		MiniPrinterFunctions.CheckStatus(this, portName, portSettings);
	}

	public void GetFirmwareInfo(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		String portName = portNameField.getText().toString();
		String portSettings = getBluetoothRetrySettings();

		// The portable printer and non portable printer have the same
		MiniPrinterFunctions.CheckFirmwareVersion(this, portName, portSettings);
	}

	public void ShowBarcode(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		Intent myIntent = new Intent(this, BarcodePrintingMini.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void ShowBarcode2d(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		Intent myIntent = new Intent(this, barcodeselector2d.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void ShowCut(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());

		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());
		MiniPrinterFunctions.performCut(this);
	}

	public void ShowTextFormating(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		Intent myIntent = new Intent(this, textFormatingMiniActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void ShowKanjiTextFormating(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		Intent myIntent = new Intent(this, kanjiTextFormatingMiniActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void ShowRasterPrinting(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		Intent myIntent = new Intent(this, rasterPrintingActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void ShowImagePrinting(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		Intent myIntent = new Intent(this, imagePrintingActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void MCR(View view) {
		if (!checkClick.isClickEvent())
			return;
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		MiniPrinterFunctions.MCRStart(this, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings());
	}

	public void SampleReceipt(View view) {
		if (!checkClick.isClickEvent())
			return;

		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		PrinterTypeActivity.setPortName(portNameField.getText().toString());
		PrinterTypeActivity.setPortSettings(getBluetoothRetrySettings());

		Intent myIntent = new Intent(this, SampleReciptActivity.class);
		myIntent.putExtra("PRINTERTYPE", 3);
		startActivityFromChild(this, myIntent, 0);
	}

	public void PortDiscovery(View view) {
		if (!checkClick.isClickEvent())
			return;
		List<PortInfo> BTPortList;

		final EditText editPortName;
		final ArrayList<PortInfo> arrayDiscovery;

		ArrayList<String> arrayPortName;

		arrayDiscovery = new ArrayList<PortInfo>();
		arrayPortName = new ArrayList<String>();

		try {
			BTPortList = StarIOPort.searchPrinter("BT:"); // "port discovery" of Portable printer is support only Bluetooth port

			for (PortInfo portInfo : BTPortList) {
				arrayDiscovery.add(portInfo);
			}

			arrayPortName = new ArrayList<String>();

			for (PortInfo discovery : arrayDiscovery) {
				String portName;

				portName = discovery.getPortName();

				if (discovery.getMacAddress().equals("") == false) {
					portName += "\n - " + discovery.getMacAddress();
					if (discovery.getModelName().equals("") == false) {
						portName += "\n - " + discovery.getModelName();
					}
				}
				arrayPortName.add(portName);
			}

		} catch (StarIOPortException e) {
			e.printStackTrace();
		}

		editPortName = new EditText(this);

		new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle("Please Select IP Address or Input Port Name").setCancelable(false).setView(editPortName).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
				EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
				portNameField.setText(editPortName.getText());

				SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("m_portName", portNameField.getText().toString());
				editor.commit();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
			}
		}).setItems(arrayPortName.toArray(new String[0]), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int select) {
				EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
				portNameField.setText(arrayDiscovery.get(select).getPortName());

				SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("m_portName", portNameField.getText().toString());
				editor.commit();
			}
		}).show();
	}

	private void InitializeComponent() { // delete view of some function button for Portable Printer
		TextView titleText = (TextView) findViewById(R.id.textView1);
		titleText.setText("Star Micronics Portable Printer Samples");

		Spinner spinner_bluetooth_connectRetry_type = (Spinner) findViewById(R.id.spinner_bluetooth_connectRetry_type);
		ArrayAdapter<String> ad_bluetooth_connectRetry_type = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "OFF", "ON" });
		spinner_bluetooth_connectRetry_type.setAdapter(ad_bluetooth_connectRetry_type);
		ad_bluetooth_connectRetry_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Button cashDrawerButton = (Button) findViewById(R.id.button_OpenCashDrawer);
		cashDrawerButton.setVisibility(View.GONE);

		Button cashDrawerButton2 = (Button) findViewById(R.id.button_OpenCashDrawer2);
		cashDrawerButton2.setVisibility(View.GONE);

		Button cutButton = (Button) findViewById(R.id.button_Cut);
		cutButton.setVisibility(View.GONE);

		Button bluetoothSettingButton = (Button) findViewById(R.id.button_BluetoothSetting);
		bluetoothSettingButton.setVisibility(View.GONE);

		TextView portNumberText = (TextView) findViewById(R.id.textView2);
		portNumberText.setVisibility(View.GONE);

		TextView bluetoothText = (TextView) findViewById(R.id.textView3);
		bluetoothText.setVisibility(View.INVISIBLE);

		TextView sensorActiveText = (TextView) findViewById(R.id.textView4);
		sensorActiveText.setVisibility(View.INVISIBLE);

		Spinner spinner_tcp_port_number = (Spinner) findViewById(R.id.spinner_tcp_port_number);
		spinner_tcp_port_number.setVisibility(View.INVISIBLE);

		Spinner spinner_bluetooth_communication_type = (Spinner) findViewById(R.id.spinner_bluetooth_communication_type);
		spinner_bluetooth_communication_type.setVisibility(View.INVISIBLE);

		Spinner spinner_sensor_active = (Spinner) findViewById(R.id.spinner_SensorActive);
		spinner_sensor_active.setVisibility(View.GONE);

	}

	private String getBluetoothRetrySettings() {
		String retrySetting = "";

		Spinner spinner_bluetooth_connectRetry_type = (Spinner) findViewById(R.id.spinner_bluetooth_connectRetry_type);
		switch (spinner_bluetooth_connectRetry_type.getSelectedItemPosition()) {
		case 0:
			retrySetting = "mini";
			break;
		case 1:
			retrySetting = "mini;l";
			break;
		}

		return retrySetting;
	}
}