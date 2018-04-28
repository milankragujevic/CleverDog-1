package com.soowin.cleverdog.info.index;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */

public class TicketListBean extends BaseBean{

    /**
     * state : 1
     * result : {"page":{"total":"15","totalpage":2,"pageNo":1,"hasPrev":"false","PrevPage":1,"hasNext":"true","nextPage":2},"data":[{"id":"20","user_id":"30","thumb":"http://192.168.1.113:982/uploads/2017/09/20170912101309170.jpeg","content":"","add_time":"1970-01-01 08:00:00","size":{"0":1668,"1":2500,"2":2,"3":"width=\"1668\" height=\"2500\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"19","user_id":"30","thumb":"http://192.168.1.113:982/uploads/2017/09/2017091209524697.jpeg","content":"13739767521","add_time":"1970-01-01 08:00:00","size":{"0":2668,"1":1772,"2":2,"3":"width=\"2668\" height=\"1772\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"18","user_id":"30","thumb":"http://192.168.1.113:982/uploads/2017/09/2017091209524697.jpeg","content":"13739767521","add_time":"1970-01-01 08:00:00","size":{"0":2668,"1":1772,"2":2,"3":"width=\"2668\" height=\"1772\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"17","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203151518379-300x133.jpg","content":"213","add_time":"2017-09-11 15:42:30","size":{"0":300,"1":133,"2":2,"3":"width=\"300\" height=\"133\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"15","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203114051636-300x243.jpg","content":"绕弯儿 rew","add_time":"2017-09-11 14:35:48","size":{"0":300,"1":243,"2":2,"3":"width=\"300\" height=\"243\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"14","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203110033317-300x101.png","content":"二位认为","add_time":"2017-09-11 14:35:30","size":{"0":300,"1":101,"2":3,"3":"width=\"300\" height=\"101\"","bits":8,"mime":"image/png"}},{"id":"13","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/01/2016013013432757.png","content":"324而法国","add_time":"2017-09-11 14:35:13","size":{"0":233,"1":58,"2":3,"3":"width=\"233\" height=\"58\"","bits":8,"mime":"image/png"}},{"id":"12","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/01/20160130161909725-300x162.png","content":"324324","add_time":"2017-09-11 14:34:59","size":{"0":300,"1":162,"2":3,"3":"width=\"300\" height=\"162\"","bits":8,"mime":"image/png"}},{"id":"11","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203151318409-300x85.jpg","content":"324","add_time":"2017-09-11 14:34:40","size":{"0":300,"1":85,"2":2,"3":"width=\"300\" height=\"85\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"10","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203110626364-300x192.jpg","content":"213424","add_time":"2017-09-11 14:34:24","size":{"0":300,"1":192,"2":2,"3":"width=\"300\" height=\"192\"","bits":8,"channels":3,"mime":"image/jpeg"}}]}
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
         * page : {"total":"15","totalpage":2,"pageNo":1,"hasPrev":"false","PrevPage":1,"hasNext":"true","nextPage":2}
         * data : [{"id":"20","user_id":"30","thumb":"http://192.168.1.113:982/uploads/2017/09/20170912101309170.jpeg","content":"","add_time":"1970-01-01 08:00:00","size":{"0":1668,"1":2500,"2":2,"3":"width=\"1668\" height=\"2500\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"19","user_id":"30","thumb":"http://192.168.1.113:982/uploads/2017/09/2017091209524697.jpeg","content":"13739767521","add_time":"1970-01-01 08:00:00","size":{"0":2668,"1":1772,"2":2,"3":"width=\"2668\" height=\"1772\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"18","user_id":"30","thumb":"http://192.168.1.113:982/uploads/2017/09/2017091209524697.jpeg","content":"13739767521","add_time":"1970-01-01 08:00:00","size":{"0":2668,"1":1772,"2":2,"3":"width=\"2668\" height=\"1772\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"17","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203151518379-300x133.jpg","content":"213","add_time":"2017-09-11 15:42:30","size":{"0":300,"1":133,"2":2,"3":"width=\"300\" height=\"133\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"15","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203114051636-300x243.jpg","content":"绕弯儿 rew","add_time":"2017-09-11 14:35:48","size":{"0":300,"1":243,"2":2,"3":"width=\"300\" height=\"243\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"14","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203110033317-300x101.png","content":"二位认为","add_time":"2017-09-11 14:35:30","size":{"0":300,"1":101,"2":3,"3":"width=\"300\" height=\"101\"","bits":8,"mime":"image/png"}},{"id":"13","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/01/2016013013432757.png","content":"324而法国","add_time":"2017-09-11 14:35:13","size":{"0":233,"1":58,"2":3,"3":"width=\"233\" height=\"58\"","bits":8,"mime":"image/png"}},{"id":"12","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/01/20160130161909725-300x162.png","content":"324324","add_time":"2017-09-11 14:34:59","size":{"0":300,"1":162,"2":3,"3":"width=\"300\" height=\"162\"","bits":8,"mime":"image/png"}},{"id":"11","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203151318409-300x85.jpg","content":"324","add_time":"2017-09-11 14:34:40","size":{"0":300,"1":85,"2":2,"3":"width=\"300\" height=\"85\"","bits":8,"channels":3,"mime":"image/jpeg"}},{"id":"10","user_id":"1","thumb":"http://192.168.1.113:982/uploads/2016/02/20160203110626364-300x192.jpg","content":"213424","add_time":"2017-09-11 14:34:24","size":{"0":300,"1":192,"2":2,"3":"width=\"300\" height=\"192\"","bits":8,"channels":3,"mime":"image/jpeg"}}]
         */

        private PageBean page;
        private List<DataBean> data;

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
             * total : 15
             * totalpage : 2
             * pageNo : 1
             * hasPrev : false
             * PrevPage : 1
             * hasNext : true
             * nextPage : 2
             */

            private String total;
            private int totalpage;
            private int pageNo;
            private String hasPrev;
            private int PrevPage;
            private String hasNext;
            private int nextPage;

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

            public int getPageNo() {
                return pageNo;
            }

            public void setPageNo(int pageNo) {
                this.pageNo = pageNo;
            }

            public String getHasPrev() {
                return hasPrev;
            }

            public void setHasPrev(String hasPrev) {
                this.hasPrev = hasPrev;
            }

            public int getPrevPage() {
                return PrevPage;
            }

            public void setPrevPage(int PrevPage) {
                this.PrevPage = PrevPage;
            }

            public String getHasNext() {
                return hasNext;
            }

            public void setHasNext(String hasNext) {
                this.hasNext = hasNext;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }
        }

        public static class DataBean {
            /**
             * id : 20
             * user_id : 30
             * thumb : http://192.168.1.113:982/uploads/2017/09/20170912101309170.jpeg
             * content :
             * add_time : 1970-01-01 08:00:00
             * size : {"0":1668,"1":2500,"2":2,"3":"width=\"1668\" height=\"2500\"","bits":8,"channels":3,"mime":"image/jpeg"}
             */

            private String id;
            private String user_id;
            private String thumb;
            private String content;
            private String add_time;
            private SizeBean size;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getThumb() {
                return thumb;
            }

            public void setThumb(String thumb) {
                this.thumb = thumb;
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

            public SizeBean getSize() {
                return size;
            }

            public void setSize(SizeBean size) {
                this.size = size;
            }

            public static class SizeBean {
                /**
                 * 0 : 1668
                 * 1 : 2500
                 * 2 : 2
                 * 3 : width="1668" height="2500"
                 * bits : 8
                 * channels : 3
                 * mime : image/jpeg
                 */

                @SerializedName("0")
                private int _$0;
                @SerializedName("1")
                private int _$1;
                @SerializedName("2")
                private int _$2;
                @SerializedName("3")
                private String _$3;
                private int bits;
                private int channels;
                private String mime;

                public int get_$0() {
                    return _$0;
                }

                public void set_$0(int _$0) {
                    this._$0 = _$0;
                }

                public int get_$1() {
                    return _$1;
                }

                public void set_$1(int _$1) {
                    this._$1 = _$1;
                }

                public int get_$2() {
                    return _$2;
                }

                public void set_$2(int _$2) {
                    this._$2 = _$2;
                }

                public String get_$3() {
                    return _$3;
                }

                public void set_$3(String _$3) {
                    this._$3 = _$3;
                }

                public int getBits() {
                    return bits;
                }

                public void setBits(int bits) {
                    this.bits = bits;
                }

                public int getChannels() {
                    return channels;
                }

                public void setChannels(int channels) {
                    this.channels = channels;
                }

                public String getMime() {
                    return mime;
                }

                public void setMime(String mime) {
                    this.mime = mime;
                }
            }
        }
    }
}
