package com.lieslee.patient_care.http;

import android.util.Log;

import com.common.base.ui.BaseView;
import com.common.callback.RequestCallback;
import com.common.http.HttpResult;
import com.common.http.HttpSubscibe;
import com.common.utils.NetUtil;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.application.PatientCareApplication;
import com.socks.library.KLog;

import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by LiesLee on 17/3/28.
 */

public class HttpUtil {

    private interface DoSpecialErrorCode<T>{
        void callback(HttpResult<T> t);
    }

    /**
     * 发起请求，默认执行方式，请求前执行在主线程，请求过程在io线程，请求返回在主线程，
     * 请求体管理标记在当前页面（关闭页面后取消，非关闭dialog就取消），如需指定以上内容，可调用requestCustomScheduler(...)函数
     * @param observable 请求体
     * @param baseView //view层接口（用于做空判断和标记当前页面的所有请求和取消请求）
     * @param requestCallback 数据回调
     * @param <T>
     * @return Subscription
     */
    public static <T> Subscription requestDefault(final Observable<HttpResult<T>> observable, final BaseView baseView, final RequestCallback<T> requestCallback) {
        if (NetUtil.isConnected(PatientCareApplication.getInstance())) {
            return HttpSubscibe.subscibe(observable, new Action0() {
                        @Override
                        public void call() {
                            if (baseView == null) return;
                            if (requestCallback == null) return;
                            requestCallback.beforeRequest();
                        }
                    }, AndroidSchedulers.mainThread(), AndroidSchedulers.mainThread(), false, baseView,
                    new Subscriber<HttpResult<T>>() {
                        @Override
                        public void onCompleted() {
                            if(baseView == null) return;
                            if(requestCallback == null) return;
                            requestCallback.requestComplete();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(baseView == null) return;
                            if(requestCallback == null) return;
                            if (e instanceof HttpException) {             //HTTP错误
                                HttpException httpException = (HttpException) e;
                                KLog.e(e.getLocalizedMessage() + "\n" + e);
                                requestCallback.requestError(httpException.code(), e.getLocalizedMessage() + "\n" + e);
                            } else if (e instanceof SocketTimeoutException) {
                                KLog.e("请求超时：\n" + e);
                                requestCallback.requestError(1, "似乎网络不太给力哦~");
                            } else {
                                KLog.e(e.getLocalizedMessage() + "\n" + e);
                                if(baseView == null) return;
                                if(requestCallback == null) return;
                                requestCallback.requestError(2, "似乎链接不上哦，请稍后再试~");
                            }
                        }

                        @Override
                        public void onNext(HttpResult<T> t) {
                            //处理返回的公共错误码（200成功，201属于特殊业务处理）
                            if(baseView == null) return;
                            if(requestCallback == null) return;
                            HttpUtil.doOnSuccess(t, baseView, requestCallback, new DoSpecialErrorCode<T>() {
                                @Override
                                public void callback(HttpResult<T> t) {
                                    if(t.getStatus() == 200){ //成功码
                                        requestCallback.requestSuccess(t.getData());
                                    }else if (t.getStatus() == 201){
                                        requestCallback.requestError(3, "登录状态失效，请从新登录");
                                        //token状态失效，刷新token
                                        //request_201_recall(observable, AndroidSchedulers.mainThread(), AndroidSchedulers.mainThread(), null, baseView, requestCallback);
                                    }
                                }
                            });
                        }
                    });
        } else {
            if (baseView == null || requestCallback == null) return null;
            requestCallback.requestError(404, "检查不到网络哦, 请检查网络状态");
        }
        return null;
    }

    /**
     * 发起请求,自定义请求前 和 返回数据后所执行的线程 及 请求订阅体存放管理的位置
     * @param observable 请求体
     * @param subscribeOnScheduler 发起请求前执行回调（显示进度条等等）所在的线程
     * @param observeOnScheduler 请求过程所在的线程
     * @param cancelUnsubscribes 添加到取消请求列表，管理里面主动关闭dialog后取消相应请求（传入null就是关闭当前页面后才统一取消所有请求）
     * @param baseView //view层接口（用于做空判断和标记当前页面的所有请求和取消请求）
     * @param requestCallback 数据回调
     * @param <T>
     * @return Subscription
     */
    public static <T> Subscription requestCustomScheduler(final Observable<HttpResult<T>> observable, final Scheduler subscribeOnScheduler,
                                                          final Scheduler observeOnScheduler, final CompositeSubscription cancelUnsubscribes, final BaseView baseView,
                                                          final RequestCallback<T> requestCallback) {
        if (NetUtil.isConnected(PatientCareApplication.getInstance())) {
            return HttpSubscibe.subscibe(observable, new Action0() {
                        @Override
                        public void call() {
                            if (baseView == null) return;
                            if (requestCallback == null) return;
                            requestCallback.beforeRequest();
                        }
                    }, subscribeOnScheduler, observeOnScheduler, !(cancelUnsubscribes == null), baseView,
                    new Subscriber<HttpResult<T>>() {
                        @Override
                        public void onCompleted() {
                            if(baseView == null) return;
                            if(requestCallback == null) return;
                            requestCallback.requestComplete();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(baseView == null) return;
                            if(requestCallback == null) return;
                            if (e instanceof HttpException) {             //HTTP错误
                                HttpException httpException = (HttpException) e;
                                KLog.e(e.getLocalizedMessage() + "\n" + e);
                                requestCallback.requestError(httpException.code(), e.getLocalizedMessage() + "\n" + e);
                            } else if (e instanceof SocketTimeoutException) {
                                KLog.e("请求超时：\n" + e);
                                requestCallback.requestError(1, "似乎网络不太给力哦~");
                            } else {
                                KLog.e(e.getLocalizedMessage() + "\n" + e);
                                if(baseView == null) return;
                                if(requestCallback == null) return;
                                requestCallback.requestError(2, "似乎链接不上哦，请稍后再试~");
                            }
                        }

                        @Override
                        public void onNext(HttpResult<T> t) {
                            if(baseView == null) return;
                            if(requestCallback == null) return;
                            //处理返回的公共错误码（200成功，201属于特殊业务处理）
                            HttpUtil.doOnSuccess(t, baseView, requestCallback, new DoSpecialErrorCode<T>() {
                                @Override
                                public void callback(HttpResult<T> t) {
                                    if(t.getStatus() == 200){ //成功码
                                        requestCallback.requestSuccess(t.getData());
                                    }else if (t.getStatus() == 201){
                                        requestCallback.requestError(3, "登录状态失效，请从新登录");
                                        //token状态失效，刷新token
                                        //request_201_recall(observable, subscribeOnScheduler, observeOnScheduler, cancelUnsubscribes, baseView, requestCallback);
                                    }
                                }
                            });
                        }
                    });
        } else {
            if (baseView == null || requestCallback == null) return null;
            requestCallback.requestError(404, "检查不到网络哦, 请检查网络状态");
        }
        return null;
    }

//    /**
//     * 后台接口201的情况下，刷新token，再重新调起上一个请求
//     * @param observable
//     * @param subscribeOnScheduler
//     * @param observeOnScheduler
//     * @param cancelUnsubscribes
//     * @param baseView
//     * @param requestCallback
//     * @param <T>
//     * @return
//     */
//    private static <T> Subscription request_201_recall(final Observable<HttpResult<T>> observable, final Scheduler subscribeOnScheduler,
//                                                       final Scheduler observeOnScheduler, final CompositeSubscription cancelUnsubscribes, final BaseView baseView,
//                                                       final RequestCallback<T> requestCallback) {
//        if (NetUtil.isConnected(BeeApplication.getInstance())) {
//            HttpSubscibe.subscibe(CommonProtocol.refreshToken(), new Action0() {
//                        @Override
//                        public void call() {
//                            HttpUtil.onRequestBefore(baseView, requestCallback);
//                        }
//                    }, AndroidSchedulers.mainThread(), AndroidSchedulers.mainThread(), false, baseView,
//                    new Subscriber<HttpResult<Token>>() {
//                        @Override
//                        public void onCompleted() {
//                            HttpUtil.onCompleted(baseView, requestCallback);
//                        }
//                        @Override
//                        public void onError(Throwable e) {
//                            HttpUtil.onError(e, baseView, requestCallback);
//                        }
//                        @Override
//                        public void onNext(HttpResult<Token> t) {
//                            //处理返回的公共错误码（200成功，201属于特殊业务处理）
//                            HttpUtil.doOnSuccess(t, baseView, requestCallback, new DoSpecialErrorCode<Token>() {
//                                @Override
//                                public void callback(HttpResult<Token> t) {
//                                    //处理返回的公共错误码（200成功，201属于特殊单独业务处理）
//                                    if(t.getStatus() == 200){ //成功码
//                                        BeeApplication.getInstance().setToken(t.getData());//Token刷新成功，重新调起上一个请求
//                                        HttpUtil.requestCustomScheduler(observable, subscribeOnScheduler, observeOnScheduler,cancelUnsubscribes, baseView, requestCallback);
//                                    }else {
//                                        //token再次刷新失败直接callback错误，防止循环刷新Token
//                                        requestCallback.requestError(t.getStatus(), t.getMsg());
//                                    }
//                                }
//                            });
//                        }
//                    });
//        }
//
//        return null;
//    }




    private static <T> HttpResult doOnSuccess(HttpResult<T> t, BaseView baseView, RequestCallback requestCallback, DoSpecialErrorCode<T> specialError){
        if(baseView == null) return t;
        if(requestCallback == null) return t;
        try {
            //这里不做空判断,因为部分检查接口请求成功返回200,但数据依然是空的
            if (t != null) {
                if (t.getStatus() == 200) {
                    //数据返回
                    if(specialError != null){
                        specialError.callback(t);
                    }else{
                        requestCallback.requestError(3, "数据回调后处理错误");
                    }
                    //登录状态失效
                } else if (t.getStatus() == 202 || t.getStatus() == 207) {
                    requestCallback.requestError(202, "由于您长时间未使用"+ PatientCareApplication.getInstance().getResources().getString(R.string.app_name) +", 登录状态失效, 请重新登录!");
                    //登录状态异常（账号异常）跳登录
//                    Token token = BeeApplication.getInstance().getUser().getApi_token();
//                    BeeApplication.getInstance().setUser(null);
//                    BeeApplication.getInstance().setToken(token);
//                    Intent intent = new Intent(BeeApplication.getInstance(), LoginAct.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    ShiHuiActivityManager.getInstance().cleanActivity();
//                    intent.putExtra("isChangePass", true);
//                    BeeApplication.getInstance().startActivity(intent);
                } else if (t.getStatus() == 201) {
                    //token状态失效，刷新token
                    if(specialError != null){
                        specialError.callback(t);
                    }else{
                        requestCallback.requestError(3, "数据回调后处理错误");
                    }
                } else {
                    //请求错误回调
                    requestCallback.requestError(t.getStatus(), t.getMsg());
                }
            } else {
                requestCallback.requestError(3, "数据获取失败");
            }
        } catch (Exception e) {
            Log.e("requestCallback", "数据回调后处理错误: " + e.toString());
            e.printStackTrace();
            if(baseView == null)return t;
            if(requestCallback == null)return t;
            requestCallback.requestError(3, "数据回调后处理错误");
        }
        return t;
    }
}
