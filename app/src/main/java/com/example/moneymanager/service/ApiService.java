package com.example.moneymanager.service;

import com.example.moneymanager.Model.UpdateAppInfo;

import retrofit.http.GET;
import rx.Observable;

public interface ApiService {
    @GET("Money-manager-app/development/app/update-changelog.json")
    Observable<UpdateAppInfo> getUpdateInfo();
}
