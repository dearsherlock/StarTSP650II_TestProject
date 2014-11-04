package com.StarMicronics.StarIOSDK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PINCodeSettingActivity extends Activity implements View.OnClickListener {
	PINCodeSettingActivity me = this;

	ListView listView;
	List<Map<String, String>> currentSettings = new ArrayList<Map<String, String>>();
	SimpleAdapter adapter = null;

	Button button;

	String newPinCode = BluetoothSettingActivity.getInputedPinCode();

	int selectedIndex = 0;

	static boolean allowAlphabetPin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pincode_setting_activity);

		setTitle(getString(R.string.change_pin_code));

		findViews();
		adapter = new SimpleAdapter(this, currentSettings, android.R.layout.simple_list_item_2, new String[] { "main", "sub" }, new int[] { android.R.id.text1, android.R.id.text2 });
		listView.setAdapter(adapter);
		button.setOnClickListener(this);

		refreshSettingList();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0: // PINCode numeric
				{
					final String changePinCodeSettings[] = getResources().getStringArray(R.array.change_pin_code_settings);
					selectedIndex = (newPinCode == null) ? 0 : 1;

					new AlertDialog.Builder(view.getContext()).setTitle(getString(R.string.change_pin_code)).setSingleChoiceItems(changePinCodeSettings, selectedIndex, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							selectedIndex = which;
						}
					}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							if (selectedIndex == 0) { // Not change PINCode
								newPinCode = null;
							} else { // PinCode
								showPinCodeAlert(me);
							}

							refreshSettingList();
						}
					}).setNegativeButton(getString(R.string.cancel), null).show();
					break;
				}

				case 1: // �p��PIN�R�[�h����
				{
					final String allowAlphabetPinCodeSettings[] = getResources().getStringArray(R.array.allow_alphabet_pin_code_settings);
					selectedIndex = (allowAlphabetPin) ? 1 : 0;

					new AlertDialog.Builder(view.getContext()).setTitle(getString(R.string.allow_alphabet_pin_code)).setSingleChoiceItems(allowAlphabetPinCodeSettings, selectedIndex, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							selectedIndex = which;
						}
					}).setPositiveButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							if (selectedIndex == 0) {
								allowAlphabetPin = false;
							} else {
								allowAlphabetPin = true;
							}

							refreshSettingList();
						}
					}).setNegativeButton(getString(R.string.cancel), null).show();
					break;
				}
				}
			}
		});
	}

	private String showPinCodeAlert(PINCodeSettingActivity v) {
		final EditText editText = new EditText(v);
		if (allowAlphabetPin) {
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // For Android 4.2, Auto-completion bug fixes.
		} else {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		}

		// Input value limit
		InputFilter inputFilter = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (source.toString().matches("^[0-9]+$")) {
					return source.toString();
				}

				if (allowAlphabetPin) {
					if (source.toString().matches("^[0-9a-zA-Z]+$")) {
						return source.toString();
					}
				}
				return "";
			}
		};

		InputFilter lengthFilter = new InputFilter.LengthFilter(16); // Limit the number of characters

		// set filter
		InputFilter[] filters = new InputFilter[] { inputFilter, lengthFilter };
		editText.setFilters(filters);

		new AlertDialog.Builder(this).setTitle(getString(R.string.change_pin_code)).setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (editText.getText().length() < 4) {
					new AlertDialog.Builder(me).setTitle(getString(R.string.error)).setMessage(getString(R.string.pincode_character_number_error)).setPositiveButton("OK", null).show();

					newPinCode = null;
				} else {
					newPinCode = editText.getText().toString();
				}

				refreshSettingList();
			}
		}).setView(editText).setNegativeButton(getString(R.string.cancel), null).show();

		return null;
	}

	private void findViews() {
		listView = (ListView) findViewById(R.id.listView_security);
		button = (Button) findViewById(R.id.button_done);
	}

	private void refreshSettingList() {
		currentSettings.clear();

		Map<String, String> map0 = new HashMap<String, String>();
		map0.put("main", getString(R.string.change_pin_code));
		String[] changePinCodeSettings = getResources().getStringArray(R.array.change_pin_code_settings);
		map0.put("sub", (newPinCode == null) ? changePinCodeSettings[0] : changePinCodeSettings[1]);
		currentSettings.add(map0);

		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("main", getString(R.string.allow_alphabet_pin_code));
		String[] allowAlphabetPinCodeSettings = getResources().getStringArray(R.array.allow_alphabet_pin_code_settings);
		map1.put("sub", allowAlphabetPin ? allowAlphabetPinCodeSettings[1] : allowAlphabetPinCodeSettings[0]);
		currentSettings.add(map1);

		adapter.notifyDataSetChanged();
	}

	public void onClick(View v) {
		if (v != (Button) findViewById(R.id.button_done)) {
			return;
		}

		BluetoothSettingActivity.setInputedPinCode(newPinCode);

		finish();
	}
}
