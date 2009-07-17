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

package org.mifos.application.accounts.savings.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntrySavingsInstallmentView;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class SavingsAccountView extends View {

    private Integer accountId;

    private String depositAmountEntered;

    private String withDrawalAmountEntered;

    private List<CollectionSheetEntryInstallmentView> accountTrxnDetails;

    private Short accountType;

    private SavingsOfferingBO savingsOffering;

    private boolean isValidDepositAmountEntered;

    private boolean isValidWithDrawalAmountEntered;

    public SavingsAccountView(Integer accountId, AccountTypes accountType, SavingsOfferingBO savingsOffering) {
        this.accountId = accountId;
        this.accountType = accountType.getValue();
        this.savingsOffering = savingsOffering;
        accountTrxnDetails = new ArrayList<CollectionSheetEntryInstallmentView>();
        isValidDepositAmountEntered = true;
        isValidWithDrawalAmountEntered = true;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public List<CollectionSheetEntryInstallmentView> getAccountTrxnDetails() {
        return accountTrxnDetails;
    }

    public void addAccountTrxnDetail(CollectionSheetEntryInstallmentView accountTrxnDetail) {
        this.accountTrxnDetails.add(accountTrxnDetail);
    }

    public Short getAccountType() {
        return accountType;
    }

    public AccountTypes getType() {
        return AccountTypes.getAccountType(accountType);
    }

    public String getDepositAmountEntered() {
        return depositAmountEntered;
    }

    public void setDepositAmountEntered(String depositAmountEntered) {
        this.depositAmountEntered = depositAmountEntered;
    }

    public SavingsOfferingBO getSavingsOffering() {
        return savingsOffering;
    }

    public String getWithDrawalAmountEntered() {
        return withDrawalAmountEntered;
    }

    public void setWithDrawalAmountEntered(String withDrawalAmountEntered) {
        this.withDrawalAmountEntered = withDrawalAmountEntered;
    }

    public boolean isValidDepositAmountEntered() {
        return isValidDepositAmountEntered;
    }

    public void setValidDepositAmountEntered(boolean isValidDepositAmountEntered) {
        this.isValidDepositAmountEntered = isValidDepositAmountEntered;
    }

    public boolean isValidWithDrawalAmountEntered() {
        return isValidWithDrawalAmountEntered;
    }

    public void setValidWithDrawalAmountEntered(boolean isValidWithDrawalAmountEntered) {
        this.isValidWithDrawalAmountEntered = isValidWithDrawalAmountEntered;
    }

    public Double getTotalDepositDue() {
        Money totalDepositDue = new Money();
        if (accountTrxnDetails != null && accountTrxnDetails.size() > 0) {
            for (CollectionSheetEntryInstallmentView actionDates : accountTrxnDetails) {
                totalDepositDue = totalDepositDue.add(((CollectionSheetEntrySavingsInstallmentView) actionDates)
                        .getTotalDepositDue());
            }
        }
        return totalDepositDue.getAmountDoubleValue();
    }

}