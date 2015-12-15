package com.u8.server.sdk.txmsdk;

import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UMsdkOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.cache.UApplicationContext;
import com.u8.server.web.SendAgent;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 支付任务
 * Created by ant on 2015/10/14.
 */
public class PayTask implements Runnable, Delayed{

    public static final int STATE_INIT = 1;        //第一次状态
    public static final int STATE_COMPLETE = 2;     //完成状态
    public static final int STATE_RETRY = 3;        //重试状态
    public static final int STATE_FAILED = 4;       //失败状态

    private PayRequest payRequest;

    private int state = STATE_INIT;          //任务状态
    private long time;                       //任务执行时间
    private int retryCount = 0;              //已经重试的次数
    private int maxRetryCount;

    public PayTask(PayRequest req, long delayMillis, int maxRetryCount){

        this.payRequest = req;
        this.state = STATE_INIT;
        this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayMillis, TimeUnit.MILLISECONDS);
        this.retryCount = 0;
        this.maxRetryCount = maxRetryCount;
    }

    public void setDelay(long delayMillis){
        this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayMillis, TimeUnit.MILLISECONDS);
    }

    public PayRequest getPayRequest() {
        return payRequest;
    }

    public void setPayRequest(PayRequest payRequest) {
        this.payRequest = payRequest;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }


    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        PayTask task = (PayTask)o;
        long result = task.getTime() - this.getTime();

        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }

    /**
     * 时间到了，执行支付逻辑
     */
    @Override
    public void run() {

        if(this.state == STATE_COMPLETE || this.state == STATE_FAILED){
            return;
        }

        try{
            Log.d("now to execute a new txmsdk pay req. userID : %s; retryNum: %s ", payRequest.getUser().getId(), retryCount);

            JSONObject queryResult = queryMoney();

            if(queryResult != null){

                int coinNum = queryResult.getInt("balance");

                if(coinNum > 0){

                    JSONObject payResult = charge(coinNum);

                    if( payResult != null ){

                        this.state = STATE_COMPLETE;

                        UOrderManager orderManager = (UOrderManager) UApplicationContext.getBean("orderManager");
                        if(orderManager != null){
                            boolean firstPay = queryResult.getInt("first_save") == 1;
                            int allMoney = queryResult.getInt("save_amt");
                            String transID = payResult.getString("billno");
                            UMsdkOrder order = orderManager.generateMsdkOrder(payRequest.getUser(), transID, coinNum, firstPay, allMoney);
                            order.setState(PayState.STATE_SUC);
                            SendAgent.sendMSDKCallbackToServer(orderManager, order);
                        }

                        return;
                    }
                }


            }

            this.retryCount++;
            if(this.retryCount >= this.maxRetryCount){
                this.state = STATE_FAILED;

            }else{
                this.state = STATE_RETRY;
            }

        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }
    }

    //查询余额
    private JSONObject queryMoney(){

        try{

            UChannel channel = this.payRequest.getUser().getChannel();
            UChannelMaster master = channel.getMaster();
            String url = master.getOrderUrl();
            String scriptName = "/mpay/get_balance_m";

            Map<String,String> params = new HashMap<String, String>();

            params.put("openid", this.payRequest.getOpenID());
            params.put("openkey", this.payRequest.getOpenKey());
            params.put("pay_token", this.payRequest.getPay_token());
            params.put("ts", Long.toString(System.currentTimeMillis() / 1000));
            params.put("pf", this.payRequest.getPf());
            params.put("pfkey", this.payRequest.getPfkey());
            params.put("zoneid", this.payRequest.getZoneid());


            String resp = OpenApiV3.api_pay(url, scriptName, channel.getCpAppID(), channel.getCpAppKey(), this.payRequest.getAccountType(), params);

            JSONObject json = JSONObject.fromObject(resp);
            int ret = json.getInt("ret");
            if(ret == 0){

                return json;
            }



        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    //扣费,返回交易流水号
    private JSONObject charge(int num){
        try{

            UChannel channel = this.payRequest.getUser().getChannel();
            UChannelMaster master = channel.getMaster();
            String url = master.getOrderUrl();
            String scriptName = "/mpay/pay_m";

            Map<String,String> params = new HashMap<String, String>();

            params.put("openid", this.payRequest.getOpenID());
            params.put("openkey", this.payRequest.getOpenKey());
            params.put("pay_token", this.payRequest.getPay_token());
            params.put("ts", Long.toString(System.currentTimeMillis() / 1000));
            params.put("pf", this.payRequest.getPf());
            params.put("pfkey", this.payRequest.getPfkey());
            params.put("zoneid", this.payRequest.getZoneid());
            params.put("amt", num+"");

            String resp = OpenApiV3.api_pay(url, scriptName, channel.getCpAppID(), channel.getCpAppKey(), this.payRequest.getAccountType(), params);

            JSONObject json = JSONObject.fromObject(resp);
            int ret = json.getInt("ret");
            if(ret == 0){

                return json;
            }

        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
