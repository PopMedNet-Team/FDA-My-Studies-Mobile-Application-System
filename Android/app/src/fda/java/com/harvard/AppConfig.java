package com.harvard;

public class AppConfig {

    public static String PackageName = BuildConfig.APPLICATION_ID;
    public static String API_TOKEN = "<value of android.bundleid>:<value of android.apptoken>";
    static String GateWay = "gateway";
    static String Standalone = "standalone";
    public static String AppType = GateWay;
    public static String StudyId = "STUDY_ID_FROM_WCP";


    //AppId
    public static String APP_ID_KEY = "applicationId";
    public static String APP_ID_VALUE = "APP_ID_FROM_WCP";
    //OrgId
    public static String ORG_ID_KEY = "orgId";
    public static String ORG_ID_VALUE = "OrgName";
}
