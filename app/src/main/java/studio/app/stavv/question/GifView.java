package studio.app.stavv.question;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import java.io.IOException;

/**
 * Created by vicru on 30.09.2017.
 */

public class GifView extends WebView {

        public GifView(Context context, String path) {
            super(context);
            loadUrl(path);
            this.setBackgroundColor(0x00FFFFFF);
        }
    public GifView(Context context, String path, int scale) {
        super(context);
        loadUrl(path);
        this.setBackgroundColor(0x00FFFFFF);
        this.setPadding(0, 0, 0, 0);
        this.setInitialScale(scale);
    }



}
