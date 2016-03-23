/**
 * GSingIn namespace
 * @namespace gsignin
 **/
var gsigninExport = {};
gsigninExport.ACTION_LOGOUT = "logout";
gsigninExport.ACTION_LOGIN = "login";
gsigninExport.ACTION_CONFIG = "config";
gsigninExport.ACTION_REVOKE = "revoke";
gsigninExport.SERVICE = "signin";


/**
 * Configuration of the Google sign in api
 * @typedef {Object} ConnectionConfiguration
 * @memberof gsignin
 * @property {String} [server_client_id] Optional Server Client Id to request a token ID for a backend server.
 * @property {String} [scopes] List of scopes separated by space
 * @property {Boolean} [auth_code] if true request server side access for server_client_id
 * @property {Boolean} [refresh] forceRefresh for server side access
 */

 /**
 * This callback will be called on google api succesful connect
 * @callback gsignin.connectSuccess
 */

 /**
  * This callback will be called upon connection suspension or connection error/break
  * @callback gsignin.cbError
  * @param {gsignin.ErrorInfo} obj - Connection Error informations
  */

/**
 * The config function creates the api client and try to start the connection with google servers.
 * In the conf parameter it is possible to configure wich informations will be sent back upon succesful login.
 * @function
 * @name config
 * @memberof gsignin
 * @public
 * @param {gsignin.connectSuccess} successCallback - configuration of google api client ended succesfully and the api is now connected.
 * @param {gsignin.cbError} errorCallback - configuration ended with error.
 * @param {gsignin.ConnectionConfiguration} conf - Configuration Object for the connection see {@link gsignin.ConnectionConfiguration}
 */
gsigninExport.config = function(successCallback, errorCallback, conf) {
  console.log("executing config");
  var args = [];
  args.push(conf);
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_CONFIG, args);
};

/**
 * Login Information object.
 * Based on the configuration object used in config function the object can have less property.
 * @typedef {Object} LoginInfo
 * @memberof gsignin
 * @property {String} email - email of the user account
 * @property {String} name - user name
 * @property {String} photourl - url of the profile photo
 * @property {String} tokenid -auth toke for a backend server if specified the server_client_id in the {@link gsignin.ConnectionConfiguration}
 * @property {String} id - google user identifier
 * @property {String} auth_code - Auth token for server side api access
 */

/**
 * Performs the login. The config function must have been performed before this one.
 * @function
 * @memberof gsignin
 * @name login
 * @param {gsignin.loginSuccess} successCallback - callback called when the login has been executed succesfully
 * @param {gsignin.cbError} errorCallback - callback called when the login ends with error
 * @param {boolean} silent - true for trying silent login
 */
gsigninExport.login = function(successCallback, errorCallback, silent) {
  var bool = false;
  if(silent) bool = true;
  var args = [];
  args.push(silent);
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_LOGIN, args);
};

/**
 * Performs the logout.
 * @function
 * @memberof gsignin
 * @name logout
 * @param {function} successCallback - callback called when the revoke operation has been executed succesfully
 * @param {gsignin.cbError} errorCallback - callback called when the revoke operation ends with error
 */
gsigninExport.logout = function(successCallback, errorCallback){
  console.log("executing logout");
  var args = [];
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_LOGOUT, args);
};


/**
 * revoke operation
 * @function
 * @memberof gsignin
 * @name revoke
 * @param {function} successCallback - callback called when the revoke operation has been executed succesfully
 * @param {gsignin.cbError} errorCallback - callback called when the revoke operation ends with error
 */
 gsigninExport.revoke = function(successCallback, errorCallback){
   console.log("executing revoke");
   var args = [];
   cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_REVOKE, args);
 };

module.exports = gsigninExport;

//Types Documentation
 /**
  *  Error Information  object.
  *
  *  list of error codes:
  *  * 401 The api client has not been generated.
  *  * 402 The connection has failed.
  *  * 403 The connection has been suspended.
  *  * 404 Login Failure
  *  * 405 Logout Failure
  *  * 406 Not Conencted
  *  * 407 Revoke Error
  *  @typedef {Object} ErrorInfo
  *  @memberof gsignin
  *  @property {Integer} code - Error code
  *  @property {String} msg - Error description
  */


 //Callback Documentation
  /**
   * This callback will be called upon succesful login.
   * @callback gsignin.loginSuccess
   * @param {gsignin.LoginInfo} obj - Login informations
   */
