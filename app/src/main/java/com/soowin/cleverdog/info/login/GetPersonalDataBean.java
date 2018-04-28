package com.soowin.cleverdog.info.login;

import com.soowin.cleverdog.info.index.BaseBean;

/**
 * Created by hxt on 2017/9/13.
 */

public class GetPersonalDataBean extends BaseBean{
    /**
     * result : {"ID":"39","user_login":"15533990141","user_nicename":"15533990141","display_name":"15533990141","phone":"","sex":0,"plate":""}
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
         * ID : 39
         * user_login : 15533990141
         * user_nicename : 15533990141
         * display_name : 15533990141
         * phone :
         * sex : 0
         * plate :
         */

        private String ID;
        private String user_login;
        private String user_nicename;
        private String display_name;
        private String phone;
        private int sex;
        private String plate;

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

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getPlate() {
            return plate;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }
    }
}
