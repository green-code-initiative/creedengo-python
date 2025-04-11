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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Manage XML Backup file of profile based on JSON official profile.
 *
 * <p>Example, following JSON profile:</p>
 * <pre>
 * {
 *  "name": "creedengo way",
 *  "language": "java",
 *  "ruleKeys": [
 * 	    "GCI1",
 * 	    "GCI2"
 *  ]
 * }
 * </pre>
 * <p>may produce following XML profile:</p>
 * <pre>
 * &lt;?xml version='1.0' encoding='UTF-8'?&gt;
 * &lt;profile&gt;
 * 	&lt;name&gt;creedengo way&lt;/name&gt;
 * 	&lt;language&gt;java&lt;/language&gt;
 * 	&lt;rules&gt;
 * 		&lt;rule&gt;
 * 			&lt;repositoryKey&gt;creedengo-java&lt;/repositoryKey&gt;
 * 			&lt;key&gt;GCI1&lt;/key&gt;
 * 			&lt;type&gt;CODE_SMELL&lt;/type&gt;
 * 			&lt;priority&gt;MINOR&lt;/priority&gt;
 * 			&lt;parameters /&gt;
 * 		&lt;/rule&gt;
 * 		&lt;rule&gt;
 * 			&lt;repositoryKey&gt;creedengo-java&lt;/repositoryKey&gt;
 * 			&lt;key&gt;GCI2&lt;/key&gt;
 * 			&lt;type&gt;CODE_SMELL&lt;/type&gt;
 * 			&lt;priority&gt;MINOR&lt;/priority&gt;
 * 			&lt;parameters /&gt;
 * 		&lt;/rule&gt;
 * 	&lt;/rules&gt;
 * &lt;/profile&gt;
 * </pre>
 */
public class ProfileBackup {
	private static final MessageFormat TEMPLATE_PROFIL = new MessageFormat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					"<profile>\n" +
					"    <name>{0}</name>\n" +
					"    <language>{1}</language>\n" +
					"    <rules>\n" +
					"    {2}\n" +
					"    </rules>\n" +
					"</profile>\n"
	);
	private static final MessageFormat TEMPLATE_RULE = new MessageFormat(
			"<rule>\n" +
					"    <repositoryKey>{0}</repositoryKey>\n" +
					"    <key>{1}</key>\n" +
					"    <type>{2}</type>\n" +
					"    <priority>{3}</priority>\n" +
					"    <parameters />\n" +
					"</rule>\n"
	);

	private final ObjectMapper mapper;
	private final URI jsonProfile;

	public ProfileBackup(URI jsonProfile) {
		this.mapper = new ObjectMapper();
		// Ignore unknown properties
		this.mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

		this.jsonProfile = jsonProfile;
	}

	private transient ProfileMetadata profileMetadata;

	private ProfileMetadata profileMetadata() {
		if (profileMetadata == null) {
			try (InputStream profilJsonFile = jsonProfile.toURL().openStream()) {
				profileMetadata = mapper.readValue(profilJsonFile, ProfileMetadata.class);
			} catch (IOException e) {
				throw new RuntimeException("Unable to load JSON Profile: " + jsonProfile, e);
			}
		}
		return profileMetadata;
	}

	private RuleMetadata loadRule(String language, String ruleKey) {
		try (InputStream ruleMetadataJsonFile = ClassLoader.getSystemResourceAsStream("org/green-code-initiative/rules/" + language + "/" + ruleKey + ".json")) {
			RuleMetadata result = mapper.readValue(ruleMetadataJsonFile, RuleMetadata.class);
			result.setKey(ruleKey);
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String xmlProfile() throws IOException {
		ProfileMetadata profileMetadata = profileMetadata();
		String language = profileMetadata.getLanguage();
		List<RuleMetadata> rules = profileMetadata.getRuleKeys().stream()
		                                          .map(ruleKey -> this.loadRule(language, ruleKey))
		                                          .collect(Collectors.toList());
		StringBuilder output = new StringBuilder();
		String repositoryKey = "creedengo-" + profileMetadata.getLanguage();
		rules.forEach(rule -> output.append(
				xmlRule(
						repositoryKey,
						rule.getKey(),
						rule.getType(),
						rule.getDefaultSeverity().toUpperCase()
				))
		);
		return TEMPLATE_PROFIL.format(new Object[]{
				profileMetadata.getName(),
				profileMetadata.getLanguage(),
				output.toString()
		});
	}

	private String xmlRule(String repositoryKey, String key, String type, String priority) {
		return TEMPLATE_RULE.format(new Object[]{
				repositoryKey,
				key,
				type,
				priority
		});
	}

	/**
	 * Get the content of XML Profil in datauri format.
	 */
	public URL profileDataUri() {
		try {
			String xmlProfileContent = xmlProfile();
			String xmlProfileBase64encoded = Base64.getEncoder().encodeToString(xmlProfileContent.getBytes());
			return new URL("data:text/xml;base64," + xmlProfileBase64encoded);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String language() {
		return profileMetadata().getLanguage();
	}


	public String name() {
		return profileMetadata().getName();
	}
}
