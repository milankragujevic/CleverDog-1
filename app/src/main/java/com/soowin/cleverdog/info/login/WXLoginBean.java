package com.soowin.cleverdog.info.login;

/**
 * Created by hxt on 2017/9/12.
 */

public class WXLoginBean {

    /**
     * access_token : yDD4Pm7_6od_kNZ3NPfJqgiy5kgfTfXXAPBAn_re1ko1sYckdQo5y1Uf_5IH9ePzNU12eu3TzpNMxatGmEayeA
     * expires_in : 7200
     * refresh_token : PJ5f5bMqs6Db3wkw6tWBu7-UGQdA2NlbhPP0DLrbl5WQldTubYcYVEpRFgnVUfO2wFz34wlGXjMSr-BMFsqtNg
     * openid : o_02dw-PvuTGr2XjRBmAePpjxhso
     * scope : snsapi_userinfo
     * unionid : o8T7Fvg6vCMlNXWUzTPOfsP6ogl0
     */

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
