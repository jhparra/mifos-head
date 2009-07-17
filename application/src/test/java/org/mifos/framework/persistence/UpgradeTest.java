/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.persistence;

import org.testng.annotations.Test;

import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class UpgradeTest extends TestCase {

    public void testIncrementVersion() throws Exception {
        Database database = DummyUpgrade.databaseWithVersionTable(53);
        new DummyUpgrade(54).upgradeVersion(database.openConnection());
        assertEquals(54, new DatabaseVersionPersistence(database.openConnection()).read());
    }

    public void testNotReadyToIncrement() throws Exception {
        Database database = DummyUpgrade.databaseWithVersionTable(53);
        new DummyUpgrade(55).upgradeVersion(database.openConnection());
        assertEquals(53, new DatabaseVersionPersistence(database.openConnection()).read());
    }

    public void testValidateLookupValueKey() throws Exception {
        String validKey = "Permissions-Groups-CanBlacklistAGroup";
        String format = "Permissions-";
        assertTrue(DummyUpgrade.validateLookupValueKey(format, validKey));
        String invalidKey = "Groups-CanBlacklistAGroup";
        assertFalse(DummyUpgrade.validateLookupValueKey(format, invalidKey));
        invalidKey = null;
        assertFalse(DummyUpgrade.validateLookupValueKey(format, invalidKey));
        invalidKey = "";
        assertFalse(DummyUpgrade.validateLookupValueKey(format, invalidKey));
    }
}