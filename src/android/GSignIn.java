package tumblr.corinzio.cordova.plugins.signin;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
/**
 * This class manage the Google Sign In flow
 */
public class GSignIn extends CordovaPlugin{
    private static final String LOG_TAG = "GSignIn";

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      // your init code here
      Log.d(LOG_TAG,"init plugin gsignin");
  }
  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
      if ("echo".equals(action)) {
          final long duration = args.getLong(0);
          cordova.getThreadPool().execute(new Runnable() {
              public void run() {
                  String message = args.getString(0);
                  callbackContext.success(message); // Thread-safe.
              }
          });
          return true;
      }
      return false;
  }
}