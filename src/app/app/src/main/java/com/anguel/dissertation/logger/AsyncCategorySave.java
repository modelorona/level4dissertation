package com.anguel.dissertation.logger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.appcategory.AppCategory;
import com.anguel.dissertation.persistence.appcategory.AppCategoryDatabase;

public class AsyncCategorySave extends AsyncTask<Object, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Object... params) {
        Context c= (Context) params[0];
        AppCategory appCategory = (AppCategory) params[1];

        AppCategoryDatabase.getInstance(c).appCategoryDao().insertAppCategory(appCategory);
        return true;
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_category", "started saving category");
    }
}
