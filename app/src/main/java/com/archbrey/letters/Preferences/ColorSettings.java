package com.archbrey.letters.Preferences;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.archbrey.letters.R;

public class ColorSettings {
    private GridView mainMenuBox;
    private Context settingsContext;
    private static Resources rMainSettings;

    public GridView DrawBox(GridView getgridBox, Context c, Resources getR) {
        SettingsActivity.infoView.setText(getR.getString(R.string.color_scheme));
        SettingsActivity.menuArea = getR.getString(R.string.color_scheme);
        SettingsActivity.menuLevel = 1;
        mainMenuBox = getgridBox;
        settingsContext = c;
        rMainSettings = getR;

        // Set the name of the background colors displayed on the menu
        String[] menuItems = getR.getStringArray(R.array.background_names);

        new SettingsDrawer(settingsContext, mainMenuBox, menuItems);
        setListener();

        return mainMenuBox;
    } //public LinearLayout DrawBox ()


    public void setListener() {
        mainMenuBox.setOnItemClickListener(new MenuClickListener());
    }


    private class MenuClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            // TODO: there has to be a cleaner approach to this.
            //noinspection SimplifiableIfStatement
            switch (position) {
                case 0:
                    setColorScheme(R.color.white, R.color.Black_0, R.color.Black_2, "dark1");
                    break;
                case 1:
                    setColorScheme(R.color.white, R.color.Black_2, R.color.Black_4, "dark2");
                    break;
                case 2:
                    setColorScheme(R.color.white, R.color.Black_4, R.color.Black_6, "dark3");
                    break;
                case 3:
                    setColorScheme(R.color.white, R.color.Black_6, R.color.Black_8, "dark4");
                    break;
                case 4:
                    setColorScheme(R.color.white, R.color.Black_8, R.color.Black_A, "dark5");
                    break;
                case 5:
                    setColorScheme(R.color.white, R.color.Black_A, R.color.Black_D, "dark6");
                    break;
                case 6:
                    setColorScheme(R.color.white, R.color.Black_D, R.color.Black_F, "dark7");
                    break;
                case 7:
                    setColorScheme(R.color.black, R.color.White_0, R.color.White_2, "light1");
                    break;
                case 8:
                    setColorScheme(R.color.black, R.color.White_2, R.color.White_4, "light2");
                    break;
                case 9:
                    setColorScheme(R.color.black, R.color.White_4, R.color.White_6, "light3");
                    break;
                case 10:
                    setColorScheme(R.color.black, R.color.White_6, R.color.White_8, "light4");
                    break;
                case 11:
                    setColorScheme(R.color.black, R.color.White_8, R.color.White_A, "light5");
                    break;
                case 12:
                    setColorScheme(R.color.black, R.color.White_A, R.color.White_D, "light6");
                    break;
                case 13:
                    setColorScheme(R.color.black, R.color.White_D, R.color.White_F, "light7");
                    break;
                default:
                    break;
            } //switch (position)

            DrawBox(mainMenuBox, settingsContext, rMainSettings);
        }// public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)


        private void setColorScheme(int textColor, int backColor, int backerColor, String schemeName) {
            // Set the new colors
            SettingsActivity.textColor = rMainSettings.getColor(textColor);
            SettingsActivity.backColor = rMainSettings.getColor(backColor);
            SettingsActivity.backerColor = rMainSettings.getColor(backerColor);
            SettingsActivity.prefsEditor.putString("colorscheme", schemeName);

            // Store the settings
            SettingsActivity.SettingChanged = true;
            SettingsActivity.prefsEditor.commit();

            // Change the colors on the current view
            SettingsActivity.infoBox.setBackgroundColor(backerColor);
            SettingsActivity.infoView.setTextColor(textColor);
        }
    } //private class MenuClickListener implements AdapterView.OnItemClickListener
} //public class ColorSettings
