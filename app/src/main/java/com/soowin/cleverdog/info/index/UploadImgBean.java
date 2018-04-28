package com.soowin.cleverdog.info.index;

import java.util.List;

/**
 * Created by hxt on 2017/9/14.
 */

public class UploadImgBean extends BaseBean{

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * url : http://192.168.1.113:982/uploads/2017/09/20170914084516475.png
         */

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
