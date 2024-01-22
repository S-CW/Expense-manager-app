package com.example.moneymanager.service;

import com.example.moneymanager.Model.UpdateAppInfo;

import retrofit.http.GET;
import rx.Observable;

public interface ApiService {
    @GET("api/get-version-info")
    Observable<UpdateAppInfo> getUpdateInfo();
}
