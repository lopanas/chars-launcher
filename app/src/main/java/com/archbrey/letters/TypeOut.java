package com.archbrey.letters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archbrey.letters.Preferences.SettingsActivity;

public class TypeOut {
    public static RelativeLayout typeoutBox;

    public static TextView typeoutView;
    public static TextView editView;
    static TextView findToggleView;

    private static Context mainContext;

    private GlobalHolder global;

    static int TextSize = 24;
    static boolean findStatus;
    static int editMode;

    private static KeypadShortcuts keypadShortcutsHandle;

    TypeOut() {
        global = new GlobalHolder();
    }

    public RelativeLayout getLayout() {
        return typeoutBox;
    }

    public TextView getTypeoutView() {
        return typeoutView;
    }

    boolean getFindStatus() {
        return findStatus;
    }

    public void setFindStatus(boolean getFindStatus) {
        SideButton delButton;

        int findToggleTextSize = 24;
        global = new GlobalHolder();
        delButton = global.getDelButton();

        findStatus = getFindStatus;

        if (!findStatus) {
            findToggleView.setText(mainContext.getString(R.string.find));
            findToggleTextSize = 15;
            //  drawerBox.setVisibility(View.INVISIBLE);
            //  typeoutBox.setVisibility(View.INVISIBLE);
            //  if (!LaunchpadActivity.isSetAsHome)
            //      delButton.Key.setText(String.valueOf(Character.toChars(8595))); //"down" button
            //  else

            displayButton(delButton.Key, "arrowUp");
        }

        findToggleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, findToggleTextSize);
    }

    private void toggleFindStatus() {
        //SideButton delButton;
        int findToggleTextSize;
        View drawerBox = global.getDrawerBox();
        // delButton = global.getDelButton();

        if (findStatus) {
            findStatus = false;
            findToggleView.setText(mainContext.getString(R.string.find));
            findToggleTextSize = 15;
            drawerBox.setVisibility(View.INVISIBLE);
            typeoutBox.setVisibility(View.GONE);
            LaunchpadActivity.clockoutBox.setVisibility(View.VISIBLE);

            //  if (!LaunchpadActivity.isSetAsHome)
            //      delButton.Key.setText(String.valueOf(Character.toChars(8595))); // "down" button
            //  else

            displayButton(DrawKeypadBox.delButton.Key, "arrowUp");
            LaunchpadActivity.prefsEditor.putBoolean("findStatus", false);
        } else {
            findStatus = true;
            global.setFindString(""); // Delete currently viewed letter before searching
            typeoutView.setText("");

            findToggleTextSize = 24;
            displayButton(findToggleView, "close");
            displayButton(DrawKeypadBox.delButton.Key, "arrowLeft");
            editView.setVisibility(View.GONE);
            LaunchpadActivity.prefsEditor.putBoolean("findStatus", true);
        }

        LaunchpadActivity.prefsEditor.commit();
        findToggleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, findToggleTextSize);
    }

    public RelativeLayout DrawBox(RelativeLayout getTypeoutBox, Context c, Resources getR) {
        typeoutBox = getTypeoutBox;
        mainContext = c;
//        Resources rTypeout = getR;
//
//        int typeoutTextSize;
//
//        int horizontal_margin = (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, 5, getR.getDisplayMetrics()
//        );

        int vertical_margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getR.getDisplayMetrics()
        );

        int touchview_width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, getR.getDisplayMetrics()
        );

        int findToggleTextSize = 15;
        typeoutView = new TextView(mainContext);
        typeoutView.setText(" ");
        typeoutView.setGravity(Gravity.CENTER_HORIZONTAL);
        typeoutView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize);
        typeoutView.setTextColor(SettingsActivity.textColor);

        editView = new TextView(mainContext);
        displayButton(editView, "plusMinus");

        editView.setGravity(Gravity.CENTER_HORIZONTAL);
        editView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize);
        editView.setTextColor(SettingsActivity.textColor);

        findToggleView = new TextView(mainContext);

        if (findStatus) displayButton(findToggleView, "close");
        else findToggleView.setText(mainContext.getString(R.string.find));

        findToggleView.setGravity(Gravity.CENTER_HORIZONTAL);
        findToggleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, findToggleTextSize);
        findToggleView.setTextColor(SettingsActivity.textColor);

        RelativeLayout.LayoutParams typeoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        typeoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        typeoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        typeoutParams.setMargins(0, vertical_margin, 0, vertical_margin);

        RelativeLayout.LayoutParams editViewParams = new RelativeLayout.LayoutParams(
                touchview_width,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        editViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        editViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        //   editViewParams.setMargins(0, 0, horizontal_margin, 0);

        RelativeLayout.LayoutParams findToggleParams = new RelativeLayout.LayoutParams(
                touchview_width,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        findToggleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        findToggleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        //  findToggleParams.setMargins(horizontal_margin, vertical_margin, 0, vertical_margin);

        typeoutBox = new RelativeLayout(mainContext);
        typeoutBox.setBackgroundColor(SettingsActivity.backerColor);

        typeoutBox.addView(editView, editViewParams);
        typeoutBox.addView(findToggleView, findToggleParams);
        typeoutBox.addView(typeoutView, typeoutParams);

        editView.setVisibility(View.GONE);
        return typeoutBox;
    }

    public void setListener() {
        findToggleView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleFindStatus();
            }
        });

        editView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // SetAppList setApps;
                // setApps = new SetAppList();
                // toggleFindStatus();
                SelectMode();
            }
        });

        typeoutView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PackageManager pmForListener;
                pmForListener = global.getPackageManager();
                int selected = KeypadTouchListener.SelectedKeyButton;
                String shortcutPackage = DrawKeypadBox.keypadButton[selected].ShortcutPackage;

                // Check if keypad has assigned shortcut
                if ((shortcutPackage.length() > 1) && editMode == 1) {
                    Intent launchIntent = pmForListener.getLaunchIntentForPackage(shortcutPackage);

                    if (launchIntent.resolveActivity(pmForListener) != null) {
                        global.getMainContext().startActivity(launchIntent);
                        LaunchpadActivity.drawerBox.setVisibility(View.GONE);
                        typeoutBox.setVisibility(View.GONE);
                        LaunchpadActivity.clockoutBox.setVisibility(View.VISIBLE);
                    }

                    //  AppItem launched;
                    //   launched = new AppItem();
                    //   launched.pkgname = DrawKeypadBox.keypadButton[KeypadTouchListener.SelectedKeyButton].ShortcutPackage;
                    //   launched.label = DrawKeypadBox.keypadButton[KeypadTouchListener.SelectedKeyButton].ShortcutLabel;
                    //   launched.name = "blank";
                    //   new GetAppList().addRecentApp(launched);
                }
            }
        });
    }

    private void SelectMode() {
        if (TypeOut.editMode < 10) {
            LaunchpadActivity.drawerBox.setVisibility(View.VISIBLE);
            LaunchpadActivity.keypadBox.setVisibility(View.GONE);
            LaunchpadActivity.filterBox.setVisibility(View.INVISIBLE);
            TypeOut.findToggleView.setVisibility(View.GONE);
        }

        if (TypeOut.editMode >= 50) {
            // ShortcutSelect();
            ClockOut.gestureShortcuts[TypeOut.editMode].shortcutLabel = " ";
            ClockOut.gestureShortcuts[TypeOut.editMode].shortcutPackage = " ";
            deleteShortcut(TypeOut.editMode);
            LaunchpadActivity.clockoutHandle.shortcutPicker(TypeOut.editMode);
        }

        switch (TypeOut.editMode) {
            case 1:
                editMode = 11;
                keypadShortcutsHandle = new KeypadShortcuts();
                keypadShortcutsHandle.DrawBox(mainContext);
                break;
            case 2:
                editMode = 12;
                FilterEdit filterEditHandle = new FilterEdit();
                filterEditHandle.DrawBox(mainContext);
                break;
            case 11:
                int keyPosition = KeypadTouchListener.SelectedKeyButton;
                DrawKeypadBox.keypadButton[keyPosition].ShortcutPackage = " ";
                DrawKeypadBox.keypadButton[keyPosition].ShortcutLabel = " ";
                deleteShortcut(keyPosition);
                keypadShortcutsHandle.DrawBox(mainContext);
                break;
            case 12:
                editMode = 17;
                filterEditHandle = new FilterEdit();
                filterEditHandle.DrawBox(mainContext);
                break;
            case 17:
                editMode = 12;
                filterEditHandle = new FilterEdit();
                filterEditHandle.DrawBox(mainContext);
                break;
            default:
                break;
        }
    }

    private void deleteShortcut(int shortcut) {
        DBHelper dbHandlers = new DBHelper(mainContext);
        dbHandlers.DeleteShorcut(shortcut);
        dbHandlers.close();
    }

    // TODO: this should be on its own class
    private void displayButton(TextView textView, String iconName) {
        displayButton(textView, iconName, true);
    }

    private void displayButton(TextView textView, String iconName, Boolean margins) {
        String icon;

        switch (iconName) {
            case "arrowUp":
                icon = String.valueOf(Character.toChars(8593)); // ↑
                break;
            case "arrowDown":
                icon = String.valueOf(Character.toChars(8595)); // ↓
                break;
            case "arrowLeft":
                icon = String.valueOf(Character.toChars(8656)); // ⇐
                break;
            case "close":
                icon = String.valueOf(Character.toChars(215)); // ×
                break;
            case "plusMinus":
                icon = String.valueOf(Character.toChars(177)); // ±
                break;
            default:
                icon = iconName;
                break;
        }

        if (margins) textView.setText(String.format("  %s  ", icon));
        else textView.setText(icon);
    }
}
