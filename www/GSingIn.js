/**
 * @namespace window
 */

/**
 * @exports gsignin
 */
var gsigninExport = {};


gsinginExport.echo =  function(successCallback, errorCallback, msg){
  exec(successCallback, errorCallback, msg);
};

module.exports = gsigninExport;
