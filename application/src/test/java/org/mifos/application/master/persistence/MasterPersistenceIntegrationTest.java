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

package org.mifos.application.master.persistence;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.LocalizedTextLookup;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.activity.DynamicLookUpValueCreationTypes;

import java.util.List;

public class MasterPersistenceIntegrationTest extends MifosIntegrationTestCase {

    final private static short DEFAULT_LOCALE = (short) 1;

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testEntityMasterRetrieval() throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();
        CustomValueDto paymentTypes = masterPersistence.getCustomValueList(MasterConstants.ATTENDENCETYPES,
                "org.mifos.application.master.business.CustomerAttendanceType", "attendanceId");
        List<CustomValueListElementDto> paymentValues = paymentTypes.getCustomValueListElements();
       Assert.assertEquals(4, paymentValues.size());

    }

    @Test @org.junit.Ignore
    public void testEntityMasterRetrievalForInvalidConnection() throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();

        try {
            masterPersistence.getCustomValueList(MasterConstants.ATTENDENCETYPES,
                    "org.mifos.application.master.business.CustomerAttendanceType", "attendanceId");
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    @Test
    public void testGetLookUpEntity() throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();
        CustomValueDto gender = masterPersistence.getLookUpEntity(MasterConstants.GENDER, Short.valueOf("1"));
        List<CustomValueListElementDto> genderValues = gender.getCustomValueListElements();
       Assert.assertEquals(2, genderValues.size());

    }

    @Test
    public void testRetrieveMasterEntities() throws NumberFormatException, PersistenceException {
        MasterPersistence masterPersistence = new MasterPersistence();
        List<ValueListElement> masterEntity = masterPersistence.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES,
                Short.valueOf("1"));
        // 131 if includes the empty lookup_name for lookup id 259, 263
       Assert.assertEquals(131, masterEntity.size());
    }

    @Test @org.junit.Ignore
    public void testRetrieveMasterEntitiesForInvalidConnection() throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();

        try {
            masterPersistence.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, Short.valueOf("1"));
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }
 @org.junit.Ignore
    public void retrieveCustomFieldsDefinitionForInvalidConnection() throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();

        try {
            masterPersistence.retrieveCustomFieldsDefinition(EntityType.CLIENT);
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    @Test
    public void testGetMasterEntityName() throws NumberFormatException, PersistenceException {
        MasterPersistence masterPersistence = new MasterPersistence();
       Assert.assertEquals("Partial Application", masterPersistence.retrieveMasterEntities(1));
    }

    @Test
    public void testRetrieveMasterDataEntity() throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();
        List<MasterDataEntity> masterDataList = masterPersistence
                .retrieveMasterDataEntity("org.mifos.accounts.business.AccountStateEntity");
       Assert.assertEquals(18, masterDataList.size());
        for (MasterDataEntity masterDataEntity : masterDataList) {
            for (LookUpValueLocaleEntity lookUpValueLocaleEntity : masterDataEntity.getLookUpValue()
                    .getLookUpValueLocales()) {
               Assert.assertEquals(Short.valueOf("1"), lookUpValueLocaleEntity.getLocaleId());
            }
        }
    }

    @Test @Ignore("Convert to unit test")
    public void testRetrieveMasterDataEntityForInvalidConnection() throws Exception {
        MasterPersistence masterPersistence = new MasterPersistence();

        try {
            masterPersistence.retrieveMasterDataEntity("org.mifos.accounts.business.AccountStateEntity");
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    private boolean foundStringInCustomValueList(final MasterPersistence masterPersistence, final String CustomValueListName,
            final String searchString, final short localId) throws PersistenceException {
        List<ValueListElement> salutations = masterPersistence.retrieveMasterEntities(CustomValueListName, localId);
        boolean foundString = false;
        for (ValueListElement entity : salutations) {
            if (entity.getName().compareTo(searchString) == 0) {
                foundString = true;
            }
        }
        return foundString;
    }

    private Integer findValueListElementId(final MasterPersistence masterPersistence, final String CustomValueListName,
            final String searchString, final short localId) throws PersistenceException {
        List<ValueListElement> salutations = masterPersistence.retrieveMasterEntities(CustomValueListName, localId);
        Integer elementId = null;
        for (ValueListElement entity : salutations) {
            if (entity.getName().compareTo(searchString) == 0) {
                elementId = entity.getId();
            }
        }
        return elementId;
    }

    @Test
    public void testAddAndDeleteValueListElement() throws Exception {
        // get the CustomValueDto that we want to add to
        MasterPersistence masterPersistence = new MasterPersistence();
        CustomValueDto salutationValueList = masterPersistence.getLookUpEntity(MasterConstants.SALUTATION,
                DEFAULT_LOCALE);

        // add a CustomValueListElementDto to the list
        final String NEW_SALUTATION_STRING = "Sir";
        LocalizedTextLookup lookupValueEntity = masterPersistence.addValueListElementForLocale(
                DynamicLookUpValueCreationTypes.LookUpOption, salutationValueList.getEntityId(), NEW_SALUTATION_STRING);
        StaticHibernateUtil.flushSession();

        // verify that the new salutation was created
        Integer newSalutationId = findValueListElementId(masterPersistence, MasterConstants.SALUTATION,
                NEW_SALUTATION_STRING, DEFAULT_LOCALE);
       Assert.assertTrue(newSalutationId != null);

        StaticHibernateUtil.flushAndClearSession();
        // remove the new salutation
        masterPersistence.deleteValueListElement(newSalutationId);

        // verify that the new salutation was deleted
        Assert.assertFalse(foundStringInCustomValueList(masterPersistence, MasterConstants.SALUTATION, NEW_SALUTATION_STRING,
                DEFAULT_LOCALE));
    }

    @Test
    public void testUpdateValueListElement() throws Exception {
        // get a CustomValueListElementDto (as a BusinessActivityEntity)
        MasterPersistence masterPersistence = new MasterPersistence();
        List<ValueListElement> salutations = masterPersistence.retrieveMasterEntities(MasterConstants.SALUTATION,
                DEFAULT_LOCALE);
        ValueListElement first = salutations.get(0);
        Integer id = first.getId();
        String originalName = first.getName();

        // update it
        final String UPDATED_NAME = "Mister";
        first.setName(UPDATED_NAME);

        // save it
        masterPersistence.updateValueListElementForLocale(id, UPDATED_NAME);
        StaticHibernateUtil.flushSession();

        // get the element back
        // and verify that it has the new value
        salutations.clear();
        salutations = masterPersistence.retrieveMasterEntities(MasterConstants.SALUTATION, DEFAULT_LOCALE);
        for (ValueListElement entity : salutations) {
            if (entity.getId() == id) {
               Assert.assertEquals(entity.getName(), UPDATED_NAME);
            }
        }
        // restore it
        masterPersistence.updateValueListElementForLocale(id, originalName);
        StaticHibernateUtil.flushSession();

    }

}
