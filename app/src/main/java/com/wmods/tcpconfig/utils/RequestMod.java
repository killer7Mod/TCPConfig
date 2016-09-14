package com.wmods.tcpconfig.utils;

import java.net.*;
import java.util.*;

public class RequestMod
{

	private static HashMap<String, String> h;

	public static void load(String str)
	{
		if (str == null || str.length() == 0)
		{
			h = null;
			return;
		}
		h = Utils.splitToHashMap(str, "\n", ":");
	}

	public static void set(TCP tcp,String[] param)
	{
		if (h == null || h.isEmpty())
		{
			return;
		}

		String[] keys = Utils.getKeys(h);
		String val;
		for (String s : keys)
		{
			if (s.contains("METHOD") && (val = h.get(s)) != null)
			{
				if (val.contains(">"))
				{
					String str[] = val.split(">", 2);
					if (tcp.getMethod().equals(str[0].trim().toUpperCase()))
						tcp.setMethod(str[1].trim().toUpperCase());
				}
			}
			if (s.contains("PROTOCOL") && (val = h.get(s)) != null)
			{
				tcp.setProtocol(val);
			}
			if (s.contains("URL") && (val = h.get(s)) != null)
			{
				String[] vals = val.split(";");
				if (vals.length == 4)
				{
					URLControl URL = new URLControl(tcp.getURL());
					if (!vals[0].equals("DEFAULT"))
					{
						String tmp = URL.getProtocol();
					    vals[0] = vals[0].replace("fromURL", tmp != null ? tmp : "");
						URL.setProtocol(vals[0]);
					}
					
					if (!vals[1].equals("DEFAULT"))
					{
						String tmp = URL.getHost();
					    vals[1] = vals[1].replace("fromURL", tmp != null ? tmp : (param[0] != null ? param[0] : ""));
						vals[1] = vals[1].replace("fromURL2", param[2] != null ? param[2] : "");
						URL.setHost(vals[1]);
					}
					
					if (!vals[2].equals("DEFAULT"))
					{
						String tmp = URL.getPort();
					    vals[2] = vals[2].replace("fromURL", tmp != null ? tmp : (param[1].length() > 1 ? param[1] : ""));
						URL.setPort(vals[2]);
					}
					
					if (!vals[3].equals("DEFAULT"))
					{
						String tmp = URL.getPath();
					    vals[3] = vals[3].replace("fromURL", tmp != null ? tmp : "");
						URL.setPath(vals[3]);
					}
					tcp.setURL(URL.getString());
				}
			}
		}
	}

}
