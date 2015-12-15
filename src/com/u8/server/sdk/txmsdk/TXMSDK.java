package com.u8.server.sdk.txmsdk;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import net.sf.json.JSONObject;

/**
 * 腾讯应用宝
 * Created by ant on 2015/10/14.
 */
public class TXMSDK implements ISDKScript{

    @Override
    public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            int type = json.getInt("accountType");
            String openId = json.getString("openId");

            String accountType = "qq";
            if(type == 1){
                accountType = "wx";
            }

            callback.onSuccess(new SDKVerifyResult(true, openId, accountType+"-"+openId, ""));

        }catch(Exception e){
            callback.onFailed(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
