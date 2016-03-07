/**
 * @namespace window
 */
/**
 * @exports gsignin
 */
var gsigninExport = {};
gsigninExport.ACTION_LOGIN = "login";
gsigninExport.ACTION_CONFIG = "config";
gsigninExport.ACTION_ECHO = "echo";
gsigninExport.SERVICE = "signin";
gsigninExport.echo = function(successCallback, errorCallback, msg) {
  console.log("executing echo");
  var args = [];
  args.push(msg);
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, gsigninExport.ACTION_ECHO, args);
};
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
module.exports = gsigninExport;
