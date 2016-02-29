/**
 * @namespace window
 */

/**
 * @exports gsignin
 */
var gsigninExport = {};


gsigninExport.echo =  function(successCallback, errorCallback, msg){
  cordova.exec(successCallback, errorCallback, msg);
};

module.exports = gsigninExport;
