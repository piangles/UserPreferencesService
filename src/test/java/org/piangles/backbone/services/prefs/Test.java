package org.piangles.backbone.services.prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.piangles.core.util.coding.JSON;

public class Test
{
	public static void main(String[] args) throws Exception
	{
		UserPreference prefs = new UserPreference("", null);//createDefaultProperties();
		for (Entry<String, Object> es: prefs.getNVPair().entrySet())
		{
			if (es.getValue() instanceof Object[])
			{
				List<String> listAsStr = Arrays.asList((Object[])es.getValue()).stream().map(Object::toString).collect(Collectors.toList());
				es.setValue(String.join("|", listAsStr));
			}
		}
		byte[] propertiesAsJson = JSON.getEncoder().encode(prefs);
		System.out.println(new String(propertiesAsJson));

		prefs = JSON.getDecoder().decode(propertiesAsJson, UserPreference.class);
		for (Entry<String, Object> es: prefs.getNVPair().entrySet())
		{
			String valueAsStr = (String)es.getValue();
			es.setValue(valueAsStr.split("|"));
		}
	}
	
	private static UserPreference createDefaultProperties()
	{
		String[] sectors = new String[]{"Technology", "Finance", "Energy", "Biotech", "Airlines"};
		UserPreference prefs = new UserPreference("userId");
		List<String> preferedSectors = new ArrayList<>();

		int start = new Random().nextInt(4);
		preferedSectors.add(sectors[start]);
		preferedSectors.add(sectors[start + 1]);
		
		prefs.getNVPair().put("PreferedSectors", preferedSectors.toArray());
		return prefs;
	}
}
