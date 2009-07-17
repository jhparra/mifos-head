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

package org.mifos.application.collectionsheet.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistance.service.BulkEntryPersistenceService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class BulkEntryViewIntegrationTest extends MifosIntegrationTest {

    public BulkEntryViewIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private CustomerBO center;

    private CustomerBO group;

    private LoanBO account1;

    private LoanBO account2;

    private LoanOfferingBO loanOffering;

    private ClientBO client;

    private CustomerPersistence customerPersistence;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        customerPersistence = new CustomerPersistence();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUpWithoutDeletingProduct(account1);
        TestObjectFactory.cleanUpWithoutDeletingProduct(account2);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.removeObject(loanOffering);
        StaticHibernateUtil.closeSession();
        customerPersistence = null;
        super.tearDown();
    }

    public void testMultipleRepaymentAccountsForSingleProduct() throws SystemException, ApplicationException {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account1 = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        account2 = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.closeSession();

        CollectionSheetEntryBO bulkEntry = new CollectionSheetEntryBO();
        bulkEntry.setOffice(getOfficeView(center.getOffice()));
        bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
        bulkEntry.setPaymentType(getPaymentTypeView());
        bulkEntry.setTransactionDate(new java.sql.Date(System.currentTimeMillis()));
        CustomerView parentCustomer = getCustomerView(center);
        bulkEntry.buildBulkEntryView(parentCustomer);

        CollectionSheetEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
        CollectionSheetEntryView groupBulkEntryView = parentBulkEntryView.getCollectionSheetEntryChildren().get(0);
        List<LoanAccountsProductView> loanProducts = groupBulkEntryView.getLoanAccountDetails();
        LoanAccountsProductView loanAccountsProductView = loanProducts.get(0);
        assertEquals("The Size of products ", loanProducts.size(), 1);
        assertEquals("The size of loan Accounts", loanAccountsProductView.getLoanAccountViews().size(), 2);
        assertEquals("The amount due for loan account1", account1.getTotalAmountDue().getAmountDoubleValue(), 212.0);
        assertEquals("The amount due for loan account2", account2.getTotalAmountDue().getAmountDoubleValue(), 212.0);
        assertEquals("The toal amount due for the product", loanAccountsProductView.getTotalAmountDue().doubleValue(),
                424.0);
        StaticHibernateUtil.closeSession();
        account1 = (LoanBO) new AccountPersistence().getAccount(account1.getAccountId());
        account2 = (LoanBO) new AccountPersistence().getAccount(account2.getAccountId());
    }

    public void testMultipleRepaymentDisbursalAccountsForSingleProduct() throws SystemException, ApplicationException {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account1 = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        account2 = TestObjectFactory.createLoanAccountWithDisbursement("42423142341", group,
                AccountState.LOAN_APPROVED, startDate, loanOffering, 1);
        StaticHibernateUtil.closeSession();

        CollectionSheetEntryBO bulkEntry = new CollectionSheetEntryBO();
        bulkEntry.setOffice(getOfficeView(center.getOffice()));
        bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
        bulkEntry.setPaymentType(getPaymentTypeView());
        bulkEntry.setTransactionDate(new java.sql.Date(startDate.getTime()));
        CustomerView parentCustomer = getCustomerView(center);
        bulkEntry.buildBulkEntryView(parentCustomer);

        CollectionSheetEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
        CollectionSheetEntryView groupBulkEntryView = parentBulkEntryView.getCollectionSheetEntryChildren().get(0);
        List<LoanAccountsProductView> loanProducts = groupBulkEntryView.getLoanAccountDetails();
        LoanAccountsProductView loanAccountsProductView = loanProducts.get(0);
        assertEquals("The Size of products ", loanProducts.size(), 1);
        assertEquals("The size of loan Accounts", loanAccountsProductView.getLoanAccountViews().size(), 2);
        assertEquals("The amount due for loan account1", account1.getTotalAmountDue().getAmountDoubleValue(), 212.0);
        assertEquals("The toal amount due for the product", loanAccountsProductView.getTotalAmountDue().doubleValue(),
                242.0);
        assertEquals("The toal amount to be disbursed for the product", loanAccountsProductView
                .getTotalDisburseAmount().doubleValue(), 300.0);
        StaticHibernateUtil.closeSession();
        account1 = (LoanBO) new AccountPersistence().getAccount(account1.getAccountId());
        account2 = (LoanBO) new AccountPersistence().getAccount(account2.getAccountId());
    }

    public void testMultipleDisbursalAccountsForSingleProduct() throws SystemException, ApplicationException {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account1 = TestObjectFactory.createLoanAccountWithDisbursement("42423142341", group,
                AccountState.LOAN_APPROVED, new Date(System.currentTimeMillis()), loanOffering, 1);
        account2 = TestObjectFactory.createLoanAccountWithDisbursement("42423142341", group,
                AccountState.LOAN_APPROVED, new Date(System.currentTimeMillis()), loanOffering, 1);
        StaticHibernateUtil.closeSession();

        CollectionSheetEntryBO bulkEntry = new CollectionSheetEntryBO();
        bulkEntry.setOffice(getOfficeView(center.getOffice()));
        bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
        bulkEntry.setPaymentType(getPaymentTypeView());
        bulkEntry.setTransactionDate(new java.sql.Date(System.currentTimeMillis()));
        CustomerView parentCustomer = getCustomerView(center);
        bulkEntry.buildBulkEntryView(parentCustomer);

        CollectionSheetEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
        CollectionSheetEntryView groupBulkEntryView = parentBulkEntryView.getCollectionSheetEntryChildren().get(0);
        List<LoanAccountsProductView> loanProducts = groupBulkEntryView.getLoanAccountDetails();
        LoanAccountsProductView loanAccountsProductView = loanProducts.get(0);
        assertEquals("The Size of products ", loanProducts.size(), 1);
        assertEquals("The size of loan Accounts", loanAccountsProductView.getLoanAccountViews().size(), 2);
        assertEquals("The toal amount due for the product", loanAccountsProductView.getTotalAmountDue().doubleValue(),
                60.0);
        assertEquals("The toal amount to be disbursed for the product", loanAccountsProductView
                .getTotalDisburseAmount().doubleValue(), 600.0);
        StaticHibernateUtil.closeSession();
        account1 = (LoanBO) new AccountPersistence().getAccount(account1.getAccountId());
        account2 = (LoanBO) new AccountPersistence().getAccount(account2.getAccountId());
    }

    public void testPopulateForCustomerAccount() throws PersistenceException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        BulkEntryPersistenceService bulkEntryPersistanceService = new BulkEntryPersistenceService();
        List<CollectionSheetEntryInstallmentView> bulkEntryAccountActionViews = bulkEntryPersistanceService
                .getBulkEntryActionView(DateUtils.getCurrentDateWithoutTimeStamp(), center.getSearchId(), center
                        .getOffice().getOfficeId(), AccountTypes.CUSTOMER_ACCOUNT);
        List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews = bulkEntryPersistanceService
                .getBulkEntryFeeActionView(DateUtils.getCurrentDateWithoutTimeStamp(), center.getSearchId(), center
                        .getOffice().getOfficeId(), AccountTypes.CUSTOMER_ACCOUNT);
        assertNotNull(center.getCustomerAccount());
        CollectionSheetEntryView collectionSheetEntryView = new CollectionSheetEntryView(getCustomerView(center));
        collectionSheetEntryView.populateCustomerAccountInformation(center, bulkEntryAccountActionViews,
                collectionSheetEntryAccountFeeActionViews);
        CustomerAccountView customerAccountView = collectionSheetEntryView.getCustomerAccountDetails();
        assertEquals("The retrieved accountId of the customer account should be equal to the created", center
                .getCustomerAccount().getAccountId(), customerAccountView.getAccountId());
        assertEquals("The size of the due insallments is ", customerAccountView.getAccountActionDates().size(), 1);
        assertEquals("The amount due is ", customerAccountView.getTotalAmountDue().getAmountDoubleValue(), 100.0);
    }

    public void testPopulateAttendance() throws SystemException, ApplicationException {
        BulkEntryPersistenceService bulkEntryPersistenceService = new BulkEntryPersistenceService();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);

        java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();

        java.sql.Date sqlMeetingDate = new java.sql.Date(Calendar.getInstance().getTimeInMillis());

        ClientAttendanceBO expectedClientAttendance = new ClientAttendanceBO();
        expectedClientAttendance.setAttendance(AttendanceType.ABSENT);
        expectedClientAttendance.setMeetingDate(meetingDate);
        client.addClientAttendance(expectedClientAttendance);
        customerPersistence.createOrUpdate(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        List<ClientAttendanceDto> clientAttendanceList = bulkEntryPersistenceService.getClientAttendance(meetingDate,
                center.getOffice().getOfficeId());

        CollectionSheetEntryView collectionSheetEntryView = new CollectionSheetEntryView(getCustomerView(client));
        collectionSheetEntryView.populateClientAttendance(client.getCustomerId(), sqlMeetingDate, clientAttendanceList);

        assertEquals("Attendance was set", expectedClientAttendance.getAttendance().toString(),
                collectionSheetEntryView.getAttendence().toString());

        CollectionSheetEntryBO bulkEntry = new CollectionSheetEntryBO();
        bulkEntry.setOffice(getOfficeView(center.getOffice()));
        bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
        bulkEntry.setPaymentType(getPaymentTypeView());
        bulkEntry.setTransactionDate(new java.sql.Date(System.currentTimeMillis()));
        CustomerView parentCustomer = getCustomerView(center);
        bulkEntry.buildBulkEntryView(parentCustomer);

        CollectionSheetEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
        CollectionSheetEntryView groupBulkEntryView = parentBulkEntryView.getCollectionSheetEntryChildren().get(0);
        CollectionSheetEntryView clientBulkEntryView = groupBulkEntryView.getCollectionSheetEntryChildren().get(0);

        // System.out.println(clientBulkEntryView.getAttendence());

        assertEquals("Testing BulkEntryBO.buildBulkEntryView", expectedClientAttendance.getAttendance().toString(),
                clientBulkEntryView.getAttendence().toString());

        StaticHibernateUtil.closeSession();
    }

    private PersonnelView getPersonnelView(PersonnelBO personnel) {
        PersonnelView personnelView = new PersonnelView(personnel.getPersonnelId(), personnel.getDisplayName());
        return personnelView;
    }

    private PaymentTypeView getPaymentTypeView() {
        PaymentTypeView paymentTypeView = new PaymentTypeView();
        paymentTypeView.setPaymentTypeId(Short.valueOf("1"));
        return paymentTypeView;
    }

    private OfficeView getOfficeView(OfficeBO office) throws OfficeException {
        OfficeView officeView = new OfficeView(office.getOfficeId(), office.getOfficeName(), office.getOfficeLevel()
                .getValue(), office.getVersionNo());
        return officeView;
    }

    private CustomerView getCustomerView(CustomerBO customer) {
        CustomerView customerView = new CustomerView();
        customerView.setCustomerId(customer.getCustomerId());
        customerView.setCustomerLevelId(customer.getCustomerLevel().getId());
        customerView.setCustomerSearchId(customer.getSearchId());
        customerView.setDisplayName(customer.getDisplayName());
        customerView.setGlobalCustNum(customer.getGlobalCustNum());
        customerView.setOfficeId(customer.getOffice().getOfficeId());
        if (null != customer.getParentCustomer())
            customerView.setParentCustomerId(customer.getParentCustomer().getCustomerId());
        customerView.setPersonnelId(customer.getPersonnel().getPersonnelId());
        customerView.setStatusId(customer.getCustomerStatus().getId());
        return customerView;
    }

}