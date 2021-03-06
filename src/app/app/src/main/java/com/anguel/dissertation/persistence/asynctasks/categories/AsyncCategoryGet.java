package com.anguel.dissertation.persistence.asynctasks.categories;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.entity.appcategory.AppCategory;
import com.anguel.dissertation.persistence.entity.appcategory.AppCategoryDatabase;

import java.util.List;

public class AsyncCategoryGet extends AsyncTask<Context, Void, List<AppCategory>> {
    @Override
    protected List<AppCategory> doInBackground(Context... objects) {
        return AppCategoryDatabase.getInstance(objects[0]).appCategoryDao().getAll();
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_category", "started getting category");
    }
}
