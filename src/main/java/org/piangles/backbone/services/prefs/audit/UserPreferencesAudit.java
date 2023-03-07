package org.piangles.backbone.services.prefs.audit;

import org.piangles.backbone.services.prefs.UserPreferences;

public interface UserPreferencesAudit 
{
	public void performAudit(UserPreferences userPreferences);
	
	public String retrieveLatestAuditRecord(UserPreferences userPreferences);
}
