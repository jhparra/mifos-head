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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Money;

public class CollSheetCustBO extends BusinessObject {

    private Long collSheetCustId;

    private Integer custId;

    private String custDisplayName;

    private Money custAccntFee;

    private Money custAccntPenalty;

    private Short custLevel;

    private Integer custAccntId;

    private Money totalDueSavingsLoan;

    private Short custOfficeId;

    private String searchId;

    private CollectionSheetBO collectionSheet;

    private Set<CollSheetLnDetailsEntity> collectionSheetLoanDetails;

    private Set<CollSheetSavingsDetailsEntity> collSheetSavingsDetails;

    private HashMap<Integer, Money> totalSavingsAmountForAccount;

    private Money collectiveLoanAmntDue;

    private Money collectiveLoanDisbursal;

    private Money collectiveSavingsAmntDue;

    private Money collectiveAccntCharges;

    private Money collectiveTotalCollection;

    private Money collectiveNetCashIn;

    private Integer parentCustomerId;

    private Short loanOfficerId;

    public CollSheetCustBO(Integer custId, String custDisplayName, Short custLevel, Short custOfficeId,
            String searchId, Short loanOfficerId) {
        super();
        initFields(custId, custDisplayName, custLevel, custOfficeId, searchId, loanOfficerId);
    }

    public CollSheetCustBO() {
        this(null, null, null, null, null, null);
    }

    private void initFields(Integer custId, String custDisplayName, Short custLevel, Short custOfficeId,
            String searchId, Short loanOfficerId) {
        this.custId = custId;
        this.custDisplayName = custDisplayName;
        this.custLevel = custLevel;
        this.custOfficeId = custOfficeId;
        this.searchId = searchId;
        this.loanOfficerId = loanOfficerId;
        collectionSheetLoanDetails = new HashSet<CollSheetLnDetailsEntity>();
        collSheetSavingsDetails = new HashSet<CollSheetSavingsDetailsEntity>();
    }

    public void populateInstanceForTest(Integer custId, String custDisplayName, Short custLevel, Short custOfficeId,
            String searchId, Short loanOfficerId) {
        initFields(custId, custDisplayName, custLevel, custOfficeId, searchId, loanOfficerId);
    }

    public Long getCollSheetCustId() {
        return collSheetCustId;
    }

    public void setCollSheetCustId(Long collSheetCustId) {
        this.collSheetCustId = collSheetCustId;
    }

    public Money getCustAccntFee() {
        return custAccntFee;
    }

    public void setCustAccntFee(Money custAccntFee) {
        this.custAccntFee = custAccntFee;
    }

    public Integer getCustAccntId() {
        return custAccntId;
    }

    public void setCustAccntId(Integer custAccntId) {
        this.custAccntId = custAccntId;
    }

    public Money getCustAccntPenalty() {
        return custAccntPenalty;
    }

    public void setCustAccntPenalty(Money custAccntPenalty) {
        this.custAccntPenalty = custAccntPenalty;
    }

    public String getCustDisplayName() {
        return custDisplayName;
    }

    public void setCustDisplayName(String custDisplayName) {
        this.custDisplayName = custDisplayName;
    }

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public Short getCustLevel() {
        return custLevel;
    }

    public void setCustLevel(Short custLevel) {
        this.custLevel = custLevel;
    }

    public Money getTotalDueSavingsLoan() {
        return totalDueSavingsLoan;
    }

    public void setTotalDueSavingsLoan(Money totalDueSavingsLoan) {
        this.totalDueSavingsLoan = totalDueSavingsLoan;
    }

    public Short getCustOfficeId() {
        return custOfficeId;
    }

    public void setCustOfficeId(Short custOfficeId) {
        this.custOfficeId = custOfficeId;
    }

    public CollectionSheetBO getCollectionSheet() {
        return collectionSheet;
    }

    public void setCollectionSheet(CollectionSheetBO collectionSheet) {
        this.collectionSheet = collectionSheet;
    }

    /**
     * If the collSheetCustId is not null it compares for equality based on it
     * else it checks for collectionSheet not being null in which case it
     * returns true if both custId and collectionSheet.collSheetDate is equal to
     * the corresponding properties of the passed object else it calls
     * super.equals();
     * 
     * @param obj
     *            - Object to be compared for equality.
     */
    @Override
    public boolean equals(Object obj) {
        CollSheetCustBO collectionSheetCustomerObj = (CollSheetCustBO) obj;
        if (null != collSheetCustId && null != collectionSheetCustomerObj.getCollSheetCustId()) {
            return collSheetCustId.equals(collectionSheetCustomerObj.getCollSheetCustId());
        } else if (null != this.collectionSheet && null != collectionSheetCustomerObj.getCollectionSheet()) {
            return this.custId.equals(collectionSheetCustomerObj.getCustId())
                    && this.collectionSheet.getCollSheetDate().equals(
                            collectionSheetCustomerObj.getCollectionSheet().getCollSheetDate());
        } else {
            return super.equals(collectionSheetCustomerObj);
        }

    }

    @Override
    public int hashCode() {
        return this.custId.hashCode();
    }

    /**
     * This method populates customer details from the passed in customer
     * object.
     */
    public void populateCustomerDetails(CustomerBO customer) {
        this.custId = customer.getCustomerId();
        this.custLevel = customer.getCustomerLevel().getId();
        this.custDisplayName = customer.getDisplayName();
        this.searchId = customer.getSearchId();
        CustomerBO parentCustomer = customer.getParentCustomer();
        if (null != parentCustomer) {
            this.parentCustomerId = parentCustomer.getCustomerId();
        }
        this.loanOfficerId = customer.getPersonnel().getPersonnelId();
        this.custOfficeId = customer.getOffice().getOfficeId();
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public void populateAccountDetails(AccountActionDateEntity accountActionDateEntity) {
        if (accountActionDateEntity instanceof CustomerScheduleEntity) {
            CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) accountActionDateEntity;
            this.custAccntId = accountActionDate.getAccount().getAccountId();
            this.custAccntFee = accountActionDate.getTotalFeeDue().add(accountActionDate.getMiscFeeDue());
        } else if (accountActionDateEntity instanceof LoanScheduleEntity) {
            LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountActionDateEntity;
            this.custAccntPenalty = accountActionDate.getPenaltyDue();
            this.custAccntId = accountActionDate.getAccount().getAccountId();
            this.custAccntFee = accountActionDate.getTotalFeeDue().add(accountActionDate.getMiscFeeDue());
        }
    }

    /**
     * This method adds the passed collectionSheetLoanDetail object to the
     * collectionSheetLoanDetailSet. It also sets the bidirectional
     * relationship. If the set is null it instantates a new HashSet.
     */
    public void addCollectionSheetLoanDetail(CollSheetLnDetailsEntity collectionSheetLoanDetail) {
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                "inside add collection sheet loan detail method");
        collectionSheetLoanDetail.setCollectionSheetCustomer(this);
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                "after setting the collection sheet customer relation ship");
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                "Total Amnt due to be disbursed is" + collectionSheetLoanDetail.getAmntToBeDisbursed());
        this.collectiveLoanAmntDue = new Money().add(collectiveLoanAmntDue).add(
                collectionSheetLoanDetail.getTotalAmntDue());
        this.collectiveLoanDisbursal = new Money().add(collectiveLoanDisbursal).add(
                collectionSheetLoanDetail.getAmntToBeDisbursed());
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after updating the totals");
        if (null == collectionSheetLoanDetails) {
            collectionSheetLoanDetails = new HashSet<CollSheetLnDetailsEntity>();
        }
        this.collectionSheetLoanDetails.add(collectionSheetLoanDetail);
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                "after adding loan details object to the set");
    }

    public Set<CollSheetLnDetailsEntity> getCollectionSheetLoanDetails() {
        return collectionSheetLoanDetails;
    }

    public void setCollectionSheetLoanDetails(Set<CollSheetLnDetailsEntity> collectionSheetLoanDetailsSet) {
        this.collectionSheetLoanDetails = collectionSheetLoanDetailsSet;
    }

    public Set<CollSheetSavingsDetailsEntity> getCollSheetSavingsDetails() {
        return collSheetSavingsDetails;
    }

    public void setCollSheetSavingsDetails(Set<CollSheetSavingsDetailsEntity> collSheetSavingsDetails) {
        this.collSheetSavingsDetails = collSheetSavingsDetails;
    }

    /**
     * This method adds the passed collectionSheetSavingsDetail object to the
     * collectionSheetSavingsDetail Set. It also sets the bidirectional
     * relationship.
     */
    public void addCollectionSheetSavingsDetail(CollSheetSavingsDetailsEntity collectionSheetSavingsDetail) {
        collectionSheetSavingsDetail.setCollectionSheetCustomer(this);
        this.collectiveSavingsAmntDue = new Money().add(collectiveSavingsAmntDue).add(
                collectionSheetSavingsDetail.getRecommendedAmntDue())
                .add(collectionSheetSavingsDetail.getAmntOverDue());
        if (null == collSheetSavingsDetails) {
            collSheetSavingsDetails = new HashSet<CollSheetSavingsDetailsEntity>();
        }
        addToTotalSavingsForAccount(collectionSheetSavingsDetail.getAccountId(), collectiveSavingsAmntDue);
        this.collSheetSavingsDetails.add(collectionSheetSavingsDetail);

    }

    private void addToTotalSavingsForAccount(Integer accountId, Money accountSavingsAmntDue) {
        Money totalAmountDueForAccount = new Money();
        if (null == totalSavingsAmountForAccount) {
            totalSavingsAmountForAccount = new HashMap<Integer, Money>();
        }
        if (totalSavingsAmountForAccount.containsKey(accountId)) {
            totalAmountDueForAccount = totalSavingsAmountForAccount.get(accountId);
            totalSavingsAmountForAccount.put(accountId, totalAmountDueForAccount.add(accountSavingsAmntDue));
        } else {
            totalSavingsAmountForAccount.put(accountId, accountSavingsAmntDue);
        }
    }

    public Money getTotalSavingsForAccount(Integer accountId) {
        Money totalAmountDueForAccount = new Money();
        if (totalSavingsAmountForAccount.containsKey(accountId)) {
            totalAmountDueForAccount = totalSavingsAmountForAccount.get(accountId);
        }
        return totalAmountDueForAccount;
    }

    public CollSheetLnDetailsEntity getLoanDetailsForAccntId(Integer accountId) {
        if (null != collectionSheetLoanDetails && collectionSheetLoanDetails.size() > 0) {
            for (CollSheetLnDetailsEntity collSheetLnDetail : collectionSheetLoanDetails) {
                if (collSheetLnDetail.getAccountId().equals(accountId)) {
                    return collSheetLnDetail;
                }

            }
        }
        return null;
    }

    public CollSheetSavingsDetailsEntity getSavingsDetailsForAccntId(Integer accountId) {
        if (null != collSheetSavingsDetails && collSheetSavingsDetails.size() > 0) {
            for (CollSheetSavingsDetailsEntity collSheetSavingDetail : collSheetSavingsDetails) {
                if (collSheetSavingDetail.getAccountId().equals(accountId)) {
                    return collSheetSavingDetail;
                }

            }
        }
        return null;
    }

    public Money getCollectiveAccntCharges() {
        return collectiveAccntCharges;
    }

    public void setCollectiveAccntCharges(Money collectiveAccntCharges) {
        this.collectiveAccntCharges = collectiveAccntCharges;
    }

    public Money getCollectiveLoanAmntDue() {
        return collectiveLoanAmntDue;
    }

    public void setCollectiveLoanAmntDue(Money collectiveLoanAmntDue) {
        this.collectiveLoanAmntDue = collectiveLoanAmntDue;
    }

    public Money getCollectiveLoanDisbursal() {
        return collectiveLoanDisbursal;
    }

    public void setCollectiveLoanDisbursal(Money collectiveLoanDisbursal) {
        this.collectiveLoanDisbursal = collectiveLoanDisbursal;
    }

    public Money getCollectiveNetCashIn() {
        return collectiveNetCashIn;
    }

    public void setCollectiveNetCashIn(Money collectiveNetCashIn) {
        this.collectiveNetCashIn = collectiveNetCashIn;
    }

    public Money getCollectiveSavingsAmntDue() {
        return collectiveSavingsAmntDue;
    }

    public void setCollectiveSavingsAmntDue(Money collectiveSavingsAmntDue) {
        this.collectiveSavingsAmntDue = collectiveSavingsAmntDue;
    }

    public Money getCollectiveTotalCollection() {
        return collectiveTotalCollection;
    }

    public void setCollectiveTotalCollection(Money collectiveTotalCollection) {
        this.collectiveTotalCollection = collectiveTotalCollection;
    }

    public Integer getParentCustomerId() {
        return parentCustomerId;
    }

    public void setParentCustomerId(Integer parentCustomerId) {
        this.parentCustomerId = parentCustomerId;
    }

    public void addCollectiveTotalsForChild(CollSheetCustBO collSheetCustomer) {
        this.collectiveAccntCharges = new Money().add(collectiveAccntCharges).add(
                collSheetCustomer.getCollectiveAccntCharges());
        this.collectiveLoanAmntDue = new Money().add(collectiveLoanAmntDue).add(
                collSheetCustomer.getCollectiveLoanAmntDue());
        this.collectiveLoanDisbursal = new Money().add(collectiveLoanDisbursal).add(
                collSheetCustomer.getCollectiveLoanDisbursal());
        this.collectiveNetCashIn = new Money().add(collectiveNetCashIn).add(collSheetCustomer.getCollectiveNetCashIn());
        this.collectiveSavingsAmntDue = new Money().add(collectiveSavingsAmntDue).add(
                collSheetCustomer.getCollectiveSavingsAmntDue());
        this.collectiveTotalCollection = new Money().add(collectiveTotalCollection).add(
                collSheetCustomer.getCollectiveTotalCollection());

    }

    public Short getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(Short loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

}