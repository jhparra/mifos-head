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

package org.mifos.dto.domain;

import java.util.List;
import java.math.BigDecimal;


public class ParseResultDto {
    private final List<String> parseErrors;
    private final List<AccountPaymentParametersDto> successfullyParsedPayments;

	private Integer numberOfReadRows;
	private Integer numberOfIgnoredRows;
	private Integer numberOfErrorRows;
	private BigDecimal totalAmountOfTransactionsImported;
	private BigDecimal totalAmountOfTransactionsWithError;

    public ParseResultDto(final List<String> parseErrors, final List<AccountPaymentParametersDto> successfullyParsedRows) {
        this.parseErrors = parseErrors;
        this.successfullyParsedPayments = successfullyParsedRows;
    }

    public List<String> getParseErrors() {
        return this.parseErrors;
    }

    public List<AccountPaymentParametersDto> getSuccessfullyParsedPayments() {
        return this.successfullyParsedPayments;
    }

	public Integer getNumberOfErrorRows() {
		return numberOfErrorRows;
	}

	public void setNumberOfErrorRows(Integer numberOfErrorRows) {
		this.numberOfErrorRows = numberOfErrorRows;
	}

	public Integer getNumberOfIgnoredRows() {
		return numberOfIgnoredRows;
	}

	public void setNumberOfIgnoredRows(Integer numberOfIgnoredRows) {
		this.numberOfIgnoredRows = numberOfIgnoredRows;
	}

	public Integer getNumberOfReadRows() {
		return numberOfReadRows;
	}

	public void setNumberOfReadRows(Integer numberOfReadRows) {
		this.numberOfReadRows = numberOfReadRows;
	}

	public BigDecimal getTotalAmountOfTransactionsImported() {
		return totalAmountOfTransactionsImported;
	}

	public void setTotalAmountOfTransactionsImported(BigDecimal totalAmountOfTransactionsImported) {
		this.totalAmountOfTransactionsImported = totalAmountOfTransactionsImported;
	}

	public BigDecimal getTotalAmountOfTransactionsWithError() {
		return totalAmountOfTransactionsWithError;
	}

	public void setTotalAmountOfTransactionsWithError(BigDecimal totalAmountOfTransactionsWithError) {
		this.totalAmountOfTransactionsWithError = totalAmountOfTransactionsWithError;
	}

	public boolean isAmountInformationFilled() {
		return totalAmountOfTransactionsImported != null && totalAmountOfTransactionsWithError != null;
	}

	public boolean isExtraRowInformationFilled() {
		return numberOfErrorRows != null && numberOfIgnoredRows != null && numberOfReadRows != null;
	}

}