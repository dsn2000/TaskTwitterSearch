package ru.task.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 30.11.12
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class HttpSocket<Result_, ObjectForResult_> {


    private final Activity context;

    protected HttpSocket(Activity context) {
        this.context = context;
    }

    public void downloadUrl(String stringUrl, ObjectForResult_ objectForResult) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            HttpSocketAsyncTask download = new HttpSocketAsyncTask<String, Void, Result_, ObjectForResult_>(objectForResult);
            download.execute(stringUrl);
        } else {
            System.out.println("No network connection available.");
        }
    }

    protected abstract Result_ readerInputStream(InputStream is, HttpSocketAsyncTask httpSocketAsyncTask) throws IOException;

    protected abstract void getHttpResult(Result_ result, ObjectForResult_ objectForResult);


    protected class HttpSocketAsyncTask<Params, Progress, Result, ObjectForResult> extends AsyncTask<Params, Progress, Result> {
        private Object data;
        private final WeakReference<ObjectForResult> objectForResult;

        public Object getData() {
            return data;
        }


        public HttpSocketAsyncTask(ObjectForResult objectForResult) {
            this.objectForResult = new WeakReference<ObjectForResult>(objectForResult);
        }

        @Override
        protected void onPostExecute(Result result) {
            if (!isCancelled()) {
                getHttpResult((Result_) result, (ObjectForResult_) objectForResult.get());
            }
        }

        @Override
        protected Result doInBackground(Params... objects) {

            try {
                data = objects[0];
                //final String dataString = String.valueOf(data);
                return downloadUrl(objects[0]);
            } catch (IOException e) {
                System.out.println("Unable to retrieve web page. URL may be invalid.");
                return null;
            }

        }


        private Result downloadUrl(Params pUrl) throws IOException {
            if (!isCancelled()) {
                InputStream is = null;
                HttpURLConnection conn = null;
                try {
                    String sUrl = (String) pUrl;
                    URL url = new URL(sUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    int response = conn.getResponseCode();
                    System.out.println(response);

                    is = conn.getInputStream();

                    return (Result) readerInputStream(is, this);

                } finally {

                    if (conn != null) {
                        conn.disconnect();
                    }
                    if (is != null) {
                        is.close();
                    }
                }
            }
            return null;
        }

        public ObjectForResult getAttachedObjectForResult() {

            return objectForResult.get();
        }
    }


}