package org.piangles.backbone.services.prefs.dao;

import com.TBD.backbone.services.prefs.UserPreference;
import com.TBD.core.dao.DAOException;

public interface UserPreferenceDAO
{
	public UserPreference retrieveUserPreference(String userId) throws DAOException;
	
	public void persistUserPreference(String userId, UserPreference prefs) throws DAOException;

}
