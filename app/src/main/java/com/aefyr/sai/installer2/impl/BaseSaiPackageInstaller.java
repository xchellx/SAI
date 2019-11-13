package com.aefyr.sai.installer2.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.aefyr.sai.installer2.base.SaiPackageInstaller;
import com.aefyr.sai.installer2.base.SaiPiSessionObserver;
import com.aefyr.sai.installer2.base.model.SaiPiSessionParams;
import com.aefyr.sai.installer2.base.model.SaiPiSessionState;
import com.aefyr.sai.installer2.base.model.SaiPiSessionStatus;
import com.aefyr.sai.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@SuppressLint("UseSparseArrays")
public abstract class BaseSaiPackageInstaller implements SaiPackageInstaller {

    private Context mContext;
    private long mLastSessionId = 0;

    private ConcurrentHashMap<String, SaiPiSessionParams> mCreatedSessions = new ConcurrentHashMap<>();

    private ConcurrentSkipListMap<String, SaiPiSessionState> mSessionStates = new ConcurrentSkipListMap<>();

    private Set<SaiPiSessionObserver> mObservers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected BaseSaiPackageInstaller(Context c) {
        mContext = c.getApplicationContext();
    }

    @Override
    public String createSession(SaiPiSessionParams params) {
        String sessionId = newSessionId();
        mCreatedSessions.put(sessionId, params);
        setSessionState(sessionId, new SaiPiSessionState(sessionId, SaiPiSessionStatus.CREATED));
        return sessionId;
    }

    @Override
    public void registerSessionObserver(SaiPiSessionObserver observer) {
        mObservers.add(observer);
    }

    @Override
    public void unregisterSessionObserver(SaiPiSessionObserver observer) {
        mObservers.remove(observer);
    }

    @Override
    public List<SaiPiSessionState> getSessions() {
        return Collections.unmodifiableList(new ArrayList<>(mSessionStates.values()));
    }

    protected void setSessionState(String sessionId, SaiPiSessionState state) {
        mSessionStates.put(sessionId, state);
        Log.d(tag(), state.toString() + "\n" + Utils.throwableToString(new Exception()));
        Utils.onMainThread(() -> {
            for (SaiPiSessionObserver observer : mObservers)
                observer.onSessionStateChanged(state);
        });
    }

    protected SaiPiSessionParams takeCreatedSession(String sessionId) {
        return mCreatedSessions.remove(sessionId);
    }

    @SuppressLint("DefaultLocale")
    protected String newSessionId() {
        long sessionId = mLastSessionId++;
        return String.format("%d@%s", sessionId, getClass().getName());
    }

    protected Context getContext() {
        return mContext;
    }

    protected abstract String tag();
}
