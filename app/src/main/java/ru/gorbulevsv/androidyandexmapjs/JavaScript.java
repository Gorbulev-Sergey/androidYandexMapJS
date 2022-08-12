package ru.gorbulevsv.androidyandexmapjs;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScript {
    Context context;
    JavaScript(Context c) {
        context = c;
    }
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
}
