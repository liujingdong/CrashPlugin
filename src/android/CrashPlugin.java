package custom.cordova.crash;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by CrazyDong on 2018/6/5.
 */
public class CrashPlugin extends CordovaPlugin {
  private CallbackContext mCallbackContext;
  //定义文件存放路径
  private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashInfo/";
  //定义文件后缀
  private static final String FILE_NAME_SUFFIX = ".txt";
  //TODO 测试时使用
//  private int mArr[] = {1,2,3};

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException{

    this.mCallbackContext = callbackContext;

    if(action.equals("getCrash")){
      //获取crash信息
      File dir = new File(PATH);
      if(!(dir == null || !dir.exists() || !dir.isDirectory())){
        for (File file : dir.listFiles()) {
          readFile(file);
        }
        deleteAllFiles(dir);
      }

      return true;
    }

    return false;
  }

  @Override
  public void onStart() {
    super.onStart();
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        dumpExceptionToSDCard(e);
      }
    });

  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
      //TODO 测试时使用
//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        try {
//          Thread.sleep(1000);
//          int a  = mArr[5];
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//      }
//    }).start();
  }

  //记录异常信息到本地文本中
  private void dumpExceptionToSDCard(Throwable throwable){
    File dir = new File(PATH);
    if(!dir.exists()){
      dir.mkdirs();
    }

    long currentTime = System.currentTimeMillis();
    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTime));
    //建立记录Crash文本
    File file = new File(PATH + time + FILE_NAME_SUFFIX);
    try {
      PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
      printWriter.println(time);
      dumpPhoneInfo(printWriter);//记录手机信息
      printWriter.println();
      throwable.printStackTrace(printWriter);//将异常变成String写入文件
      printWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e("错误失败", "记录Crash信息失败");
    }

  }

  //记录手机信息
  private void dumpPhoneInfo(PrintWriter printWriter) {
    //系统版本号
    printWriter.print("OS Version:");
    printWriter.print(Build.VERSION.RELEASE);
    printWriter.print("_");
    printWriter.println(Build.VERSION.SDK_INT);
    //硬件制造商
    printWriter.print("制造商:");
    printWriter.println(Build.MANUFACTURER);
    //系统定制商
    printWriter.print("品牌:");
    printWriter.println(Build.BRAND);
    //手机型号
    printWriter.print("手机型号:");
    printWriter.println(Build.MODEL);

    //App信息
    try {
      PackageInfo packageInfo = cordova.getActivity().getApplicationContext().getPackageManager()
        .getPackageInfo(cordova.getActivity().getPackageName(), 0);
      String versionName = packageInfo.versionName;//版本号
      int versionCode = packageInfo.versionCode;//版本次数
      String versionPackage = packageInfo.packageName;//包名

      printWriter.print("versionName:");
      printWriter.println(versionName);

      printWriter.print("versionCode:");
      printWriter.println(versionCode);

      printWriter.print("versionPackage:");
      printWriter.println(versionPackage);

      //手机分辨率
      DisplayMetrics dm = new DisplayMetrics();
      cordova.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
      int width = dm.widthPixels;
      int height = dm.heightPixels;

      printWriter.print("DisplayWidth:");
      printWriter.println(width);

      printWriter.print("DisplayHeight:");
      printWriter.println(height);



    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  //读取txt里面的内容
  private void readFile(File file){
    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
      try {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        String tmp;
        String backStr = "";
        while ((tmp = br.readLine()) != null){
          backStr += tmp + ",";
        }
        br.close();
        in.close();
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,backStr);
        pluginResult.setKeepCallback(true);
        mCallbackContext.sendPluginResult(pluginResult);

      } catch (Exception  e) {
        mCallbackContext.error(e.toString());
      }

    }else {
      mCallbackContext.error("SD Err");

    }

  }

  //删除CrashInfo文件夹信息
  private void deleteAllFiles(File dir){
    if(dir == null || !dir.exists() || !dir.isDirectory()){
      return;
    }else{
      for (File file : dir.listFiles()) {
        if(file.isFile()){
          file.delete();
        }else if(file.isDirectory()){
          deleteAllFiles(dir);
        }
      }
      dir.delete();
    }

  }


}
