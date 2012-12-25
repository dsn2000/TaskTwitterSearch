package ru.task.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.task.R;
import ru.task.image.ImageCache;
import ru.task.image.*;
import ru.task.json.JsonReaderTwitterHttp;
import ru.task.utils.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 27.11.12
 * Time: 21:49
 * To change this template use File | Settings | File Templates.
 */
public class TwittArrayAdapter extends ArrayAdapter<Twitt> implements ObjectHttpResult {

    private Integer pageNumber = 1;
    private final JsonReaderTwitterHttp jsonReaderTwitterHttp;
    private final int resource;
    private final ImageManager mImageManager;
    private final String textSearch;
    private final MsgBox msgBox;
    private boolean fullList;
    private String tweetsNumberOf;
    private boolean tweetsBrowser;
    private boolean downloadImage;

    public TwittArrayAdapter(Context context,
                             int resource,
                             List<Twitt> items,
                             String textSearch, MsgBox msgBox) {
        super(context, resource, items);
        this.resource = resource;
        this.msgBox = msgBox;
        jsonReaderTwitterHttp = new JsonReaderTwitterHttp((Activity) context);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(context, 0.25f);
        // Set memory cache to 25% of mem class
        mImageManager = new ImageManager(context);
        mImageManager.addImageCache(cacheParams);
        mImageManager.setImageFadeIn(true);
        this.textSearch = textSearch;
        fullList = false;
    }

    @Override
    public void addHttpResult(Object result, int type) {
        if (result != null) {
            List<Twitt> twitts = (List<Twitt>) result;
            if (twitts.size() != 0) {
                addAll(twitts);
            }
            else {
                fullList = true;
            }
        } else if (type == Message.NOT_INTERNET) {
            msgBox.runMsgBox("No internet results ", "Exit", Message.NOT_INTERNET);
        }
        ((MainActivity) getContext()).setProgressVisibility(false);
    }

    public class ViewHolder implements ObjectHttpResult {
        ImageView iconView;
        TextView textView;
        Button webButton;
        ProgressBar progressBar;
        int position;

        public ImageView getIconView() {
            return iconView;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        @Override
        public void addHttpResult(Object bitmap, int type) {
            if (bitmap != null) {
                iconView.setImageBitmap((Bitmap) bitmap);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            } else if (type == Message.NOT_INTERNET) {
                msgBox.runMsgBox("No internet results ", "Exit", Message.NOT_INTERNET);
            }
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        Twitt classInstance = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(resource, null, true);
            holder = new ViewHolder();
            holder.iconView = (ImageView) rowView.findViewById(R.id.imageView);
            holder.textView = (TextView) rowView.findViewById(R.id.textView);
            holder.webButton = (Button) rowView.findViewById(R.id.button);
            holder.progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar);
            holder.position = position;
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        if (downloadImage) {
            holder.progressBar.setVisibility(ProgressBar.VISIBLE);
            mImageManager.loadImage(classInstance.getProfileImageUrl(), holder);
        }
        else {
            holder.progressBar.setVisibility(ProgressBar.INVISIBLE);
            holder.getIconView().setImageBitmap(null);
        }
        holder.webButton.setTag(classInstance.getUrl());
        holder.webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button webButton = (Button) v.findViewById(R.id.button);
                String url = (String) webButton.getTag();
                Intent intent;
                if (tweetsBrowser) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                }
                else {
                    url += "/actions";
                    intent = new Intent(webButton.getContext(), WebActivity.class);
                    intent.putExtra(Message.EXTRA_MESSAGE_URL, url);
                }
                webButton.getContext().startActivity(intent);
            }
        });

        holder.textView.setText(classInstance.getText());
        if (position > getCount() - 2  && !fullList) {
            addTwitts();
        }

        return rowView;
    }

    public void addTwitts() {
        ((MainActivity) getContext()).setProgressVisibility(true);
        String queryUtf8 = null;
        try {
            queryUtf8 = URLEncoder.encode(textSearch, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            msgBox.runMsgBox("Bad query ", "OK", Message.BAD_QUERY);
        }
        jsonReaderTwitterHttp.downloadUrl("http://search.twitter.com/search.json?q=" + queryUtf8 + "&rpp=" + tweetsNumberOf + "&include_entities=false&result_type=mixed&page=" + pageNumber, this);

        pageNumber++;
    }

    public void getPrefsВownloadImage() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        downloadImage = prefs.getBoolean(Preferences.CHECKBOX_DOWNLOAD_IMAGE_PREF, true);
    }

    public void getPrefsTweetsBrowser() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        tweetsBrowser = prefs.getBoolean(Preferences.CHECKBOX_TWEETS_BROWSER_PREF, false);
  }

    public void getPrefsTweetsNumberOf() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        tweetsNumberOf = prefs.getString(Preferences.LIST_TWEETS_NUMBEROF_PREF, "20");
    }
}
