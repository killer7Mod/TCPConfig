package com.wmods.tcpconfig.utils;

import java.util.*;

public class Utils
{
	private static byte[] end = new byte[]{(byte)13,(byte)10,(byte)13,(byte)10};

	public static boolean checkend(byte[] b)
	{
		return FindBytes(b, end);
	}

	public static boolean checkstart(byte[] b)
	{
		return FindBytes(b, "GET".getBytes()) || FindBytes(b, "POST".getBytes()) || FindBytes(b, "HEAD".getBytes()) || FindBytes(b, "CONNECT".getBytes()) || FindBytes(b, "OPTIONS".getBytes()) || FindBytes(b, "TRACE".getBytes());

	}

	public static boolean FindBytes(byte[] b, byte[] src)
	{
		int comparelen = src.length;
		int len = b.length;
		if (len < comparelen)
			return false;
		if (len >= comparelen)
		{
			search: for (int i = 0; i < len; ++i)
			{
				for (int j = 0; j < comparelen; ++j)
				{
					if (i + j >= len || b[i + j] != src[j])
					{
						continue search;
					}
				}
				return true;
			}
		}
		return false;
	}


	public static HashMap<String,String> splitToHashMap(String s, String split, String separator)
	{
		HashMap<String, String> hm = new HashMap<String,String>();
		String[] strs = s.split(split);
		for (String sm : strs)
		{
			if (sm.contains(separator))
			{
				String[] sp = sm.split(separator, 2);
				hm.put(sp[0].trim(), sp[1].trim());
			}
		}
		return hm;
	}

	public static String[] getKeys(HashMap<String,String> hm)
	{
		Set<String> keyset = hm.keySet();
		return keyset.toArray(new String[keyset.size()]);
	}

}
