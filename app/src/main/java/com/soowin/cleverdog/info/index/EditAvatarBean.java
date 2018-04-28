package com.soowin.cleverdog.info.index;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class EditAvatarBean extends BaseBean {

    /**
     * status : ok
     * state : 1
     * result : [{"url":"http://chetongcheng.yanzhaoit.cn/uploads/2017/07/20170713142751438.jpg"}]
     * message : 上传成功
     */

    private List<ResultBean> result;


    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * url : http://chetongcheng.yanzhaoit.cn/uploads/2017/07/20170713142751438.jpg
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
