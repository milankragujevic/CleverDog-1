package com.soowin.cleverdog.info.login;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class IsOkBean2 {

    /**
     * status : ok
     * state : 1
     * result : []
     * message : 重置成功
     */

    private String status;
    private int state;
    private String message;
    private List<?> result;

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

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }
}
