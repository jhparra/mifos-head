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

package org.mifos.application.customer.center.business.service;

import java.util.Date;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterBusinessServiceIntegrationTest extends MifosIntegrationTest {
    public CenterBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private SavingsTestHelper helper = new SavingsTestHelper();

    private SavingsOfferingBO savingsOffering;

    private SavingsBO savingsBO;

    private CenterBusinessService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = (CenterBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Center);
    }

    @Override
    public void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(savingsBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetCenter() throws Exception {
        center = createCenter("center1");
        createAccountsForCenter();
        savingsBO = getSavingsAccount(center, "fsaf6", "ads6");
        StaticHibernateUtil.closeSession();
        center = service.getCenter(center.getCustomerId());
        assertNotNull(center);
        assertEquals("center1", center.getDisplayName());
        assertEquals(2, center.getAccounts().size());
        assertEquals(0, center.getOpenLoanAccounts().size());
        assertEquals(1, center.getOpenSavingAccounts().size());
        assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
        StaticHibernateUtil.closeSession();
        retrieveAccountsToDelete();
    }

    public void testSuccessfulGet() throws Exception {
        center = createCenter("Center2");
        createAccountsForCenter();
        savingsBO = getSavingsAccount(center, "fsaf6", "ads6");
        StaticHibernateUtil.closeSession();
        center = service.getCenter(center.getCustomerId());
        assertNotNull(center);
        assertEquals("Center2", center.getDisplayName());
        assertEquals(2, center.getAccounts().size());
        assertEquals(0, center.getOpenLoanAccounts().size());
        assertEquals(1, center.getOpenSavingAccounts().size());
        assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
        StaticHibernateUtil.closeSession();
        retrieveAccountsToDelete();
    }

    public void testFailureGet() throws Exception {
        center = createCenter("Center1");
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getCenter(center.getCustomerId());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testFailureFindBySystemId() throws Exception {
        center = createCenter("Center1");
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.findBySystemId(center.getGlobalCustNum());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testSearch() throws Exception {
        center = createCenter("center1");
        QueryResult queryResult = service.search("center1", Short.valueOf("1"));
        assertEquals(1, queryResult.getSize());
        assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testFailureSearch() throws Exception {
        center = createCenter("Center1");
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.search("center1", Short.valueOf("1"));
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private SavingsBO getSavingsAccount(CustomerBO customerBO, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
                AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
    }

    private CenterBO createCenter(String name) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createCenter(name, meeting);
    }

    private GroupBO createGroup(String groupName) {
        return TestObjectFactory.createGroupUnderCenter(groupName, CustomerStatus.GROUP_ACTIVE, center);
    }

    private ClientBO createClient(String clientName) {
        return TestObjectFactory.createClient(clientName, CustomerStatus.CLIENT_ACTIVE, group);
    }

    private void createAccountsForCenter() throws Exception {
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        client = createClient("Client_Active_test");
    }

    private void retrieveAccountsToDelete() {
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }
}