/**
 * @namespace window
 */
/**
 * @exports gsignin
 */
var gsigninExport = {};
gsigninExport.ACTION_LOGOUT = "logout";
gsigninExport.ACTION_LOGIN = "login";
gsigninExport.ACTION_CONFIG = "config";
gsigninExport.SERVICE = "signin";

gsigninExport.config = function(successCallback, errorCallback, conf) {
  console.log("executing config");
  var args = [];
  args.push(conf);
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_CONFIG, args);
};
gsigninExport.login = function(successCallback, errorCallback) {
  console.log("executing login");
  var args = [];
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_LOGIN, args);
};
gsigninExport.logout = function(successCallback, errorCallback){
  console.log("executing logout");
  var args = [];
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_LOGOUT, args);
};


module.exports = gsigninExport;
