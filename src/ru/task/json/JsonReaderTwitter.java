package ru.task.json;

import android.util.JsonReader;
import ru.task.utils.Twitt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 30.11.12
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class JsonReaderTwitter {


    public static List<Twitt> readInputStream(InputStream is) throws IOException {
        return readJsonStream(is);
    }

    private static List<Twitt> readJsonStream(InputStream is) throws IOException {
        Reader isReader = new InputStreamReader(is, "UTF-8");
        JsonReader reader = new JsonReader(isReader);
        return readMessagesArray(reader);
    }

    private static List<Twitt> readMessagesArray(JsonReader reader) throws IOException {
        List<Twitt> twitts = new LinkedList<Twitt>();
        reader.beginObject();
        String tag = reader.nextName();
        while (!tag.equals("results")) {
            reader.skipValue();
            tag = reader.nextName();
        }
        reader.beginArray();
        while (reader.hasNext()) {
            twitts.add(readMessage(reader));
        }
        reader.endArray();
        return twitts;
    }

    private static Twitt readMessage(JsonReader reader) throws IOException {
        String id_str = null;
        String user = null;
        String profile_image_url = null;
        String text = null;
        String url;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id_str")) {
                id_str = reader.nextString();
            } else if (name.equals("from_user")) {
                user = reader.nextString();
            } else if (name.equals("profile_image_url")) {
                profile_image_url = reader.nextString();
            } else if (name.equals("text")) {
                text = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        url = "https://mobile.twitter.com/" + user + "/status/" + id_str;
        return new Twitt(profile_image_url, text, url);
    }
}