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

package org.greencodeinitiative.creedengo.python.checks;

import org.sonar.check.Rule;

import java.util.regex.Pattern;

@Rule(key = "GCI24")
public class AvoidUnlimitedSQLRequest extends AbstractSQLPatternCheck {

    private static final String MESSAGE_RULE = "Don't use the query SELECT _ FROM _ WHERE _ without a limit";

    private static final Pattern PATTERN = Pattern.compile("(?i).*select.*from(?!.*\\blimit\\b).*");

    @Override
    protected String getMessageRule() {
        return MESSAGE_RULE;
    }

    @Override
    protected Pattern getPattern() {
        return PATTERN;
    }
}