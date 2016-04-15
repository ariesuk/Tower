package com.example.a.tower;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

/**
 * Created by a on 2016/4/7.
 */

import lombok.Getter;
import lombok.Setter;

@Getter
public class Cell implements Parcelable {

    public static final String INVALID_PSC = "invalid";

    // Cell Specific Variables
    /**
     * Current Cell ID
     * Cell Identification code
     */
    @Setter
    private int cid;

    /**
     * Location Area Code
     */
    @Setter
    private int lac;

    /**
     * Mobile Country Code
     */
    @Setter
    private int mcc;

    /**
     * Mobile Network Code
     */
    @Setter
    private int mnc;

    /**
     * [dBm] RX signal "power"
     * Signal Strength Measurement (dBm)
     */
    @Setter
    private int dbm;

    /**
     * Primary Scrambling Code
     */
    private int psc;

    /**
     * Relative Signal Strength Indicator [dBm, asu etc.]
     * Received Signal Strength Indicator (RSSI)
     */
    private int rssi;

    /**
     * Timing Advance [LTE,GSM]
     * LTE Timing Advance or Integer.MAX_VALUE if unavailable
     */
    @Setter
    private int timingAdvance;

    /**
     * Cell-ID for [CDMA]
     *
     * CDMA System ID
     *
     * @return System ID or Integer.MAX_VALUE if not supported
     */
    @Setter
    private int sid;

    /**
     * Timestamp of current cell information
     */
    @Setter
    private long timestamp;

    // Tracked Cell Specific Variables
    /**
     * Current Network Type
     */
    @Setter
    private int netType;
    /**
     * Current ground speed in metres/second
     */
    @Setter
    private double speed;
    /**
     * Location accuracy in metres or 0.0 if unavailable
     */
    @Setter
    private double accuracy;
    @Setter
    private double bearing;

    /**
     * Longitude Geolocation
     */
    @Setter
    private double lon;

    /**
     * Latitude
     */
    @Setter
    private double lat;

    {
        cid = Integer.MAX_VALUE;
        lac = Integer.MAX_VALUE;
        mcc = Integer.MAX_VALUE;
        mnc = Integer.MAX_VALUE;
        dbm = Integer.MAX_VALUE;
        psc = Integer.MAX_VALUE;
        rssi = Integer.MAX_VALUE;
        timingAdvance = Integer.MAX_VALUE;
        sid = Integer.MAX_VALUE;
        netType = Integer.MAX_VALUE;
        lon = 0.0;
        lat = 0.0;
        speed = 0.0;
        accuracy = 0.0;
        bearing = 0.0;
    }

    public Cell() {
    }

    public Cell(int CID, int lac, int mcc, int mnc, int dbm, long timestamp) {
        super();
        this.cid = CID;
        this.lac = lac;
        this.mcc = mcc;
        this.mnc = mnc;
        this.dbm = dbm;
        this.rssi = Integer.MAX_VALUE;
        this.psc = Integer.MAX_VALUE;
        this.timestamp = timestamp;
        this.timingAdvance = Integer.MAX_VALUE;
        this.sid = Integer.MAX_VALUE;
        this.netType = Integer.MAX_VALUE;
        this.lon = 0.0;
        this.lat = 0.0;
        this.speed = 0.0;
        this.accuracy = 0.0;
        this.bearing = 0.0;
    }

    public Cell(int CID, int lac, int signal, int psc, int netType, boolean dbm) {
        this.cid = CID;
        this.lac = lac;
        this.mcc = Integer.MAX_VALUE;
        this.mnc = Integer.MAX_VALUE;

        if (dbm) {
            this.dbm = signal;
        } else {
            this.rssi = signal;
        }
        this.psc = psc;

        this.netType = netType;
        this.timingAdvance = Integer.MAX_VALUE;
        this.sid = Integer.MAX_VALUE;
        this.lon = 0.0;
        this.lat = 0.0;
        this.speed = 0.0;
        this.accuracy = 0.0;
        this.bearing = 0.0;
        this.timestamp = SystemClock.currentThreadTimeMillis();
    }

    public Cell(int cid, int lac, int mcc, int mnc, int dbm, double accuracy, double speed,
                double bearing, int netType, long timestamp) {
        this.cid = cid;
        this.lac = lac;
        this.mcc = mcc;
        this.mnc = mnc;
        this.dbm = dbm;
        this.rssi = Integer.MAX_VALUE;
        this.timingAdvance = Integer.MAX_VALUE;
        this.sid = Integer.MAX_VALUE;
        this.accuracy = accuracy;
        this.speed = speed;
        this.bearing = bearing;
        this.netType = netType;
        this.timestamp = timestamp;
    }

    /**
     * Set Primary Scrambling Code (PSC) of current Cell
     *
     * @param psc Primary Scrambling Code
     */
    public void setPsc(int psc) {
        if (psc == -1) {
            this.psc = Integer.MAX_VALUE;
        } else {
            this.psc = psc;
        }
    }

    /**
     * Radio Access Technology (RAT)
     *
     * Some places in the app refers to this as the Network Type.
     *
     * For our purposes, network types displayed to the user is referred to as RAT.
     *
     * @return Current cell's Radio Access Technology (e.g. UMTS, GSM) or null if not known
     */
    public String getRat() {
        return getRatFromInt(this.netType);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cid;
        result = prime * result + lac;
        result = prime * result + mcc;
        result = prime * result + mnc;
        if (psc != -1) {
            result = prime * result + psc;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (((Object) this).getClass() != obj.getClass()) {
            return false;
        }
        Cell other = (Cell) obj;
        if (this.psc != Integer.MAX_VALUE) {
            return this.cid == other.getCid() && this.lac == other.getLac() && this.mcc == other
                    .getMcc() && this.mnc == other.getMnc() && this.psc == other.getPsc();
        } else {
            return this.cid == other.getCid() && this.lac == other.getLac() && this.mcc == other
                    .getMcc() && this.mnc == other.getMnc();
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("cid - ").append(cid).append("\n");
        result.append("LAC - ").append(lac).append("\n");
        result.append("MCC - ").append(mcc).append("\n");
        result.append("MNC - ").append(mnc).append("\n");
        result.append("DBm - ").append(dbm).append("\n");
        result.append("PSC - ").append(validatePscValue(psc)).append("\n");
        result.append("Type - ").append(netType).append("\n");
        result.append("Lon - ").append(lon).append("\n");
        result.append("Lat - ").append(lat).append("\n");

        return result.toString();
    }

    public boolean isValid() {
        return this.getCid() != Integer.MAX_VALUE && this.getLac() != Integer.MAX_VALUE;
    }

    /**
     * Get a human-readable string of RAT/Network Type
     *
     * Frustratingly it looks like the app uses RAT & Network Type interchangably with both either
     * being an integer representation (TelephonyManager's constants) or a human-readable string.
     *
     * @param netType The integer representation of the network type, via TelephonyManager
     * @return Human-readable representation of network type (e.g. "EDGE", "LTE")
     */
    public static String getRatFromInt(int netType) {
        switch (netType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
            default:
                return String.valueOf(netType);
        }
    }

    public static String validatePscValue(Context c, String psc) {
        return validatePscValue(c, Integer.parseInt(psc));
    }

    /**
     * Validate PSC is in bounds, return i18n'd "Unknown" if invalid
     *
     * @see #validatePscValue(int)
     *
     * @param c Used for getString translations
     * @param psc
     * @return PSC or "Unknown "if invalid
     */
    public static String validatePscValue(Context c, int psc) {
        String s = validatePscValue(psc);
        if (s.equals(INVALID_PSC)) {
            return c.getString(R.string.unknown);
        }
        return s;
    }

    public static String validatePscValue(String psc) {
        return validatePscValue(Integer.parseInt(psc));
    }

    /**
     * Validate PSC is in bounds
     *
     * Database import stores cell's PSC as "666" if its absent in OCID. This method will return
     * "invalid" instead.
     *
     * Use this method to translate/i18n a cell's missing PSC value.
     *
     * @param psc
     * @return PSC or "invalid" untranslated string if invalid
     */
    public static String validatePscValue(int psc) {
        if (psc < 0 || psc > 511) {
            return INVALID_PSC;
        }
        return String.valueOf(psc);
    }

    // Parcelling
    public Cell(Parcel in) {
        String[] data = new String[15];

        in.readStringArray(data);
        cid = Integer.valueOf(data[0]);
        lac = Integer.valueOf(data[1]);
        mcc = Integer.valueOf(data[2]);
        mnc = Integer.valueOf(data[3]);
        dbm = Integer.valueOf(data[4]);
        psc = Integer.valueOf(data[5]);
        rssi = Integer.valueOf(data[6]);
        timingAdvance = Integer.valueOf(data[7]);
        sid = Integer.valueOf(data[8]);
        netType = Integer.valueOf(data[9]);
        lon = Double.valueOf(data[10]);
        lat = Double.valueOf(data[11]);
        speed = Double.valueOf(data[12]);
        accuracy = Double.valueOf(data[13]);
        bearing = Double.valueOf(data[14]);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(this.cid),
                String.valueOf(this.lac),
                String.valueOf(this.mcc),
                String.valueOf(this.mnc),
                String.valueOf(this.dbm),
                String.valueOf(this.psc),
                String.valueOf(this.rssi),
                String.valueOf(this.timingAdvance),
                String.valueOf(this.sid),
                String.valueOf(this.netType),
                String.valueOf(this.lon),
                String.valueOf(this.lat),
                String.valueOf(this.speed),
                String.valueOf(this.accuracy),
                String.valueOf(this.bearing)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Cell createFromParcel(Parcel in) {
            return new Cell(in);
        }

        public Cell[] newArray(int size) {
            return new Cell[size];
        }
    };
}
