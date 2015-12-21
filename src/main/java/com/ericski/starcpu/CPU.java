package com.ericski.starcpu;

import java.lang.management.ManagementFactory;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class CPU
{

	private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

	// no instances please & thank you
	private CPU()	{	}

	public static double SystemCpuLoad()
	{

		try
		{
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = M_BEAN_SERVER.getAttributes(name, new String[] {"SystemCpuLoad"});

			if (list.isEmpty()) return 0.0;

			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();

			return ((int) (value * 1000) / 1000.0);

		}
		catch (MalformedObjectNameException | NullPointerException | InstanceNotFoundException | ReflectionException e)
		{
			return 0.0;
		}
	}

}
