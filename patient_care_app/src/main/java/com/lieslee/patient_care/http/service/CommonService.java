package com.lieslee.patient_care.http.service;

import com.common.http.HttpResult;
import com.lieslee.patient_care.bean.NewsListResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by LiesLee on 16/8/23.
 */
public interface CommonService {

    /**
     * 刷新登录状态
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("api.php")
    Observable<HttpResult<String>> commonString(@FieldMap Map<String, Object> params);


    /**
     * 新闻列表
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("api.php")
    Observable<HttpResult<NewsListResponse>> downloadLists(@FieldMap Map<String, Object> params);



}
