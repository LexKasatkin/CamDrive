package com.camdrive;

/**
 * Created by root on 05.09.16.
 */
import android.content.SharedPreferences;

/**
 * Created by lex on 28.06.16.
 */
public class DataAuthentication{
    private String SESSIONID = "SESSION";
    private String PASSWORD = "PASSWORD";
    private String EMAIL = "EMAIL";
    String REMEMBER_CODE="REMEMBER_CODE";
    String IDENTITY="IDENTITY";
    private String mMail;
    private String mPassword;
    private String sessionID;
    String identity;
    String remember_code;
    void DataAuthentication(){

    }

    void loadDataAuthentication(SharedPreferences sPref) {
        mPassword = sPref.getString(PASSWORD, null);
        mMail = sPref.getString(EMAIL, null);
        sessionID = sPref.getString(SESSIONID, null);
        identity=sPref.getString(IDENTITY,null);
        remember_code=sPref.getString(REMEMBER_CODE, null);
    }


    public String getmPassword() {
        return mPassword;
    }

    public String getmMail() {
        return mMail;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public void setmMail(String mMail) {
        this.mMail = mMail;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setRemember_code(String remember_code) {
        this.remember_code = remember_code;
    }

    public String getIdentity() {
        return identity;
    }

    public String getRemember_code() {
        return remember_code;
    }

    void saveText(SharedPreferences sharedPreferences, String sessionid, String identity,
                  String remember_code, String email, String passw) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(SESSIONID, sessionid);
        ed.putString(EMAIL, email);
        ed.putString(PASSWORD, passw);
        ed.putString (IDENTITY, identity);
        ed.putString (REMEMBER_CODE, remember_code);
        ed.commit();
    }
}
