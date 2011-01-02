/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.loan.struts.action;

import junit.framework.Assert;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.loan.business.LoanSummaryEntity;
import org.mifos.accounts.loan.struts.actionforms.RepayLoanActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RepayLoanActionStrutsTest extends MifosMockStrutsTestCase {

    public RepayLoanActionStrutsTest() throws Exception {
        super();
    }

    protected AccountBO accountBO = null;

    private CustomerBO center = null;

    private CustomerBO group = null;
    private UserContext userContext;
    private String flowKey;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, RepayLoanAction.class);
        accountBO = getLoanAccount();
        StaticHibernateUtil.flushSession();
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
    }

    @Override
    protected void tearDown() throws Exception {
        accountBO = null;
        group = null;
        center = null;
        super.tearDown();
    }

    public void testLoadRepayment() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "loadRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.LOAD_SUCCESS);
        Money amount = (Money) SessionUtils.getAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, request);
       Assert.assertEquals(amount, ((LoanBO) accountBO).getEarlyRepayAmount());
    }

    public void testRepaymentPreview() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
    }

    public void testRepaymentPrevious() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "previous");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.PREVIOUS_SUCCESS);
    }

    public void testMakeRepaymentForCurrentDateSameAsInstallmentDate() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(false);
        setActionForm(repayLoanActionForm);
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(amount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }
    
    public void testMakeRepaymentForCurrentDateSameAsInstallmentDateWithInterestWaiver() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();
        Money waivedAmount = amount.subtract(((LoanBO) accountBO).waiverAmount());
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(true);
        setActionForm(repayLoanActionForm);
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(waivedAmount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }

    public void testMakeRepaymentForCurrentDateLiesBetweenInstallmentDates() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        changeFirstInstallmentDate(accountBO);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();

        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(false);
        setActionForm(repayLoanActionForm);
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(amount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }

    public void testMakeRepaymentForCurrentDateLiesBetweenInstallmentDatesWithInterestWaiver() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        changeFirstInstallmentDate(accountBO);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();
        Money waivedAmount = amount.subtract(((LoanBO) accountBO).waiverAmount());

        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(true);
        setActionForm(repayLoanActionForm);
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(waivedAmount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }

    private void changeFirstInstallmentDate(AccountBO accountBO) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH - 1);
        currentDateCalendar = new GregorianCalendar(year, month, day);
        for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountActionDateEntity, new java.sql.Date(currentDateCalendar
                    .getTimeInMillis()));
            break;
        }
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

}
