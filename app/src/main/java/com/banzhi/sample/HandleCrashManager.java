package com.banzhi.sample;

import android.util.Log;

import com.banzhi.lib.listener.HandleCrashProxy;
import com.banzhi.rxhttp.RxHttp;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/3/2.
 * @desciption :
 * @version :
 * </pre>
 */
public class HandleCrashManager implements HandleCrashProxy {

    @Override
    public void handleCrash(String extra, Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(extra);
        Throwable throwable = new Throwable(e);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            StackTraceElement[] trace = cause.getStackTrace();
            for (StackTraceElement element : trace) {
                sb.append("\nat\t" + element);
            }
            cause = cause.getCause();
        }
        Log.e("handleCrash", sb.toString());
        Log.e("TIME 38", new Date().toString());
        String url = "http://120.24.152.60:5000/v1/home/AddAppError";
        RxHttp.Companion.getInstance().getService(CrashServer.class)
                .crashReport(url, new CrashBean(sb.toString()))
                .enqueue(new Callback<BaseBean>() {
                    @Override
                    public void onResponse(Call<BaseBean> call, Response<BaseBean> response) {
                        Log.e("TAGGGG", "onResponse: " + response.body().Message);
                    }

                    @Override
                    public void onFailure(Call<BaseBean> call, Throwable t) {
                        Log.e("TAGGGG", "onFailure: " + t.getMessage());
                    }
                });

    }

    public static class CrashBean {
        String ErrorLog;

        public CrashBean(String errorLog) {
            ErrorLog = errorLog;
        }

        public String getErrorLog() {
            return ErrorLog;
        }

        public void setErrorLog(String errorLog) {
            ErrorLog = errorLog;
        }
    }
}
