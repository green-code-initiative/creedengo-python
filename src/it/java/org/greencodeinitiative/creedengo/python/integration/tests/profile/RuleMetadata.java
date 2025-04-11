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

public class RuleMetadata {
	private String key;
	private String type;
	private String defaultSeverity;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultSeverity() {
		return defaultSeverity;
	}

	public void setDefaultSeverity(String defaultSeverity) {
		this.defaultSeverity = defaultSeverity;
	}

	@Override
	public String toString() {
		return "RuleMetadata{" +
				"key='" + key + '\'' +
				", type='" + type + '\'' +
				", defaultSeverity='" + defaultSeverity + '\'' +
				'}';
	}
}
