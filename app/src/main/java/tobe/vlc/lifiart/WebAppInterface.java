package tobe.vlc.lifiart;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    Context mContext;
    MainActivity ma;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c, MainActivity ma) {
        mContext = c;
        this.ma = ma;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void getZones(String museumId, String locationId) {
            ma.setContentByTextId(museumId, locationId);
    }
}