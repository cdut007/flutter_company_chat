package com.ultralinked.voip.api;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by LAIWENJIE$ on 2015/11/19.
 */
public class LocationMessage extends CustomMessage {

    public static final String TAG = "LocationMessage";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String ACCURACY = "accuracy";

    public static final String TITLE = "title";

    public static final String SUBTITLE = "subTitle";

    @Override
    public void parseData(JSONObject json) throws JSONException {
        super.parseData(json);
        parseLocation(getJsonData());//data
    }

    private void parseLocation(JSONObject data) throws JSONException {

        latitude = data.getDouble("latitude");
        longitude = data.getDouble("longitude");

        String body = data.getString("body");

        if (!TextUtils.isEmpty(body)) {
            int locStart = body.indexOf("http://maps.google.com/?q=");
            if (locStart >= 0) {
                locStart += "http://maps.google.com/?q=".length();
                String loc = body.substring(locStart);
                if (TextUtils.isEmpty(loc))
                    return;
                int locEnd = loc.indexOf("&");
                if (locEnd >= 0)
                    loc = loc.substring(0, locEnd);
                int postion = loc.indexOf(",");
                if (postion > 0) {
                    String laAndLon[] = loc.split(",");
                    try {
                        latitude = Double.parseDouble(laAndLon[0]);
                        longitude = Double.parseDouble(laAndLon[1]);
                    } catch (Exception e) {
                        Log.i(
                                TAG,
                                (new StringBuilder())
                                        .append("parse to double exception:")
                                        .append(e.getMessage()).toString());
                        latitude = 0.0D;
                        longitude = 0.0D;
                    }
                }
            }
            subTitle = getUrlParam(body, "&subTitle=");
            title = getUrlParam(body, "&title=");
        }
    }

    private String getUrlParam(String url, String name) {
        int isubTitle = url.indexOf(name);
        if (isubTitle >= 0) {
            isubTitle += name.length();
            String desc = url.substring(isubTitle);
            int iSpitChat = desc.indexOf("&");
            if (iSpitChat > 0)
                desc = desc.substring(0, iSpitChat);
            desc = desc.replace("null", "");
            return desc;
        } else {
            return null;
        }
    }

    protected void setLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String[] getLocation() {
        String result[] = new String[2];
        result[0] = Double.toString(longitude);
        result[1] = Double.toString(latitude);
        return result;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getLocationDesc() {
        return subTitle;
    }


    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    private static final long serialVersionUID = -6121254108866010288L;
    private double longitude;
    private double latitude;
    private String title;
    private String subTitle;

    protected static String getTextLocationJson(HashMap<String, String> msg, Options options) {
        JSONObject jsonObject = new JSONObject();
        StringBuilder locationInfo = new StringBuilder();
        String longitude = msg.get(LONGITUDE);
        String latitude = msg.get(LATITUDE);
        String accuracy = msg.get(ACCURACY);
        locationInfo.append("http://maps.google.com/?q=");
        locationInfo.append(latitude);
        locationInfo.append(",");
        locationInfo.append(longitude);
        locationInfo.append("&version=1.0");
        if (!TextUtils.isEmpty(msg.get(TITLE))) {
            locationInfo.append("&title=");
            locationInfo.append(msg.get(TITLE));
        }
        if (!TextUtils.isEmpty(msg.get(SUBTITLE))) {
            locationInfo.append("&subTitle=");
            locationInfo.append(msg.get(SUBTITLE));
        }
        try {
            if (!TextUtils.isEmpty(locationInfo))
                jsonObject.put("body", locationInfo);
            if (!TextUtils.isEmpty(longitude))
                jsonObject.put("longitude", longitude);
            if (!TextUtils.isEmpty(latitude))
                jsonObject.put("latitude", latitude);
            if (!TextUtils.isEmpty(accuracy))
                jsonObject.put("accuracy", accuracy);
            return getFormatMessageJson(TAG,jsonObject, Message.MESSAGE_TYPE_LOCATION, options);
        } catch (JSONException e) {
            Log.i(
                    TAG,
                    (new StringBuilder())
                            .append("getTextLocationJson error e:")
                            .append(e.getMessage()).toString());
        }
        return null;
    }


}
