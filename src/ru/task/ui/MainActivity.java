package ru.task.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import ru.task.R;
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
public class MainActivity extends Activity {

    private TwittArrayAdapter adapter;
    private List<Twitt> twitts;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }


    public void myClickHandler(View view) {


        EditText editText = (EditText) findViewById(R.id.editText);

        twitts = new ArrayList<Twitt>();
        adapter = new TwittArrayAdapter(this,
                R.layout.list, twitts, editText.getText());
        ListView listView = (ListView) findViewById(R.id.listView);
        adapter.addTwitts();
        listView.setAdapter(adapter);

    }


}
