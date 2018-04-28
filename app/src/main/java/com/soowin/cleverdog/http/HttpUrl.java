package com.soowin.cleverdog.http;

/**
 * Created by Administrator on 2017/6/9.
 */

public class HttpUrl {
    /**
     * 测试地址
     **/
//    public String hostUrl = "http://192.168.1.113:982/";
//    public String hostUrl = "http://192.168.61.3:982/";
    /**
     * 正式地址
     **/
    public String hostUrl = "http://222.223.239.214:802/";


    /**
     * 登录接口【login】
     */
    public String login = "user/login";
    /**
     * 注册
     */
    public String registe = "user/registe";
    /**
     * 获取个人资料
     */
    public String getPersonalData = "user/getPersonalData";
    /**
     * 修改个人资料
     */
    public String editPersonalData = "user/editPersonalData";
    /**
     * 发送验证码  发送短信验证码(注册)
     */
    public String getVerification = "user/getVerification";
    /**
     * 修改密码
     */
    public String modifyPassword = "user/modifyPassword";
    /**
     * 10 重置密码
     */
    public String resetPassword = "user/resetPassword";
    /**
     * 找回密码验证验证码【verificationCode】
     */
    public String verificationCode = "user/verificationCode";
    /**
     * 发送短信验证码接口（重置）【sendVerification】
     */
    public String sendVerification = "user/sendVerification";
    /**
     * 上传用户头像接口
     */
    public String EditAvatar_user = "user/EditAvatar_user";
    /**
     * 罚单列表【TicketList】
     */
    public String TicketList = "ticket/TicketList";
    /**
     * 获取罚单信息【getTicket】
     */
    public String getTicket = "ticket/getTicket";

    /**
     * 违章点列表【violationList】
     */
    public String violationList = "violation/violationList";
    /**
     * 广告【ToolList】
     */
    public String ToolList = "tool/ToolList";
    /**
     * 上传图片【uploadImg】
     */
    public String uploadImg = "uploadimg/uploadImg";

    /**
     * 用户第三方登录【third_login】
     */
    public String third_login = "user/third_login";

    /**
     * 自动登录【autologin】
     */
    public String autologin = "user/autologin";
    /**
     * 设置违章点【setup】
     */
    public String setup = "violation/setup";
    /**
     * 添加违章点【addregion】
     */
    public String addregion = "violation/addregion";
 /**
     * 重复登陆【tokendouble】
     */
    public String tokendouble = "user/tokendouble";

}
