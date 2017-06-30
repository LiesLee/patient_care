package com.lieslee.patient_care.http.protocol;


import com.common.http.HostType;
import com.common.http.HttpResult;
import com.common.utils.MD5Util;
import com.lieslee.patient_care.bean.NewsListResponse;
import com.lieslee.patient_care.http.manager.RetrofitManager;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by LiesLee on 16/10/17.
 */
public class CommonProtocol extends BaseProtocol {
    /**
     * get news list
     * @return
     */
    public static Observable<HttpResult<NewsListResponse>> downloadLists(long update_time){
        Map<String, Object> params = new HashMap<>();
        params.put("update_time", update_time);
        return RetrofitManager.getInstance(HostType.USER_HOST).getCommonService()
                .downloadLists(createPatams(params,"downloadLists"));
    }


}
