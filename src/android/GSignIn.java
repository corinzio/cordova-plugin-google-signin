package tumblr.corinzio.cordova.plugins.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class manage the Google Sign In flow
 */
public class GSignIn extends CordovaPlugin implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String LOG_TAG = "GSignIn";
    /**
     * ACTIONS
     **/
    private static final String ACT_CONFIG = "config";
    private static final String ACT_LOGIN = "login";
    private static final String ACT_LOGOUT = "logout";
    private static final String ACT_REVOKE = "revoke";

    /**
     * LOGIN OPTION
     */
    private static final String LOGIN_CFG_SILENT = "silent";

    /**
     * CONFIG OPTIONS
     **/
    private static final String CONFIG_SERVERID = "server_client_id";
    private static final String CONFIG_SCOPES = "scopes";
    private static final String CONFIG_AUTHCODE = GSignIn.LOGIN_AUTHCODE;
    private static final String CONFIG_REFRESH = "refresh";

    /**
     * ERRORS JSON OBJECT PARAMETERS
     */
    private static final String ERROR_CODE = "code";
    private static final String ERROR_MSG = "msg";

    /**
     * JSON OBJECT LOGIN PARAMETERS
     */
    private static final String LOGIN_EMAIL = "email";
    private static final String LOGIN_PHOTO = "photourl";
    private static final String LOGIN_NAME = "name";
    private static final String LOGIN_TOKENID = "tokenid";
    private static final String LOGIN_ID = "id";
    private static final String LOGIN_AUTHCODE = "auth_code";


    /**
     * ERRORS CODE
     **/
    private static final int ERRCODE_NO_APICLIENT = 401;
    private static final int ERRCODE_CONNECTION_FAILED = 402;
    private static final int ERRCODE_CONNECTION_SUSPENDED = 403;
    private static final int ERRCODE_LOGIN_FAILED = 404;
    private static final int ERRCODE_LOGOUT_FAILED = 405;
    private static final int ERRCODE_NOT_CONNECTED = 406;
    private static final int ERRCODE_REVOKE_FAILED = 407;
    /**
     * ERRORS MESSAGE
     **/
    private static final String ERRMSG_NO_APICLIENT = "Google Api Client not present or configured";
    private static final String ERRMSG_NET_LOST = "Network lost, automatic retry...";
    private static final String ERRMSG_SRV_KILLED = "Service killed, automatic retry...";
    private static final String ERRMSG_LOGIN_FAILED = "Login Failed";
    private static final String ERRMSG_LOGOUT_FAILED = "Logout Failed";
    private static final String ERRMSG_NOT_CONNECTED = "Api not connected";
    private static final String ERRMSG_REVOKE_FAILED = "Revoke failed:";

    /**
     * REQUEST CODES
     */
    private static final int RC_SIGN_IN = 9001;

    private final AtomicBoolean m_running = new AtomicBoolean(false);
    private GoogleApiClient m_gApiClient;
    private CallbackContext loginCallback;
    private CallbackContext connectCallback;
    private CallbackContext logoutCallback;
    private CallbackContext revokeCallback;

    /**
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return true if action is valid
     * @throws JSONException
     */
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        /**Configuration
         * first creates a new api client and the start connection on it**/
        if (action.equals(GSignIn.ACT_CONFIG)) {
            GoogleApiClient api = this.getM_gApiClient();
            this.setM_gApiClient(null);
            if (api != null) api.disconnect();

            this.setConnectCallback(callbackContext);

            try {
                JSONObject conf = args.getJSONObject(0);
                this.configureSignIn(conf);
                this.connect();
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
            }
            return true;
        }
        /**
         * Login
         */
        if (action.equals(GSignIn.ACT_LOGIN)) {
            if(this.check_api(callbackContext)) {
                this.setLoginCallback(callbackContext);
                this.signIn();
            }
            return true;
        }
        /**
         * Logout
         */
        if (action.equals(GSignIn.ACT_LOGOUT)) {
            if(this.check_api(callbackContext)) {
                this.setLogoutCallback(callbackContext);
                this.signOut();
            }
            return true;
        }
        /**
         * Revoke
         */
        if (action.equals(GSignIn.ACT_REVOKE)){
            if(this.check_api(callbackContext)) {
                this.setRevokeCallback(callbackContext);
            }
            return true;
        }
        return false;
    }

    private synchronized boolean check_api(final CallbackContext callbackContext){
        //verify if operations are running
        boolean isFree = this.m_running.compareAndSet(false,true);
        if(!isFree){
            //operations already running
            return false;
        }
        if(this.getM_gApiClient() == null){
            //no api configured
            try {
                JSONObject obj = new JSONObject();
                obj.put(GSignIn.ERROR_CODE,GSignIn.ERRCODE_NO_APICLIENT);
                obj.put(GSignIn.ERROR_MSG,GSignIn.ERRMSG_NO_APICLIENT);
                callbackContext.error(obj);
                return false;
            } catch (JSONException e){
                e.printStackTrace();
                return false;
            }
        }
        if(!this.getM_gApiClient().isConnected()){
            //api not connected
            try{
                JSONObject obj = new JSONObject();
                obj.put(GSignIn.ERROR_CODE,GSignIn.ERRCODE_NOT_CONNECTED);
                obj.put(GSignIn.ERROR_MSG, GSignIn.ERRMSG_NOT_CONNECTED);
                callbackContext.error(obj);
                return false;
            }catch (JSONException e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void revoke() {
        final GoogleApiClient api = this.getM_gApiClient();
        Auth.GoogleSignInApi.revokeAccess(api).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        GSignIn.this.m_running.set(false);
                        if(status.isSuccess()){
                            GSignIn.this.getRevokeCallback().success();
                        }else{
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put(GSignIn.ERROR_CODE, GSignIn.ERRCODE_REVOKE_FAILED);
                                obj.put(GSignIn.ERROR_MSG, GSignIn.ERRMSG_REVOKE_FAILED + ": " + status.getStatusMessage());
                                GSignIn.this.getLogoutCallback().error(obj);
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void signOut() {
        final GoogleApiClient api = this.getM_gApiClient();
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    Auth.GoogleSignInApi.signOut(api).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            //operation ended
                            GSignIn.this.m_running.set(false);
                            if (status.isSuccess()) {
                                GSignIn.this.getLogoutCallback().success();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject();
                                    obj.put(GSignIn.ERROR_CODE, GSignIn.ERRCODE_LOGOUT_FAILED);
                                    obj.put(GSignIn.ERROR_MSG, GSignIn.ERRMSG_LOGOUT_FAILED + ": " + status.getStatusMessage());
                                    GSignIn.this.getLogoutCallback().error(obj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
    }

    private void signIn() {
        //use of lazy evaluation
        final GoogleApiClient api = this.getM_gApiClient();
        /** starts login process **/
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    GoogleApiClient api = getM_gApiClient();
                    cordova.setActivityResultCallback(GSignIn.this);
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(api);
                    cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
    }

    /**
     * Starts the connection of api client to google servers
     */
    private void connect() {
        final GoogleApiClient api = this.getM_gApiClient();
        if (api == null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put(GSignIn.ERROR_CODE, GSignIn.ERRCODE_NO_APICLIENT);
                obj.put(GSignIn.ERROR_MSG, GSignIn.ERRMSG_NO_APICLIENT);
                this.getConnectCallback().error(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (api != null && (api.isConnected() || api.isConnecting())) return;

        this.cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                api.connect();
            }
        });
    }

    /**
     * Configure Google Sign-in options. and start connection to google api
     *
     * @param conf JSON object containing needed configurations for google api client
     *             the key "server_client_id" store backend server client ID
     */
    private void configureSignIn(JSONObject conf) {
        GoogleSignInOptions.Builder gsoBuilder;
        GoogleSignInOptions gso;
        boolean auth_code = false;
        boolean force_refresh = false;
        String serv_id;
		String scopes;
		String[] scope_list;

        /** Get Configurations options **/
        //server client id
        try {
            serv_id = conf.getString(GSignIn.CONFIG_SERVERID);
        } catch (JSONException e) {
            serv_id = "";
        }
		//scopes list
		try {
			scopes = conf.getString(GSignIn.CONFIG_SCOPES);
		} catch (JSONException e){
			scopes = "";
		}
        //authcode
        if(!serv_id.equals("")){
           auth_code = conf.optBoolean(GSignIn.CONFIG_AUTHCODE, false);
           if(auth_code) force_refresh = conf.optBoolean(GSignIn.CONFIG_REFRESH,false);
        }

        gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId();
        /** add backend server clientid **/
        if (!serv_id.equals("")) gsoBuilder.requestIdToken(serv_id);
		/** add request scopes **/
		if(!scopes.equals("")) {
			scope_list = scopes.split("\\s+");
			for( String scope: scope_list){
                gsoBuilder.requestScopes(new Scope(scope));
			}
		}
        /** add auth request **/
        if(auth_code){
            gsoBuilder.requestServerAuthCode(serv_id, force_refresh);
        }
        /** create options **/
        gso = gsoBuilder.build();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this.cordova.getActivity().getApplicationContext());
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        builder.addApi(Auth.GOOGLE_SIGN_IN_API, gso);
        this.setM_gApiClient(builder.build());
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(GSignIn.LOG_TAG, "connection error");
        JSONObject obj = new JSONObject();
        int code = connectionResult.getErrorCode();
        String msg = connectionResult.getErrorMessage();
        try {
            obj.put(GSignIn.ERROR_CODE, GSignIn.ERRCODE_CONNECTION_FAILED);
            obj.put(GSignIn.ERROR_MSG, "error: " + code + " " + msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.getConnectCallback().error(obj);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(GSignIn.LOG_TAG, "gapi connected");
        this.getConnectCallback().success();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(GSignIn.LOG_TAG, "onConnectionSuspended");
        JSONObject obj = new JSONObject();
        String msg;
        int code;
        try {
            if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                code = GSignIn.ERRCODE_CONNECTION_SUSPENDED;
                msg = GSignIn.ERRMSG_NET_LOST;
            } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                code = GSignIn.ERRCODE_CONNECTION_SUSPENDED;
                msg = GSignIn.ERRMSG_SRV_KILLED;
            } else {
                code = GSignIn.ERRCODE_CONNECTION_FAILED;
                msg = "Unknown error";
            }
            obj.put(GSignIn.ERROR_CODE, code);
            obj.put(GSignIn.ERROR_MSG, msg);
            this.getConnectCallback().error(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized GoogleApiClient getM_gApiClient() {
        return m_gApiClient;
    }

    private synchronized void setM_gApiClient(GoogleApiClient m_gApiClient) {
        this.m_gApiClient = m_gApiClient;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //operation completed
            this.m_running.getAndSet(false);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private synchronized CallbackContext getLoginCallback() {
        return loginCallback;
    }

    private synchronized void setLoginCallback(CallbackContext loginCallback) {
        this.loginCallback = loginCallback;
    }

    private synchronized void handleSignInResult(GoogleSignInResult result) {
        JSONObject obj = new JSONObject();
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            String id = acct.getId();
            String token = acct.getIdToken();
            Uri photourl = acct.getPhotoUrl();
            String name = acct.getDisplayName();
            String auth_code = acct.getServerAuthCode();
            try {
                if(email != null) obj.put(GSignIn.LOGIN_EMAIL, email);
                if(name != null)  obj.put(GSignIn.LOGIN_NAME, name);
                if(photourl != null) obj.put(GSignIn.LOGIN_PHOTO, photourl);
                if(token != null ) obj.put(GSignIn.LOGIN_TOKENID, token);
                if(id != null) obj.put(GSignIn.LOGIN_ID, id);
                if(auth_code != null) obj.put(GSignIn.LOGIN_AUTHCODE, auth_code);
                this.getLoginCallback().success(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                obj.put(GSignIn.ERROR_CODE, GSignIn.ERRCODE_LOGIN_FAILED);
                obj.put(GSignIn.ERROR_MSG, GSignIn.ERRMSG_LOGIN_FAILED);
                this.getLoginCallback().error(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized CallbackContext getConnectCallback() {
        return connectCallback;
    }

    private synchronized void setConnectCallback(CallbackContext connectCallback) {
        this.connectCallback = connectCallback;
    }


    private synchronized CallbackContext getLogoutCallback() {
        return logoutCallback;
    }

    private synchronized void setLogoutCallback(CallbackContext logoutCallback) {
        this.logoutCallback = logoutCallback;
    }

    private void trySilentSignIn(){
        final GoogleApiClient api = this.getM_gApiClient();
        if(api == null || !this.m_running.compareAndSet(false,true) || !api.isConnected()){
            return;
        }
        this.cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(api);
                if(pendingResult.isDone()){
                    //operation completed
                    GSignIn.this.m_running.set(false);
                    GSignIn.this.handleSignInResult(pendingResult.get());
                }
                else{
                    //operation copmpleted
                    pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                            GSignIn.this.m_running.set(false);
                            GSignIn.this.handleSignInResult(googleSignInResult);
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(GSignIn.LOG_TAG,"Cordova plugin destroyed");
    }

    private CallbackContext getRevokeCallback() {
        return revokeCallback;
    }

    private void setRevokeCallback(CallbackContext revokeCallback) {
        this.revokeCallback = revokeCallback;
    }
}
