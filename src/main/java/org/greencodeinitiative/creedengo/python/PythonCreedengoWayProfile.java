/*
 * creedengo - Python language - Provides rules to reduce the environmental footprint of your Python programs
 * Copyright © 2024 Green Code Initiative (https://green-code-initiative.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.greencodeinitiative.creedengo.python;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
//import org.sonar.api.utils.log.Logger;
//import org.sonar.api.utils.log.Loggers;
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader;

import static org.greencodeinitiative.creedengo.python.PythonRuleRepository.LANGUAGE;
import static org.greencodeinitiative.creedengo.python.PythonRuleRepository.REPOSITORY_KEY;

public final class PythonCreedengoWayProfile implements BuiltInQualityProfilesDefinition {

//	private static final Logger LOGGER = Loggers.get(PythonCreedengoWayProfile.class);

	static final String PROFILE_NAME = "creedengo way";
	static final String PROFILE_PATH = PythonCreedengoWayProfile.class.getPackageName().replace('.', '/') + "/creedengo_way_profile.json";

	@Override
	public void define(Context context) {
//		LOGGER.debug("--- DDC --- PythonCreedengoWayProfile --- define --- BEGIN");
		NewBuiltInQualityProfile creedengoProfile = context.createBuiltInQualityProfile(PROFILE_NAME, LANGUAGE);
		loadProfile(creedengoProfile);
		creedengoProfile.done();
//		LOGGER.debug("--- DDC --- PythonCreedengoWayProfile --- define --- FIN");
	}

	private void loadProfile(NewBuiltInQualityProfile profile) {
		BuiltInQualityProfileJsonLoader.load(profile, REPOSITORY_KEY, PROFILE_PATH);
	}
}
