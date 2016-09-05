/**
 * Created by root on 05.09.16.
 */
package com.camdrive;

import android.content.SharedPreferences;
import android.content.res.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by lex on 28.06.16.
 */
public class HTTPrequest {
    DataAuthentication dataAuthentication;
    String url;
    SharedPreferences sharedPreferences;

    public HTTPrequest(DataAuthentication da, String urlthis, SharedPreferences sharedPreferences){
        this.dataAuthentication=da;
        this.url=urlthis;
        this.sharedPreferences=sharedPreferences;
    }
    public String getJSONString(){
        String json_string = new String();
        final BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie sessionid_cookie = new BasicClientCookie("session", dataAuthentication.getSessionID());
        BasicClientCookie identity_cookie = new BasicClientCookie("identity", dataAuthentication.getIdentity());
        BasicClientCookie remember_code_cookie = new BasicClientCookie("remember_code", dataAuthentication.getRemember_code());
        cookieStore.addCookie(identity_cookie);
        cookieStore.addCookie(remember_code_cookie);
        cookieStore.addCookie(sessionid_cookie);
        // Create local HTTP context - to store cookies
        HttpContext localContext = new BasicHttpContext();
        // Bind custom cookie store to the local context
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGetJson = new HttpGet(url);
        HttpResponse httpResponse = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("email", dataAuthentication.getmMail()));
        nameValuePairs.add(new BasicNameValuePair("passw", dataAuthentication.getmPassword()));
        //            httpGetJson.(new UrlEncodedFormEntity(nameValuePairs));
        try {
            httpGetJson.setHeader("Cookie","session=" + dataAuthentication.getSessionID()+"; "+
            "remember_code="+dataAuthentication.remember_code+"; "+"identity="+dataAuthentication.identity);
            httpGetJson.setHeader("Host", "demo.officer24.ru");
            httpGetJson.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpGetJson.setHeader("Accept-Language", "ru-RU,en;q=0.5");
            httpGetJson.setHeader("X-Requested-With", "XMLHttpRequest");
            httpResponse = httpclient.execute(httpGetJson, localContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = httpResponse.getEntity();

        try {
            List<Cookie> cookies =cookieStore.getCookies();
            String session = null;
            String remember_code = null;
            String identity = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("session")) {
                    session=cookie.getValue();
                }
                if(cookie.getName().equals("remember_code")){
                    remember_code=cookie.getValue();
                }
                if(cookie.getName().equals("identity")){
                    identity=cookie.getValue();
                }
            }
            dataAuthentication.saveText(sharedPreferences, session,identity,remember_code,dataAuthentication.getmMail(),dataAuthentication.getmPassword());
            json_string = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json_string;
    }
}
