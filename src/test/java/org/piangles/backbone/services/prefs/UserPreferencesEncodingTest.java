/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.backbone.services.prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.piangles.core.util.coding.JSON;

public class UserPreferencesEncodingTest
{
	public static void main(String[] args) throws Exception
	{
		UserPreferences prefs = new UserPreferences("", null);//createDefaultProperties();
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

		prefs = JSON.getDecoder().decode(propertiesAsJson, UserPreferences.class);
		for (Entry<String, Object> es: prefs.getNVPair().entrySet())
		{
			String valueAsStr = (String)es.getValue();
			es.setValue(valueAsStr.split("|"));
		}
	}
	
	private static UserPreferences createDefaultProperties()
	{
		String[] sectors = new String[]{"Technology", "Finance", "Energy", "Biotech", "Airlines"};
		UserPreferences prefs = new UserPreferences("userId");
		List<String> preferedSectors = new ArrayList<>();

		int start = new Random().nextInt(4);
		preferedSectors.add(sectors[start]);
		preferedSectors.add(sectors[start + 1]);
		
		prefs.getNVPair().put("PreferedSectors", preferedSectors.toArray());
		return prefs;
	}
}
