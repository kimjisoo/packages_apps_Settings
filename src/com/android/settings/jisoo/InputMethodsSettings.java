/*
 * Copyright (C) 2014 Slimroms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.jisoo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class InputMethodsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "KeyboardInputSettings";

    private static final String PREF_DISABLE_FULLSCREEN_KEYBOARD = "disable_fullscreen_keyboard";
    private static final String KEY_IME_SWITCHER = "status_bar_ime_switcher";
    private static final String VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";
    private static final String SHOW_ENTER_KEY = "show_enter_key";

    private SwitchPreference mDisableFullscreenKeyboard;
    private SwitchPreference mStatusBarImeSwitcher;
    private ListPreference mVolumeKeyCursorControl;
    private SwitchPreference mShowEnterKey;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.INPUTMETHOD_LANGUAGE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.input_methods_settings);

        mDisableFullscreenKeyboard =
            (SwitchPreference) findPreference(PREF_DISABLE_FULLSCREEN_KEYBOARD);
        mDisableFullscreenKeyboard.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.DISABLE_FULLSCREEN_KEYBOARD, 0) == 1);
        mDisableFullscreenKeyboard.setOnPreferenceChangeListener(this);

        // Enable or disable mStatusBarImeSwitcher based on boolean value: config_show_cmIMESwitcher
        final Preference keyImeSwitcherPref = findPreference(KEY_IME_SWITCHER);
        if (keyImeSwitcherPref != null) {
            if (!getResources().getBoolean(com.android.internal.R.bool.config_show_IMESwitcher)) {
                getPreferenceScreen().removePreference(keyImeSwitcherPref);
            } else {
                mStatusBarImeSwitcher = (SwitchPreference) keyImeSwitcherPref;
                mStatusBarImeSwitcher.setOnPreferenceChangeListener(this);
            }
        }

        mVolumeKeyCursorControl = (ListPreference) findPreference(VOLUME_KEY_CURSOR_CONTROL);
        if(mVolumeKeyCursorControl != null) {
            mVolumeKeyCursorControl.setValue(Integer.toString(Settings.System.getInt(
                    getContentResolver(), Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0)));
            mVolumeKeyCursorControl.setSummary(mVolumeKeyCursorControl.getEntry());
            mVolumeKeyCursorControl.setOnPreferenceChangeListener(this);
        }

        mShowEnterKey = (SwitchPreference) findPreference(SHOW_ENTER_KEY);
        mShowEnterKey.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.FORMAL_TEXT_INPUT, 0) == 1);
        mShowEnterKey.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mStatusBarImeSwitcher != null) {
            mStatusBarImeSwitcher.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_IME_SWITCHER, 1) != 0);
        }

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mVolumeKeyCursorControl) {
            String volumeKeyCursorControl = (String) objValue;
            int val = Integer.parseInt(volumeKeyCursorControl);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL, val);
            int index = mVolumeKeyCursorControl.findIndexOfValue(volumeKeyCursorControl);
            mVolumeKeyCursorControl.setSummary(mVolumeKeyCursorControl.getEntries()[index]);
            return true;
        } else if (preference == mDisableFullscreenKeyboard) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DISABLE_FULLSCREEN_KEYBOARD,  (Boolean) objValue ? 1 : 0);
            return true;
        } else if (preference == mStatusBarImeSwitcher) {
            Settings.System.putInt(getContentResolver(),
                Settings.System.STATUS_BAR_IME_SWITCHER, (Boolean) objValue ? 1 : 0);
            return true;
        } else if (preference == mShowEnterKey) {
            Settings.System.putInt(getContentResolver(),
                Settings.System.FORMAL_TEXT_INPUT, (Boolean) objValue ? 1 : 0);
            return true;
        }
        return false;
    }
}
