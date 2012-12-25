package ru.task.json;

import android.app.Activity;
import ru.task.utils.HttpSocket;
import ru.task.utils.Twitt;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 05.12.12
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */


public class JsonReaderTwitterHttp extends HttpSocket<List<Twitt>> {

    public JsonReaderTwitterHttp(Activity context) {
        super(context);
    }

    @Override
    protected List<Twitt> readerInputStream(InputStream is, HttpSocketAsyncTask httpSocketAsyncTask) throws IOException {
        return JsonReaderTwitter.readInputStream(is);
    }

}

