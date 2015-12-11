package com.fgossa.nowcastparseproject;

/**
 * Created by FGO on 12/10/15.
 */

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MomentApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Moment.class);
        Parse.initialize(this, "Q1CVvGdv8OlpFCbDK6xMY69zYmdbMElHuVVAvtui", "llIA9q1G35B5Oo4o7IUQxpFPIyPUcxfqweIJLRim");
        ParseInstallation.getCurrentInstallation().saveInBackground();
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}