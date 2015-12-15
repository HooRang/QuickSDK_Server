package com.u8.server.data;

import com.u8.server.cache.CacheManager;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单对象
 */

@Entity
@Table(name = "uorder")
public class UOrder {

    @Id
    private Long orderID;       //订单号
    private int appID;          //当前所属游戏ID
    private int channelID;      //当前所属渠道ID
    private int userID;         //U8Server这边对应的用户ID
    private String username;    //U8Server这边生成的用户名
    private String productName; //游戏中商品名称
    private String productDesc; //游戏中商品描述
    private int money;  //单位 分, 下单时收到的金额，实际充值的金额以这个为准
    private int realMoney;  //单位 分，渠道SDK支付回调通知返回的金额，记录，留作查账
    private String currency; //币种
    private String roleID;      //游戏中角色ID
    private String roleName;    //游戏中角色名称
    private String serverID;    //服务器ID
    private String serverName;  //服务器名称
    private int state;          //订单状态
    private String channelOrderID;  //渠道SDK对应的订单号
    private String extension;       //扩展数据
    private Date createdTime;       //订单创建时间
    private String sdkOrderTime;          //渠道SDK那边订单交易时间
    private Date completeTime;          //订单完成时间

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("orderID", orderID+"");
        json.put("appID", appID);

        UGame game = getGame();

        json.put("appName", game == null ? "":game.getName());
        json.put("channelID", channelID);

        UChannel channel = getChannel();
        json.put("channelName", channel == null ? "":channel.getMaster().getMasterName());
        json.put("userID", userID);
        json.put("username", username);
        json.put("productName", productName);
        json.put("productDesc", productDesc);
        json.put("money", money);
        json.put("realMoney", money);
        json.put("currency", currency);
        json.put("roleID", roleID);
        json.put("roleName", roleName);
        json.put("serverID", serverID);
        json.put("serverName", serverName);
        json.put("state", state);
        json.put("channelOrderID", channelOrderID);
        json.put("extension", extension);
        json.put("createdTime", TimeFormater.format_default(createdTime));
        json.put("sdkOrderTime", sdkOrderTime);
        json.put("completeTime", completeTime);

        return json;
    }

    public UChannel getChannel(){

        return CacheManager.getInstance().getChannel(this.channelID);
    }

    public UGame getGame(){

        return CacheManager.getInstance().getGame(this.appID);
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(int realMoney) {
        this.realMoney = realMoney;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getChannelOrderID() {
        return channelOrderID;
    }

    public void setChannelOrderID(String channelOrderID) {
        this.channelOrderID = channelOrderID;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSdkOrderTime() {
        return sdkOrderTime;
    }

    public void setSdkOrderTime(String sdkOrderTime) {
        this.sdkOrderTime = sdkOrderTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }
}
