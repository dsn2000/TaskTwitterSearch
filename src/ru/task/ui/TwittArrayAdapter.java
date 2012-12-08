package ru.task.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.task.R;
import ru.task.image.ImageCache;
import ru.task.image.*;
import ru.task.json.*;
import ru.task.utils.ObjectHttpResult;
import ru.task.utils.Twitt;

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
    public final static String EXTRA_MESSAGE_URL = "ru.task.ui.MESSAGE_URL";
    private final Integer twittsInPage = 20;
    private final int resource;
    private final ImageManager mImageManager;
    private final String textSearch;

    public TwittArrayAdapter(Context context,
                             int resource,
                             List<Twitt> items,
                             Editable textSearch) {
        super(context, resource, items);
        this.resource = resource;
        jsonReaderTwitterHttp = new JsonReaderTwitterHttp((Activity) context);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(context, 0.25f);
        // Set memory cache to 25% of mem class
        mImageManager = new ImageManager(context);
        mImageManager.addImageCache(cacheParams);
        mImageManager.setImageFadeIn(true);
        this.textSearch = textSearch.toString();
    }


    @Override
    public void addHttpResult(Object result) {
        if (result != null) {
            List<Twitt> twitts = (List<Twitt>) result;
            addAll(twitts);
        }
    }


    static class ViewHolder {
        ImageView iconView;
        TextView textView;
        Button webButton;
        int position;
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
            holder.position = position;
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        mImageManager.loadImage(classInstance.getProfile_image_url(), holder.iconView);
        holder.webButton.setTag(classInstance.getUrl());
        holder.webButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Button webButton = (Button) v.findViewById(R.id.button);
                String url = (String) webButton.getTag();

                Intent intent = new Intent(webButton.getContext(), WebActivity.class);
                intent.putExtra(EXTRA_MESSAGE_URL, url);
                webButton.getContext().startActivity(intent);
            }

        });

        holder.textView.setText(classInstance.getText());
        if (position > getCount() - 2) {
            System.out.println(position);
            addTwitts();
        }

        return rowView;
    }


    public void addTwitts() {

        String queryUtf8 = null;
        try {
            queryUtf8 = URLEncoder.encode(textSearch, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(queryUtf8);
        jsonReaderTwitterHttp.downloadUrl("http://search.twitter.com/search.json?q=" + queryUtf8 + "&rpp=" + twittsInPage.toString() +"&include_entities=false&result_type=mixed&page=" + pageNumber, this);

        pageNumber++;
    }

}

