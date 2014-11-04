package com.StarMicronics.StarIOSDK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.text.Spanned;

import com.starmicronics.stario.StarBluetoothManager;
import com.starmicronics.stario.StarBluetoothManager.StarBluetoothSecurity;
import com.starmicronics.stario.StarBluetoothManager.StarDeviceType;
import com.starmicronics.stario.StarIOPortException;

public class BluetoothSettingActivity extends Activity implements View.OnClickListener {
	BluetoothSettingActivity me = this;
	boolean changePinCode = false;
	int selectedIndex;

	ListView listView = null;
	Button button = null;

	List<Map<String, String>> listViewItems = new ArrayList<Map<String, String>>();
	SimpleAdapter adapter;

	private StarBluetoothManager printerManager = null;
	private String bluetoothDeviceName = null;
	private String iOSPortName = null;
	private boolean autoConnect = false;
	private StarBluetoothSecurity securityType = StarBluetoothSecurity.SSP;
	private static String pinCode = null;
	static final private int TIMEOUTMILLIS = 10000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);
		findViews();

		adapter = new SimpleAdapter(this, listViewItems, android.R.layout.simple_list_item_2, new String[] { "main", "sub" }, new int[] { android.R.id.text1, android.R.id.text2 });
		listView.setAdapter(adapter);
		button.setOnClickListener(this);
		refreshSettingList();

		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		String portName = pref.getString("bluetoothSettingPortName", "BT:Star Micronics");
		String portSettings = pref.getString("bluetoothSettingPortSettings", "");

		try {
			printerManager = new StarBluetoothManager(portName, portSettings, TIMEOUTMILLIS, StarDeviceType.StarDeviceTypeDesktopPrinter);
		} catch (StarIOPortException e) {
			new AlertDialog.Builder(me).setTitle(getString(R.string.error)).setMessage(e.getMessage()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish(); // close Activity
				}
			}).setCancelable(false).show();

			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(me);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.show();

		// Need to check PortSettings, because occured error in getPort method if the value is invalid portSettings.

		class FunctionViewOpenTask extends AsyncTask<Void, Void, Boolean> {
			private String errorMessage = "";

			@Override
			protected Boolean doInBackground(Void... params) {

				try {
					loadSetting(); // Load Current BluetoothSetting
				} catch (StarIOPortException e) {
					e.printStackTrace();
					errorMessage = e.getMessage();
					return false;
				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean resultCode) {

				progressDialog.dismiss();

				if (resultCode == true) {
					refreshSettingList();
				} else {
					String deviceType;

					if (printerManager.getDeviceType() == StarDeviceType.StarDeviceTypeDesktopPrinter) {
						deviceType = "StarDeviceTypeDesktopPrinter";
					} else {
						deviceType = "StarDeviceTypePortablePrinter";
					}

					errorMessage += "\n- Port Info - \n  portName : " + printerManager.getPortName() + "\n  portSettings : " + printerManager.getPortSettings() + "\n  portTimeoutMillis : " + printerManager.getTimeoutMillis() + "\n  StarDeviceType : " + deviceType;
					new AlertDialog.Builder(me).setTitle(getString(R.string.error)).setMessage(getString(R.string.cannot_communicate_message) + " : " + errorMessage).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).setCancelable(false).show();
				}
			}
		}

		new FunctionViewOpenTask().execute();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// for editText of deviceName and iOSPortName
			private InputFilter[] getEditTextFilters() {
				// Input value limit
				InputFilter inputFilter = new InputFilter() {
					public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
						if (source.toString().matches("^[0-9a-zA-Z;:!?#$%&,.@_\\-= \\\\/\\*\\+~\\^\\[\\{\\(\\]\\}\\)\\|]+$")) {
							return source.toString();
						}
						return "";
					}
				};

				InputFilter lengthFilter = new InputFilter.LengthFilter(16); // Limit the number of characters

				InputFilter[] filters = new InputFilter[] { inputFilter, lengthFilter };

				return filters;
			}

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
				case 0: // Device Name
				{
					// Create TextEdit to enter bluetooth device name.
					final EditText editText = new EditText(view.getContext());
					editText.setInputType(InputType.TYPE_CLASS_TEXT); // disable new line
					editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // For Android 4.2, Auto-completion bug fixes.
					editText.setText(bluetoothDeviceName);

					// set filter
					editText.setFilters(getEditTextFilters());

					// Display AlertDialog
					new AlertDialog.Builder(view.getContext()).setTitle(getString(R.string.device_name)).setView(editText).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (editText.getText().length() == 0) {
								return;
							}
							bluetoothDeviceName = editText.getText().toString();
							iOSPortName = editText.getText().toString();
							refreshSettingList();
						}
					}).setNegativeButton(getString(R.string.cancel), null).show();
					break;
				}
				case 1: // iOS Port Name
				{
					final EditText editText = new EditText(view.getContext());
					editText.setInputType(InputType.TYPE_CLASS_TEXT);
					editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // For Android 4.2, Auto-completion bug fixes.
					editText.setText(iOSPortName);

					// set filter
					editText.setFilters(getEditTextFilters());

					new AlertDialog.Builder(view.getContext()).setTitle(getString(R.string.iOS_port_name)).setView(editText).setPositiveButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							iOSPortName = editText.getText().toString();
							refreshSettingList();
						}
					}).setNegativeButton("Cancel", null).show();
					break;
				}
				case 2: // auto connect
				{
					final String items[] = getResources().getStringArray(R.array.auto_connect_settings);
					selectedIndex = autoConnect ? 0 : 1;

					new AlertDialog.Builder(view.getContext()).setTitle(getString(R.string.auto_connect)).setSingleChoiceItems(items, selectedIndex, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							selectedIndex = which;
						}
					}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// autoConnect = (selectedIndex == 0) ? true : false;
							if (selectedIndex == 0) {
								// Check security type. When auto connect is ON, do not set PINCODE.
								if (securityType == StarBluetoothSecurity.PINCODE) {
									new AlertDialog.Builder(me).setTitle(getString(R.string.Confirmation)).setMessage(getString(R.string.AutoConnectionAlert)).setPositiveButton(getString(R.string.AutoConnectionUseSSP), new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int which) {
											securityType = StarBluetoothSecurity.SSP;
											autoConnect = true;
											refreshSettingList();
										}
									}).setNegativeButton(getString(R.string.cancel), null).show();
								} else {
									autoConnect = true;
									refreshSettingList();
								}
							} else {
								autoConnect = false;
								refreshSettingList();
							}
						}

					}).setNegativeButton("Cancel", null).show();
					break;
				}
				case 3: // Change Security type
				{
					if (autoConnect == true) {
						new AlertDialog.Builder(me).setTitle(getString(R.string.Confirmation)).setMessage(getString(R.string.SecuritySettingAlert)).setNegativeButton("OK", null).show();
						break;
					}

					final String items[] = getResources().getStringArray(R.array.security_settings);
					selectedIndex = (securityType == StarBluetoothSecurity.SSP) ? 0 : 1;

					new AlertDialog.Builder(view.getContext()).setTitle(getString(R.string.security)).setSingleChoiceItems(items, selectedIndex, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							selectedIndex = which;
						}
					}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							securityType = (selectedIndex == 0) ? StarBluetoothSecurity.SSP : StarBluetoothSecurity.PINCODE;
							refreshSettingList();
						}
					}).setNegativeButton(getString(R.string.cancel), null).show();
					break;
				}
				case 4: // Change PIN Code
					Intent intent = new Intent(view.getContext(), PINCodeSettingActivity.class);
					startActivity(intent);
					break;
				case 5: // Help
					Intent intentHelp = new Intent(view.getContext(), BluetoothSettingHelpActivity.class);
					startActivity(intentHelp);
					break;
				}
			}
		});
	}

	private void findViews() {
		listView = (ListView) findViewById(R.id.listView_settings);
		button = (Button) findViewById(R.id.button_done);
	}

	private void loadSetting() throws StarIOPortException {
		String errorMessage = "";

		// load current bluetooth settings
		try {
			printerManager.open();

			printerManager.loadSetting();

			// get bluetooth settings
			bluetoothDeviceName = printerManager.getBluetoothDeviceName();
			iOSPortName = printerManager.getiOSPortName();
			autoConnect = printerManager.getAutoConnect();
			securityType = printerManager.getSecurityType();

		} catch (StarIOPortException e) {
			bluetoothDeviceName = null;
			iOSPortName = null;
			autoConnect = false;
			securityType = StarBluetoothSecurity.SSP;
			pinCode = null;

			e.printStackTrace();
			errorMessage += e.getMessage();
			throw new StarIOPortException(errorMessage);
		} finally {
			if (printerManager.isOpened()) {
				try {
					printerManager.close();
				} catch (StarIOPortException e) {
					e.printStackTrace();
					errorMessage += " / " + e.getMessage();
					throw new StarIOPortException(errorMessage);
				}
			}
		}
	}

	private void refreshSettingList() {
		listViewItems.clear();

		Map<String, String> map0 = new HashMap<String, String>();
		map0.put("main", getString(R.string.device_name));
		map0.put("sub", bluetoothDeviceName);
		listViewItems.add(map0);

		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("main", getString(R.string.iOS_port_name));
		map1.put("sub", iOSPortName);
		listViewItems.add(map1);

		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("main", getString(R.string.auto_connect));
		String autoConnectSettings[] = getResources().getStringArray(R.array.auto_connect_settings);
		map2.put("sub", autoConnect ? autoConnectSettings[0] : autoConnectSettings[1]);
		listViewItems.add(map2);

		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("main", getString(R.string.security));
		String security_strings[] = getResources().getStringArray(R.array.security_settings);
		map3.put("sub", (securityType == StarBluetoothSecurity.SSP) ? security_strings[0] : security_strings[1]);
		listViewItems.add(map3);

		Map<String, String> map4 = new HashMap<String, String>();
		map4.put("main", getString(R.string.change_pin_code));
		String changePinCodeSettings[] = getResources().getStringArray(R.array.change_pin_code_description);
		map4.put("sub", (pinCode == null) ? changePinCodeSettings[0] : changePinCodeSettings[1]);
		listViewItems.add(map4);

		Map<String, String> map5 = new HashMap<String, String>();
		map5.put("main", getString(R.string.help));
		map5.put("sub", "");
		listViewItems.add(map5);

		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshSettingList();

	}

	public void onClick(View v) {
		new AlertDialog.Builder(v.getContext()).setMessage(getString(R.string.update_confirmation_message)).setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				final ProgressDialog progressDialog = new ProgressDialog(me);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();

				class ApplySettingsTask extends AsyncTask<Void, Void, Boolean> {
					private String errorMessage = "";

					@Override
					protected Boolean doInBackground(Void... params) {
						try {
							setPrinterSettings();
						} catch (StarIOPortException e) {
							errorMessage = e.getMessage();
							return false;
						}

						return true;
					}

					@Override
					protected void onPostExecute(Boolean resultCode) {
						progressDialog.dismiss();

						if (resultCode == Boolean.TRUE) {

							new AlertDialog.Builder(me).setTitle(getString(R.string.complete)).setMessage(getString(R.string.complete_description)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finish(); // close Activity
								}
							}).show();

						} else {
							new AlertDialog.Builder(me).setTitle(getString(R.string.error)).setMessage(getString(R.string.cannot_communicate_message) + " : " + errorMessage).setPositiveButton("OK", null).show();
						}
					}
				}

				new ApplySettingsTask().execute();
			}
		}).setNegativeButton(getString(R.string.do_not_update), null).show();
	}

	private void setPrinterSettings() throws StarIOPortException {
		String errorMessage = "";

		try {
			// open
			printerManager.open();

			// set Device Name
			printerManager.setBluetoothDeviceName(bluetoothDeviceName);

			// set iOS PortName
			printerManager.setiOSPortName(iOSPortName);

			// set Security setting
			printerManager.setSecurityType(securityType);

			// set AutoConnection
			printerManager.setAutoConnect(autoConnect);

			// change PINCode
			if (securityType == StarBluetoothSecurity.PINCODE) {
				if (pinCode != null) {
					printerManager.setPinCode(pinCode);

					if (printerManager.getPinCode() != pinCode) {
						throw new StarIOPortException("Could not set a pinCode.");
					}
				}
			}

			printerManager.apply();
		} catch (StarIOPortException e) {
			e.printStackTrace();
			errorMessage += e.getMessage();
			throw new StarIOPortException(errorMessage);
		} finally {
			if (printerManager.isOpened()) {
				try {
					printerManager.close();
				} catch (StarIOPortException e) {
					e.printStackTrace();
					errorMessage += " / " + e.getMessage();
					throw new StarIOPortException(errorMessage);
				}
			}
		}
	}

	public static void setInputedPinCode(String pinCode) {
		BluetoothSettingActivity.pinCode = pinCode;
	}

	public static String getInputedPinCode() {
		return BluetoothSettingActivity.pinCode;
	}
}
