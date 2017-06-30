package com.lieslee.patient_care.http.manager;


import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.http.HostType;
import com.common.utils.logMessageQueue.ThreadPoolManager;
import com.lieslee.patient_care.BuildConfig;
import com.lieslee.patient_care.application.PatientCareApplication;
import com.lieslee.patient_care.http.HttpConstants;
import com.lieslee.patient_care.http.service.CommonService;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * ClassName: RetrofitManager
 * Author: LiesLee
 * Fuction: Retrofit请求管理类
 */
public class RetrofitManager {

    ThreadPoolManager tpm = null;

    /**
     * 是否使用消息队列打Log
     */
    boolean isMsgQueueLog = false;

    private static volatile OkHttpClient sOkHttpClient;
    private int hostType;
    // 管理不同HostType的单例
    private static SparseArray<RetrofitManager> sInstanceManager = new SparseArray<>(HostType.TYPE_COUNT);


    private CommonService commonService;

    /** Response拦截器 */
    private Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            final Request request = chain.request();
            final Response response = chain.proceed(request);

            final ResponseBody responseBody = response.body();
            final long contentLength = responseBody.contentLength();

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(charset);
                } catch (UnsupportedCharsetException e) {
                    KLog.e("Couldn't decode the response body; charset is likely malformed.");
                    return response;
                }
            }
            if (BuildConfig.DEBUG) {
                JSONObject jo = JSON.parseObject(buffer.clone().readString(charset));
                if (contentLength != 0) {
                    if (isMsgQueueLog) {
                        if (tpm == null) {
                            tpm = ThreadPoolManager.newInstance();
                        }
                        tpm.addLogMsg(jo.toJSONString());
                    } else {
                        KLog.json(jo.toString());
                        KLog.e(jo.toString());
                    }
                }
            }
            return response;
        }
    };



    private RetrofitManager() {}


    private RetrofitManager(@HostType.HostTypeChecker int hostType) {
        this.hostType = hostType;
    }

    /**
     * 获取单例
     *
     * @param hostType host类型
     * @return 实例
     */
    public static RetrofitManager getInstance(int hostType) {
        RetrofitManager instance = sInstanceManager.get(hostType);
        if (instance == null) {
            instance = new RetrofitManager(hostType);
            sInstanceManager.put(hostType, instance);
            return instance;
        } else {
            return instance;
        }
    }

    /**
     * 获取单例
     *
     * @return 实例
     */
    public static RetrofitManager getDefaultInstance() {
        RetrofitManager instance = sInstanceManager.get(HostType.USER_HOST);
        if (instance == null) {
            instance = new RetrofitManager(HostType.USER_HOST);
            sInstanceManager.put(HostType.USER_HOST, instance);
            return instance;
        } else {
            return instance;
        }
    }



    private Retrofit createRetrofit(){
        return new Retrofit.Builder().baseUrl(HttpConstants.getHost(hostType))
                .client(getOkHttpClient()).addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
    }

    // 配置OkHttpClient
    private OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (sOkHttpClient == null) {
                    // OkHttpClient配置是一样的,静态创建一次即可
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(PatientCareApplication.getInstance().getCacheDir(), "HttpCache"),
                            1024 * 1024 * 100);

                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    sOkHttpClient = new OkHttpClient.Builder().cache(cache)
                            .addInterceptor(mLoggingInterceptor)    //拦截请求结果打印
                            .addInterceptor(setHeaderInterator()) //设置请求头
                            .addInterceptor(loggingInterceptor)
                            .retryOnConnectionFailure(true)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(180, TimeUnit.SECONDS)
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .build();

                }
            }
        }
        return sOkHttpClient;
    }

    /**
     * 监听器实现拦截请求头统一设置请求头、设备号、token
     * @return
     */
    private Interceptor setHeaderInterator() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                builder.addHeader("Content-Type", "application/json");
                builder.addHeader("charset", "UTF-8");

                Request request = builder.build();
                return chain.proceed(request);
            }

        };
    }


    public CommonService getCommonService() {
        if(commonService == null){
            commonService = createRetrofit().create(CommonService.class);
        }
        return commonService;
    }


}
