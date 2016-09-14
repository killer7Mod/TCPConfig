package com.wmods.tcpconfig.utils;

import java.util.*;


public class URLControl
{
	String URL;
	String PROTOCOL;
	String HOST;
	String PORT;
	String PATH;

	public URLControl(String url)
	{
		URL = url;
		int pos,pos2,pos3;
		pos = pos2 = pos3 = -1;
		if (URL.startsWith("/"))
		{
			if (URL.length() > 1)
			{
				PATH = URL.substring(1);
			}
		}
		else if ((pos = URL.indexOf("://") + 3) != 2)
		{
			PROTOCOL = URL.substring(0, pos - 3);
			if ((pos3 = URL.indexOf("/", pos)) != -1)
			{
				if (URL.substring(pos3).length() > 1)
					PATH = URL.substring(pos3 + 1);
				if ((pos2 = URL.indexOf(":", pos)) != -1)
				{
					if (pos2 < pos3)
					{
						PORT = URL.substring(pos2 + 1, pos3);
					}
				}
			}
			if (pos2 == -1)
			{
				if (pos3 != -1)
					HOST = URL.substring(pos, pos3);
				else
					HOST = URL.substring(pos);
			}
			else
			{
				HOST = URL.substring(pos, pos2 < pos3 ? pos2 : pos3);
			}
		}
		else if (URL.matches("[a-zA-Z0-9.:]{1,}"))
		{
			if ((pos = URL.indexOf(":")) != -1)
			{
				HOST = URL.substring(0, pos);
				PORT = URL.substring(pos + 1);
			}
			else
				HOST = URL;
		}
	}

	public void setPort(String s)
	{
		this.PORT = s;
	}

	public void setHost(String s)
	{
		this.HOST = s;
	}

	public void setProtocol(String s)
	{
		this.PROTOCOL = s;
	}

	public void setPath(String s)
	{
		this.PATH = s;
	}

	public String getPort()
	{
		return this.PORT;
	}

	public String getHost()
	{
		return this.HOST;
	}

	public String getProtocol()
	{
		return this.PROTOCOL;
	}

	public String getPath()
	{
		return this.PATH;
	}

	public String getString()
	{
		StringBuilder sb = new StringBuilder();
		if (PROTOCOL != null && HOST != null)
		{
			sb.append(PROTOCOL);
			sb.append("://");
			sb.append(HOST);
		}
		else if (HOST != null)
		{
			sb.append(HOST);
		}
		if (PORT != null && sb.length() > 0)
		{
			sb.append(":");
			sb.append(PORT);
		}
		if (PATH != null)
		{
			sb.append("/");
			sb.append(PATH);
		}
		else if(PROTOCOL != null && HOST != null)
		{
			sb.append("/");
		}
		if (sb.length() == 0)
		{
			sb.append(URL);
		}
		return sb.toString();
	}

}
