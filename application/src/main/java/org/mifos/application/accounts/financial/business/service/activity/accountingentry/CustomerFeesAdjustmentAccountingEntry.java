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

package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;

public class CustomerFeesAdjustmentAccountingEntry extends BaseAccountingEntry {

    @Override
    protected void getSpecificAccountActionEntry() throws FinancialException {
        CustomerTrxnDetailEntity customertrxn = (CustomerTrxnDetailEntity) financialActivity.getAccountTrxn();
        Set<FeesTrxnDetailEntity> feesTrxn = customertrxn.getFeesTrxnDetails();
        Iterator<FeesTrxnDetailEntity> iterFees = feesTrxn.iterator();
        FinancialActionBO finActionFee = FinancialActionCache.getFinancialAction(FinancialActionConstants.FEEPOSTING);
        while (iterFees.hasNext()) {
            FeesTrxnDetailEntity feeTrxn = iterFees.next();
            addAccountEntryDetails(removeSign(feeTrxn.getFeeAmount()), finActionFee, feeTrxn.getAccountFees().getFees()
                    .getGlCode(), FinancialConstants.DEBIT);

            addAccountEntryDetails(removeSign(feeTrxn.getFeeAmount()), finActionFee, getGLcode(finActionFee
                    .getApplicableDebitCharts()), FinancialConstants.CREDIT);
        }
        // For Misc Fee
        FinancialActionBO finActionMiscFee = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.CUSTOMERACCOUNTMISCFEESPOSTING);
        addAccountEntryDetails(removeSign(customertrxn.getMiscFeeAmount()), finActionMiscFee,
                getGLcode(finActionMiscFee.getApplicableDebitCharts()), FinancialConstants.CREDIT);

        addAccountEntryDetails(removeSign(customertrxn.getMiscFeeAmount()), finActionMiscFee,
                getGLcode(finActionMiscFee.getApplicableCreditCharts()), FinancialConstants.DEBIT);

    }
}