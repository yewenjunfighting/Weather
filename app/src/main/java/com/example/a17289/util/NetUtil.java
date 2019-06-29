package com.example.a17289.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 17289 on 2019/6/29.
 */

public class NetUtil {
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;

    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo == null) {
            return NETWORK_NONE;
        }

        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_WIFI;
        }else if(nType == ConnectivityManager.TYPE_MOBILE) {
            return NETWORK_MOBILE;
        }
        return NETWORK_NONE;
    }
}
