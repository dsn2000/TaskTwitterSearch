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
public abstract class HttpSocket<Result_> {

    private final Activity context;

    protected HttpSocket(Activity context) {
        this.context = context;
    }

    public void downloadUrl(String stringUrl, ObjectHttpResult objectForResult) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            HttpSocketAsyncTask download = new HttpSocketAsyncTask<String, Void, Result_>(objectForResult);
            download.execute(stringUrl);
        } else {
            objectForResult.addHttpResult(null, Message.NOT_INTERNET);
        }
    }

    protected abstract Result_ readerInputStream(InputStream is, HttpSocketAsyncTask httpSocketAsyncTask) throws IOException;

    protected class HttpSocketAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
        private Object data;
        private final WeakReference<ObjectHttpResult> objectHttpResult;
        private int error = 0;

        public Object getData() {
            return data;
        }

        public HttpSocketAsyncTask(ObjectHttpResult objectHttpResult) {
            this.objectHttpResult = new WeakReference<ObjectHttpResult>(objectHttpResult);
        }

        @Override
        protected void onPostExecute(Result result) {
            ObjectHttpResult objectHttpResultRef = objectHttpResult.get();
            if (objectHttpResultRef != null) {
                objectHttpResultRef.addHttpResult(result, error);
            }
        }

        @Override
        protected Result doInBackground(Params... objects) {

            try {
                data = objects[0];
                return downloadUrl(objects[0]);
            } catch (IOException e) {
                e.printStackTrace();
                error = Message.NOT_INTERNET;
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

        public ObjectHttpResult getAttachedObjectForResult() {

            return objectHttpResult.get();
        }
    }


}