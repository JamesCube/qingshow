package com.focosee.qingshow.model;

import android.content.SharedPreferences;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by i068020 on 2/21/15.
 */
public enum QSModel {
    INSTANCE;

    private MongoPeople user;

    public boolean loggedin() {
        return user != null;
    }

    public MongoPeople getUser() {
        return user;
    }

    public void setUser(MongoPeople _user) {
        if(null == _user) return;
        this.user = _user;
        saveUser(_user._id);
    }

    public void saveUser(String id){
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.putString("id", id);
        editor.commit();
    }
}
