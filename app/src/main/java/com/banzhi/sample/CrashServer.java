package com.banzhi.sample;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/3/2.
 * @desciption :
 * @version :
 * </pre>
 */
public interface CrashServer {

    @POST
    Call<BaseBean> crashReport(@Url String url, @Body HandleCrashManager.CrashBean bean);
}
