package com.archbrey.letters.Preferences;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.archbrey.letters.R;

public class MainSettings {
    public static GridView mainMenuBox;
    private static Context settingsContext;
    private static Resources rMainSettings;

    public MainSettings() {
    }

    public GridView DrawBox(GridView getGridBox, Context c, Resources getR) {
        String label = getR.getString(R.string.settings);
        SettingsActivity.infoView.setText(label);
        SettingsActivity.menuArea = label;

        mainMenuBox = getGridBox;
        settingsContext = c;
        rMainSettings = getR;

        String[] menuItems = getR.getStringArray(R.array.setting_items);

        new SettingsDrawer(settingsContext, mainMenuBox, menuItems);
        setListener();

        return mainMenuBox;
    }

    public void setListener() {
        mainMenuBox.setOnItemClickListener(new MenuClickListener());
        mainMenuBox.setOnItemLongClickListener(new MenuLongClickNuller());
    }

    private class MenuClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            TextView textView = (TextView) ((LinearLayout) view).getChildAt(0);

            String label = textView.getText().toString();
            SettingsActivity.infoView.setText(label);
            SettingsActivity.menuArea = label;

            // The order of the cases need to match the order of the string-array 'setting_items'
            switch (position) {
                case 0:
                    ColorSettings colorSettingsHandle = new ColorSettings();
                    colorSettingsHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                case 1:
                    ClockSettings clockSettingsHandle = new ClockSettings();
                    clockSettingsHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                case 2:
                    ColumnSettings columnSettingsHandle = new ColumnSettings();
                    columnSettingsHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                case 3:
                    DrawerTextSize textSizeHandle = new DrawerTextSize();
                    textSizeHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                case 4:
                    FilterEnable filterEnableHandle = new FilterEnable();
                    filterEnableHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                case 5:
                    FilterLabels labelsHandle = new FilterLabels();
                    labelsHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                case 6:
                    HeightSettings heightHandle = new HeightSettings();
                    heightHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                case 7:
                    LandscapeHandedness handednessHandle = new LandscapeHandedness();
                    handednessHandle.DrawBox(mainMenuBox, settingsContext, rMainSettings);
                    break;
                default:
                    break;
            }
        }
    }

    public class MenuLongClickNuller implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View viewItem, int pos, long l) {
            // Do nothing
            return true;
        }
    }
}
