package com.soowin.cleverdog.info.index;

import java.util.List;

/**
 * Created by hxt on 2017/9/14.
 */

public class ViolationListBean extends BaseBean {
//交通标识的选择
// （cmpi.车让人违章
// rslv.轧实线违章
// ygv.黄色网格违章
// sluv.直行道掉头违章
// wv.鸣笛违章
// hbv.远光灯违章
// nstop,禁止停车）
// 可单选

    /**
     * state : 1
     * result : {"page":{"total":"3","totalpage":1,"pageNo":"1","hasPrev":"false","PrevPage":"1","hasNext":"false","nextPage":"1"},"data":[{"id":"5","section":"建华大街跃进路北行100米，残联门前人行横道","stop_time":"1429045380-1505192340","signs":"nstop","content":"反对","add_time":"2017-09-14 12:58:25","position":"114.51731,38.057967","start_time":"1429045380","end_time":"1505192340"},{"id":"5","section":"建华大街跃进路北行100米，残联门前人行横道","stop_time":"1429045380-1505192340","signs":"nstop","content":"反对","add_time":"2017-09-14 12:58:25","position":"114.51525,38.064387","start_time":"1429045380","end_time":"1505192340"},{"id":"5","section":"建华大街跃进路北行100米，残联门前人行横道","stop_time":"1429045380-1505192340","signs":"nstop","content":"反对","add_time":"2017-09-14 12:58:25","position":"114.526665,38.059183","start_time":"1429045380","end_time":"1505192340"}]}
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
         * page : {"total":"3","totalpage":1,"pageNo":"1","hasPrev":"false","PrevPage":"1","hasNext":"false","nextPage":"1"}
         * data : [{"id":"5","section":"建华大街跃进路北行100米，残联门前人行横道","stop_time":"1429045380-1505192340","signs":"nstop","content":"反对","add_time":"2017-09-14 12:58:25","position":"114.51731,38.057967","start_time":"1429045380","end_time":"1505192340"},{"id":"5","section":"建华大街跃进路北行100米，残联门前人行横道","stop_time":"1429045380-1505192340","signs":"nstop","content":"反对","add_time":"2017-09-14 12:58:25","position":"114.51525,38.064387","start_time":"1429045380","end_time":"1505192340"},{"id":"5","section":"建华大街跃进路北行100米，残联门前人行横道","stop_time":"1429045380-1505192340","signs":"nstop","content":"反对","add_time":"2017-09-14 12:58:25","position":"114.526665,38.059183","start_time":"1429045380","end_time":"1505192340"}]
         */

        private PageBean page;
        private List<DataBean> data;
        private String versionnumber;

        public String getVersionnumber() {
            return versionnumber;
        }

        public void setVersionnumber(String versionnumber) {
            this.versionnumber = versionnumber;
        }

        public PageBean getPage() {
            return page;
        }

        public void setPage(PageBean page) {
            this.page = page;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class PageBean {
            /**
             * total : 3
             * totalpage : 1
             * pageNo : 1
             * hasPrev : false
             * PrevPage : 1
             * hasNext : false
             * nextPage : 1
             */

            private String total;
            private int totalpage;
            private String pageNo;
            private String hasPrev;
            private String PrevPage;
            private String hasNext;
            private String nextPage;

            public String getTotal() {
                return total;
            }

            public void setTotal(String total) {
                this.total = total;
            }

            public int getTotalpage() {
                return totalpage;
            }

            public void setTotalpage(int totalpage) {
                this.totalpage = totalpage;
            }

            public String getPageNo() {
                return pageNo;
            }

            public void setPageNo(String pageNo) {
                this.pageNo = pageNo;
            }

            public String getHasPrev() {
                return hasPrev;
            }

            public void setHasPrev(String hasPrev) {
                this.hasPrev = hasPrev;
            }

            public String getPrevPage() {
                return PrevPage;
            }

            public void setPrevPage(String PrevPage) {
                this.PrevPage = PrevPage;
            }

            public String getHasNext() {
                return hasNext;
            }

            public void setHasNext(String hasNext) {
                this.hasNext = hasNext;
            }

            public String getNextPage() {
                return nextPage;
            }

            public void setNextPage(String nextPage) {
                this.nextPage = nextPage;
            }
        }

        public static class DataBean {
            /**
             * id : 5
             * section : 建华大街跃进路北行100米，残联门前人行横道
             * stop_time : 1429045380-1505192340
             * signs : nstop
             * content : 反对
             * add_time : 2017-09-14 12:58:25
             * position : 114.51731,38.057967
             * start_time : 1429045380
             * end_time : 1505192340
             */

            private String id;
            private String section;
            private String stop_time;
            private String signs;
            private String content;
            private String add_time;
            private String position;
            private String start_time;
            private String end_time;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSection() {
                return section;
            }

            public void setSection(String section) {
                this.section = section;
            }

            public String getStop_time() {
                return stop_time;
            }

            public void setStop_time(String stop_time) {
                this.stop_time = stop_time;
            }

            public String getSigns() {
                return signs;
            }

            public void setSigns(String signs) {
                this.signs = signs;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getPosition() {
                return position;
            }

            public void setPosition(String position) {
                this.position = position;
            }

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }
        }
    }

}
