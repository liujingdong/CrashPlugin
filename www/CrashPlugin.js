/**
 *  2018/6/5 CrazyDong
 *  变更描述：
 *  功能说明：
 */

var exec = require('cordova/exec');

module.exports = {
    /*调用对比
     * js: cordova.exec(callbackContext.success, callbackContext.error, PluginName, action, args);
     * java: public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
     * */
    getCrash:function(action,success,err){
        exec(success,err, "CrashPlugin", action, null);
    }
}