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

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistance.service.BulkEntryPersistenceService;
import org.mifos.application.collectionsheet.util.helpers.BulkEntryDataView;
import org.mifos.application.collectionsheet.util.helpers.BulkEntryNodeBuilder;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.client.business.service.ClientService;
import org.mifos.application.customer.client.business.service.StandardClientService;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class CollectionSheetEntryBO extends BusinessObject {

    private CustomerPersistence customerPersistenceService;
    private ClientService clientService;
    private PersonnelView loanOfficer;
    private OfficeView office;
    private PaymentTypeView paymentType;
    private CollectionSheetEntryView bulkEntryParent;
    private String receiptId;
    private Date receiptDate;
    private Date transactionDate;
    private List<PrdOfferingBO> loanProducts;
    private List<PrdOfferingBO> savingsProducts;
    private int totalCustomers;
    private static MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);

    public CollectionSheetEntryBO() {
        super();
        customerPersistenceService = new CustomerPersistence();
        clientService = new StandardClientService();
    }

    public CollectionSheetEntryBO(UserContext userContext) {
        super(userContext);
        customerPersistenceService = new CustomerPersistence();
    }

    public CollectionSheetEntryView getBulkEntryParent() {
        return bulkEntryParent;
    }

    public void setBulkEntryParent(CollectionSheetEntryView bulkEntryParent) {
        this.bulkEntryParent = bulkEntryParent;
    }

    public PersonnelView getLoanOfficer() {
        return loanOfficer;
    }

    public void setLoanOfficer(PersonnelView loanOfficer) {
        this.loanOfficer = loanOfficer;
    }

    public OfficeView getOffice() {
        return office;
    }

    public void setOffice(OfficeView office) {
        this.office = office;
    }

    public PaymentTypeView getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeView paymentType) {
        this.paymentType = paymentType;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<PrdOfferingBO> getLoanProducts() {
        return loanProducts;
    }

    public void setLoanProducts(List<PrdOfferingBO> loanProducts) {
        this.loanProducts = loanProducts;
    }

    public List<PrdOfferingBO> getSavingsProducts() {
        return savingsProducts;
    }

    public void setSavingsProducts(List<PrdOfferingBO> savingsProducts) {
        this.savingsProducts = savingsProducts;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    /**
     * This method populates all the data required for the BulkEntry. This
     * method retrieves all the customers and there respective accounts. This
     * forms a BulkEntry object which can be used to display data in BulkEntry
     * page.
     * 
     */
    public void buildBulkEntryView(CustomerView parentCustomer) throws SystemException, ApplicationException {
        BulkEntryPersistenceService bulkEntryPersistenceService = new BulkEntryPersistenceService();
        List<CustomerBO> allChildNodes = retrieveActiveCustomersUnderParent(parentCustomer.getCustomerSearchId(),
                office.getOfficeId());
        List<CollectionSheetEntryInstallmentView> bulkEntryLoanScheduleViews = bulkEntryPersistenceService
                .getBulkEntryActionView(transactionDate, parentCustomer.getCustomerSearchId(), office.getOfficeId(),
                        AccountTypes.LOAN_ACCOUNT);
        List<CollectionSheetEntryInstallmentView> bulkEntrySavingsScheduleViews = bulkEntryPersistenceService
                .getBulkEntryActionView(transactionDate, parentCustomer.getCustomerSearchId(), office.getOfficeId(),
                        AccountTypes.SAVINGS_ACCOUNT);
        List<CollectionSheetEntryInstallmentView> bulkEntryCustomerScheduleViews = bulkEntryPersistenceService
                .getBulkEntryActionView(transactionDate, parentCustomer.getCustomerSearchId(), office.getOfficeId(),
                        AccountTypes.CUSTOMER_ACCOUNT);
        List<CollectionSheetEntryAccountFeeActionView> bulkEntryLoanFeeScheduleViews = bulkEntryPersistenceService
                .getBulkEntryFeeActionView(transactionDate, parentCustomer.getCustomerSearchId(), office.getOfficeId(),
                        AccountTypes.LOAN_ACCOUNT);
        List<CollectionSheetEntryAccountFeeActionView> bulkEntryCustomerFeeScheduleViews = bulkEntryPersistenceService
                .getBulkEntryFeeActionView(transactionDate, parentCustomer.getCustomerSearchId(), office.getOfficeId(),
                        AccountTypes.CUSTOMER_ACCOUNT);
        List<ClientAttendanceDto> clientAttendance = bulkEntryPersistenceService.getClientAttendance(transactionDate,
                office.getOfficeId());

        logger.debug("bulkEntryClientAttendanceViews" + clientAttendance.size());

        Collections.sort(allChildNodes, CustomerBO.searchIdComparator());

        totalCustomers = allChildNodes.size();
        bulkEntryParent = BulkEntryNodeBuilder.buildBulkEntry(allChildNodes, parentCustomer, transactionDate,
                bulkEntryLoanScheduleViews, bulkEntryCustomerScheduleViews, bulkEntryLoanFeeScheduleViews,
                bulkEntryCustomerFeeScheduleViews, clientAttendance);
        BulkEntryNodeBuilder.buildBulkEntrySavingsAccounts(bulkEntryParent, transactionDate,
                bulkEntrySavingsScheduleViews);
        BulkEntryNodeBuilder.buildBulkEntryClientAttendance(bulkEntryParent, transactionDate, clientAttendance);
    }

    public List<CustomerBO> retrieveActiveClientsUnderParent(String searchId) throws SystemException,
            ApplicationException {
        return customerPersistenceService.getClientsUnderParent(searchId, office.getOfficeId());
    }

    private List<CustomerBO> retrieveActiveCustomersUnderParent(String searchId, Short officeId)
            throws SystemException, ApplicationException {
        return customerPersistenceService.getCustomersUnderParent(searchId, officeId);
    }

    public void setBulkEntryDataView(BulkEntryDataView bulkEntryDataView) {
        if (null == bulkEntryParent || null == bulkEntryDataView) {
            return;
        }
        if (bulkEntryParent.getCustomerDetail().getCustomerLevelId().equals(CustomerLevel.CENTER.getValue())) {
            populateCenter(bulkEntryDataView);
        } else {
            populateGroup(bulkEntryDataView);
        }
    }

    private void populateCenter(BulkEntryDataView bulkEntryDataView) {
        List<CollectionSheetEntryView> bulkEntryChildrenViews = bulkEntryParent.getCollectionSheetEntryChildren();
        int rowIndex = 0;
        for (CollectionSheetEntryView bulkEntryChildernView : bulkEntryChildrenViews) {
            List<CollectionSheetEntryView> bulkEntrySubChildrenViews = bulkEntryChildernView
                    .getCollectionSheetEntryChildren();
            for (CollectionSheetEntryView bulkEntrySubChildView : bulkEntrySubChildrenViews) {
                setLoanAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getLoanAmountEntered(),
                        bulkEntryDataView.getDisbursementAmountEntered());
                setSavingsAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                        bulkEntryDataView.getWithDrawalAmountEntered());
                setCustomerAccountAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView
                        .getCustomerAccountAmountEntered());
                setClientAttendance(bulkEntrySubChildView, bulkEntryDataView.getAttendance()[rowIndex]);
                rowIndex++;
            }
            setLoanAmountEntered(bulkEntryChildernView, rowIndex, bulkEntryDataView.getLoanAmountEntered(),
                    bulkEntryDataView.getDisbursementAmountEntered());
            setSavingsAmountEntered(bulkEntryChildernView, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                    bulkEntryDataView.getWithDrawalAmountEntered());
            setCustomerAccountAmountEntered(bulkEntryChildernView, rowIndex, bulkEntryDataView
                    .getCustomerAccountAmountEntered());
            rowIndex++;
        }
        setSavingsAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                bulkEntryDataView.getWithDrawalAmountEntered());
        setCustomerAccountAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getCustomerAccountAmountEntered());
    }

    private void populateGroup(BulkEntryDataView bulkEntryDataView) {
        int rowIndex = 0;
        List<CollectionSheetEntryView> bulkEntrySubChildrens = bulkEntryParent.getCollectionSheetEntryChildren();
        for (CollectionSheetEntryView bulkEntrySubChildView : bulkEntrySubChildrens) {
            setLoanAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getLoanAmountEntered(),
                    bulkEntryDataView.getDisbursementAmountEntered());
            setSavingsAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                    bulkEntryDataView.getWithDrawalAmountEntered());
            setCustomerAccountAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView
                    .getCustomerAccountAmountEntered());
            setClientAttendance(bulkEntrySubChildView, bulkEntryDataView.getAttendance()[rowIndex]);
            rowIndex++;
        }

        setLoanAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getLoanAmountEntered(), bulkEntryDataView
                .getDisbursementAmountEntered());
        setSavingsAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                bulkEntryDataView.getWithDrawalAmountEntered());
        setCustomerAccountAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getCustomerAccountAmountEntered());
    }

    private void setLoanAmountEntered(CollectionSheetEntryView collectionSheetEntryView, int rowIndex,
            String[][] loanAmountsEntered, String[][] disBursementAmountEntered) {
        int columnIndex = 0;
        for (PrdOfferingBO prdOffering : loanProducts) {
            String enteredAmountValue = loanAmountsEntered[rowIndex][columnIndex];
            String disbursementAmountEntered = disBursementAmountEntered[rowIndex][columnIndex];
            collectionSheetEntryView.setLoanAmountsEntered(prdOffering.getPrdOfferingId(), enteredAmountValue,
                    disbursementAmountEntered);
            columnIndex++;
        }
    }

    private void setSavingsAmountEntered(CollectionSheetEntryView collectionSheetEntryView, int rowIndex,
            String[][] depositAmountsEntered, String[][] withDrawalsAmountEntered) {
        int columnIndex = 0;
        for (PrdOfferingBO prdOffering : savingsProducts) {
            String depositAmountEnteredValue = depositAmountsEntered[rowIndex][columnIndex];
            String withDrawalAmountEnteredValue = withDrawalsAmountEntered[rowIndex][columnIndex];
            if (depositAmountEnteredValue != null || withDrawalAmountEnteredValue != null) {
                collectionSheetEntryView.setSavinsgAmountsEntered(prdOffering.getPrdOfferingId(),
                        depositAmountEnteredValue, withDrawalAmountEnteredValue);
            }
            columnIndex++;
        }
    }

    private void setCustomerAccountAmountEntered(CollectionSheetEntryView collectionSheetEntryView, int rowIndex,
            String[] customerAccountAmountEntered) {
        String customerAccountAmountEnteredValue = customerAccountAmountEntered[rowIndex];
        if (customerAccountAmountEnteredValue != null)
            collectionSheetEntryView.setCustomerAccountAmountEntered(customerAccountAmountEnteredValue);
    }

    private void setClientAttendance(CollectionSheetEntryView collectionSheetEntryView, String attendance) {
        if (null != attendance && attendance.length() > 0) {
            collectionSheetEntryView.setAttendence(Short.valueOf(attendance));
        }
    }

    public CustomerPersistence getCustomerPersistenceService() {
        return this.customerPersistenceService;
    }

    public void setCustomerPersistenceService(CustomerPersistence customerPersistenceService) {
        this.customerPersistenceService = customerPersistenceService;
    }

    public ClientService getClientService() {
        return this.clientService;
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

}