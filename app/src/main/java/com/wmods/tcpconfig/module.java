package com.wmods.tcpconfig;
import com.wmods.tcpconfig.utils.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import java.net.*;
import android.webkit.*;
import android.util.*;
import android.text.*;

public class module 
implements IXposedHookZygoteInit,
IXposedHookLoadPackage,
LogFace
{

	private static boolean ENABLED_MODULE;

	private static boolean LOG_NET;

	private static boolean USE_IN_SOCKET;

	private static boolean USE_IN_HEADERGROUP;

	private static boolean USE_IN_ALL;

	private static BasicHeader[] CUSTOM_HEADER;

	private static boolean LOG_ADV;

	private static boolean USE_IN_WEBVIEW;

	private static ArrayList<String> USE_IN_PORTS;

	@Override
	public void log(Object obj)
	{
		if (LOG_NET)
		{
			if (obj instanceof String)
			{
				XposedBridge.log((String)obj);
			}
			else
			{
				XposedBridge.log((Throwable)obj);
			}
		}
	}

	public void dump_stack_trace()
	{
		if (LOG_ADV)
			log(Log.getStackTraceString(new Exception()));
	}

	private static XSharedPreferences prefs;

	public static void reload()
	{
		int i;
		prefs.reload();
		ENABLED_MODULE = prefs.getBoolean(Common.ENABLED_MODULE, false);
		LOG_NET = prefs.getBoolean(Common.LOG_NET, false);
		LOG_ADV = prefs.getBoolean(Common.LOG_ADV, false);
		USE_IN_SOCKET = prefs.getBoolean(Common.USE_IN_SOCKET, false);
		USE_IN_HEADERGROUP = prefs.getBoolean(Common.USE_IN_HEADERGROUP, false);
		USE_IN_WEBVIEW = prefs.getBoolean(Common.USE_IN_WEBVIEW, false);
		USE_IN_ALL = prefs.getBoolean(Common.USE_IN_ALL, false); 
		String str = prefs.getString(Common.CUSTOM_HEADER, "");
		if (!TextUtils.isEmpty(str))
		{
			String[] ss = str.split("\n");
			CUSTOM_HEADER = new BasicHeader[ss.length];
			i = 0;
			for (String tmp: ss)
			{
				String[] tmp2 = tmp.split(":", 2);
				CUSTOM_HEADER[i] = new BasicHeader(tmp2[0].trim(), tmp2[1].trim());
				i++;
			}
		}
		else
		{
			CUSTOM_HEADER = null;
		}
		
		String tmp = prefs.getString(Common.CUSTOM_REQUEST, null);
		RequestMod.load(tmp);

		String tmp2 = prefs.getString(Common.USE_IN_PORTS, null);
		USE_IN_PORTS = new ArrayList<String>();
		if (tmp2 != null && tmp2.length() > 0)
		{
			String[] split = tmp2.split(";");
			for (String tmp3 : split)
			{
				USE_IN_PORTS.add(tmp3);
			}

		}


	}

	private static boolean PassCheck(String AppName)
	{
		if (USE_IN_ALL)
		{
			Set<String> list = prefs.getStringSet(Common.LIST_OF_APPS2, new HashSet<String>());
			return !list.contains(AppName);
		}
		Set<String> list = prefs.getStringSet(Common.LIST_OF_APPS, new HashSet<String>());
		return list.contains(AppName);
	}

	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable
	{

		try
		{
			XposedHelpers.findAndHookMethod("java.net.Socket", lpparam.classLoader, "getOutputStream", new XC_MethodHook(){
					@Override
					protected void afterHookedMethod(MethodHookParam param)
					{
						reload();
						if (ENABLED_MODULE && USE_IN_SOCKET && PassCheck(lpparam.packageName))
						{
							dump_stack_trace();
							log(lpparam.packageName);
							String host;
							String ip;
							Socket s = ((Socket)param.thisObject);
							InetAddress inet = s.getInetAddress();
							try
							{
								host = inet.getHostName();
							}
							catch (Exception e)
							{
								host = "";
							}
							try
							{
								ip = inet.getHostAddress();
							}
							catch (Exception e)
							{
								ip = "";
							}
							String port = String.valueOf(s.getPort());
							log("Socket: " + ip + ":" + port);
							log("Socket Host: " + host);
							if (USE_IN_PORTS.contains("default") || USE_IN_PORTS.contains(port))
							{
								OutputModStream outputstream = new OutputModStream((OutputStream)param.getResult());
								outputstream.setSocketParam(host, port, ip);
								outputstream.setHeaders(CUSTOM_HEADER);
								outputstream.setFace(module.this);
								param.setResult(outputstream);
							}
						}
					}
				});


			XposedBridge.hookAllConstructors(XposedHelpers.findClass("org.apache.http.message.AbstractHttpMessage", lpparam.classLoader), new XC_MethodHook(){
					@Override
					protected void afterHookedMethod(MethodHookParam param)throws Throwable
					{
						if (ENABLED_MODULE && USE_IN_HEADERGROUP && PassCheck(lpparam.packageName))
						{
							dump_stack_trace();
							String pkg = lpparam.packageName;
							log(pkg);
							Class<AbstractHttpMessage> classAsbtractHttpMessage= AbstractHttpMessage.class;
							Field fieldHeadgroup = classAsbtractHttpMessage.getDeclaredField("headergroup");
							fieldHeadgroup.setAccessible(true);
							Object headgroup = fieldHeadgroup.get(param.thisObject);
							Class<HeaderGroup> classHeadGroup = HeaderGroup.class;
							Field fieldHeaders = classHeadGroup.getDeclaredField("headers");
							fieldHeaders.setAccessible(true);
							ListMod lm = new ListMod(pkg, CUSTOM_HEADER);
							lm.setLog(module.this);
							fieldHeaders.set(headgroup, lm);

						}
					}

				});

			XposedHelpers.findAndHookMethod("android.webkit.WebView", lpparam.classLoader, "loadUrl", String.class, new XC_MethodHook(){


					public void beforeHookedMethod(XC_MethodHook.MethodHookParam param)throws Throwable
					{
						if (ENABLED_MODULE && USE_IN_WEBVIEW && PassCheck(lpparam.packageName))
						{
							dump_stack_trace();
							log("WebView.loadUrl(String) is intercepted");
							if (((String)param.args[0]).startsWith("http"))
								log("URL: " + param.args[0]);
							((WebView)param.thisObject).loadUrl((String)param.args[0], null);
							param.setResult(null);
						}
					}

				});

			XposedHelpers.findAndHookMethod("android.webkit.WebView", lpparam.classLoader, "loadUrl", String.class, Map.class, new XC_MethodHook(){

					public void beforeHookedMethod(XC_MethodHook.MethodHookParam param)throws Throwable
					{

						if (ENABLED_MODULE && USE_IN_WEBVIEW && PassCheck(lpparam.packageName))
						{
							dump_stack_trace();
							Map<String,String> hm;
							log("WebView Hooking..");
							if (((String)param.args[0]).startsWith("http"))
								log("URL: " + param.args[0]);
							Object o = param.args[1];
							if (o != null)
							{
								hm = (Map<String,String>)o;
							}
							else
							{
								hm = new HashMap<String,String>();
							}
							for (BasicHeader bh : CUSTOM_HEADER)
							{
								if (hm.containsKey(bh.getName()))
								{
									hm.remove(bh.getName());
								}
								hm.put(bh.getName(), bh.getValue());
							}
							param.args[1] = hm;
						}
					}

				});

		}
		catch (Throwable e)
		{
			log("Error" + e.toString());
		}

	}

	@Override
	public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable
	{
		prefs = new XSharedPreferences(module.class.getPackage().getName());
		prefs.makeWorldReadable();
	}

}
