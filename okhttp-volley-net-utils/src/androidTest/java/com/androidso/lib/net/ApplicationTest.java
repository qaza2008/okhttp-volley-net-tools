package com.androidso.lib.net;

import android.app.Application;
import android.support.v4.database.DatabaseUtilsCompat;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test(){
        int a = 10;
        assertEquals(a,10);

    }

}