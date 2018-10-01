package com.archbrey.letters;

//import android.app.Activity;
//import android.app.Instrumentation;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
//import android.net.Uri;
import android.os.Handler;
//import android.provider.Settings;
import android.support.v4.view.MotionEventCompat;
//import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
//import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
//import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.archbrey.letters.Preferences.MainSettings;
import com.archbrey.letters.Preferences.SettingsActivity;

//import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.List;
import java.util.Locale;
//import java.util.Date;

public class ClockOut {

    public GlobalHolder global;

    public RelativeLayout clockoutBox;

    public TextView clockView;
    private TextView dateView;

    private TextView leftGestures;
    private TextView rightGestures;

    private Context mainContext;
    private static Resources rClockout;

    private float initialTouchX;
    private float initialTouchY;
    private static int screenHeight;

    private static Handler msghandler;
    private static Runnable lingerMsg;

    private static Handler popSelectHandler;
    private static Runnable triggerPopMenu;

    private static SimpleDateFormat dateFormat;
    private static SimpleDateFormat timeFormat;

    private final int TAP_CLOCK = 50;
    private final int UP_LEFTSIDE = 51;
    private final int UP_RIGHTSIDE = 52;
    private final int DN_LEFTSIDE = 53;
    private final int DN_RIGHTSIDE = 54;

    private PackageManager pmForClock;

    private static int swipeType;

    private static int touchOrientation; //0 for unset, 1 for vertical, 2 for horizontal

    private static AppItem[] allApps;
    static GestureShortcut[] gestureShortcuts;

    ClockOut() {
        global = new GlobalHolder();
        mainContext = global.getMainContext();
        pmForClock = global.getPackageManager();

        clockView = new TextView(mainContext);
        dateView = new TextView(mainContext);
        leftGestures = new TextView(mainContext);
        rightGestures = new TextView(mainContext);
        clockoutBox = new RelativeLayout(mainContext);
        rClockout = global.getResources();

        Locale loc = rClockout.getConfiguration().getLocales().get(0);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", loc);
        timeFormat = new SimpleDateFormat("HH:mm", loc);

        swipeType = 0;

        gestureShortcuts = new GestureShortcut[55];
        for (int inc = 50; inc <= 54; inc++) {
            gestureShortcuts[inc] = new GestureShortcut();
            switch (inc) {
                case TAP_CLOCK:
                    gestureShortcuts[inc].typeName = rClockout.getString(R.string.tap_clock);
                    break;
                case UP_LEFTSIDE:
                    gestureShortcuts[inc].typeName = String.valueOf(Character.toChars(8593))
                            + " "
                            + rClockout.getString(R.string.left_side);
                    break;
                case UP_RIGHTSIDE:
                    gestureShortcuts[inc].typeName = String.valueOf(Character.toChars(8593))
                            + " "
                            + rClockout.getString(R.string.right_side);
                    break;
                case DN_LEFTSIDE:
                    gestureShortcuts[inc].typeName = String.valueOf(Character.toChars(8595))
                            + " "
                            + rClockout.getString(R.string.left_side);
                    break;
                case DN_RIGHTSIDE:
                    gestureShortcuts[inc].typeName = String.valueOf(Character.toChars(8595))
                            + " "
                            + rClockout.getString(R.string.right_side);
                    break;
            }
        }

        msghandler = new Handler();
        lingerMsg = new Runnable() {
            public void run() {
                refreshClock();
            }
        };

        popSelectHandler = new Handler();
        triggerPopMenu = new Runnable() {
            public void run() {
                popSelectBox();
            }
        };
    }

    @SuppressLint("RtlHardcoded")
    public RelativeLayout DrawBox(RelativeLayout getTypeoutBox, Resources getR) {
        clockoutBox = getTypeoutBox;

        int clockTextSize = 80;
        int dateTextSize = 20;
        int gestureInfoSize = 12;

        int clockTop_margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200, getR.getDisplayMetrics()
        );

        int dateTop_margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getR.getDisplayMetrics()
        );

        int gestureInfo_margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getR.getDisplayMetrics()
        );

        clockView.setText("04:20");
        clockView.setGravity(Gravity.CENTER_HORIZONTAL);
        clockView.setTextSize(TypedValue.COMPLEX_UNIT_SP, clockTextSize);
        clockView.setTextColor(SettingsActivity.textColor);

        dateView.setText("07 Oct 2018");
        dateView.setGravity(Gravity.CENTER_HORIZONTAL);
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, dateTextSize);
        dateView.setTextColor(SettingsActivity.textColor);

        rightGestures.setText(" ");
        rightGestures.setGravity(Gravity.RIGHT);
        rightGestures.setTextSize(TypedValue.COMPLEX_UNIT_SP, gestureInfoSize);
        rightGestures.setTextColor(SettingsActivity.textColor);

        leftGestures.setText(" ");
        leftGestures.setGravity(Gravity.LEFT);
        leftGestures.setTextSize(TypedValue.COMPLEX_UNIT_SP, gestureInfoSize);
        leftGestures.setTextColor(SettingsActivity.textColor);

        clockView.setId(R.id.clockView);

        RelativeLayout.LayoutParams clockviewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        clockviewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        clockviewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        clockviewParams.setMargins(0, clockTop_margin, 0, 0);

        RelativeLayout.LayoutParams dateviewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        dateviewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        dateviewParams.addRule(RelativeLayout.BELOW, clockView.getId());
        dateviewParams.setMargins(0, dateTop_margin, 0, 0);

        RelativeLayout.LayoutParams leftGestureParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        leftGestureParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftGestureParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        leftGestureParams.setMargins(gestureInfo_margin, 0, 0, gestureInfo_margin);

        RelativeLayout.LayoutParams rightGestureParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        rightGestureParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightGestureParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rightGestureParams.setMargins(0, 0, gestureInfo_margin, gestureInfo_margin);

        clockoutBox.setBackgroundColor(SettingsActivity.clockBack);
        clockoutBox.addView(clockView, clockviewParams);
        clockoutBox.addView(dateView, dateviewParams);
        clockoutBox.addView(leftGestures, leftGestureParams);
        clockoutBox.addView(rightGestures, rightGestureParams);

        Display display = LaunchpadActivity.mainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        return clockoutBox;
    }

    void refreshClock() {
        Calendar calendar = Calendar.getInstance();

        clockView.setText(timeFormat.format(calendar.getTime()));
        dateView.setText(dateFormat.format(calendar.getTime()));
        dateView.append("\n");

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                dateView.append(rClockout.getString(R.string.sun));
                break;
            case Calendar.MONDAY:
                dateView.append(rClockout.getString(R.string.mon));
                break;
            case Calendar.TUESDAY:
                dateView.append(rClockout.getString(R.string.tue));
                break;
            case Calendar.WEDNESDAY:
                dateView.append(rClockout.getString(R.string.wed));
                break;
            case Calendar.THURSDAY:
                dateView.append(rClockout.getString(R.string.thu));
                break;
            case Calendar.FRIDAY:
                dateView.append(rClockout.getString(R.string.fri));
                break;
            case Calendar.SATURDAY:
                dateView.append(rClockout.getString(R.string.sat));
                break;
        }

        refreshGestures();
    }

    private void refreshGestures() {
        leftGestures.setText("\n");
        rightGestures.setText("\n");

        setGestureLabel(leftGestures, gestureShortcuts[UP_LEFTSIDE].shortcutLabel, 0);
        setGestureLabel(rightGestures, gestureShortcuts[UP_RIGHTSIDE].shortcutLabel, 1);
        setGestureLabel(leftGestures, gestureShortcuts[DN_LEFTSIDE].shortcutLabel, 2);
        setGestureLabel(rightGestures, gestureShortcuts[DN_RIGHTSIDE].shortcutLabel, 3);
    }

    private void setGestureLabel(TextView gestures, String label, int position) {
        String iconUp = String.valueOf(Character.toChars(8593));
        String iconDown = String.valueOf(Character.toChars(8595));
        String start = "";
        String end = "";

        if (label != null && label.length() > 1) {
            switch (position) {
                case 0: // Top left
                    start = iconUp;
                    end = label;
                    break;
                case 1: // Top right
                    start = label;
                    end = iconUp;
                    break;
                case 2: // Bottom left
                    gestures.append("\n");
                    start = iconDown;
                    end = label;
                    break;
                case 3: // Bottom right
                    gestures.append("\n");
                    start = label;
                    end = iconDown;
                    break;
                default:
                    break;
            }

            gestures.append(start + " " + end);
        }
    }

    public void setListener() {
        clockView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent launchIntent = pmForClock.getLaunchIntentForPackage(
                                gestureShortcuts[TAP_CLOCK].shortcutPackage
                        );

                        if (launchIntent != null) {
                            global.getMainContext().startActivity(launchIntent);
                        } else {
                            shortcutPicker(TAP_CLOCK);
                        }
                    }
                }
        );

        clockView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        shortcutPicker(TAP_CLOCK);
                        return true;
                    }
                }
        );

        dateView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent calendarIntent = new Intent(Intent.ACTION_MAIN);
                        calendarIntent.addCategory(Intent.CATEGORY_APP_CALENDAR);

                        try {
                            mainContext.startActivity(calendarIntent);
                        } catch (ActivityNotFoundException anfe) {
                            // Log.d(TAG, "Google Voice Search is not found");
                        }
                    }
                }
        );

        // Use the whole main screen instead of just the clock area to listen to gestures
        LaunchpadActivity.mainScreen.setOnTouchListener(
                new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {

                        float currentX = event.getRawX();
                        float currentY = event.getRawY();

                        int action = event.getActionMasked();
                        switch (action) {
                            case (MotionEvent.ACTION_DOWN):
                                initialTouchX = currentX;
                                initialTouchY = currentY;
                                msghandler.removeCallbacks(lingerMsg);
                                popSelectHandler.removeCallbacks(triggerPopMenu);
                                touchOrientation = 0; // Reset to unknown touch orientation
                                popSelectHandler.postDelayed(triggerPopMenu, 1500);
                                return true;
                            case (MotionEvent.ACTION_MOVE):
                                if ((touchOrientation == 0) || (touchOrientation == 1))
                                    determineVerticalGesture(currentY);
                                return true;
                            case (MotionEvent.ACTION_UP):
                                msghandler.postDelayed(lingerMsg, 500);
                                popSelectHandler.removeCallbacks(triggerPopMenu);
                                if (touchOrientation == 1) evaluateGesture();
                                return true;
                            default:
                                return true;
                        }
                    }
                }
        );
    }

    void RetrieveSavedShortcuts(Context getContext) {
        allApps = global.getAllAppItems();
        mainContext = getContext;
        DBHelper dbHandler = new DBHelper(mainContext);

        for (int inc = 50; inc <= 54; inc++) {
            String shortcutFound = dbHandler.RetrievePackage(inc);

            gestureShortcuts[inc].shortcutPackage = " ";
            gestureShortcuts[inc].shortcutLabel = " ";

            for (AppItem app : allApps) {
                if (shortcutFound.equals(app.pkgname)) {
                    gestureShortcuts[inc].shortcutLabel = app.label;
                    gestureShortcuts[inc].shortcutPackage = app.pkgname;
                    break;
                }
            }
        }

        dbHandler.close();
    }

    private void evaluateGesture() {
        GestureShortcut launchGesture = gestureShortcuts[swipeType];

        Intent launchIntent = pmForClock.getLaunchIntentForPackage(launchGesture.shortcutPackage);

        if (launchIntent != null) {
            global.getMainContext().startActivity(launchIntent);
        }
    }

    private void determineVerticalGesture(float getCurrentY) {
        float movementY;
        int boxWidth;
        int boxLocation[];
        boxLocation = new int[2];

        boxWidth = LaunchpadActivity.clockoutBox.getWidth();
        LaunchpadActivity.clockoutBox.getLocationOnScreen(boxLocation);

        movementY = (getCurrentY - initialTouchY) / screenHeight;

        if (Math.abs(movementY) >= 0.075) {
            popSelectHandler.removeCallbacks(triggerPopMenu);
            touchOrientation = 1; // Lock to vertical movement

            if ((initialTouchX - boxLocation[0]) < boxWidth / 2) {
                if (movementY < 0) swipeType = UP_LEFTSIDE;
                if (movementY > 0) swipeType = DN_LEFTSIDE;
            } else {
                if (movementY < 0) swipeType = UP_RIGHTSIDE;
                if (movementY > 0) swipeType = DN_RIGHTSIDE;
            }
        }
    }

    void shortcutPicker(int getSwipeType) {
        TypeOut.editMode = getSwipeType;

        LaunchpadActivity.hideDrawerAllApps = false;
        LaunchpadActivity.keypadBox.setVisibility(View.GONE);
        LaunchpadActivity.filterBox.setVisibility(View.INVISIBLE);
        LaunchpadActivity.clockoutBox.setVisibility(View.GONE);
        LaunchpadActivity.drawerBox.setVisibility(View.VISIBLE);

        TypeOut.typeoutBox.setVisibility(View.VISIBLE);
        TypeOut.findToggleView.setVisibility(View.GONE);
        TypeOut.editView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        TypeOut.typeoutView.setText(gestureShortcuts[getSwipeType].typeName);

        if (gestureShortcuts[getSwipeType].shortcutPackage.length() > 1) {
            TypeOut.editView.setVisibility(View.VISIBLE);
            TypeOut.typeoutView.append(" - ");
            TypeOut.typeoutView.append(gestureShortcuts[getSwipeType].shortcutLabel);
            TypeOut.editView.setText("  "); // X button margin
            TypeOut.editView.append(String.valueOf(Character.toChars(215))); // X button
            TypeOut.editView.append(" "); // X button margin
        } else {
            TypeOut.typeoutView.append(" - " + rClockout.getString(R.string.unassigned));
            TypeOut.editView.setText(" "); // X button not needed if unassigned
        }

        allApps = global.getAllAppItems();
        LaunchpadActivity.drawDrawerBox.DrawBox(allApps);

        LaunchpadActivity.appGridView.setOnItemClickListener(new ClickSelectListener(getSwipeType));
        LaunchpadActivity.appGridView.setOnItemLongClickListener(new MenuLongClickNuller());
    }

    public class MenuLongClickNuller implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View viewItem, int pos, long l) {
            // Do nothing
            return true;
        }
    }

    private class ClickSelectListener implements AdapterView.OnItemClickListener {
        int clickSwipeType;

        private ClickSelectListener(int getSwipeType) {
            clickSwipeType = getSwipeType;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            gestureShortcuts[clickSwipeType].shortcutPackage = allApps[position].pkgname;
            gestureShortcuts[clickSwipeType].shortcutLabel = allApps[position].label;
            shortcutPicker(clickSwipeType);

            DBHelper dbHandler = new DBHelper(mainContext);
            dbHandler.AssignShorcut(clickSwipeType, allApps[position].pkgname);
            dbHandler.close();
        }
    }

    private void popSelectBox() {
        AppItem[] menuItems;
        LaunchpadActivity.hideDrawerAllApps = false;

        LaunchpadActivity.keypadBox.setVisibility(View.GONE);
        LaunchpadActivity.filterBox.setVisibility(View.INVISIBLE);
        LaunchpadActivity.clockoutBox.setVisibility(View.GONE);
        LaunchpadActivity.drawerBox.setVisibility(View.VISIBLE);
        TypeOut.typeoutBox.setVisibility(View.VISIBLE);
        TypeOut.findToggleView.setVisibility(View.GONE);
        TypeOut.editView.setVisibility(View.GONE);
        TypeOut.typeoutView.setText(rClockout.getString(R.string.select_gesture));

        menuItems = new AppItem[4];

        for (int menuInc = 0; menuInc < menuItems.length; menuInc++) {
            menuItems[menuInc] = new AppItem();
        }

        menuItems[0].label = gestureShortcuts[UP_LEFTSIDE].typeName;
        menuItems[1].label = gestureShortcuts[UP_RIGHTSIDE].typeName;
        menuItems[2].label = gestureShortcuts[DN_LEFTSIDE].typeName;
        menuItems[3].label = gestureShortcuts[DN_RIGHTSIDE].typeName;

        LaunchpadActivity.drawDrawerBox.DrawBox(menuItems);
        setPopMenuListener();
    }

    private void setPopMenuListener() {
        LaunchpadActivity.appGridView.setOnItemClickListener(new PopMenuClickListener());
        LaunchpadActivity.appGridView.setOnItemLongClickListener(new MenuLongClickNuller());
    }

    private class PopMenuClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            switch (position) {
                case 0:
                    shortcutPicker(UP_LEFTSIDE);
                    break;
                case 1:
                    shortcutPicker(UP_RIGHTSIDE);
                    break;
                case 2:
                    shortcutPicker(DN_LEFTSIDE);
                    break;
                case 3:
                    shortcutPicker(DN_RIGHTSIDE);
                    break;
                default:
                    break;
            }
        }
    }
}
