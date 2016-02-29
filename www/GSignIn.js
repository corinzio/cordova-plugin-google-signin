/**
 * @namespace window
 */

/**
 * @exports gsignin
 */
var gsigninExport = {};


gsigninExport.echo =  function(successCallback, errorCallback, msg){
  exec(successCallback, errorCallback, msg);
};

module.exports = gsigninExport;
