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

package org.mifos.test.acceptance.framework.testhelpers;

import com.thoughtworks.selenium.Selenium;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loan.ApplyChargePage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentConfirmationPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanConfirmationPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditAccountStatusConfirmationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.EditPreviewLoanAccountPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalChooseLoanInstancePage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalPreviewPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSchedulePreviewPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchResultsPage;
import org.mifos.test.acceptance.framework.loan.ViewInstallmentDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Holds methods common to most loan tests.
 *
 */
public class LoanTestHelper {

    private final Selenium selenium;
    private final NavigationHelper navigationHelper;

    public LoanTestHelper(Selenium selenium) {
        this.selenium = selenium;
        this.navigationHelper = new NavigationHelper(selenium);
    }

    public LoanTestHelper(Selenium selenium, NavigationHelper helper) {
        this.selenium = selenium;
        this.navigationHelper = helper;
    }

    /**
     * Creates a loan account.
     * @param searchParameters Parameters to find the client/group that will be the owner of the account.
     * @param submitAccountParameters The parameters for the loan account.
     */
    public LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
            CreateLoanAccountSubmitParameters submitAccountParameters) {
        CreateLoanAccountEntryPage createLoanAccountEntryPage = navigateToLoanAccountEntryPage(searchParameters);
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountEntryPage
        .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters);
        createLoanAccountConfirmationPage.verifyPage();
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        loanAccountPage.verifyPage();
        return loanAccountPage;
    }

    /**
     * Creates a loan account.
     * @param searchParameters Parameters to find the client/group that will be the owner of the account.
     * @param submitAccountParameters The parameters for the loan account.
     * @param questionResponseParameters The parameters for the create loan question responses.
     */
    public LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
            CreateLoanAccountSubmitParameters submitAccountParameters, QuestionResponseParameters questionResponseParameters) {
        CreateLoanAccountEntryPage createLoanAccountEntryPage = navigateToLoanAccountEntryPage(searchParameters);
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountEntryPage
        .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters, questionResponseParameters);
        createLoanAccountConfirmationPage.verifyPage();
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        loanAccountPage.verifyPage();
        return loanAccountPage;
    }

    private CreateLoanAccountEntryPage navigateToLoanAccountEntryPage(CreateLoanAccountSearchParameters searchParameters) {
        CreateLoanAccountSearchPage createLoanAccountSearchPage = navigateToCreateLoanAccountSearchPage();
        createLoanAccountSearchPage.verifyPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
        .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        return createLoanAccountEntryPage;
    }

    /**
     * TODO: Create a helper for creating a GLIM loan account.
     */


    /**
     * Edits the loan account with id loanId and updates its settings with the ones in params.
     * @param loanId The account id.
     * @param accountSubmitParameters The create loan parameters.
     * @param editAccountParameters The edit loan parameters.
     * @return edit preview loan account page
     */
    public EditPreviewLoanAccountPage changeLoanAccountInformation(String loanId, CreateLoanAccountSubmitParameters accountSubmitParameters, EditLoanAccountInformationParameters editAccountParameters) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();

        EditLoanAccountInformationPage editAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
        editAccountInformationPage.verifyPage();

        editAccountInformationPage.editAccountParams(accountSubmitParameters, editAccountParameters);
        return editAccountInformationPage.submitAndNavigateToAccountInformationPreviewPage();
    }

    /**
     * Edits the loan account with id loanId and updates its settings with the ones in params.
     * @param loanId The account id.
     * @param params The status parameters.
     */
    public void changeLoanAccountStatus(String loanId, EditLoanAccountStatusParameters params) {
        changeLoanAccountStatusProvidingQuestionGroupResponses(loanId, params, null);
    }

    public void changeLoanAccountStatusProvidingQuestionGroupResponses(String loanId, EditLoanAccountStatusParameters params, QuestionResponseParameters responseParameters) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();

        EditLoanAccountStatusPage editAccountStatusPage = loanAccountPage.navigateToEditAccountStatus();
        editAccountStatusPage.verifyPage();

        editAccountStatusPage.submitAndNavigateToNextPage(params);

        if (responseParameters != null) {
            populateQuestionGroupResponses(responseParameters);
        }

        loanAccountPage = new EditAccountStatusConfirmationPage(selenium).submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyPage();
    }

    private void populateQuestionGroupResponses(QuestionResponseParameters responseParameters) {
        QuestionResponsePage responsePage = new QuestionResponsePage(selenium);
        responsePage.populateAnswers(responseParameters);
        responsePage.navigateToNextPage();
    }


    /**
     * Disburses the loan with id <tt>loanId</tt>.
     * @param loanId The system/global id of the loan that'll be disbursed.
     * @param disburseParameters The disbursal parameters.
     * @return The loan account page of the loan account with id loanId.
     */
    public LoanAccountPage disburseLoan(String loanId, DisburseLoanParameters disburseParameters) {
        DisburseLoanPage disburseLoanPage = prepareToDisburseLoan(loanId);

        DisburseLoanConfirmationPage disburseLoanConfirmationPage = disburseLoanPage.submitAndNavigateToDisburseLoanConfirmationPage(disburseParameters);
        disburseLoanConfirmationPage.verifyPage();

        LoanAccountPage loanAccountPage = disburseLoanConfirmationPage.submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyPage();

        return loanAccountPage;
    }

    public void editLoanProduct(String loanProduct, boolean interestWaiver) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setInterestWaiver(interestWaiver);
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitInterestWaiverChanges(formParameters);
        loanProductDetailsPage = editLoanProductPreviewPage.submit();
    }

    public void editLoanProduct(String loanProduct, String... questionGroup) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setQuestionGroups(Arrays.asList(questionGroup));
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitQuestionGroupChanges(formParameters);
        loanProductDetailsPage = editLoanProductPreviewPage.submit();
    }

    public DisburseLoanPage prepareToDisburseLoan(String loanId) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();

        DisburseLoanPage disburseLoanPage = loanAccountPage.navigateToDisburseLoan();
        disburseLoanPage.verifyPage();
        return disburseLoanPage;
    }

    /**
     * Applies a charge to the loan account with id <tt>loanId</tt>.
     * @param loanId The account id.
     * @param params The charge parameters (amount and type).
     * @return The loan account page for the loan account.
     */
    public LoanAccountPage applyCharge(String loanId, ChargeParameters params) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();

        ApplyChargePage applyChargePage = loanAccountPage.navigateToApplyCharge();
        applyChargePage.verifyPage();

        loanAccountPage = applyChargePage.submitAndNavigateToApplyChargeConfirmationPage(params);
        loanAccountPage.verifyPage();

        return loanAccountPage;
    }

    /**
     * Applies a payment to the loan account with id <tt>loanId</tt>.
     * @param loanId The account id.
     * @param paymentParams The payment parameters.
     * @return The loan account page for the loan account.
     */
    public LoanAccountPage applyPayment(String loanId, PaymentParameters paymentParams) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();

        ApplyPaymentPage applyPaymentPage = loanAccountPage.navigateToApplyPayment();
        applyPaymentPage.verifyPage();

        ApplyPaymentConfirmationPage applyPaymentConfirmationPage = applyPaymentPage.submitAndNavigateToApplyPaymentConfirmationPage(paymentParams);
        applyPaymentConfirmationPage.verifyPage();

        loanAccountPage = applyPaymentConfirmationPage.submitAndNavigateToLoanAccountDetailsPage();
        loanAccountPage.verifyPage();

        return loanAccountPage;
    }

    /**
     * Waive the fee associated with the loan account with id <tt>loanId</tt>.
     * @param loanId The loan account id.
     */
    public void waiveFee(String loanId) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();

        ViewInstallmentDetailsPage installmentDetailsPage = loanAccountPage.navigateToViewInstallmentDetails();
        installmentDetailsPage.verifyPage();

        installmentDetailsPage.waiveFee();
    }

    /**
     * Waive the penalty associated with the loan account with id <tt>loanId</tt>.
     * @param loanId The loan account id.
     */
    public void waivePenalty(String loanId) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();

        ViewInstallmentDetailsPage installmentDetailsPage = loanAccountPage.navigateToViewInstallmentDetails();
        installmentDetailsPage.verifyPage();

        installmentDetailsPage.waivePenalty();
    }

    /**
     * Redoes the loan disbursal.
     * @param clientName The name of the client.
     * @param loanProduct The name of the loan product.
     * @param params The parameters for the loan disbursal.
     */
    public void redoLoanDisbursal(String clientName, String loanProduct, RedoLoanDisbursalParameters params) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.verifyPage();

        RedoLoanDisbursalSearchPage searchPage = adminPage.navigateToRedoLoanDisbursal();
        searchPage.verifyPage();

        RedoLoanDisbursalSearchResultsPage resultsPage = searchPage.searchAndNavigateToRedoLoanDisbursalPage(clientName);
        resultsPage.verifyPage();

        RedoLoanDisbursalChooseLoanInstancePage chooseLoanPage = resultsPage.navigateToRedoLoanDisbursalChooseLoanProductPage(clientName);
        chooseLoanPage.verifyPage();

        RedoLoanDisbursalEntryPage dataEntryPage = chooseLoanPage.submitAndNavigateToRedoLoanDisbursalEntryPage(loanProduct);
        dataEntryPage.verifyPage();

        RedoLoanDisbursalSchedulePreviewPage schedulePreviewPage = dataEntryPage.submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(params);
        schedulePreviewPage.verifyPage();

        RedoLoanDisbursalPreviewPage previewPage = schedulePreviewPage.submitAndNavigateToRedoLoanDisbursalPreviewPage();
        previewPage.verifyPage();

        CreateLoanAccountConfirmationPage confirmationPage = previewPage.submitAndNavigateToLoanAccountConfirmationPage();
        confirmationPage.verifyPage();
    }


    public LoanAccountPage navigateToLoanAccountPage(CreateLoanAccountSearchParameters searchParams) {
        String searchString = searchParams.getSearchString();

        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        SearchResultsPage searchResultsPage = homePage.search(searchString);

        LoanAccountPage loanAccountPage = searchResultsPage.navigateToLoanAccountDetailPage(searchString);
        loanAccountPage.verifyPage();
        return loanAccountPage;
    }

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        clientsAndAccountsPage.verifyPage();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPage(CreateLoanAccountSearchParameters searchParameters) {
        return navigateToLoanAccountEntryPage(searchParameters);
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPageWithoutLogout(CreateLoanAccountSearchParameters searchParameters) {
        ClientsAndAccountsHomepage clientsAndAccountsPage = new HomePage(selenium).navigateToClientsAndAccountsUsingHeaderTab();
        clientsAndAccountsPage.verifyPage();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
        createLoanAccountSearchPage.verifyPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
        .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        return createLoanAccountEntryPage;
    }

    public DisburseLoanPage prepareToDisburseLoanWithoutLogout(HomePage homePage, String loanId) {
        SearchResultsPage searchResultsPage = homePage.search(loanId);
        searchResultsPage.verifyPage();
        LoanAccountPage loanAccountPage = searchResultsPage.navigateToLoanAccountDetailPage(loanId);
        loanAccountPage.verifyPage();
        loanAccountPage.verifyPage();

        DisburseLoanPage disburseLoanPage = loanAccountPage.navigateToDisburseLoan();
        disburseLoanPage.verifyPage();
        return disburseLoanPage;
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPageWithoutLogout(String clientName, String loanProductName) {
        return navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters(clientName,loanProductName));
    }
    
    public CreateLoanAccountSearchParameters setLoanSearchParameters(String clientName, String loanProductName) {
        CreateLoanAccountSearchParameters accountSearchParameters = new CreateLoanAccountSearchParameters();
        accountSearchParameters.setSearchString(clientName);
        accountSearchParameters.setLoanProduct(loanProductName);
        return accountSearchParameters;
    }


    public AbstractPage setApplicationTime(DateTime systemDateTime) throws UnsupportedEncodingException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemDateTime);
        return new AbstractPage(selenium);
    }
}
