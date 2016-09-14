package com.wmods.tcpconfig.utils;
import java.io.*;
import java.util.*;
import org.apache.http.message.*;
import org.apache.http.*;
import com.wmods.tcpconfig.*;

public class OutputModStream extends OutputStream
{
	private ByteArrayOutputStream baos;

	private OutputStream os;

	private BasicHeader[] headers;

	private LogFace logger;

	private int size;

	private boolean isCheck;

	private boolean isTcp;

	private String[] param;

	public OutputModStream(OutputStream os)
	{
		this.os = os;
		this.size = 0;
		this.isCheck = true;
		this.isTcp = false;
		this.baos = new ByteArrayOutputStream();
	}

	public void setSocketParam(String... param)
	{
		// TODO: Implement this method
		this.param = param;
	}

    public void setFace(LogFace lf)
	{
		this.logger = lf;
	}

	public void setHeaders(BasicHeader[] h)
	{
		this.headers = h;
	}


	@Override
	public void write(int p1) throws IOException
	{
		if (isCheck)
		{
			CheckStart(p1);
			return;
		}
		if (isTcp)
		{
			CheckEnd(p1);
			return;
		}
		os.write(p1);
	}

	@Override
	public void write(byte[] buffer) throws IOException
	{
		if (Utils.checkstart(buffer) && Utils.checkend(buffer))
		{
			String param = new String(buffer);
			logger.log("***RECEIVED REQUEST write(byte[]):\n" + param);
			TCP tcp = new TCP(param);
			set(tcp);
			String param2 = tcp.toString();
			logger.log("***SEND REQUEST write(byte[]):\n" + param2);
			os.write(param2.getBytes());
			os.flush();
			isCheck = false;
			isTcp = false;
			return;
		}
		os.write(buffer);
	}

	private void CheckStart(int p1) throws IOException
	{
		baos.write(p1);
		if (size++ >= 10)
		{
			if (Utils.checkstart(baos.toByteArray()))
			{
				isCheck = false;
				isTcp = true;
			}
			else
			{
				os.write(baos.toByteArray());
				isCheck = false;
		        baos = null;
			}
		}
	}

	private void CheckEnd(int p1) throws IOException
	{
		baos.write(p1);
		if (p1 == 10 && Utils.checkend(baos.toByteArray()))
		{
			String param = new String(baos.toByteArray());
			logger.log("***RECEIVED REQUEST write(int):\n" + param);
			TCP tcp = new TCP(param);
			set(tcp);
			param = tcp.toString();
			logger.log("***SEND REQUEST write(int):\n" + param);
			os.write(param.getBytes());
			os.flush();
			isTcp = false;
		}

	}

	private void set(TCP tcp)
	{
		if(headers != null)
		for (BasicHeader header : headers)
		{
			if (header != null)
				tcp.setHeader(header);
		}
		RequestMod.set(tcp,param);
	}

	@Override
	public void close() throws IOException
	{
		os.close();
	}

	@Override
	public void flush() throws IOException
	{
		os.flush();
	}

}
