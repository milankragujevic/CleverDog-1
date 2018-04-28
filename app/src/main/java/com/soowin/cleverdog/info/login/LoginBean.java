package com.soowin.cleverdog.info.login;

import com.soowin.cleverdog.info.index.BaseBean;

/**
 * Created by Administrator on 2017/7/13.
 * 登录
 */

public class LoginBean extends BaseBean{

    /**
     * result : {"user_login":"15533990141","user_nicename":"15533990141","user_email":"","user_url":"","user_registered":"2017-09-13 01:58:57","user_status":"0","display_name":"15533990141","phone":"","token":"155339901411505269458","versionnumber":"4","avatar":""}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * user_login : 15533990141
         * user_nicename : 15533990141
         * user_email :
         * user_url :
         * user_registered : 2017-09-13 01:58:57
         * user_status : 0
         * display_name : 15533990141
         * phone :
         * token : 155339901411505269458
         * versionnumber : 4
         * avatar :
         */
        private String ID;
        private String user_login;
        private String user_nicename;
        private String user_email;
        private String user_url;
        private String user_registered;
        private String user_status;
        private String display_name;
        private String phone;
        private String token;
        private String versionnumber;
        private String avatar;
        private String setviolation;

        public String getSetviolation() {
            return setviolation;
        }

        public void setSetviolation(String setviolation) {
            this.setviolation = setviolation;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getUser_login() {
            return user_login;
        }

        public void setUser_login(String user_login) {
            this.user_login = user_login;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getUser_url() {
            return user_url;
        }

        public void setUser_url(String user_url) {
            this.user_url = user_url;
        }

        public String getUser_registered() {
            return user_registered;
        }

        public void setUser_registered(String user_registered) {
            this.user_registered = user_registered;
        }

        public String getUser_status() {
            return user_status;
        }

        public void setUser_status(String user_status) {
            this.user_status = user_status;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getVersionnumber() {
            return versionnumber;
        }

        public void setVersionnumber(String versionnumber) {
            this.versionnumber = versionnumber;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
