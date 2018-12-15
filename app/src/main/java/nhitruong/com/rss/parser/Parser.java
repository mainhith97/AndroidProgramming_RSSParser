package nhitruong.com.rss.parser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class Parser extends AsyncTask<String, Void, String> implements Observer {

    private XMLParser xmlParser;
    private static ArrayList<Article> articles = new ArrayList<>();

    private OnTaskCompleted onComplete;

    public Parser() {

        xmlParser = new XMLParser();
        xmlParser.addObserver(this);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(ArrayList<Article> list);

        void onError(Exception exception);
    }

    public void onFinish(OnTaskCompleted onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    protected String doInBackground(String... ulr) {

        Response response = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ulr[0])
                .build();

        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful())
                return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            onComplete.onError(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null) {
            try {
                xmlParser.parseXML(result);
                Log.i("RSS Parser ", "RSS parsed correctly!");
            } catch (Exception e) {
                e.printStackTrace();
                onComplete.onError(e);
            }
        } else
            onComplete.onError(new Exception("RSS parse operation returned null"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable observable, Object data) {
        articles = (ArrayList<Article>) data;
        onComplete.onTaskCompleted(articles);
    }

}
