package hu.kovand.sketch3d.fragment;

import hu.kovand.sketch3d.R;
import hu.kovand.sketch3d.R.xml;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
