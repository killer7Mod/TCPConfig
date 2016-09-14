package com.wmods.tcpconfig.utils;
import java.util.*;
import java.io.*;
import org.apache.http.*;

public class TCP
{
	private String tcp_origin;
	private String url;
	private String method;
	private String protocol;
	private HashMap<String,String> headers;

	public TCP(String str)
	{
		tcp_origin = str;
		process();
	}

	public void setProtocol(String val)
	{
		this.protocol = val;
	}

	public void setMethod(String val)
	{
		this.method = val;
	}

	private void process()
	{
		String[] split = tcp_origin.replace("\r\n\r\n", "").split("\r\n");
		String[] tmp2 = split[0].split(" ");
        method = tmp2[0];
		url = tmp2[1];
		protocol = tmp2[2];
		headers = new HashMap<String,String>();
		for (int i = 1;i < split.length;i++)
		{
			String[] p = split[i].split(":", 2);
			headers.put(p[0].trim(), p[1].trim());
		}
		tmp2 = null;
		split = null;
	}

	public void setHeader(String h, String v)
	{
		removeHeader(h);
		headers.put(h, v);
	}

	public void setHeader(Header h)
	{
		removeHeader(h.getName());
		headers.put(h.getName(), h.getValue());
	}


	public void removeHeader(String h)
	{
		if (headers.containsKey(h))
		{
			headers.remove(h);
		}
	}

	public HashMap<String,String> getHeaders()
	{
		return headers;
	}

	public String getURL()
	{
		return url;
	}

	public void setURL(String url)
	{
		this.url = url;
	}

	public String getMethod()
	{
		return this.method;
	}


	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s %s %s\r\n", method, url, protocol));
        Set<String> keys = headers.keySet();
		String[] tmp = keys.toArray(new String[keys.size()]);
        for (String t : tmp)
		{
			sb.append(t + ": " + headers.get(t));
			sb.append("\r\n");
		}
		if (tmp.length == 0)
		{
			sb.append("\r\n");
		}
		sb.append("\r\n");
		return sb.toString();
	}

	public void close()
	{
		tcp_origin = null;
		method = null;
		protocol = null;
		url = null;
		headers.clear();
		headers = null;
	}

}
