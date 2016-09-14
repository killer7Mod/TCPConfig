package com.wmods.tcpconfig;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.preference.*;
import java.util.*;

public class SettingsActivity 
extends PreferenceActivity
{

	private SharedPreferences prefs;

	private SwitchPreference enabled_module;

	private SwitchPreference log_net;

	private SwitchPreference use_in_socket;

	private SwitchPreference use_in_headergroup;

	private MultiSelectListPreference list_apps;

	private EditTextPreference custom_header;

	private EditTextPreference custom_request;

	private SwitchPreference use_in_all;

	private MultiSelectListPreference list_apps2;

	private SwitchPreference log_adv;

	private SwitchPreference use_in_webview;

	private EditTextPreference use_in_ports;


    @SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
		prefs = getPreferenceManager().getSharedPreferences();
		addPreferencesFromResource(R.xml.preferences);
		enabled_module = (SwitchPreference)findPreference(Common.ENABLED_MODULE);
		log_net = (SwitchPreference)findPreference(Common.LOG_NET);
		log_adv = (SwitchPreference)findPreference(Common.LOG_ADV);
		use_in_socket = (SwitchPreference)findPreference(Common.USE_IN_SOCKET);
		use_in_webview = (SwitchPreference)findPreference(Common.USE_IN_WEBVIEW);
		use_in_headergroup = (SwitchPreference)findPreference(Common.USE_IN_HEADERGROUP);
		use_in_all = (SwitchPreference)findPreference(Common.USE_IN_ALL);
		list_apps = (MultiSelectListPreference)findPreference(Common.LIST_OF_APPS);
		list_apps2 = (MultiSelectListPreference)findPreference(Common.LIST_OF_APPS2);
		custom_header = (EditTextPreference)findPreference(Common.CUSTOM_HEADER);
		custom_request = (EditTextPreference)findPreference(Common.CUSTOM_REQUEST);
		use_in_ports = (EditTextPreference)findPreference(Common.USE_IN_PORTS);
		load2();
		new LoadApps().execute();
	}

	private void load1()
	{
		use_in_all.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference p1)
				{
					list_apps.setEnabled(!use_in_all.isChecked());
					list_apps2.setEnabled(use_in_all.isChecked());
					return true;
				}


			});
		list_apps.setEnabled(!use_in_all.isChecked());
		list_apps2.setEnabled(use_in_all.isChecked());
	}



	private void load2()
	{
		custom_header.setSummary(custom_header.getText());
		
		custom_header.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference p1, Object p2)
				{
					p1.setSummary((String)p2);
					return true;
				}
		});
		
	}

	public class LoadApps extends AsyncTask<Void, Void, Void>
	{
		List<CharSequence> appNames = new ArrayList<CharSequence>();
		List<CharSequence> packageNames = new ArrayList<CharSequence>();
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> packages = pm
		.getInstalledApplications(PackageManager.GET_META_DATA);

		@Override
		protected void onPreExecute()
		{
			list_apps.setEnabled(false);
			list_apps2.setEnabled(false);
		}

		@Override
		protected Void doInBackground(Void... arg0)
		{
			List<String[]> sortedApps = new ArrayList<String[]>();

			for (ApplicationInfo app : packages)
			{
				sortedApps.add(new String[] {
								   app.packageName,
								   app.loadLabel(getPackageManager())
								   .toString() });
			}

			Collections.sort(sortedApps, new Comparator<String[]>() {
					@Override
					public int compare(String[] entry1, String[] entry2)
					{
						return entry1[1].compareToIgnoreCase(entry2[1]);
					}
				});

			for (int i = 0; i < sortedApps.size(); i++)
			{
				appNames.add(sortedApps.get(i)[1]);
				packageNames.add(sortedApps.get(i)[0]);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			CharSequence[] appNamesList = appNames
				.toArray(new CharSequence[appNames.size()]);
			CharSequence[] packageNamesList = packageNames
				.toArray(new CharSequence[packageNames.size()]);

			list_apps.setEntries(appNamesList);
			list_apps.setEntryValues(packageNamesList);
			list_apps2.setEntries(appNamesList);
			list_apps2.setEntryValues(packageNamesList);
			load1();
		}
	}

}
