package com.u8.server.sdk.txmsdk;

import com.u8.server.data.UChannel;
import com.u8.server.data.UUser;

/**
 * Created by ant on 2015/10/14.
 */
public class PayRequest {

    private UUser user;
    private int accountType;    //0:QQ;1:微信
    private String openID;      //从手Q登录态或微信登录态中获取的openid的值
    private String openKey;     //从手Q登录态或微信登录态中获取的access_token 的值
    private String pay_token;   //从手Q登录态中获取的pay_token的值; 微信登录时特别注意该参数传空。
    private String pf;          //平台来源
    private String pfkey;       //跟平台来源和openkey根据规则生成的一个密钥串
    private String zoneid;      //账户分区ID

    public UUser getUser() {
        return user;
    }

    public void setUser(UUser user) {
        this.user = user;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public String getOpenKey() {
        return openKey;
    }

    public void setOpenKey(String openKey) {
        this.openKey = openKey;
    }

    public String getPay_token() {
        return pay_token;
    }

    public void setPay_token(String pay_token) {
        this.pay_token = pay_token;
    }

    public String getPf() {
        return pf;
    }

    public void setPf(String pf) {
        this.pf = pf;
    }

    public String getPfkey() {
        return pfkey;
    }

    public void setPfkey(String pfkey) {
        this.pfkey = pfkey;
    }

    public String getZoneid() {
        return zoneid;
    }

    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }
}
