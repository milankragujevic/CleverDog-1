package com.soowin.cleverdog.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;


import com.google.gson.Gson;
import com.soowin.cleverdog.info.index.BaseBean;
import com.soowin.cleverdog.utlis.PublicApplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hext on 2016/10/14.
 */
public class HttpTool {
    private static final String COOKIES_KEY = "Set-Cookie";
    private static final String TAG = HttpTool.class.getSimpleName();
    //    String COOKIE_KEY = "Set-Cookie";
    String result = "";
    private static final int TIME_OUT = 10 * 10000000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";

    public String httpGet(String strUrl, String data) {
        URL url;
        try {
//            url = new URL(PublicApplication.urlData.hostUrl + "?json=" + strUrl + data);
            url = new URL(/*PublicApplication.urlData.hostUrl + "?json=" + */strUrl + data);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader buffer = new BufferedReader(in);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");
            String inputLine;
            while ((inputLine = buffer.readLine()) != null) {
                result += inputLine + "\n";
            }
            Log.e("HttpTool", "result=" + result);
            in.close();
            urlConn.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String httpPost1(String strUrl, String data) {
        URL url;
        try {
            url = new URL(PublicApplication.urlData.hostUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);//输入流
            connection.setDoOutput(true);//输出流
            connection.setUseCaches(false);//缓存
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("cookie", getCookie());
            connection.setChunkedStreamingMode(0);//请求超时 不重复请求
//            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//jdk1.4换成这个,连接超时
//            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //jdk1.4换成这个,读操作超时
            connection.setConnectTimeout(10000);//jdk 1.5换成这个,连接超时
            connection.setReadTimeout(10000);//jdk 1.5换成这个,读操作超时
//            connection.setRequestProperty("Content-Type","");//设置头信息
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String param = "json=" + strUrl + data;
            Log.e("httptool", "param=" + param);
            out.writeBytes(param);
            out.flush();
            out.close();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                Map<String, List<String>> headerFields = new HashMap<>();
                headerFields = connection.getHeaderFields();
                setCookies(headerFields);
                BufferedReader buffer = new BufferedReader(in);
                String inputLine;
                while ((inputLine = buffer.readLine()) != null) {
                    result += inputLine + "\n";
                    Log.e(TAG, "httpPost: " + result);
                }
                in.close();
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return "timeout";
        }
        return result;
    }

    public String httpPost(String strUrl, String data) {
        URL url;
        try {
            url = new URL(PublicApplication.urlData.hostUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);//输入流
            connection.setDoOutput(true);//输出流
            connection.setUseCaches(false);//缓存
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("cookie", getCookie());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String param = "json=" + strUrl + data;
            Log.e("httptool", "param=" + param);
            out.writeBytes(param);
            out.flush();
            out.close();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                Map<String, List<String>> headerFields = new HashMap<>();
                headerFields = connection.getHeaderFields();
                setCookies(headerFields);
                BufferedReader buffer = new BufferedReader(in);
                String inputLine;
                while ((inputLine = buffer.readLine()) != null) {
                    result += inputLine + "\n";
                }
                in.close();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return "timeout";
        }
        return result;
    }

    protected static String getCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie("cookie");
        if (cookie != null) {
            return cookie;
        } else {
            return "";
        }
    }

    protected static void setCookie(String cookie) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie("cookie", cookie);
    }

    /**
     * 存储cookie
     *
     * @param headerFields
     */
    protected static void setCookies(Map<String, List<String>> headerFields) {
        if (null == headerFields) {
            return;
        }
        List<String> cookies = headerFields.get(COOKIES_KEY);
        if (null == cookies) {
            return;
        }
        for (String cookie : cookies) {
            setCookie(cookie);
        }
    }

    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(String key, File file, String RequestURL) {
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型
        try {
            URL url = new URL(RequestURL);
            Log.e(TAG, "uploadFile: " + RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);

            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);
            //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                /** * 当文件不为空，把文件包装并且上传 */
                Log.e(TAG, "uploadFile: " + file);
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);

                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                Log.e(TAG, "uploadFile: " + sb.toString());
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));

                String result = "";
                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);

                    result += line;
                    Log.e(TAG, "uploadFile: result" + result);
                }
                Log.e(TAG, "uploadFile: " + result);
                return result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }

    /**
     * 网络请求是否失败
     *
     * @param msg     网络请求返回值
     * @param srlMsrl 下拉刷新控件
     * @param context 上下文关系
     * @return
     */
    public static boolean isHttpError(Message msg, SwipeRefreshLayout srlMsrl, Context context) {
        if (msg != null)
            if (msg.obj.toString().equals("timeout")) {
                Toast.makeText(context, "网络繁忙请稍后重试", Toast.LENGTH_SHORT).show();
            } else {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    Gson gson = new Gson();
                    BaseBean dataBase = new BaseBean();
                    dataBase = gson.fromJson(msg.obj.toString(),
                            BaseBean.class);
                    int state = dataBase.getState();
                    if (state == 0) {
                        Toast.makeText(context, dataBase.getMessage(), Toast.LENGTH_SHORT).show();
                        if (srlMsrl != null)
                            srlMsrl.setRefreshing(false);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    Toast.makeText(context, "网络繁忙请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        if (srlMsrl != null)
            srlMsrl.setRefreshing(false);
        return true;
    }

    /**
     * 网络请求是否为空
     *
     * @param msg     网络请求返回值
     * @param srlMsrl 下拉刷新控件
     * @param context 上下文关系
     * @return
     */
    public static boolean isHttpNull(Message msg, SwipeRefreshLayout srlMsrl, Context context) {
        Gson gson = new Gson();
        BaseBean dataBase = new BaseBean();
        dataBase = gson.fromJson(msg.obj.toString(),
                BaseBean.class);
        int state = dataBase.getState();
        if (state == 2) {
            Toast.makeText(context, dataBase.getMessage(), Toast.LENGTH_SHORT).show();
            if (srlMsrl != null)
                srlMsrl.setRefreshing(false);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }


    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    /**
     * okhttp的post请求
     *
     * @param body
     * @return
     */
    public static String okPost(RequestBody body) {
        try {
            String url = PublicApplication.urlData.hostUrl;
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(okhttp3.HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(okhttp3.HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            /**
             * 打印日志
             */
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body1 = (FormBody) request.body();
                for (int i = 0; i < body1.size(); i++) {
                    sb.append(body1.encodedName(i) + "=" + body1.encodedValue(i) + "&");
                }
                sb.delete(sb.length() - 1, sb.length());
                Log.e(TAG, "RequestParams:" + sb.toString());
            }

            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            return response.body().string();
        } catch (SocketTimeoutException e) {
            return "timeout";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }
        return cookieHeader.toString();
    }

    /*public void receiveHeaders(Headers headers) throws IOException {
        if (client.cookieJar() == CookieJar.NO_COOKIES) return;

        List<Cookie> cookies = Cookie.parseAll(userRequest.url(), headers);
        if (cookies.isEmpty()) return;

        client.cookieJar().saveFromResponse(userRequest.url(), cookies);
    }*/

}
