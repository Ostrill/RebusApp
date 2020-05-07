package com.vladimir.rebusapp.api;

import com.vladimir.rebusapp.database.rebuses.Rebus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RebusApi {

    @GET("game.json")
    Call<ArrayList<Rebus>> getRebusList();

    @GET("description.json")
    Call<Description> getDescription();

}
