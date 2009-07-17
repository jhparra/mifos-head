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

package org.mifos.application.productdefinition.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.framework.exceptions.PersistenceException;

public class GracePeriodTypeEntity extends MasterDataEntity {

    public GracePeriodTypeEntity(GraceType graceType) {
        super(graceType.getValue());
    }

    protected GracePeriodTypeEntity() {
        super();
    }

    public void update(String name) throws ProductDefinitionException {
        setName(name);
    }

    public GraceType asEnum() {
        return GraceType.fromInt(getId());
    }

}