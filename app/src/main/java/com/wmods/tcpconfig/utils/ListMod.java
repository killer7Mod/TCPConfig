package com.wmods.tcpconfig.utils;

import java.util.*;
import org.apache.http.*;
import org.apache.http.message.*;

public class ListMod extends ArrayList
{

	private LogFace lf;

	private BasicHeader[] headers;

	private String pkg;

	public ListMod(String name,BasicHeader[] headers){
		this.headers = headers;
        this.set();
		pkg = name;
	}

	public void setLog(LogFace lf){
		this.lf = lf;
	}
	
	private void set()
	{

			for (Header h : headers)
			{
				super.add(h);
			}
	}


	@Override
	public boolean add(Object object)
	{
		if (check(object))
			return true;
			lf.log(pkg+":"+object.toString());
		return super.add(object);
	}

	@Override
	public Object set(int index, Object object)
	{
		if (check(object))
			return object;
			lf.log(pkg+":"+object.toString());
		return super.set(index, object);
	}

	private boolean check(Object object)
	{
		for (Header h : headers)
		{
			if (((Header)object).getName().equals(h.getName()))
				return true;
		}
		return false;
	}



	@Override
	public boolean remove(Object object)
	{
		if (check(object))
			return true;
		return super.remove(object);
	}

	@Override
	public void clear()
	{
		super.clear();
		set();
	}

}
