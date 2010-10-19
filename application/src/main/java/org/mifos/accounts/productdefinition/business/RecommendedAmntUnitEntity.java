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

package org.mifos.accounts.productdefinition.business;

import java.util.Set;

import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;

public class RecommendedAmntUnitEntity extends MasterDataEntity {

    /** The composite primary key value */
    private Short id;

    private Short localeId;
    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    protected RecommendedAmntUnitEntity() {
        super();
    }

    public RecommendedAmntUnitEntity(RecommendedAmountUnit recommendedAmountUnit) {
        this.id = recommendedAmountUnit.getValue();
    }

    @Override
    public Short getId() {
        return id;
    }

    public Short getLocaleId() {
        return localeId;
    }

    @Override
    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    @Override
    public String getName() {
        return MessageLookup.getInstance().lookup(getLookUpValue());
    }

    @Override
    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    protected void setId(Short id) {
        this.id = id;
    }

    @Override
    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    protected void setName(String name) {
        MessageLookup.getInstance().updateLookupValue(getLookUpValue(), name);
    }

    public boolean isPerIndividual() {
        return RecommendedAmountUnit.PER_INDIVIDUAL.getValue().equals(this.id);
    }
}
