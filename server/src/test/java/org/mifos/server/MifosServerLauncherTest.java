/*
 * Copyright (c) 2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.server;

import org.junit.Test;


/**
 * Tests launching of Mifos through simple embedded Servlet Container Web Server.
 * 
 * @author Michael Vorburger
 */
public class MifosServerLauncherTest {

	@Test
	public void testMifosStartupAndAssertLoginPageSeen() throws Exception {
		final ServerLauncher serverLauncher = new ServerLauncher();
		serverLauncher.startServer();
		
		// TODO Assert the Login Page is accessible. Use WebDriver / Selenium 2.0, share code in testFramework?
		
		serverLauncher.stopServer();
	}

}
