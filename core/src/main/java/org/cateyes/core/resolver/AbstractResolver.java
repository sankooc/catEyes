/*
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cateyes.core.resolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cateyes.core.conn.ApacheConnector;
import org.cateyes.core.conn.ConnectorProvider;

public abstract class AbstractResolver implements Resolver {

	abstract protected String[] getRegexStrings();

	protected ApacheConnector connector = ConnectorProvider.getCommonConnector();
	

	protected ThreadLocal<String> threadlocal = new ThreadLocal<String>();
	
	//video quality
	protected int quality =-1;
	
	private Pattern[] patterns;

	protected AbstractResolver() {
		String[] regx = getRegexStrings();
		if (null == regx) {
			patterns = new Pattern[0];
			return;
		}
		patterns = new Pattern[regx.length];
		for (int i = 0; i < regx.length; i++) {
			patterns[i] = Pattern.compile(regx[i]);
		}
	}

	public boolean isPrefer(String uri) {
		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(uri);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
}
