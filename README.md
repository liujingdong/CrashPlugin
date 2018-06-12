---
title: android crash report 
description: 自动监听android里面crash信息,以txt文本存储在SD卡的CrashInfo文件夹里面,调用getCrash可获取crash信息,获取后自动删除CrashInfo文件夹
Installation: ionic plugin add https://github.com/liujingdong/CrashPlugin.git
Supported Platforms: android
usage:

	CrashPlugin.getCrash("getCrash",function(resule){
		var listInfo = resule.split(",");
		var jsonInfo = JSON.stringify(listInfo);
		console.log(">>>"+jsonInfo);
	},function(err){

	});

	resule即为crash信息,jsonInfo信息如下,去掉收尾""符号,可JSON在线解析:
	"[
		"2018-06-11 14:41:24",
		"OS Version:6.0.1_23",
		"制造商:vivo",
		"品牌:vivo",
		"versionName:1.0.0",
		"versionCode:1",
		"versionPackage:com.chinaZhongWang.community",
		"DisplayWidth:720",
		"DisplayHeight:1280",
		"",
		"java.lang.ArrayIndexOutOfBoundsException: length=3; index=5",
		"	at custom.cordova.crash.CrashPlugin$2.run(CrashPlugin.java:95)",
		"	at java.lang.Thread.run(Thread.java:818)",
		""
    ]"
---

