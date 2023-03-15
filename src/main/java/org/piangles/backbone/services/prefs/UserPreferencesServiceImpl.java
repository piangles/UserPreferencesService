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

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.prefs.dao.UserPreferencesDAO;
import org.piangles.backbone.services.prefs.dao.UserPreferencesMongoDAOImpl;
import org.piangles.backbone.services.prefs.dao.UserPreferencesRDBMSDAOImpl;
import org.piangles.core.dao.DAOException;
import org.piangles.core.util.abstractions.ConfigProvider;

public final class UserPreferencesServiceImpl implements UserPreferencesService
{
	private static final String COMPONENT_ID = "131a693b-a821-4e58-a44e-ddef529ca634";
	private static final String DEFAULT_DAO_TYPE = "NoSql";
	private static final String DAO_TYPE = "DAOType";

	private LoggingService logger = Locator.getInstance().getLoggingService();

	private UserPreferencesDAO userPreferencesDAO = null;
	
	public UserPreferencesServiceImpl() throws Exception
	{
		ConfigProvider cp = new DefaultConfigProvider(UserPreferencesService.NAME, COMPONENT_ID);
		Properties props = cp.getProperties();
		if (DEFAULT_DAO_TYPE.equals(props.getProperty(DAO_TYPE)))
		{
			userPreferencesDAO = new UserPreferencesMongoDAOImpl(cp); 
		}
		else
		{
			userPreferencesDAO = new UserPreferencesRDBMSDAOImpl(cp); 
		}
		logger.info("Starting UserPreferenceService with DAO: " + userPreferencesDAO.getClass());
	}
	
	@Override
	public void persistUserPreferences(String userId, UserPreferences prefs) throws UserPreferencesException
	{
		UserPreferences userPreferences = null;
		try
		{
			logger.info("Persisting UserPreferences for : " + userId);
			if (prefs == null || prefs.getNVPair() == null)
			{
				prefs = new UserPreferences(userId);
			}
			
			logger.info("Checking if UserPreferences already exist for : " + userId);
			//below is always guaranteed to return a value as the retrieve builds a seeding a value when null NVPair
			userPreferences = this.retrieveUserPreferences(userId);
			
			for (Entry<String, Object> es: prefs.getNVPair().entrySet())
			{
				//when key does not already exist, add it to the NVPair
				if (!userPreferences.getNVPair().containsKey(es.getKey()))
				{
					logger.info("UserPreferences already exist for : " + userId + " adding new preferences with key: " + es.getKey());
				}
				// already exists with same key, just update the mapping with new value
				else
				{
					logger.info("UserPreferences already exist for : " + userId + " and key: " + es.getKey());
				}
				
				//map-put replaces the current value for a certain key, below should take care of add & update cases
				userPreferences.getNVPair().put(es.getKey(), es.getValue());

				//convert lists to strings
				if (es.getValue() instanceof Object[])
				{
					List<String> listAsStr = Arrays.asList((Object[])es.getValue()).stream().map(Object::toString).collect(Collectors.toList());
					es.setValue(listAsStr);
				}
			}

			userPreferencesDAO.persistUserPreferences(userPreferences);
		}
		catch (DAOException e)
		{
			String message = "Faied persisting UserPreferences for UserId: " + userId;
			logger.error(message + ". Reason: " + e.getMessage(), e);
			throw new UserPreferencesException(message);
		}
	}

	@Override
	public UserPreferences retrieveUserPreferences(String userId) throws UserPreferencesException
	{
		UserPreferences userPreference = null;
		try
		{
			logger.info("Retriving UserPreferences for : " + userId);
			userPreference = userPreferencesDAO.retrieveUserPreferences(userId);
			if (userPreference == null || userPreference.getNVPair() == null)
			{
				userPreference = new UserPreferences(userId);
				/**
				 * Without the below, the object when encoded to JSON 
				 * will return a null NVPair instead of creating an
				 * Map with no values.
				 */
				userPreference.setValue("PiAngles-Seeding", "SampleValue");
			}
		}
		catch (DAOException e)
		{
			String message = "Faied retriving UserPreferences for UserId: " + userId;
			logger.error(message + ". Reason: " + e.getMessage(), e);
			throw new UserPreferencesException(message);
		}
		return userPreference;
	}
}
