package com.StarMicronics.StarIOSDK;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.StarMicronics.StarIOSDK.RasterDocument.RasPageEndMode;
import com.StarMicronics.StarIOSDK.RasterDocument.RasSpeed;
import com.StarMicronics.StarIOSDK.RasterDocument.RasTopMargin;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class DKAirCashActivity extends Activity {

	private Context me = this;
	private String strInterface = "";
	private String strPrintArea = "";
	private PrintRecieptThread printthread = null;
	private static int printableArea = 576; // for raster data
	private static AlertDialog alert = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.dkaircash);
		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		portNameField.setText(pref.getString("printerportName", "TCP:192.168.192.45"));
		drawerportNameField.setText(pref.getString("drawerportName", "TCP:192.168.192.10"));

		InitializeComponent();
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private void InitializeComponent() { // delete view of some function button for POS Printer Line mode

		Spinner spinner_bluetooth_connectRetry_type = (Spinner) findViewById(R.id.spinner_bluetooth_connectRetry_type);
		ArrayAdapter<String> ad_bluetooth_connectRetry_type = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "OFF", "ON" });
		spinner_bluetooth_connectRetry_type.setAdapter(ad_bluetooth_connectRetry_type);
		ad_bluetooth_connectRetry_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
		Spinner spinner_printerType = (Spinner) findViewById(R.id.spinner_printerType);
		ArrayAdapter<String> ad_spinner_printerType = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "POS Printer", "Portable Printer" });
		spinner_printerType.setAdapter(ad_spinner_printerType);
		ad_spinner_printerType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner_tcp_port_number = (Spinner) findViewById(R.id.spinner_tcp_port_number);
		ArrayAdapter<String> ad_tcp_port_number = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "Standard", "9100", "9101", "9102", "9103", "9104", "9105", "9106", "9107", "9108", "9109" });
		spinner_tcp_port_number.setAdapter(ad_tcp_port_number);
		ad_tcp_port_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner_bluetooth_communication_type = (Spinner) findViewById(R.id.spinner_bluetooth_communication_type);
		ArrayAdapter<String> ad_bluetooth_communication_type = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "SSP", "PIN Code" });
		spinner_bluetooth_communication_type.setAdapter(ad_bluetooth_communication_type);
		ad_bluetooth_communication_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner_dkaircash_input_password = (Spinner) findViewById(R.id.spinner_displayInputPassword);
		ArrayAdapter<String> ad_dkaircash_input_password = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "ON", "OFF" });
		spinner_dkaircash_input_password.setAdapter(ad_dkaircash_input_password);
		ad_dkaircash_input_password.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		

		Spinner spinner_dkaircash_DrawerLANType = (Spinner) findViewById(R.id.spinner_DrawerLANType);
		ArrayAdapter<String> ad_dkaircash_DrawerLANType = new ArrayAdapter<String>(this, R.layout.spinner, new String[] { "Wired", "Wireless" });
		spinner_dkaircash_DrawerLANType.setAdapter(ad_dkaircash_DrawerLANType);
		ad_dkaircash_DrawerLANType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	public void Help(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		Intent myIntent = new Intent(this, helpActivity.class);
		startActivityFromChild(this, myIntent, 0);
	}

	public void CashDrawerBluetoothSetting(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
		String drawerportName = drawerportNameField.getText().toString();
		String drawerportSettings = getPortSettingsOption(drawerportName, true);

		// Check Bluetooth interface
		if (!drawerportName.startsWith("BT:")) {
			new AlertDialog.Builder(this).setTitle(getString(R.string.error)).setMessage(getString(R.string.bluetooth_interface_only)).setNegativeButton("OK", null).show();
		} else {
			SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putString("bluetoothSettingPortName", drawerportName);
			editor.putString("bluetoothSettingPortSettings", drawerportSettings);
			editor.commit();

			Intent myIntent = new Intent(this, BluetoothSettingActivity.class);
			startActivityFromChild(this, myIntent, 0);
		}
	}

	private String getBluetoothRetrySettings() {
		String retrySetting = "";

		Spinner spinner_bluetooth_connectRetry_type = (Spinner) findViewById(R.id.spinner_bluetooth_connectRetry_type);
		switch (spinner_bluetooth_connectRetry_type.getSelectedItemPosition()) {
		case 0:
			retrySetting = "";
			break;
		case 1:
			retrySetting = ";l";
			break;
		}

		return retrySetting;
	}

	private String getDrawerLANTypeSetting() {
		String drawerlanSetting = "";

		Spinner spinner_drawer_lan_type = (Spinner) findViewById(R.id.spinner_DrawerLANType);
		switch (spinner_drawer_lan_type.getSelectedItemPosition()) {
		case 0:// Wired
			drawerlanSetting = "";
			break;
		case 1:// Wireless
			drawerlanSetting = ";wl";
			break;
		}

		return drawerlanSetting;
	}

	private String getTCPPortSettings() {
		String portSettings = "";

		Spinner spinner_tcp_port_number = (Spinner) findViewById(R.id.spinner_tcp_port_number);
		switch (spinner_tcp_port_number.getSelectedItemPosition()) {
		case 0:
			portSettings = "";
			break;
		case 1:
			portSettings = "9100";
			break;
		case 2:
			portSettings = "9101";
			break;
		case 3:
			portSettings = "9102";
			break;
		case 4:
			portSettings = "9103";
			break;
		case 5:
			portSettings = "9104";
			break;
		case 6:
			portSettings = "9105";
			break;
		case 7:
			portSettings = "9106";
			break;
		case 8:
			portSettings = "9107";
			break;
		case 9:
			portSettings = "9108";
			break;
		case 10:
			portSettings = "9109";
			break;
		}

		return portSettings;
	}

	private String getBluetoothCommunicationType() {
		String portSettings = "";

		Spinner spinner_bluetooth_communication_type = (Spinner) findViewById(R.id.spinner_bluetooth_communication_type);
		switch (spinner_bluetooth_communication_type.getSelectedItemPosition()) {
		case 0:
			portSettings = "";
			break;
		case 1:
			portSettings = ";p";
			break;
		}

		return portSettings;
	}

	private String getPortSettingsOption(String portName, boolean useDrawerPort) {
		String portSettings = "";

		if (portName.toUpperCase(Locale.US).startsWith("TCP:")) {
			portSettings += getTCPPortSettings();
			if (useDrawerPort) {
				portSettings += getDrawerLANTypeSetting();
			}

		} else if (portName.toUpperCase(Locale.US).startsWith("BT:")) {
			portSettings += getBluetoothCommunicationType(); // Bluetooth option of "portSettings" must be last.
			portSettings += getBluetoothRetrySettings();
		}

		return portSettings;
	}

	private boolean getPrinterType() {
		Spinner spinner_printerType = (Spinner) findViewById(R.id.spinner_printerType);

		switch (spinner_printerType.getSelectedItemPosition()) {
		case 0: // "POS Printer"
			return true;
		case 1: // "Portable Printer"
			return false;
		default:
			return true;
		}
	}

	private boolean getInputPassword() {
		Spinner spinner_InputPassword = (Spinner) findViewById(R.id.spinner_displayInputPassword);

		switch (spinner_InputPassword.getSelectedItemPosition()) {
		case 0: // "ON"
		default:
			return true;
		case 1: // "OFF"
			return false;
		}
	}

	public void PortDiscovery(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		// Check Printer Type
		if (getPrinterType()) {
			// POS printer
			final String item_list[] = new String[] { "LAN", "Bluetooth", "All", };

			strInterface = "LAN";

			Builder portDiscoveryDialog = new AlertDialog.Builder(this);
			portDiscoveryDialog.setIcon(android.R.drawable.checkbox_on_background);
			portDiscoveryDialog.setTitle("Port Discovery List");
			portDiscoveryDialog.setCancelable(false);
			portDiscoveryDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					strInterface = item_list[whichButton];
				}
			});

			portDiscoveryDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

					if (true == strInterface.equals("LAN")) {
						getPortDiscovery("LAN");
					} else if (strInterface.equals("Bluetooth")) {
						getPortDiscovery("Bluetooth");
					} else {
						getPortDiscovery("All");
					}
				}

			});
			portDiscoveryDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
				}

			});
			portDiscoveryDialog.show();

		} else {
			// Portable printer
			getPortablePortDiscovery(view);
		}

	}

	private void getPortDiscovery(String interfaceName) {
		List<PortInfo> BTPortList;
		List<PortInfo> TCPPortList;
		final EditText editPortName;

		final ArrayList<PortInfo> arrayDiscovery;
		ArrayList<String> arrayPortName;

		arrayDiscovery = new ArrayList<PortInfo>();
		arrayPortName = new ArrayList<String>();

		try {
			if (interfaceName.equals("Bluetooth") == true || interfaceName.equals("All") == true) {
				BTPortList = StarIOPort.searchPrinter("BT:");

				for (PortInfo portInfo : BTPortList) {
					arrayDiscovery.add(portInfo);
				}
			}

			if (interfaceName.equals("LAN") == true || interfaceName.equals("All") == true) {
				TCPPortList = StarIOPort.searchPrinter("TCP:");

				for (PortInfo portInfo : TCPPortList) {
					arrayDiscovery.add(portInfo);

					// Check SAC10 model
					if (portInfo.getModelName().startsWith("SAC") == true) {
						arrayDiscovery.remove(portInfo);
					}
				}
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

		new AlertDialog.Builder(this).setIcon(android.R.drawable.checkbox_on_background).setTitle("Please Select IP Address or Input Port Name").setCancelable(false).setView(editPortName).setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int button) {
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

				EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
				portNameField.setText(editPortName.getText());

				SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("printerportName", portNameField.getText().toString());
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
				editor.putString("printerportName", portNameField.getText().toString());
				editor.commit();
			}
		}).show();
	}

	private void getDrawerPortDiscovery(String interfaceName) {
		List<PortInfo> BTPortList;
		List<PortInfo> TCPPortList;
		final EditText editPortName;

		final ArrayList<PortInfo> arrayDiscovery;
		ArrayList<String> arrayPortName;

		arrayDiscovery = new ArrayList<PortInfo>();
		arrayPortName = new ArrayList<String>();

		try {
			if (interfaceName.equals("Bluetooth") == true || interfaceName.equals("All") == true) {
				BTPortList = StarIOPort.searchPrinter("BT:");

				for (PortInfo portInfo : BTPortList) {
					arrayDiscovery.add(portInfo);
				}
			}

			if (interfaceName.equals("LAN") == true || interfaceName.equals("All") == true) {
				TCPPortList = StarIOPort.searchPrinter("TCP:");

				for (PortInfo portInfo : TCPPortList) {

					arrayDiscovery.add(portInfo);

					// Check SAC10 model
					if (portInfo.getModelName().startsWith("SAC") == false) {
						arrayDiscovery.remove(portInfo);
					}

				}
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

		new AlertDialog.Builder(this).setIcon(android.R.drawable.checkbox_on_background).setTitle("Please Select IP Address or Input Port Name").setCancelable(false).setView(editPortName).setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int button) {
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

				EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
				drawerportNameField.setText(editPortName.getText());
				SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("drawerportName", drawerportNameField.getText().toString());
				editor.commit();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int button) {
			}

		}).setItems(arrayPortName.toArray(new String[0]), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int select) {
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

				EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
				drawerportNameField.setText(arrayDiscovery.get(select).getPortName());

				SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("drawerportName", drawerportNameField.getText().toString());
				editor.commit();
			}
		}).show();
	}

	public void getPortablePortDiscovery(View view) {
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
				editor.putString("printerportName", portNameField.getText().toString());
				editor.commit();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
			}
		}).setItems(arrayPortName.toArray(new String[0]), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int select) {
				EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
				EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);

				portNameField.setText(arrayDiscovery.get(select).getPortName());

				SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("printerportName", portNameField.getText().toString());
				editor.putString("drawerportName", drawerportNameField.getText().toString());
				editor.commit();
			}
		}).show();
	}

	public void DrawerPortDiscovery(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		final String item_list[] = new String[] { "LAN", "Bluetooth", "All", };

		strInterface = "LAN";

		Builder portDiscoveryDialog = new AlertDialog.Builder(this);
		portDiscoveryDialog.setIcon(android.R.drawable.checkbox_on_background);
		portDiscoveryDialog.setTitle("Port Discovery List");
		portDiscoveryDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				strInterface = item_list[whichButton];
			}
		});

		portDiscoveryDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
				if (true == strInterface.equals("LAN")) {
					getDrawerPortDiscovery("LAN");
				} else if (strInterface.equals("Bluetooth")) {
					getDrawerPortDiscovery("Bluetooth");
				} else {
					getDrawerPortDiscovery("All");
				}

			}
		});
		portDiscoveryDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		portDiscoveryDialog.show();

	}

	public void GetStatus(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		String portName = portNameField.getText().toString();
		String portSettings;

		if (getPrinterType()) {
			portSettings = getPortSettingsOption(portName, false);

			// The portable printer and non portable printer have the same
			CheckStatus(this, portName, portSettings, true);
		} else {
			portSettings = "mini";

			// The portable printer and non portable printer have the same
			MiniPrinterFunctions.CheckStatus(this, portName, portSettings);
		}
	}

	public void GetPrinterFirmwareInfo(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
		String portName = portNameField.getText().toString();
		String portSettings;

		if (getPrinterType()) {
			portSettings = getPortSettingsOption(portName, false);

			// The portable printer and non portable printer have the same
			PrinterFunctions.CheckFirmwareVersion(this, portName, portSettings);
		} else {
			portSettings = "mini";

			// The portable printer and non portable printer have the same
			MiniPrinterFunctions.CheckFirmwareVersion(this, portName, portSettings);
		}
	}

	public void SampleReceipt(final View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		// showDialog(DIALOG_PRINTABLEAREA_ID);
		final String item_list[];

		if (getPrinterType()) { // POSPrinter
			item_list = new String[] { getResources().getString(R.string.printArea3inch), getResources().getString(R.string.printArea4inch), };

			strPrintArea = getResources().getString(R.string.printArea3inch);

		} else { // PortablePrinter
			item_list = new String[] { getResources().getString(R.string.printArea2inch), getResources().getString(R.string.printArea3inch), getResources().getString(R.string.printArea4inch), };

			strPrintArea = getResources().getString(R.string.printArea2inch);
		}

		Builder printableAreaDialog = new AlertDialog.Builder(this);
		printableAreaDialog.setIcon(android.R.drawable.checkbox_on_background);
		printableAreaDialog.setTitle("Paper Size List");
		printableAreaDialog.setCancelable(false);
		printableAreaDialog.setSingleChoiceItems(item_list, 0, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				strPrintArea = item_list[whichButton];
			}
		});
		printableAreaDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
				((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

				// Printer Port Name
				EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
				PrinterTypeActivity.setPortName(portNameField.getText().toString());

				// Drawer Port Name
				EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
				final String drawerportName = drawerportNameField.getText().toString();
				final String drawerportSettings = getPortSettingsOption(drawerportName, true);

				final EditText editView = new EditText(DKAirCashActivity.this);
				editView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				editView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });

				final byte[] commands = new byte[] { 0x07 }; // Drawer open command

				if (getPrinterType()) { // POS Printer

					dialog.dismiss();

					// POSPrinter
					PrinterTypeActivity.setPortSettings(getPortSettingsOption(PrinterTypeActivity.getPortName(), false));
					String commandType = "Raster";

					ArrayList<byte[]> list = CreateSampleReceipt(PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandType, getResources(), strPrintArea);

					byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(list);

					printthread = new PrintRecieptThread(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), commandToSendToPrinter);
					printthread.start();// Start Thread

					int result = printthread.startPrint(); // Start Printing

					if (result == 0) {

						if (getInputPassword()) { // ON

							// show password dialog
							new AlertDialog.Builder(DKAirCashActivity.this).setIcon(android.R.drawable.ic_dialog_info).setTitle("Please Input Password").setView(editView).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

									String sText = editView.getText().toString();

									// Password check
									if (sText.matches("1234")) {
										communicationOpenDrawer(drawerportName, drawerportSettings, commands);
									} else {
										// Drawer ReleasePort
										postMessage("Failure", "The password is incorrect. stop the process.");
									}
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

								}
							}).show();

						} else { // OFF
							communicationOpenDrawer(drawerportName, drawerportSettings, commands);
						}
					}

				} else { // Portable Printer

					dialog.dismiss();

					// Portable Printer
					PrinterTypeActivity.setPortSettings("mini");

					boolean result = MiniPrinterFunctions.PrintSampleReceipt(me, PrinterTypeActivity.getPortName(), PrinterTypeActivity.getPortSettings(), strPrintArea);

					if (result == true) {
						if (getInputPassword()) { // ON

							// show password dialog
							new AlertDialog.Builder(DKAirCashActivity.this).setIcon(android.R.drawable.ic_dialog_info).setTitle("Please Input Password").setView(editView).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int whichButton) {
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

									String sText = editView.getText().toString();

									// Password check
									if (sText.matches("1234")) {
										communicationOpenDrawer(drawerportName, drawerportSettings, commands);
									} else {
										// Drawer ReleasePort
										postMessage("Failure", "The password is incorrect. Stop the process.");
									}
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int whichButton) {
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
									((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
								}
							}).show();

						} else { // OFF
							communicationOpenDrawer(drawerportName, drawerportSettings, commands);
						}
					}
				}
			}
		});
		printableAreaDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		printableAreaDialog.show();

	}

	protected void communicationOpenDrawer(final String drawerportName, final String drawerportSetting, final byte[] drawerCommand) {

		AsyncTask<Void, Void, StarPrinterStatus> DrawerOpenCheckTask = new AsyncTask<Void, Void, StarPrinterStatus>() {

			StarIOPort port = null;

			@Override
			protected StarPrinterStatus doInBackground(Void... params) {
				StarPrinterStatus status = new StarPrinterStatus();

				try {
					port = StarIOPort.getPort(drawerportName, drawerportSetting, 10000);
					status = port.beginCheckedBlock();
				} catch (StarIOPortException e) {

					if (port != null) {
						try {
							StarIOPort.releasePort(port);
						} catch (StarIOPortException e1) {
						}
					}
					port = null;

				}

				return status;
			}

			@Override
			protected void onPostExecute(StarPrinterStatus status) {

				if (port == null) {
					postMessage("Error", "DK-AirCash is turned off or other host is using the DK-AirCash");
					return;
				}

				if (status.compulsionSwitch == true) {
					postMessage("Warning", "Drawer was already opened");

					if (port != null) {
						try {
							StarIOPort.releasePort(port);
						} catch (StarIOPortException e) {
						}
					}

					return;
				}

				postMessage("", "Waiting for drawer to open");

				AsyncTask<Void, Void, StarPrinterStatus> task = new AsyncTask<Void, Void, StarPrinterStatus>() {

					@Override
					protected StarPrinterStatus doInBackground(Void... params) {
						StarPrinterStatus status = new StarPrinterStatus();
						long startTimeMillis = System.currentTimeMillis();

						try {
							port.writePort(drawerCommand, 0, drawerCommand.length);

							status = port.endCheckedBlock();

							while (true) { // check drawer open status for 3 sec
								SystemClock.sleep(200);

								status = port.retreiveStatus();
								if (status.compulsionSwitch == true) {
									break;
								}

								if (System.currentTimeMillis() - startTimeMillis > 3000) {
									break;
								}
							}

						} catch (StarIOPortException e) {
							// Ignore. 'onPostExecute' will show an error message.
						}

						return status;
					}

					@Override
					protected void onPostExecute(StarPrinterStatus status) {

						if ((status.offline == false) && (status.compulsionSwitch == true)) {
							postMessage("", "Waiting for drawer to close");
						} else {
							if (status.compulsionSwitch == false) {
								postMessage("Error", "Drawer didn't open");
							}

							if (port != null) {
								try {
									StarIOPort.releasePort(port);
									SystemClock.sleep(1000); // 1sec
								} catch (StarIOPortException e) {
								}
							}

							return;
						}

						AsyncTask<Void, Void, Boolean> task2 = new AsyncTask<Void, Void, Boolean>() {

							StarPrinterStatus status = new StarPrinterStatus();

							@Override
							protected Boolean doInBackground(Void... params) {
								int timeoutMillis = 30000; // 30sec
								long startTimeMillis = System.currentTimeMillis();
								while (true) { // check drawer close status
									try {
										status = port.retreiveStatus();

										if (status.compulsionSwitch == false) {
											return true;
										}

										if (System.currentTimeMillis() - startTimeMillis > timeoutMillis) {
											return false;
										}

										SystemClock.sleep(150);
									} catch (StarIOPortException e) {
										return false;
									}
								}
							}

							@Override
							protected void onPostExecute(Boolean result) {
								if (port != null) {
									try {
										StarIOPort.releasePort(port);
									} catch (StarIOPortException e) {
									}
								}

								if (result == true) {
									postMessage("", "Completed successfully");

									AsyncTask<Void, Void, Boolean> task3 = new AsyncTask<Void, Void, Boolean>() {

										@Override
										protected Boolean doInBackground(Void... params) {
											SystemClock.sleep(2000);
											return true;
										}

										@Override
										protected void onPostExecute(Boolean result) {
											postMessage("", "");
										}

									};
									task3.execute();

								} else {
									postMessage("Error", "Drawer didn't close within 30 seconds");
								}
							}

						};
						task2.execute();

					} // task onPostExecute
				};

				task.execute();
			} // DrawerOpenCheckTask onPostExecute
		};

		DrawerOpenCheckTask.execute();
	}

	private void kickCashDrawer(final String drawerportName, final String drawerportSettings, final byte[] command) {

		final EditText editView = new EditText(DKAirCashActivity.this);
		editView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		editView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });

		if (getInputPassword()) { // ON

			// show password dialog
			new AlertDialog.Builder(DKAirCashActivity.this).setIcon(android.R.drawable.ic_dialog_info).setTitle("Please Input Password").setView(editView).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

					String sText = editView.getText().toString();

					// Password check
					if (sText.matches("1234")) {
						communicationOpenDrawer(drawerportName, drawerportSettings, command);
					} else {
						// Drawer ReleasePort
						postMessage("Failure", "The password is incorrect. stop the process.");
					}
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
				}
			}).show();
		} else { // OFF

			communicationOpenDrawer(drawerportName, drawerportSettings, command);

		}
	}

	public void OpenCashDrawer(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		// Drawer Port Name
		EditText drawerportNameField1 = (EditText) findViewById(R.id.editText_DrawerPortName);
		String drawerportName = drawerportNameField1.getText().toString();
		String drawerportSettings = getPortSettingsOption(drawerportName, true);

		// show password dialog and Kick cash drawer
		byte[] commands = new byte[] { 0x07 }; // Drawer open command
		kickCashDrawer(drawerportName, drawerportSettings, commands);
	}

	public void OpenCashDrawer2(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		// Drawer Port Name
		EditText drawerportNameField1 = (EditText) findViewById(R.id.editText_DrawerPortName);
		String drawerportName = drawerportNameField1.getText().toString();
		String drawerportSettings = getPortSettingsOption(drawerportName, true);

		// show password dialog and Kick cash drawer
		byte[] commands = new byte[] { 0x1a }; // Drawer open command
		kickCashDrawer(drawerportName, drawerportSettings, commands);
	}

	public void GetCashDrawerStatus(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
		String portName = drawerportNameField.getText().toString();
		String portSettings = getPortSettingsOption(portName, true);

		CheckDrawerStatus(this, portName, portSettings);
	}

	public void GetCashDrawerFirmwareInfo(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
		String portName = drawerportNameField.getText().toString();
		String portSettings = getPortSettingsOption(portName, true);

		PrinterFunctions.CheckFirmwareVersion(this, portName, portSettings);
	}

	public void GetCashDrawerdipswInfo(View view) {
		if (!checkClick.isClickEvent()) {
			return;
		}

		EditText drawerportNameField = (EditText) findViewById(R.id.editText_DrawerPortName);
		String portName = drawerportNameField.getText().toString();
		String portSettings = getPortSettingsOption(portName, true);

		PrinterFunctions.CheckDipSwitchSettings(this, portName, portSettings);
	}

	private static byte[] convertFromListByteArrayTobyteArray(List<byte[]> ByteArray) {
		int dataLength = 0;
		for (int i = 0; i < ByteArray.size(); i++) {
			dataLength += ByteArray.get(i).length;
		}

		int distPosition = 0;
		byte[] byteArray = new byte[dataLength];
		for (int i = 0; i < ByteArray.size(); i++) {
			System.arraycopy(ByteArray.get(i), 0, byteArray, distPosition, ByteArray.get(i).length);
			distPosition += ByteArray.get(i).length;
		}

		return byteArray;
	}

	private void sendCommand(Context context, String portName, String portSettings, ArrayList<byte[]> byteList) {
		StarIOPort port = null;
		try {
			/*
			 * using StarIOPort3.1.jar (support USB Port) Android OS Version: upper 2.2
			 */
			port = StarIOPort.getPort(portName, portSettings, 10000, context);

			/*
			 * using StarIOPort.jar Android OS Version: under 2.1
			 *     port = StarIOPort.getPort(portName, portSettings, 10000);
			 */

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			/*
			 * Using Begin / End Checked Block method When sending large amounts
			 * of raster data, adjust the value in the timeout in the "StarIOPort.getPort"
			 *  in order to prevent "timeout" of the "endCheckedBlock method" while a printing.
			 * 
			 * If receipt print is success but timeout error occurs(Show message which is
			 * "There was no response of the printer within the timeout period."),
			 *  need to change value of timeout more longer in "StarIOPort.getPort" method.
			 *  (e.g.) 10000 -> 30000
			 */
			StarPrinterStatus status = port.beginCheckedBlock();

			if (status.offline == true) {
				throw new StarIOPortException("A printer is offline");
			}

			byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(byteList);
			port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);

			status = port.endCheckedBlock();

			if (status.coverOpen == true) {
				throw new StarIOPortException("Printer cover is open");
			} else if (status.receiptPaperEmpty == true) {
				throw new StarIOPortException("Receipt paper is empty");
			} else if (status.offline == true) {
				throw new StarIOPortException("Printer is offline");
			}
		} catch (StarIOPortException e) {
			postMessage("Failure", e.getMessage());
		} finally {
			if (port != null) {
				try {
					StarIOPort.releasePort(port);
				} catch (StarIOPortException e) {
				}
			}
		}
	}

	/**
	 * This function shows how to create the receipt data of a thermal POS printer.
	 *
	 * @param context
	 *     Activity for displaying messages to the user
	 * @param portName
	 *     Port name to use for communication. This should be (TCP:<IPAddress>)
	 * @param portSettings
	 *     Should be blank
	 * @param commandType
	 *     Command type to use for printing. This should be ("Line" or "Raster")
	 * @param res
	 *     The resources object containing the image data. ( e.g.) getResources())
	 * @param strPrintArea
	 *     Printable area size, This should be ("3inch (80mm)" or "4inch (112mm)")
	 * @return
	 *     print data
	 */
	public static ArrayList<byte[]> CreateSampleReceipt(String portName, String portSettings, String commandType, Resources res, String strPrintArea) {
		ArrayList<byte[]> list = new ArrayList<byte[]>();

		if (commandType == "Line") {
			if (strPrintArea.equals("3inch (80mm)")) {

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 }); // Alignment (center)

				// data = "[If loaded.. Logo1 goes here]\r\n".getBytes();
				// tempList = new Byte[data.length];
				// CopyArray(data, tempList);
				// list.addAll(Arrays.asList(tempList));
				//
				// list.add(new byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}); //Stored Logo Printing

				list.add("\nStar Clothing Boutique\r\n".getBytes());
				list.add("123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x00 }); // Alignment
				list.add(new byte[] { 0x1b, 0x44, 0x02, 0x10, 0x22, 0x00 }); // Set horizontal tab

				list.add("Date: MM/DD/YYYY".getBytes());

				list.add(new byte[] { ' ', 0x09, ' ' }); // Moving Horizontal Tab

				list.add("Time:HH:MM PM\r\n------------------------------------------------\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x45 }); // bold

				list.add("SALE \r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x46 }); // bolf off

				list.add("SKU ".getBytes());

				list.add(new byte[] { 0x09 });

				// Notice that we use a unicode representation because that is
				// how Java expresses these bytes as double byte unicode
				// This will TAB to the next horizontal position
				list.add("  Description   \u0009         Total\r\n".getBytes());
				list.add("300678566 \u0009  PLAIN T-SHIRT\u0009         10.99\r\n".getBytes());
				list.add("300692003 \u0009  BLACK DENIM\u0009         29.99\r\n".getBytes());
				list.add("300651148 \u0009  BLUE DENIM\u0009         29.99\r\n".getBytes());
				list.add("300642980 \u0009  STRIPED DRESS\u0009         49.99\r\n".getBytes());
				list.add("300638471 \u0009  BLACK BOOTS\u0009         35.99\r\n\r\n".getBytes());
				list.add("Subtotal \u0009\u0009        156.95\r\n".getBytes());
				list.add("Tax \u0009\u0009          0.00\r\n".getBytes());
				list.add("------------------------------------------------\r\n".getBytes());
				list.add("Total".getBytes());

				// Character expansion
				list.add(new byte[] { 0x06, 0x09, 0x1b, 0x69, 0x01, 0x01 });

				list.add("        $156.95\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x69, 0x00, 0x00 }); // Cancel Character Expansion

				list.add("------------------------------------------------\r\n\r\n".getBytes());
				list.add("Charge\r\n159.95\r\n".getBytes());
				list.add("Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes());

				// Specify/Cancel White/Black Invert
				list.add("\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes());

				// Specify/Cancel Underline Printing
				list.add(("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes());

				list.add("And tags attached\r\n\r\n".getBytes());

				// 1D barcode example
				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 });
				list.add(new byte[] { 0x1b, 0x62, 0x06, 0x02, 0x02 });
				list.add(" 12ab34cd56\u001e\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x64, 0x02 }); // Cut

			} else if (strPrintArea.equals("4inch (112mm)")) {

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 }); // Alignment (center)

				// list.add("[If loaded.. Logo1 goes here]\r\n".getBytes());
				// list.add(new byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}); //Stored Logo Printing

				list.add("\nStar Clothing Boutique\r\n".getBytes());
				list.add("123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x00 }); // Alignment
				list.add(new byte[] { 0x1b, 0x44, 0x02, 0x10, 0x22, 0x00 }); // Set horizontal tab

				list.add("Date: MM/DD/YYYY     \u0009               \u0009       Time:HH:MM PM\r\n".getBytes());
				list.add("---------------------------------------------------------------------\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x45 }); // bold

				list.add("SALE \r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x46 }); // bolf off

				list.add("SKU ".getBytes());

				list.add(new byte[] { 0x09 });

				// Notice that we use a unicode representation because that is
				// how Java expresses these bytes as double byte unicode
				// This will TAB to the next horizontal position
				list.add("            Description         \u0009\u0009\u0009                Total\r\n".getBytes());
				list.add("300678566      \u0009            PLAIN T-SHIRT\u0009                       10.99\r\n".getBytes());
				list.add("300692003      \u0009            BLACK DENIM\u0009                         29.99\r\n".getBytes());
				list.add("300651148      \u0009            BLUE DENIM\u0009                          29.99\r\n".getBytes());
				list.add("300642980      \u0009            STRIPED DRESS\u0009                       49.99\r\n".getBytes());
				list.add("300638471      \u0009            BLACK BOOTS\u0009                         35.99\r\n\r\n".getBytes());
				list.add("Subtotal       \u0009                       \u0009                        156.95\r\n".getBytes());
				list.add("Tax            \u0009                       \u0009                          0.00\r\n".getBytes());
				list.add("---------------------------------------------------------------------\r\n".getBytes());
				list.add("Total".getBytes());

				// Character expansion
				list.add(new byte[] { 0x06, 0x09, 0x1b, 0x69, 0x01, 0x01 });

				list.add("\u0009         $156.95\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x69, 0x00, 0x00 }); // Cancel Character Expansion

				list.add("---------------------------------------------------------------------\r\n\r\n".getBytes());
				list.add("Charge\r\n159.95\r\n".getBytes());
				list.add("Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes());

				// Specify/Cancel White/Black Invert
				list.add("\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes());

				// Specify/Cancel Underline Printing
				list.add(("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes());

				list.add("And tags attached\r\n\r\n".getBytes());

				// 1D barcode example
				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 });
				list.add(new byte[] { 0x1b, 0x62, 0x06, 0x02, 0x02 });
				list.add(" 12ab34cd56\u001e\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x64, 0x02 }); // Cut

			}
		} else if (commandType == "Raster") {

			if (strPrintArea.equals("3inch (80mm)")) {

				printableArea = 576; // Printable area in paper is 832(dot)

				RasterDocument rasterDoc = new RasterDocument(RasSpeed.Medium, RasPageEndMode.FeedAndFullCut, RasPageEndMode.FeedAndFullCut, RasTopMargin.Standard, 0, 0, 0);
				list.add(rasterDoc.BeginDocumentCommandData());

				String textToPrint = (
						"                       Star Clothing Boutique\r\n" +
						"                             123 Star Road\r\n" +
						"                           City, State 12345\r\n\r\n" +
						"Date: MM/DD/YYYY                 Time:HH:MM PM\r\n" +
						"-----------------------------------------------------------------------\r");
				list.add(createRasterCommand(textToPrint, 13, 0));

				list.add(createRasterCommand("SALE", 13, Typeface.BOLD));

				textToPrint = (
						"SKU \t\t\t                 Description \t\t                Total\r\n" +
						"300678566 \t\t\t      PLAIN T-SHIRT		\t\t    10.99\n" +
						"300692003 \t\t\t      BLACK DENIM		\t\t    29.99\n" +
						"300651148 \t\t\t      BLUE DENIM		\t\t       29.99\n" +
						"300642980 \t\t\t      STRIPED DRESS		\t       49.99\n" +
						"300638471 \t\t\t      BLACK BOOTS		\t\t       35.99\n\n" +
						"Subtotal\t\t\t\t                                              156.95\r\n" +
						"Tax		\t\t\t\t                                                     0.00\r\n" +
						"-----------------------------------------------------------------------\r\n" +
						"Total   \t                                                   $156.95\r\n" +
						"-----------------------------------------------------------------------\r\n\r\n" +
						"Charge\r\n159.95\r\n" + "Visa XXXX-XXXX-XXXX-0123\r\n");
				list.add(createRasterCommand(textToPrint, 13, 0));

				list.add(createRasterCommand("Refunds and Exchanges", 13, Typeface.BOLD));

				textToPrint = ("Within 30 days with receipt\r\n" + "And tags attached");
				list.add(createRasterCommand(textToPrint, 13, 0));

				Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.qrcode);
				StarBitmap starbitmap = new StarBitmap(bm, false, 146);
				list.add(starbitmap.getImageRasterDataForPrinting(true));

				list.add(rasterDoc.EndDocumentCommandData());

			} else if (strPrintArea.equals("4inch (112mm)")) {

				printableArea = 832; // Printable area in paper is 832(dot)

				RasterDocument rasterDoc = new RasterDocument(RasSpeed.Medium, RasPageEndMode.FeedAndFullCut, RasPageEndMode.FeedAndFullCut, RasTopMargin.Standard, 0, 0, 0);
				list.add(rasterDoc.BeginDocumentCommandData());

				String textToPrint = (
						"                                          Star Clothing Boutique\r\n" +
						"                                                123 Star Road\r\n" +
						"                                              City, State 12345\r\n\r\n" +
						"Date: MM/DD/YYYY                                                      Time:HH:MM PM\r\n" +
						"-------------------------------------------------------------------------------------------------------\r");
				list.add(createRasterCommand(textToPrint, 13, 0));

				list.add(createRasterCommand("SALE", 13, Typeface.BOLD));

				textToPrint = (
						"SKU \t\t\t                                   Description \t\t                                  Total\r\n" +
						"300678566 \t\t\t                        PLAIN T-SHIRT		\t\t                      10.99\n" +
						"300692003 \t\t\t                        BLACK DENIM		\t\t                      29.99\n" +
						"300651148 \t\t\t                        BLUE DENIM		\t\t                         29.99\n" +
						"300642980 \t\t\t                        STRIPED DRESS		\t                         49.99\n"+
						"300638471 \t\t\t                        BLACK BOOTS		\t\t                      35.99\n\n");
				list.add(createRasterCommand(textToPrint, 13, 0));

				textToPrint = (
						"Subtotal\t\t\t\t                                                                                  156.95\r\n" +
						"Tax		\t\t\t\t                                                                                         0.00\r\n" +
						"-------------------------------------------------------------------------------------------------------\r" +
						"Total   \t                                                                                       $156.95\r\n" +
						"-------------------------------------------------------------------------------------------------------\r\n\r\n" +
						"Charge\r\n159.95\r\n" +
						"Visa XXXX-XXXX-XXXX-0123\r\n");
				list.add(createRasterCommand(textToPrint, 13, 0));

				list.add(createRasterCommand("Refunds and Exchanges", 13, Typeface.BOLD));

				textToPrint = ("Within 30 days with receipt\r\n" + "And tags attached");
				list.add(createRasterCommand(textToPrint, 13, 0));

				Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.qrcode);
				StarBitmap starbitmap = new StarBitmap(bm, false, 146);
				list.add(starbitmap.getImageRasterDataForPrinting(true));

				list.add(rasterDoc.EndDocumentCommandData());

			}
		}

		return list;
	}

	private static byte[] createRasterCommand(String printText, int textSize, int bold) {
		byte[] command;

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);

		Typeface typeface;

		try {
			typeface = Typeface.create(Typeface.SERIF, bold);
		} catch (Exception e) {
			typeface = Typeface.create(Typeface.DEFAULT, bold);
		}

		paint.setTypeface(typeface);
		paint.setTextSize(textSize * 2);
		paint.setLinearText(true);

		TextPaint textpaint = new TextPaint(paint);
		textpaint.setLinearText(true);
		android.text.StaticLayout staticLayout = new StaticLayout(printText, textpaint, printableArea, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
		int height = staticLayout.getHeight();

		Bitmap bitmap = Bitmap.createBitmap(staticLayout.getWidth(), height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bitmap);
		c.drawColor(Color.WHITE);
		c.translate(0, 0);
		staticLayout.draw(c);

		StarBitmap starbitmap = new StarBitmap(bitmap, false, printableArea);

		command = starbitmap.getImageRasterDataForPrinting(true);

		return command;
	}

	/**
	 * This function shows how to print the receipt data of a thermal POS printer.
	 * 
	 * @param context
	 *     Activity for displaying messages to the user
	 * @param portName
	 *     Port name to use for communication.
	 *     This should be (TCP:<IPAddress>)
	 * @param portSettings
	 *     Should be blank
	 * @param commandType
	 *     Command type to use for printing. This should be ("Line" or "Raster")
	 * @param res
	 *     The resources object containing the image data. ( e.g.) getResources())
	 * @param strPrintArea
	 *     Printable area size, This should be ("3inch (80mm)" or "4inch (112mm)")
	 */
	public void PrintSampleReceipt(Context context, String portName, String portSettings, String commandType, Resources res, String strPrintArea) {
		if (commandType == "Line") {
			if (strPrintArea.equals("3inch (80mm)")) {
				ArrayList<byte[]> list = new ArrayList<byte[]>();

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 }); // Alignment (center)

				// data = "[If loaded.. Logo1 goes here]\r\n".getBytes();
				// tempList = new Byte[data.length];
				// CopyArray(data, tempList);
				// list.addAll(Arrays.asList(tempList));
				//
				// list.add(new byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}); // Stored Logo Printing

				list.add("\nStar Clothing Boutique\r\n".getBytes());
				list.add("123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x00 }); // Alignment

				list.add(new byte[] { 0x1b, 0x44, 0x02, 0x10, 0x22, 0x00 }); // Set horizontal tab

				list.add("Date: MM/DD/YYYY".getBytes());

				list.add(new byte[] { ' ', 0x09, ' ' }); // Moving Horizontal Tab

				list.add("Time:HH:MM PM\r\n------------------------------------------------\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x45 }); // bold

				list.add("SALE \r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x46 }); // bolf off

				list.add("SKU ".getBytes());

				list.add(new byte[] { 0x09 });

				// Notice that we use a unicode representation because that is
				// how Java expresses these bytes as double byte unicode
				// This will TAB to the next horizontal position
				list.add("  Description   \u0009         Total\r\n".getBytes());
				list.add("300678566 \u0009  PLAIN T-SHIRT\u0009         10.99\r\n".getBytes());
				list.add("300692003 \u0009  BLACK DENIM\u0009         29.99\r\n".getBytes());
				list.add("300651148 \u0009  BLUE DENIM\u0009         29.99\r\n".getBytes());
				list.add("300642980 \u0009  STRIPED DRESS\u0009         49.99\r\n".getBytes());
				list.add("300638471 \u0009  BLACK BOOTS\u0009         35.99\r\n\r\n".getBytes());
				list.add("Subtotal \u0009\u0009        156.95\r\n".getBytes());
				list.add("Tax \u0009\u0009          0.00\r\n".getBytes());
				list.add("------------------------------------------------\r\n".getBytes());
				list.add("Total".getBytes());

				// Character expansion
				list.add(new byte[] { 0x06, 0x09, 0x1b, 0x69, 0x01, 0x01 });

				list.add("        $156.95\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x69, 0x00, 0x00 }); // Cancel Character Expansion

				list.add("------------------------------------------------\r\n\r\n".getBytes());

				list.add("Charge\r\n159.95\r\n".getBytes());
				list.add("Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes());
				list.add("\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes()); // Specify/Cancel White/Black Invert

				// Specify/Cancel Underline Printing
				list.add(("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes());
				list.add("And tags attached\r\n\r\n".getBytes());

				// 1D barcode example
				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 });
				list.add(new byte[] { 0x1b, 0x62, 0x06, 0x02, 0x02 });

				list.add(" 12ab34cd56\u001e\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x64, 0x02 }); // Cut

				sendCommand(context, portName, portSettings, list);
			} else if (strPrintArea.equals("4inch (112mm)")) {
				ArrayList<byte[]> list = new ArrayList<byte[]>();

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 }); // Alignment (center)

				// data = "[If loaded.. Logo1 goes here]\r\n".getBytes();
				// tempList = new Byte[data.length];
				// CopyArray(data, tempList);
				// list.addAll(Arrays.asList(tempList));
				// list.add(new byte[]{0x1b, 0x1c, 0x70, 0x01, 0x00, '\r', '\n'}); //Stored Logo Printing

				list.add("\nStar Clothing Boutique\r\n".getBytes());
				list.add("123 Star Road\r\nCity, State 12345\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x00 }); // Alignment

				list.add(new byte[] { 0x1b, 0x44, 0x02, 0x10, 0x22, 0x00 }); // Set horizontal tab

				list.add("Date: MM/DD/YYYY     \u0009               \u0009       Time:HH:MM PM\r\n".getBytes());
				list.add("---------------------------------------------------------------------\r\n\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x45 }); // bold

				list.add("SALE \r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x46 }); // bolf off

				list.add("SKU ".getBytes());

				list.add(new byte[] { 0x09 });

				// Notice that we use a unicode representation because that is
				// how Java expresses these bytes as double byte unicode
				// This will TAB to the next horizontal position
				list.add("            Description         \u0009\u0009\u0009                Total\r\n".getBytes());
				list.add("300678566      \u0009            PLAIN T-SHIRT\u0009                       10.99\r\n".getBytes());
				list.add("300692003      \u0009            BLACK DENIM\u0009                         29.99\r\n".getBytes());
				list.add("300651148      \u0009            BLUE DENIM\u0009                          29.99\r\n".getBytes());
				list.add("300642980      \u0009            STRIPED DRESS\u0009                       49.99\r\n".getBytes());
				list.add("300638471      \u0009            BLACK BOOTS\u0009                         35.99\r\n\r\n".getBytes());
				list.add("Subtotal       \u0009                       \u0009                        156.95\r\n".getBytes());
				list.add("Tax            \u0009                       \u0009                          0.00\r\n".getBytes());
				list.add("---------------------------------------------------------------------\r\n".getBytes());
				list.add("Total".getBytes());

				// Character expansion
				list.add(new byte[] { 0x06, 0x09, 0x1b, 0x69, 0x01, 0x01 });

				list.add("\u0009         $156.95\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x69, 0x00, 0x00 }); // Cancel Character Expansion

				list.add("---------------------------------------------------------------------\r\n\r\n".getBytes());

				list.add("Charge\r\n159.95\r\n".getBytes());
				list.add("Visa XXXX-XXXX-XXXX-0123\r\n\r\n".getBytes());
				list.add("\u001b\u0034Refunds and Exchanges\u001b\u0035\r\n".getBytes()); // Specify/Cancel White/Black Invert

				// Specify/Cancel Underline Printing
				list.add(("Within " + "\u001b\u002d\u0001" + "30 days\u001b\u002d\u0000" + " with receipt\r\n").getBytes());

				list.add(("And tags attached\r\n\r\n").getBytes());

				// 1D barcode example
				list.add(new byte[] { 0x1b, 0x1d, 0x61, 0x01 });
				list.add(new byte[] { 0x1b, 0x62, 0x06, 0x02, 0x02 });

				list.add(" 12ab34cd56\u001e\r\n".getBytes());

				list.add(new byte[] { 0x1b, 0x64, 0x02 }); // Cut

				sendCommand(context, portName, portSettings, list);
			}
		} else if (commandType == "Raster") {
		}
	}

	/**
	 * This function checks the status of the printer
	 * 
	 * @param context
	 *     Activity for displaying messages to the user
	 * @param portName
	 *     Port name to use for communication.
	 *     This should be (TCP:<IPAddress>)
	 * @param portSettings
	 *     Should be blank
	 * @param sensorActiveHigh
	 *     boolean variable to tell the sensor active of CashDrawer which is High
	 */
	public void CheckStatus(Context context, String portName, String portSettings, boolean sensorActiveHigh) {
		StarIOPort port = null;
		try {
			/*
			 * using StarIOPort3.1.jar (support USB Port) Android OS Version: upper 2.2
			 */
			port = StarIOPort.getPort(portName, portSettings, 10000, context);
			/*
			 * using StarIOPort.jar Android OS Version: under 2.1
			 *     port = StarIOPort.getPort(portName, portSettings, 10000);
			 */

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			StarPrinterStatus status = port.retreiveStatus();

			if (status.offline == false) {
				postMessage("Printer", "Printer is online");
			} else {
				String message = "Printer is offline";

				if (status.receiptPaperEmpty == true) {
					message += "\nPaper is empty";
				}

				if (status.coverOpen == true) {
					message += "\nCover is open";
				}

				postMessage("Printer", message);
			}

		} catch (StarIOPortException e) {
			postMessage("Failure", "Failed to connect to printer");
		} finally {
			if (port != null) {
				try {
					StarIOPort.releasePort(port);
				} catch (StarIOPortException e) {
				}
			}
		}
	}

	/**
	 * This function checks the status of the drawer
	 * 
	 * @param context
	 *     Activity for displaying messages to the user
	 * @param portName
	 *     Port name to use for communication.
	 *     This should be (TCP:<IPAddress>)
	 * @param portSettings
	 *     Should be blank
	 */
	public void CheckDrawerStatus(Context context, String portName, String portSettings) {
		StarIOPort port = null;
		try {
			/*
			 * using StarIOPort3.1.jar (support USB Port) Android OS Version: upper 2.2
			 */
			port = StarIOPort.getPort(portName, portSettings, 10000, context);
			/*
			 * using StarIOPort.jar Android OS Version: under 2.1
			 *     port = StarIOPort.getPort(portName, portSettings, 10000);
			 */

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			StarPrinterStatus status = port.retreiveStatus();

			String message;
			if (status.offline == false) {
				if (status.compulsionSwitch == false) {
					message = "The Drawer is online\nCash Drawer: Close";
				} else {
					message = "The Drawer is online\nCash Drawer: Open";
				}
			} else {
				if (status.compulsionSwitch == false) {
					message = "The Drawer is offline\nCash Drawer: Close";
				} else {
					message = "The Drawer is offline\nCash Drawer: Open";
				}
			}
			postMessage("Drawer", message);

		} catch (StarIOPortException e) {
			postMessage("Failure", "Failed to connect to drawer");
		} finally {
			if (port != null) {
				try {
					StarIOPort.releasePort(port);
				} catch (StarIOPortException e) {
				}
			}
		}
	}

	protected void postMessage(String titleText, String messageText) {
		if (alert != null) {
			alert.dismiss();
			alert = null;
		}

		if (titleText != "" && messageText != "") {
			Builder dialog = new AlertDialog.Builder(this);
			dialog.setNegativeButton("OK", null);
			dialog.setTitle(titleText);
			dialog.setMessage(messageText);
			dialog.setCancelable(false);
			alert = dialog.create();
			alert.setCancelable(false);
			alert.show();
		} else if (messageText != "") {
			Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage(messageText);
			dialog.setCancelable(false);
			alert = dialog.create();
			alert.setCancelable(false);
			alert.show();
		}
	}
}

class PrintRecieptThread extends Thread {

	private StarIOPort port = null;
	private String portSettings = "";
	private String portName = "";
	private Context me;
	private byte[] commandToSendToPrinter = null;

	public PrintRecieptThread(Context context, String portName, String portSettings, byte[] commandToSendToPrinter) {
		this.portName = portName;
		this.portSettings = portSettings;
		this.me = context;
		this.commandToSendToPrinter = commandToSendToPrinter;
	}

	public void run() {
	}

	public int startPrint() {

		int result = 0;

		try {
			port = StarIOPort.getPort(portName, portSettings, 10000, me);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			StarPrinterStatus status = port.beginCheckedBlock();

			if (true == status.offline) {
				result = -1;
				String message = "A printer is offline";
				printhandler.obtainMessage(2, message).sendToTarget();
			}

			port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);

			// Change the timeout time of endCheckedBlock method.
			port.setEndCheckedBlockTimeoutMillis(30000);

			status = port.endCheckedBlock();

			if (status.coverOpen == true) {
				result = -1;
				String message = "Printer cover is open";
				printhandler.obtainMessage(2, message).sendToTarget();
			} else if (status.receiptPaperEmpty == true) {
				result = -1;
				String message = "Receipt paper is empty";
				printhandler.obtainMessage(2, message).sendToTarget();
			} else if (status.offline == true) {
				result = -1;
				String message = "Printer is offline";
				printhandler.obtainMessage(2, message).sendToTarget();
			}
		} catch (StarIOPortException e) {
			printhandler.obtainMessage(2, e.getMessage()).sendToTarget();
			result = -1;
		} finally {
			if (port != null) {
				try {
					StarIOPort.releasePort(port);
				} catch (StarIOPortException e) {
					result = -1;
				}
			}
		}

		return result;
	}

	private final Handler printhandler = new Handler() {
		public void handleMessage(Message message) {
			try {
				switch (message.what) {
				case 1:
					Builder dialog = new AlertDialog.Builder(me);
					dialog.setNegativeButton("OK", null);
					dialog.setTitle("Failure");
					dialog.setMessage("Service discovery failed.");
					AlertDialog alert = dialog.create();
					alert.show();
					break;

				case 2:
					Builder dialog2 = new AlertDialog.Builder(me);
					dialog2.setNegativeButton("OK", null);
					dialog2.setTitle("Failure");
					dialog2.setMessage(message.obj.toString());
					AlertDialog alert2 = dialog2.create();
					alert2.show();
					break;
				}
			} catch (Exception e) {
			}
		}
	};
}
