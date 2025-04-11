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
package org.greencodeinitiative.creedengo.python.integration.tests.profile;

import java.util.List;

public class ProfileMetadata {
	private String name;
	private String language;
	private List<String> ruleKeys;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<String> getRuleKeys() {
		return ruleKeys;
	}

	public void setRuleKeys(List<String> ruleKeys) {
		this.ruleKeys = ruleKeys;
	}

	@Override
	public String toString() {
		return "ProfileMetadata{" +
				"name='" + name + '\'' +
				", language='" + language + '\'' +
				", ruleKeys=" + ruleKeys +
				'}';
	}
}
