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

package org.mifos.test.acceptance.framework.loanproduct;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class LoanProductDetailsPage  extends MifosPage {

        public LoanProductDetailsPage(Selenium selenium) {
            super(selenium);
        }

        public LoanProductDetailsPage verifyPage() {
            verifyPage("LoanProductDetails");
            return this;
        }

        public EditLoanProductPage editLoanProduct() {
            selenium.click("loanproductdetails.link.editLoanProduct");
            waitForPageToLoad();
            return new EditLoanProductPage(selenium);

        }

        public void verifyInterestAmount(String amount) {
            Assert.assertTrue(selenium.isTextPresent(amount));
        }

        public void verifyDefaultLoanAmount(String amount) {
            Assert.assertTrue(selenium.isTextPresent(amount));
        }

    public void verifyVariableInstalmentOption(String maxGap, String minGap, String minInstalmentAmount) {
        Assert.assertTrue(selenium.isTextPresent("Minimum gap between installments: " + minGap  + " days"));
        if ("".equals(maxGap)) {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: " + maxGap  + " days"));
        }
        if ("".equals(minInstalmentAmount)) {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: N/A")) ;
        } else {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: " + minInstalmentAmount)) ;
        }
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: Yes"));
    }

    public void verifyVariableInstalmentOptionUnChecked() {
        Assert.assertTrue(!selenium.isTextPresent("Minimum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Maximum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Minimum installment amount:")) ;
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: No"));
    }

    public LoanProductDetailsPage verifyCashFlowOfEditedLoan(String warningThreshold, String indebetedValue, String repaymentCapacityValue) {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: Yes"));
        if ("".equals(warningThreshold)) {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: " + warningThreshold + " %"));
        }
        if ("".equals(indebetedValue)) {
            Assert.assertTrue(selenium.isTextPresent("Indebtedness Rate: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Indebtedness Rate: " + indebetedValue + " %"));
        }
        if ("".equals(repaymentCapacityValue)) {
            Assert.assertTrue(selenium.isTextPresent("Repayment Capacity: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Repayment Capacity: " + repaymentCapacityValue + " %"));
        }
        return this;
    }

    public LoanProductDetailsPage verifyCashFlowUncheckedInEditedProduct() {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: No"));
        Assert.assertTrue(!selenium.isTextPresent("Warning Threshold:"));
        return this;
    }
}
