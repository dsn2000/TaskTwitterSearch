package ru.task.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import ru.task.R;
import ru.task.utils.ActivityMsgBox;
import ru.task.utils.Message;
import ru.task.utils.MsgBox;
import ru.task.utils.Twitt;

import java.util.ArrayList;
import java.util.List;

//import android.util.JsonReader;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 30.11.12
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity implements ActivityMsgBox {
    private static final int PROGRESS = 0x1;
    private ProgressBar mProgress;
    private ProgressBar mProgressFooter;
    private int mProgressStatus = 0;
    private MsgBox msgBox;
    private Handler mHandler = new Handler();
    private View footer;

    private TwittArrayAdapter adapter;
    private List<Twitt> twitts;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        msgBox = new MsgBox(this, getResources().getString(R.string.app_name));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            msgBox.runMsgBox("This version android is not supported ", "Exit", Message.NOT_SUPPORTED);
        } else {
            footer = createFooter();
            listView = (ListView) findViewById(R.id.listView);
            listView.addFooterView(footer);

            mProgress = (ProgressBar) findViewById(R.id.progressBar);
            mProgressFooter = (ProgressBar) findViewById(R.id.progressBarFooter);
            setProgressVisibility(false);
        }
    }

    public void setProgressVisibility(boolean status) {
        if (status) {
            mProgress.setVisibility(ProgressBar.VISIBLE);
            mProgressFooter.setVisibility(ProgressBar.VISIBLE);
        } else {
            mProgress.setVisibility(ProgressBar.INVISIBLE);
            mProgressFooter.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    View createFooter() {
        View v = getLayoutInflater().inflate(R.layout.footer, null);
        return v;
    }

    public void myClickHandler(View view) {

        EditText editText = (EditText) findViewById(R.id.editText);
        String searchText = editText.getText().toString();
        if (!searchText.equals("")) {
            twitts = new ArrayList<Twitt>();
            adapter = new TwittArrayAdapter(this,
                    R.layout.list, twitts, searchText, msgBox);
            adapter.getPrefsВownloadImage();
            adapter.getPrefsTweetsBrowser();
            adapter.getPrefsTweetsNumberOf();
            adapter.addTwitts();
            listView.setAdapter(adapter);
        } else {
            msgBox.runMsgBox("Search query is empty ", "OK", Message.SEARCH_QUERY_EMPTY);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SETTINGS || keyCode == KeyEvent.KEYCODE_MENU) {
            Intent settingsActivity = new Intent(this, Preferences.class);
            startActivityForResult(settingsActivity, Message.REQUEST_CODE_SETTING);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && adapter != null) {
            switch (requestCode) {
                case Message.REQUEST_CODE_SETTING:
                    boolean updateDownloadImage = data.getBooleanExtra(Preferences.CHECKBOX_DOWNLOAD_IMAGE_PREF, false);
                    if (updateDownloadImage) {
                        adapter.getPrefsВownloadImage();
                    }
                    boolean updateTweetsBrowser = data.getBooleanExtra(Preferences.CHECKBOX_TWEETS_BROWSER_PREF, false);
                    if (updateTweetsBrowser) {
                        adapter.getPrefsTweetsBrowser();
                    }
                    boolean updateTweetsNumberOf = data.getBooleanExtra(Preferences.LIST_TWEETS_NUMBEROF_PREF, false);
                    if (updateTweetsNumberOf) {
                        adapter.getPrefsTweetsNumberOf();
                    }
                    break;
            }
        }
    }

    @Override
    public void action(int type) {
        switch (type) {
            case Message.NOT_SUPPORTED:
                finish();
                break;
            case Message.NOT_INTERNET:
                finish();
                break;
            case Message.BAD_QUERY:
                EditText editText = (EditText) findViewById(R.id.editText);
                editText.setText("");
                break;
            case Message.SEARCH_QUERY_EMPTY:
                break;
        }
    }
}
