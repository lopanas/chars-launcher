package com.archbrey.letters;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class GetAppList {
    // AppItem[] appItem;
    private GlobalHolder global;
    private static AppItem[] recentApps;
    private static AppItem[] filteredApps;
    static int recentAppCount;
    static AppItem[] allAppItems;

    GetAppList() {
        global = new GlobalHolder();
    }

    public void initialize() {
        filteredApps = new AppItem[1];
        recentApps = new AppItem[10];
        recentAppCount = 0;
    }

    public AppItem[] addRecentApp(AppItem getAppItem) {
        int stopPosition;

        if (recentAppCount < 10) recentAppCount++;  // Limit number of recent apps to 10

        // Set default stop position if no existing apps exist
        stopPosition = recentAppCount - 1;
        // Determine first if app already exists in list
        for (int inc = 1; inc < recentAppCount; inc++) {
            if (recentApps[inc - 1].pkgname.equals(getAppItem.pkgname)) {
                stopPosition = inc - 1;
                recentAppCount--;
            } // If (recentApps[inc].pkgname.equals(getAppItem.pkgname))
        }

        // Insert most recent app at the start position and adjust the other apps on the list
        System.arraycopy(recentApps, 0, recentApps, 1, stopPosition);

        recentApps[0] = getAppItem;
        return recentApps;
    }

    AppItem[] getFilteredApps() {
        return filteredApps;
    }

    AppItem[] getRecentApps() {
        return recentApps;
    }

    AppItem[] all_appItems(PackageManager mainPkgMgr) {
        PackageManager PkgMgr;
        PkgMgr = mainPkgMgr;

        final Intent pkgIntent = new Intent(Intent.ACTION_MAIN, null);
        pkgIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appPkgList = PkgMgr.queryIntentActivities(pkgIntent, 0);

        allAppItems = new AppItem[appPkgList.size()];

        for (int inc = 0; inc < appPkgList.size(); inc++) {
            allAppItems[inc] = new AppItem();
            allAppItems[inc].pkgname = appPkgList.get(inc).activityInfo.packageName;
            allAppItems[inc].label = appPkgList.get(inc).loadLabel(PkgMgr).toString();
            allAppItems[inc].name = appPkgList.get(inc).activityInfo.name;
        }

        new SortApps().exchange_sort(allAppItems);

        global.setAllAppItems(allAppItems);

        return allAppItems;
    }

    int filterByFirstChar(String Search) {
        int ArraySize = allAppItems.length;
        int filtercount = 0;
        String MatchValue;

        AppItem[] filteredItem;
        // AppItem[] resultItem;
        filteredItem = new AppItem[ArraySize];

        for (AppItem allAppItem : allAppItems) {
            MatchValue = String.valueOf(allAppItem.label.charAt(0)).toLowerCase();
            if (Search.toLowerCase().equals(MatchValue)) {
                filteredItem[filtercount] = new AppItem();
                filteredItem[filtercount] = allAppItem;
                filtercount++;
            }
        }

        filteredApps = new AppItem[filtercount];
        for (int inc = 0; inc < filtercount; inc++) {
            filteredApps[inc] = new AppItem();
            filteredApps[inc] = filteredItem[inc];
        }

        return filtercount;
    }

    int filterByString(String Search) {
        int ArraySize = allAppItems.length;
        int filtercount = 0;

        AppItem[] filteredItem;
        filteredItem = new AppItem[ArraySize];

        for (AppItem allAppItem : allAppItems) {
            if (allAppItem.label.toLowerCase().contains(Search.toLowerCase())) {
                filteredItem[filtercount] = new AppItem();
                filteredItem[filtercount] = allAppItem;
                filtercount++;
            }
        }

        filteredApps = new AppItem[filtercount];
        for (int inc = 0; inc < filtercount; inc++) {
            filteredApps[inc] = new AppItem();
            filteredApps[inc] = filteredItem[inc];
        }

        return filtercount;
    }
}
