/**
 * @namespace window
 */

/**
 * @exports gsignin
 */

var gsigninExport = {};

gsigninExport.SERVICE = "GSignIn";

gsigninExport.echo =  function(successCallback, errorCallback, msg){
  console.log("executing echo");
  var args = [];
  args.push(msg);
  cordova.exec(successCallback, errorCallback, gsigninExport.SERVICE, "echo", args);
};

module.exports = gsigninExport;
