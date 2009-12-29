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

package org.mifos.application.servicefacade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;

public class CollectionSheetServiceImplIntegrationTest extends MifosIntegrationTestCase {

    public CollectionSheetServiceImplIntegrationTest() throws SystemException, ApplicationException {
        super();
        collectionSheetService = DependencyInjectedServiceLocator.locateCollectionSheetService();
    }

    private static CollectionSheetService collectionSheetService;
    private TestSaveCollectionSheetStructureValidatorUtils saveCollectionSheetUtils;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        saveCollectionSheetUtils = new TestSaveCollectionSheetStructureValidatorUtils();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            saveCollectionSheetUtils.clearObjects();
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }

        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    CenterBO center;

    BigDecimal injectedLoanPayment;
    BigDecimal injectedDisbursement;

    public void testLoanOverPaymentIsIdentified() throws Exception {

        SaveCollectionSheetDto saveCollectionSheet = saveCollectionSheetUtils.createSampleSaveCollectionSheet();

        // disburse loan
        CollectionSheetErrorsView errors = null;
        try {
            errors = collectionSheetService.saveCollectionSheet(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        // over-repay loan
        Date repaymentDate = incrementCurrentDate(14);
        saveCollectionSheetUtils.setOverpayLoan();
        saveCollectionSheet = saveCollectionSheetUtils
                .assembleSaveCollectionSheetFromCreatedCenterHierarchy(repaymentDate);

        try {
            errors = collectionSheetService.saveCollectionSheet(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        if (errors != null && errors.getLoanRepaymentAccountNumbers() != null) {
            assertThat("There should have been one loan account error", errors.getLoanRepaymentAccountNumbers().size(),
                    is(1));
        } else {
            assertTrue("There should have been one loan account repayment error", false);
        }

    }

    // public void testLoanInvalidDisbursementIsIdentified() throws Exception {
    //
    // }

    private Date incrementCurrentDate(final int noOfDays) {
        return new java.sql.Date(new DateTime().plusDays(noOfDays).toDate().getTime());
    }

}