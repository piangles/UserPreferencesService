package org.piangles.backbone.services.prefs;

public class TestingNullAndSplit
{
	public static void main(String[] args) throws Exception
	{
		Object value = null;
		
		String testingSplit = "786";
		System.out.println("Splitting"+ testingSplit.split("|"));
		
		
		if (value instanceof Object[])
		{
			System.out.println("NullPointerException");
		}
	}
}
