package com.soowin.cleverdog.info.login;

/**
 * Created by Administrator on 2017/6/10.
 */

public class IsOkBean {

    /**
     * status : ok
     * state : 1
     * message : 更新成功
     */

    private String status;
    private int state;
    private String message;
    private String result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
