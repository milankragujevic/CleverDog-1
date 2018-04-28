package com.soowin.cleverdog.info.index;

/**
 * Created by hxt on 2017/8/7.
 */

public class BaseBean {

    private int state;
    private String message;
    /**
     * status : ok
     * result : {"ID":"39","user_login":"15533990141","user_pass":"$P$B42uxA43BaPtrDBgMYwU5rqAthvxFt.","user_nicename":"15533990141","user_email":"","user_url":"","user_registered":"2017-09-13 01:58:57","user_activation_key":"","user_status":"0","display_name":"15533990141","phone":""}
     */

    private String status;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
