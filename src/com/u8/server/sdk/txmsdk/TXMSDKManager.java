package com.u8.server.sdk.txmsdk;

import com.u8.server.log.Log;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 应用宝SDK相关支付逻辑
 * Created by ant on 2015/10/14.
 */
@Component("txmsdkManager")
@Scope("singleton")
public class TXMSDKManager {

    private static final long DELAY_MILLIS = 20000;      //每次延迟执行间隔,ms
    private static final int MAX_RETRY_NUM = 6;         //最多重试6次

    private static TXMSDKManager instance;

    private DelayQueue<PayTask> tasks;

    private ExecutorService executor;

    private boolean isRunning = false;

    private TXMSDKManager(){
        this.tasks = new DelayQueue<PayTask>();
        executor = Executors.newFixedThreadPool(3);
    }

    public static TXMSDKManager getInstance(){
        if(instance == null){
            instance = new TXMSDKManager();
        }
        return instance;
    }

    //添加一个新支付请求到队列中
    public void addPayRequest(PayRequest req){

        PayTask task = new PayTask(req, 100, MAX_RETRY_NUM);
        this.tasks.add(task);

        if(!isRunning){
            isRunning = true;
            execute();
        }
    }

    public void execute(){
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try{

                    while(isRunning){
                        PayTask task = tasks.take();
                        task.run();
                        if(task.getState() == PayTask.STATE_RETRY){
                            task.setDelay(DELAY_MILLIS);
                            tasks.add(task);
                        }else if(task.getState() == PayTask.STATE_FAILED){
                            Log.e("the user[%s](channel userID:%s) charge failed.", task.getPayRequest().getUser().getId(), task.getPayRequest().getUser().getChannelUserID());
                        }
                    }

                }catch (Exception e){
                    Log.e(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void destory(){
        this.isRunning = false;
        if(executor != null){
            executor.shutdown();
            executor = null;
        }
    }

}
