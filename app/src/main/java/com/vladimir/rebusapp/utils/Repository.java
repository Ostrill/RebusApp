package com.vladimir.rebusapp.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vladimir.rebusapp.api.Description;
import com.vladimir.rebusapp.api.RebusApi;
import com.vladimir.rebusapp.database.AppDatabase;
import com.vladimir.rebusapp.database.tablelevels.Level;
import com.vladimir.rebusapp.database.tablerebuses.Rebus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vladimir.rebusapp.utils.Constants.HELP_PRICE;
import static com.vladimir.rebusapp.utils.Constants.REBUSES_FOR_UNLOCK_LEVEL;
import static com.vladimir.rebusapp.utils.Constants.REBUSES_QUANTITY_FOR_LEVEL;
import static com.vladimir.rebusapp.utils.Constants.SCORE_FOR_ONE_STAR;
import static com.vladimir.rebusapp.utils.Constants.SP_FIRST_LAUNCH;
import static com.vladimir.rebusapp.utils.Constants.SP_NAME;
import static com.vladimir.rebusapp.utils.Constants.SP_SCORE;
import static com.vladimir.rebusapp.utils.Constants.SP_VERSION;

public class Repository extends Application {

    public static Repository instance;
    private static final String TAG = "Repository";

    public static final int LOADING_NOT_STARTED = 0;
    public static final int LOADING_DB_COMPLETED = 1;
    public static final int LOADING_SYNC_COMPLETED = 2;
    public static final int LOADING_SYNC_WITHOUT_RESULT = 3;
    public static final int LOADING_GLIDE_STARTS = 4;

    private AppDatabase mDatabase;
    private RebusApi mRebusApi;

    private List<Rebus> mAllRebuses;

    private MutableLiveData<Integer> mGlideLoadingPercentLiveData;
    private MutableLiveData<Integer> mLoadingStateLiveData;
    private boolean mIsSynchronized;
    private boolean mSynchronizationIsNow;
//    private boolean synchronizationCompleted;

    private SharedPreferences mSharedPreferences;

    private FirebaseStorage mStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mDatabase = Room
                .databaseBuilder(this, AppDatabase.class, "app_db")
                .fallbackToDestructiveMigration()
                .build();

        mRebusApi = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RebusApi.class);

        mStorage = FirebaseStorage.getInstance();

        mSharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        mAllRebuses = new ArrayList<>();
        mLoadingStateLiveData = new MutableLiveData<>();
        mGlideLoadingPercentLiveData = new MutableLiveData<>();

        mLoadingStateLiveData.postValue(LOADING_NOT_STARTED);
        mGlideLoadingPercentLiveData.postValue(0);
        mIsSynchronized = false;
        mSynchronizationIsNow = false;
//        synchronizationCompleted = false;

        subscribeOnDatabase();
    }

    @SuppressLint("CheckResult")
    private void subscribeOnDatabase() {
        mDatabase.levelDao().getAllRebuses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rebuses -> {
                    Log.d(TAG, "Rebuses from DB has been changed!");

                    mAllRebuses = rebuses;

                    if (!mSynchronizationIsNow) {
                        mLoadingStateLiveData.postValue(LOADING_DB_COMPLETED);
                    }

                    if (!mIsSynchronized) {
                        startSynchronization();
                        mIsSynchronized = true;
                    }
                });
    }

    public boolean isSynchronizationCompleted() {
//        return  synchronizationCompleted;
        return mIsSynchronized && !mSynchronizationIsNow;
    }

    public void setScore(int score) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(SP_SCORE, score).apply();
    }

    public int getScore() {
        return mSharedPreferences.getInt(SP_SCORE, 0);
    }

    public void setVersion(int score) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(SP_VERSION, score).apply();
    }

    public int getVersion() {
        return mSharedPreferences.getInt(SP_VERSION, -1);
    }

    public void completeFirstLaunch() {
        mSharedPreferences.edit().putBoolean(SP_FIRST_LAUNCH, true).apply();
    }

    public boolean checkFirstLaunch() {
        return mSharedPreferences.getBoolean(SP_FIRST_LAUNCH, false);
    }

    public static Repository getInstance() { return instance; }

    private void startSynchronization() {
        Log.d(TAG, "#startSynchronization");
        mSynchronizationIsNow = true;
        mRebusApi.getDescription()
                .enqueue(new Callback<Description>() {
                    @Override
                    public void onResponse(
                            Call<Description> call,
                            Response<Description> response
                    ) {
                        if (response.body() == null) {
                            endSynchronization(LOADING_SYNC_WITHOUT_RESULT);
                            return;
                        }
                        if (getVersion() == response.body().version) {
                            endSynchronization(LOADING_SYNC_WITHOUT_RESULT);
                        } else {
                            synchronizeWithFirebase(response.body().version);
                        }
                    }

                    @Override
                    public void onFailure(Call<Description> call, Throwable t) {
                        endSynchronization(LOADING_SYNC_WITHOUT_RESULT);
                    }
                });
    }

    private void synchronizeWithFirebase(int version) {
        Log.d(TAG, "#synchronizeWithFirebase");
        mRebusApi.getRebusList()
                .enqueue(new Callback<ArrayList<Rebus>>() {
                    @Override
                    public void onResponse(
                            Call<ArrayList<Rebus>> call,
                            Response<ArrayList<Rebus>> response
                    ) {
                        Log.i(TAG, "successful firebase transaction!");

                        ArrayList<Rebus> body;
                        if (response.body() == null) {
                            body = new ArrayList<>();
                        } else {
                            body = response.body();
                        }

                        new Thread(() -> {
                            compareDatabases(body);
                            setVersion(version);
                            endSynchronization(LOADING_SYNC_COMPLETED);
                        }).start();
                    }

                    @Override
                    public void onFailure(
                            Call<ArrayList<Rebus>> call,
                            Throwable t
                    ) {
                        endSynchronization(LOADING_SYNC_WITHOUT_RESULT);
                    }
                });
    }

    private void endSynchronization(int syncState) {
        Log.i(TAG, "synchronization completed!");
        if (syncState == LOADING_SYNC_COMPLETED) {
            mLoadingStateLiveData.postValue(LOADING_GLIDE_STARTS);
            preloadImages();
        } else {
            mSynchronizationIsNow = false;
//            synchronizationCompleted = true;
            mLoadingStateLiveData.postValue(syncState);
        }
    }

    public LiveData<Integer> getGlideLoadingPercentLiveData() {
        return mGlideLoadingPercentLiveData;
    }

    private void preloadImages() {
        final int[] counter = {0};
        for (int i = 0; i < mAllRebuses.size(); ++i) {
            Rebus current = mAllRebuses.get(i);
            Log.wtf(TAG, "current id is " + current.rebusId);
            Glide.with(getApplicationContext())
                    .load(getStorageRef().child(current.rebusId + ".png"))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(
                                @Nullable GlideException e,
                                Object model, Target<Drawable> target,
                                boolean isFirstResource
                        ) {
                            counter[0]++;
                            Log.d(TAG, "#preloading " + counter[0] + "/" + mAllRebuses.size());
                            checkEndPreload(counter[0]);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(
                                Drawable resource, Object model,
                                Target<Drawable> target, DataSource dataSource,
                                boolean isFirstResource
                        ) {
                            counter[0]++;
                            Log.d(TAG, "#preloading " + counter[0] + "/" + mAllRebuses.size());
                            checkEndPreload(counter[0]);
                            return false;
                        }
                    })
                    .preload();
        }
        if (mAllRebuses.size() == 0) mLoadingStateLiveData.postValue(LOADING_SYNC_COMPLETED);
    }

    public void completeLoading() {
        mLoadingStateLiveData.postValue(LOADING_SYNC_COMPLETED);
    }

    private void checkEndPreload(int counter) {
        if (counter == mAllRebuses.size()) {
            mSynchronizationIsNow = false;
//            synchronizationCompleted = true;
            mLoadingStateLiveData.postValue(LOADING_SYNC_COMPLETED);
        }
        mGlideLoadingPercentLiveData.postValue(counter * 100 / mAllRebuses.size());
    }

    private void compareDatabases(ArrayList<Rebus> fromFirebase) {
        Log.d(TAG, "#compareDatabases");
        mDatabase.levelDao().clearTable();
        mDatabase.rebusDao().clearUnsolved();
        for (int i = 0; i < fromFirebase.size(); ++i) {
            Rebus current = fromFirebase.get(i);
            if (!mDatabase.rebusDao().isRebusExists(current.rebusId)) {
                current.score = 0;
                mDatabase.rebusDao().insertNewRebus(current);
            } else {
                current.score = mDatabase.rebusDao().getScoreById(current.rebusId);
            }
            mDatabase.levelDao().addNewLevel(new Level(i, current.rebusId));
        }
    }

    public boolean isLevelUnlocked(int levelIndex) {
        int level = getLevel(levelIndex);
        return getSolvedQuantity() / REBUSES_FOR_UNLOCK_LEVEL >= level;
    }

    public int getSolvedQuantity() {
        int quantity = getAvailableRebusesQuantity();
        int solved = 0;
        for (int i = 0; i < quantity; ++i) {
            if (mAllRebuses.get(i).score != 0) solved++;
        }
        return solved;
    }

    public int getAvailableRebusesQuantity() {
        return mAllRebuses.size() / REBUSES_QUANTITY_FOR_LEVEL *
                REBUSES_QUANTITY_FOR_LEVEL;
    }

    public String levelsToUnlock(int levelIndex) {
        int level = getLevel(levelIndex);
        int quantity = level * REBUSES_FOR_UNLOCK_LEVEL - getSolvedQuantity();
        return Integer.toString(quantity);
    }

    public StorageReference getStorageRef() {
        return mStorage.getReference();
    }

    static public int getLevel(int levelIndex) {
        return levelIndex / REBUSES_QUANTITY_FOR_LEVEL;
    }

    static public int getSubLevel(int levelIndex) {
        return levelIndex % REBUSES_QUANTITY_FOR_LEVEL;
    }

    public void solveRebus(int levelIndex) {
        int score = SCORE_FOR_ONE_STAR * mAllRebuses.get(levelIndex).difficulty;
        completeSolving(levelIndex, score);
    }

    public void buySolution(int levelIndex) {
        int score = -HELP_PRICE;
        completeSolving(levelIndex, score);
    }

    private void completeSolving(int levelIndex, int score) {
        setScore(getScore() + score);
        new Thread(() ->
                mDatabase.rebusDao().solveRebus(
                    mAllRebuses.get(levelIndex).rebusId,
                    score
                )
        ).start();
    }

    public List<Rebus> getAllRebuses() {
        return mAllRebuses;
    }

    public LiveData<Integer> getLoadingStateLiveData() {
        return mLoadingStateLiveData;
    }

    public void resetAllProgress() {
        setScore(0);
        new Thread(() -> mDatabase.rebusDao().resetSolved()).start();
    }

}
