/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.application.questionnaire.migration;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.customers.surveys.business.SurveyUtils.getSurvey;
import static org.mifos.customers.surveys.business.SurveyUtils.getSurveyInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeCustomFieldEntity;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.builders.QuestionDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupInstanceDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupResponseDtoBuilder;
import org.mifos.platform.questionnaire.builders.SectionDtoBuilder;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupResponseDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireMigrationTest {

    @Mock
    private QuestionnaireMigrationMapper questionnaireMigrationMapper;

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private SurveysPersistence surveysPersistence;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private CustomerBO savingsAccountCustomer;

    @Mock
    private LoanDao loanDao;

    @Mock
    private SavingsDao savingsDao;

    @Mock
    private OfficeDao officeDao;

    @Mock
    private PersonnelDao personnelDao;

    private QuestionnaireMigration questionnaireMigration;

    private static final int QUESTION_GROUP_ID = 123;

    private Calendar calendar;

    @Before
    public void setUp() {
        questionnaireMigration = new QuestionnaireMigration(questionnaireMigrationMapper, questionnaireServiceFacade, surveysPersistence, customerDao, loanDao, savingsDao, officeDao, personnelDao);
        calendar = Calendar.getInstance();
    }

    @Test
    public void shouldMigrateSurveys() throws ApplicationException {

        Survey survey1 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.CLIENT);
        Survey survey2 = getSurvey("Sur2", "Ques2", calendar.getTime(), SurveyType.CLIENT);
        Survey surveyCenter1 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.CENTER);
        Survey surveyCenter2 = getSurvey("Sur2", "Ques2", calendar.getTime(), SurveyType.CENTER);
        Survey surveyGroup1 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.GROUP);
        Survey surveyGroup2 = getSurvey("Sur2", "Ques2", calendar.getTime(), SurveyType.GROUP);
        Survey surveyLoan1 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.LOAN);
        Survey surveyLoan2 = getSurvey("Sur2", "Ques2", calendar.getTime(), SurveyType.LOAN);
        Survey surveySavings1 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.SAVINGS);
        Survey surveySavings2 = getSurvey("Sur2", "Ques2", calendar.getTime(), SurveyType.SAVINGS);
        Survey surveyAll1 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.ALL);
        Survey surveyAll2 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.ALL);
        Survey surveyAll3 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.ALL);
        Survey surveyAll4 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.ALL);
        Survey surveyAll5 = getSurvey("Sur1", "Ques1", calendar.getTime(), SurveyType.ALL);


        List<Survey> surveys = asList(survey1, survey2, surveyAll1);
        List<Survey> surveysCenter = asList(surveyCenter1, surveyCenter2, surveyAll2);
        List<Survey> surveysGroup = asList(surveyGroup1, surveyGroup2, surveyAll3);
        List<Survey> surveysLoan = asList(surveyLoan1, surveyLoan2, surveyAll4);
        List<Survey> surveysSavings = asList(surveySavings1, surveySavings2, surveyAll5);


        when(surveysPersistence.retrieveNonPPISurveysByTypeIterator(SurveyType.CLIENT)).thenReturn(surveys.iterator());
        when(surveysPersistence.retrieveNonPPISurveysByTypeIterator(SurveyType.CENTER)).thenReturn(surveysCenter.iterator());
        when(surveysPersistence.retrieveNonPPISurveysByTypeIterator(SurveyType.GROUP)).thenReturn(surveysGroup.iterator());
        when(surveysPersistence.retrieveNonPPISurveysByTypeIterator(SurveyType.LOAN)).thenReturn(surveysLoan.iterator());
        when(surveysPersistence.retrieveNonPPISurveysByTypeIterator(SurveyType.SAVINGS)).thenReturn(surveysSavings.iterator());


        QuestionGroupDto questionGroupDto1 = getQuestionGroupDto("Sur1", "Ques1", "View", "Client");
        QuestionGroupDto questionGroupDto2 = getQuestionGroupDto("Sur2", "Ques2", "View", "Client");
        QuestionGroupDto questionGroupDtoCenter1 = getQuestionGroupDto("Sur1", "Ques1", "View", "Center");
        QuestionGroupDto questionGroupDtoCenter2 = getQuestionGroupDto("Sur2", "Ques2", "View", "Center");
        QuestionGroupDto questionGroupDtoGroup1 = getQuestionGroupDto("Sur1", "Ques1", "View", "Group");
        QuestionGroupDto questionGroupDtoGroup2 = getQuestionGroupDto("Sur2", "Ques2", "View", "Group");
        QuestionGroupDto questionGroupDtoLoan1 = getQuestionGroupDto("Sur1", "Ques1", "View", "Loan");
        QuestionGroupDto questionGroupDtoLoan2 = getQuestionGroupDto("Sur2", "Ques2", "View", "Loan");
        QuestionGroupDto questionGroupDtoSavings1 = getQuestionGroupDto("Sur1", "Ques1", "View", "Savings");
        QuestionGroupDto questionGroupDtoSavings2 = getQuestionGroupDto("Sur2", "Ques2", "View", "Savings");
        QuestionGroupDto questionGroupDtoAll1 = getQuestionGroupDto("Sur1", "Ques1", "View", "Client");
        QuestionGroupDto questionGroupDtoAll2 = getQuestionGroupDto("Sur1", "Ques1", "View", "Center");
        QuestionGroupDto questionGroupDtoAll3 = getQuestionGroupDto("Sur1", "Ques1", "View", "Group");
        QuestionGroupDto questionGroupDtoAll4 = getQuestionGroupDto("Sur1", "Ques1", "View", "Loan");
        QuestionGroupDto questionGroupDtoAll5 = getQuestionGroupDto("Sur1", "Ques1", "View", "Savings");


        SurveyInstance surveyInstance1 = getSurveyInstance(survey1, 12, 101, "Answer1");
        QuestionGroupInstanceDto questionGroupInstanceDto1 = getQuestionGroupInstanceDto("Answer1", 12, 101);
        SurveyInstance surveyInstance2 = getSurveyInstance(survey1, 13, 102, "Answer2");
        QuestionGroupInstanceDto questionGroupInstanceDto2 = getQuestionGroupInstanceDto("Answer2", 13, 102);
        SurveyInstance surveyInstance3 = getSurveyInstance(survey2, 12, 101, "Answer3");
        QuestionGroupInstanceDto questionGroupInstanceDto3 = getQuestionGroupInstanceDto("Answer3", 12, 101);
        SurveyInstance surveyInstance4 = getSurveyInstance(survey2, 13, 102, "Answer4");
        QuestionGroupInstanceDto questionGroupInstanceDto4 = getQuestionGroupInstanceDto("Answer4", 13, 102);
        SurveyInstance surveyInstanceCenter = getSurveyInstance(surveyCenter1, 12, 101, "CenterAnswer");
        QuestionGroupInstanceDto questionGroupInstanceDtoCenter = getQuestionGroupInstanceDto("CenterAnswer", 12, 101);
        SurveyInstance surveyInstanceGroup = getSurveyInstance(surveyGroup1, 13, 102, "GroupAnswer");
        QuestionGroupInstanceDto questionGroupInstanceDtoGroup = getQuestionGroupInstanceDto("GroupAnswer", 13, 102);
        SurveyInstance surveyInstanceLoan = getSurveyInstance(surveyLoan1, 12, 101, "LoanAnswer");
        QuestionGroupInstanceDto questionGroupInstanceDtoLoan = getQuestionGroupInstanceDto("LoanAnswer", 12, 101);
        SurveyInstance surveyInstanceSavings = getSurveyInstance(surveySavings1, 13, 102, "SavingsAnswer");
        QuestionGroupInstanceDto questionGroupInstanceDtoSavings = getQuestionGroupInstanceDto("SavingsAnswer", 13, 102);
        SurveyInstance surveyInstanceAll1 = getSurveyInstance(surveyAll1, 13, 102, "AnswerAll1");
        QuestionGroupInstanceDto questionGroupInstanceDtoAll1 = getQuestionGroupInstanceDto("AnswerAll1", 13, 102);
        SurveyInstance surveyInstanceAll2 = getSurveyInstance(surveyAll1, 13, 102, "AnswerAll2");
        QuestionGroupInstanceDto questionGroupInstanceDtoAll2 = getQuestionGroupInstanceDto("AnswerAll2", 13, 102);
        SurveyInstance surveyInstanceAll3 = getSurveyInstance(surveyAll1, 13, 102, "AnswerAll3");
        QuestionGroupInstanceDto questionGroupInstanceDtoAll3 = getQuestionGroupInstanceDto("AnswerAll3", 13, 102);
        SurveyInstance surveyInstanceAll4 = getSurveyInstance(surveyAll1, 13, 102, "AnswerAll4");
        QuestionGroupInstanceDto questionGroupInstanceDtoAll4 = getQuestionGroupInstanceDto("AnswerAll4", 13, 102);
        SurveyInstance surveyInstanceAll5 = getSurveyInstance(surveyAll1, 13, 102, "AnswerAll5");
        QuestionGroupInstanceDto questionGroupInstanceDtoAll5 = getQuestionGroupInstanceDto("AnswerAll5", 13, 102);


        when(questionnaireMigrationMapper.map(survey1)).thenReturn(questionGroupDto1);
        when(questionnaireMigrationMapper.map(survey2)).thenReturn(questionGroupDto2);
        when(questionnaireMigrationMapper.map(surveyCenter1)).thenReturn(questionGroupDtoCenter1);
        when(questionnaireMigrationMapper.map(surveyCenter2)).thenReturn(questionGroupDtoCenter2);
        when(questionnaireMigrationMapper.map(surveyGroup1)).thenReturn(questionGroupDtoGroup1);
        when(questionnaireMigrationMapper.map(surveyGroup2)).thenReturn(questionGroupDtoGroup2);
        when(questionnaireMigrationMapper.map(surveyLoan1)).thenReturn(questionGroupDtoLoan1);
        when(questionnaireMigrationMapper.map(surveyLoan2)).thenReturn(questionGroupDtoLoan2);
        when(questionnaireMigrationMapper.map(surveySavings1)).thenReturn(questionGroupDtoSavings1);
        when(questionnaireMigrationMapper.map(surveySavings2)).thenReturn(questionGroupDtoSavings2);
        when(questionnaireMigrationMapper.map(surveyAll1)).thenReturn(questionGroupDtoAll1);
        when(questionnaireMigrationMapper.map(surveyAll2)).thenReturn(questionGroupDtoAll2);
        when(questionnaireMigrationMapper.map(surveyAll3)).thenReturn(questionGroupDtoAll3);
        when(questionnaireMigrationMapper.map(surveyAll4)).thenReturn(questionGroupDtoAll4);
        when(questionnaireMigrationMapper.map(surveyAll5)).thenReturn(questionGroupDtoAll5);


        when(questionnaireMigrationMapper.map(eq(surveyInstance1), anyInt(), anyInt())).thenReturn(questionGroupInstanceDto1);
        when(questionnaireMigrationMapper.map(eq(surveyInstance2), anyInt(), anyInt())).thenReturn(questionGroupInstanceDto2);
        when(questionnaireMigrationMapper.map(eq(surveyInstance3), anyInt(), anyInt())).thenReturn(questionGroupInstanceDto3);
        when(questionnaireMigrationMapper.map(eq(surveyInstance4), anyInt(), anyInt())).thenReturn(questionGroupInstanceDto4);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceCenter), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoCenter);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceGroup), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoGroup);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceLoan), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoLoan);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceSavings), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoSavings);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceAll1), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoAll1);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceAll2), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoAll2);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceAll3), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoAll3);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceAll4), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoAll4);
        when(questionnaireMigrationMapper.map(eq(surveyInstanceAll5), anyInt(), anyInt())).thenReturn(questionGroupInstanceDtoAll5);


        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto1)).thenReturn(121);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto2)).thenReturn(122);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoCenter1)).thenReturn(201);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoCenter2)).thenReturn(202);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoGroup1)).thenReturn(301);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoGroup2)).thenReturn(302);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoLoan1)).thenReturn(401);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoLoan2)).thenReturn(402);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoSavings1)).thenReturn(501);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDtoSavings2)).thenReturn(502);

        when(questionnaireServiceFacade.getEventSourceId("View", "Client")).thenReturn(3);
        when(questionnaireServiceFacade.getEventSourceId("View", "Loan")).thenReturn(7);
        when(questionnaireServiceFacade.getEventSourceId("View", "Group")).thenReturn(8);
        when(questionnaireServiceFacade.getEventSourceId("View", "Center")).thenReturn(10);
        when(questionnaireServiceFacade.getEventSourceId("View", "Savings")).thenReturn(13);

        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(1111);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(2222);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto3)).thenReturn(3333);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto4)).thenReturn(4444);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoCenter)).thenReturn(5555);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoGroup)).thenReturn(6666);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoLoan)).thenReturn(7777);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoSavings)).thenReturn(8888);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoAll1)).thenReturn(9991);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoAll2)).thenReturn(9992);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoAll3)).thenReturn(9993);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoAll4)).thenReturn(9994);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDtoAll5)).thenReturn(9995);


        List<SurveyInstance> surveyInstances1 = asList(surveyInstance1, surveyInstance2);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(survey1)).thenReturn(surveyInstances1.iterator());
        List<SurveyInstance> surveyInstances2 = asList(surveyInstance3, surveyInstance4);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(survey2)).thenReturn(surveyInstances2.iterator());
        List<SurveyInstance> surveyInstancesCenter = asList(surveyInstanceCenter);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyCenter1)).thenReturn(surveyInstancesCenter.iterator());
        List<SurveyInstance> surveyInstancesGroup = asList(surveyInstanceGroup);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyGroup1)).thenReturn(surveyInstancesGroup.iterator());
        List<SurveyInstance> surveyInstancesLoan = asList(surveyInstanceLoan);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyLoan1)).thenReturn(surveyInstancesLoan.iterator());
        List<SurveyInstance> surveyInstancesSavings = asList(surveyInstanceSavings);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveySavings1)).thenReturn(surveyInstancesSavings.iterator());
        List<SurveyInstance> surveyInstancesAll1 = asList(surveyInstanceAll1);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyAll1)).thenReturn(surveyInstancesAll1.iterator());
        List<SurveyInstance> surveyInstancesAll2 = asList(surveyInstanceAll2);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyAll2)).thenReturn(surveyInstancesAll2.iterator());
        List<SurveyInstance> surveyInstancesAll3 = asList(surveyInstanceAll3);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyAll3)).thenReturn(surveyInstancesAll3.iterator());
        List<SurveyInstance> surveyInstancesAll4 = asList(surveyInstanceAll4);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyAll4)).thenReturn(surveyInstancesAll4.iterator());
        List<SurveyInstance> surveyInstancesAll5 = asList(surveyInstanceAll5);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(surveyAll5)).thenReturn(surveyInstancesAll5.iterator());


        List<Integer> questionGroupIds = questionnaireMigration.migrateSurveys();
        assertThat(questionGroupIds, is(notNullValue()));
        assertThat(questionGroupIds.size(), is(15));
        assertThat(questionGroupIds.get(0), is(121));
        assertThat(questionGroupIds.get(1), is(122));
        verify(questionnaireMigrationMapper, times(15)).map(any(Survey.class));
        verify(questionnaireMigrationMapper, times(13)).map(any(SurveyInstance.class), anyInt(), anyInt());
        verify(questionnaireServiceFacade, times(15)).createQuestionGroup(any(QuestionGroupDto.class));
        verify(questionnaireServiceFacade, times(13)).saveQuestionGroupInstance(any(QuestionGroupInstanceDto.class));
        verify(surveysPersistence, times(1)).retrieveNonPPISurveysByTypeIterator(SurveyType.CLIENT);
        verify(surveysPersistence, times(15)).retrieveInstancesBySurveyIterator(any(Survey.class));
    }

    /* there is a problem with mocking StaticHibernateUtil
    @Test
    public void shouldMigrateAdditionalFieldsForClient() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CLIENT, CustomFieldType.ALPHA_NUMERIC, EntityType.CLIENT);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CLIENT, CustomFieldType.DATE, EntityType.CLIENT);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(customerDao.retrieveCustomFieldEntitiesForClientIterator()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.CLIENT)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        when(questionnaireServiceFacade.getEventSourceId("Create", "Client")).thenReturn(1);
        ClientBO clientBO1 = SurveyUtils.getClientBO(11);
        CustomerCustomFieldEntity customField1 = CustomFieldUtils.getCustomerCustomField(1, "Ans1", clientBO1);
        CustomerCustomFieldEntity customField2 = CustomFieldUtils.getCustomerCustomField(1, "Ans2", clientBO1);
        CustomerCustomFieldEntity customFieldsaveQuestionGroupInstanc3 = CustomFieldUtils.getCustomerCustomField(1, "Ans3", clientBO1);
        List<CustomFieldForMigrationDto> customerResponses1 = asList(customerFieldEntityToMigrationDto(customField1), customerFieldEntityToMigrationDto(customField2), customerFieldEntityToMigrationDto(customField3));
        when(customerDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(customerResponses1);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 1, customerResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        ClientBO clientBO2 = SurveyUtils.getClientBO(22);
        CustomerCustomFieldEntity customField4 = CustomFieldUtils.getCustomerCustomField(2, "Ans11", clientBO2);
        CustomerCustomFieldEntity customField5 = CustomFieldUtils.getCustomerCustomField(2, "Ans22", clientBO2);
        CustomerCustomFieldEntity customField6 = CustomFieldUtils.getCustomerCustomField(2, "Ans33", clientBO2);
        List<CustomFieldForMigrationDto> customerResponses2 = asList(customerFieldEntityToMigrationDto(customField4), customerFieldEntityToMigrationDto(customField5), customerFieldEntityToMigrationDto(customField6));
        when(customerDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 1, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForClient();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.CLIENT);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(customerDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(customerDao, times(3)).retrieveCustomFieldEntitiesForClientIterator();
        verify(questionnaireMigrationMapper, times(2)).map(eq(QUESTION_GROUP_ID), eq(1), Matchers.<List<CustomFieldForMigrationDto>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }
    */

    private CustomFieldForMigrationDto customerFieldEntityToMigrationDto(CustomerCustomFieldEntity customFieldEntity) {
        CustomerBO customer = customFieldEntity.getCustomer();
        return new CustomFieldForMigrationDto(new Object[] {-1, customFieldEntity.getFieldId(), customFieldEntity.getFieldValue(),
                customer.getCreatedDate(), customer.getUpdatedDate(), customer.getCreatedBy(), customer.getUpdatedBy(), customer.getCustomerId()});
    }

    private CustomFieldForMigrationDto accountFieldEntityToMigrationDto(AccountCustomFieldEntity customFieldEntity) {
        AccountBO account = customFieldEntity.getAccount();
        return new CustomFieldForMigrationDto(new Object[] {-1, customFieldEntity.getFieldId(), customFieldEntity.getFieldValue(),
                account.getCreatedDate(), account.getUpdatedDate(), account.getCreatedBy(), account.getUpdatedBy(), account.getAccountId()});
    }

    private CustomFieldForMigrationDto officeFieldEntityToMigrationDto(OfficeCustomFieldEntity customFieldEntity) {
        OfficeBO office = customFieldEntity.getOffice();
        return new CustomFieldForMigrationDto(new Object[] {-1, customFieldEntity.getFieldId(), customFieldEntity.getFieldValue(),
                office.getCreatedDate(), office.getUpdatedDate(), office.getCreatedBy(), office.getUpdatedBy(), office.getOfficeId()});
    }

    private CustomFieldForMigrationDto personnelFieldEntityToMigrationDto(PersonnelCustomFieldEntity customFieldEntity) {
        PersonnelBO personnel = customFieldEntity.getPersonnel();
        return new CustomFieldForMigrationDto(new Object[] {-1, customFieldEntity.getFieldId(), customFieldEntity.getFieldValue(),
                personnel.getCreatedDate(), personnel.getUpdatedDate(), personnel.getCreatedBy(), personnel.getUpdatedBy(), personnel.getPersonnelId()});
    }

    /* there is a problem with mocking StaticHibernateUtil
    @Test
    public void shouldMigrateAdditionalFieldsForGroup() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.GROUP, CustomFieldType.ALPHA_NUMERIC, EntityType.GROUP);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.GROUP, CustomFieldType.DATE, EntityType.GROUP);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(customerDao.retrieveCustomFieldEntitiesForGroupIterator()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.GROUP)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        when(questionnaireServiceFacade.getEventSourceId("Create", "Group")).thenReturn(4);
        GroupBO groupBO1 = getGroupBO(11);
        CustomerCustomFieldEntity customField1 = CustomFieldUtils.getCustomerCustomField(1, "Ans1", groupBO1);
        CustomerCustomFieldEntity customField2 = CustomFieldUtils.getCustomerCustomField(1, "Ans2", groupBO1);
        CustomerCustomFieldEntity customField3 = CustomFieldUtils.getCustomerCustomField(1, "Ans3", groupBO1);
        List<CustomFieldForMigrationDto> customerResponses1 = asList(customerFieldEntityToMigrationDto(customField1), customerFieldEntityToMigrationDto(customField2), customerFieldEntityToMigrationDto(customField3));
        when(customerDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(customerResponses1);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 4, customerResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        GroupBO groupBO2 = getGroupBO(22);
        CustomerCustomFieldEntity customField4 = CustomFieldUtils.getCustomerCustomField(2, "Ans11", groupBO2);
        CustomerCustomFieldEntity customField5 = CustomFieldUtils.getCustomerCustomField(2, "Ans22", groupBO2);
        CustomerCustomFieldEntity customField6 = CustomFieldUtils.getCustomerCustomField(2, "Ans33", groupBO2);
        List<CustomFieldForMigrationDto> customerResponses2 = asList(customerFieldEntityToMigrationDto(customField4), customerFieldEntityToMigrationDto(customField5), customerFieldEntityToMigrationDto(customField6));
        when(customerDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 4, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForGroup();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.GROUP);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(customerDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(customerDao, times(3)).retrieveCustomFieldEntitiesForGroupIterator();
        verify(questionnaireMigrationMapper, times(2)).map(eq(QUESTION_GROUP_ID), eq(4), Matchers.<List<CustomFieldForMigrationDto>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }
    */

    /* there is a problem with mocking StaticHibernateUtil
    @Test
    public void shouldMigrateAdditionalFieldsForLoan() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CLIENT, CustomFieldType.ALPHA_NUMERIC, EntityType.LOAN);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CLIENT, CustomFieldType.DATE, EntityType.LOAN);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(loanDao.retrieveCustomFieldEntitiesForLoan()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.LOAN)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        when(questionnaireServiceFacade.getEventSourceId("Create", "Loan")).thenReturn(2);
        LoanBO loanBO1 = getLoanBO(11);
        AccountCustomFieldEntity customField1 = CustomFieldUtils.getLoanCustomField(1, "Ans1", loanBO1);
        AccountCustomFieldEntity customField2 = CustomFieldUtils.getLoanCustomField(1, "Ans2", loanBO1);
        AccountCustomFieldEntity customField3 = CustomFieldUtils.getLoanCustomField(1, "Ans3", loanBO1);
        List<CustomFieldForMigrationDto> loanResponses1 = asList(accountFieldEntityToMigrationDto(customField1), accountFieldEntityToMigrationDto(customField2), accountFieldEntityToMigrationDto(customField3));
        when(loanDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(loanResponses1);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 2, loanResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        LoanBO loanBO2 = getLoanBO(22);
        AccountCustomFieldEntity customField4 = CustomFieldUtils.getLoanCustomField(2, "Ans11", loanBO2);
        AccountCustomFieldEntity customField5 = CustomFieldUtils.getLoanCustomField(2, "Ans22", loanBO2);
        AccountCustomFieldEntity customField6 = CustomFieldUtils.getLoanCustomField(2, "Ans33", loanBO2);
        List<CustomFieldForMigrationDto> customerResponses2 = asList(accountFieldEntityToMigrationDto(customField4), accountFieldEntityToMigrationDto(customField5), accountFieldEntityToMigrationDto(customField6));
        when(loanDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 2, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForLoan();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.LOAN);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(loanDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(loanDao, times(3)).retrieveCustomFieldEntitiesForLoan();
        verify(questionnaireMigrationMapper, times(2)).map(eq(QUESTION_GROUP_ID), eq(2), Matchers.<List<CustomFieldForMigrationDto>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }
    */

    /* there is a problem with mocking StaticHibernateUtil
    @Test
    public void shouldMigrateAdditionalFieldsForSavings() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CLIENT, CustomFieldType.ALPHA_NUMERIC, EntityType.SAVINGS);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CLIENT, CustomFieldType.DATE, EntityType.SAVINGS);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(savingsDao.retrieveCustomFieldEntitiesForSavings()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.SAVINGS)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        when(questionnaireServiceFacade.getEventSourceId("Create", "Savings")).thenReturn(12);
        SavingsAccountBuilder builder = new SavingsAccountBuilder().withCustomer(savingsAccountCustomer);
        SavingsBO savingsBO1 = getSavingsBO(builder, 11);
        AccountCustomFieldEntity customField1 = CustomFieldUtils.getLoanCustomField(1, "Ans1", savingsBO1);
        AccountCustomFieldEntity customField2 = CustomFieldUtils.getLoanCustomField(1, "Ans2", savingsBO1);
        AccountCustomFieldEntity customField3 = CustomFieldUtils.getLoanCustomField(1, "Ans3", savingsBO1);
        List<CustomFieldForMigrationDto> savingsResponses1 = asList(accountFieldEntityToMigrationDto(customField1), accountFieldEntityToMigrationDto(customField2), accountFieldEntityToMigrationDto(customField3));
        when(savingsDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(savingsResponses1);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 12, savingsResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        SavingsBO savingsBO2 = getSavingsBO(builder, 22);
        AccountCustomFieldEntity customField4 = CustomFieldUtils.getLoanCustomField(2, "Ans11", savingsBO2);
        AccountCustomFieldEntity customField5 = CustomFieldUtils.getLoanCustomField(2, "Ans22", savingsBO2);
        AccountCustomFieldEntity customField6 = CustomFieldUtils.getLoanCustomField(2, "Ans33", savingsBO2);
        List<CustomFieldForMigrationDto> customerResponses2 = asList(accountFieldEntityToMigrationDto(customField4), accountFieldEntityToMigrationDto(customField5), accountFieldEntityToMigrationDto(customField6));
        when(savingsDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 12, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForSavings();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.SAVINGS);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(savingsDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(savingsDao, times(3)).retrieveCustomFieldEntitiesForSavings();
        verify(questionnaireMigrationMapper, times(2)).map(eq(QUESTION_GROUP_ID), eq(12), Matchers.<List<CustomFieldForMigrationDto>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }
    */

    /* there is a problem with mocking StaticHibernateUtil
    @Test
    public void shouldMigrateAdditionalFieldsForCenter() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CENTER, CustomFieldType.ALPHA_NUMERIC, EntityType.CENTER);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CENTER, CustomFieldType.DATE, EntityType.CENTER);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(customerDao.retrieveCustomFieldEntitiesForCenterIterator()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.CENTER)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        when(questionnaireServiceFacade.getEventSourceId("Create", "Center")).thenReturn(9);
        CenterBO centerBO1 = getCenterBO(11);
        CustomerCustomFieldEntity customField1 = CustomFieldUtils.getCustomerCustomField(1, "Ans1", centerBO1);
        CustomerCustomFieldEntity customField2 = CustomFieldUtils.getCustomerCustomField(1, "Ans2", centerBO1);
        CustomerCustomFieldEntity customField3 = CustomFieldUtils.getCustomerCustomField(1, "Ans3", centerBO1);
        List<CustomFieldForMigrationDto> customerResponses1 = asList(customerFieldEntityToMigrationDto(customField1), customerFieldEntityToMigrationDto(customField2), customerFieldEntityToMigrationDto(customField3));
        when(customerDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(customerResponses1);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 9, customerResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        CenterBO centerBO2 = getCenterBO(22);
        CustomerCustomFieldEntity customField4 = CustomFieldUtils.getCustomerCustomField(2, "Ans11", centerBO2);
        CustomerCustomFieldEntity customField5 = CustomFieldUtils.getCustomerCustomField(2, "Ans22", centerBO2);
        CustomerCustomFieldEntity customField6 = CustomFieldUtils.getCustomerCustomField(2, "Ans33", centerBO2);
        List<CustomFieldForMigrationDto> customerResponses2 = asList(customerFieldEntityToMigrationDto(customField4), customerFieldEntityToMigrationDto(customField5), customerFieldEntityToMigrationDto(customField6));
        when(customerDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 9, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForCenter();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.CENTER);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(customerDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(customerDao, times(3)).retrieveCustomFieldEntitiesForCenterIterator();
        verify(questionnaireMigrationMapper, times(2)).map(eq(QUESTION_GROUP_ID), eq(9), Matchers.<List<CustomFieldForMigrationDto>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }
    */

    /* there is a problem with mocking StaticHibernateUtil
    @Test
    public void shouldMigrateAdditionalFieldsForOffice() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CLIENT, CustomFieldType.ALPHA_NUMERIC, EntityType.CENTER);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CLIENT, CustomFieldType.DATE, EntityType.CENTER);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(officeDao.retrieveCustomFieldEntitiesForOffice()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.OFFICE)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        when(questionnaireServiceFacade.getEventSourceId("Create", "Office")).thenReturn(14);
        OfficeBO officeBO1 = getOfficeBO(11);
        OfficeCustomFieldEntity customField1 = CustomFieldUtils.getOfficeCustomField(1, "Ans1", officeBO1);
        OfficeCustomFieldEntity customField2 = CustomFieldUtils.getOfficeCustomField(1, "Ans2", officeBO1);
        OfficeCustomFieldEntity customField3 = CustomFieldUtils.getOfficeCustomField(1, "Ans3", officeBO1);
        List<CustomFieldForMigrationDto> officeResponses1 = asList(officeFieldEntityToMigrationDto(customField1), officeFieldEntityToMigrationDto(customField2), officeFieldEntityToMigrationDto(customField3));
        when(officeDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(officeResponses1);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 14, officeResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        OfficeBO officeBO2 = getOfficeBO(22);
        OfficeCustomFieldEntity customField4 = CustomFieldUtils.getOfficeCustomField(2, "Ans11", officeBO2);
        OfficeCustomFieldEntity customField5 = CustomFieldUtils.getOfficeCustomField(2, "Ans22", officeBO2);
        OfficeCustomFieldEntity customField6 = CustomFieldUtils.getOfficeCustomField(2, "Ans33", officeBO2);
        List<CustomFieldForMigrationDto> officeResponses2 = asList(officeFieldEntityToMigrationDto(customField4), officeFieldEntityToMigrationDto(customField5), officeFieldEntityToMigrationDto(customField6));
        when(officeDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(officeResponses2);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 14, officeResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForOffice();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.OFFICE);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(officeDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(officeDao, times(3)).retrieveCustomFieldEntitiesForOffice();
        verify(questionnaireMigrationMapper, times(2)).map(eq(QUESTION_GROUP_ID), eq(14), Matchers.<List<CustomFieldForMigrationDto>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }
    */

    /* there is a problem with mocking StaticHibernateUtil
    @Test
    public void shouldMigrateAdditionalFieldsForPersonnel() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CLIENT, CustomFieldType.ALPHA_NUMERIC, EntityType.CENTER);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CLIENT, CustomFieldType.DATE, EntityType.CENTER);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(personnelDao.retrieveCustomFieldEntitiesForPersonnel()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.PERSONNEL)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        when(questionnaireServiceFacade.getEventSourceId("Create", "Personnel")).thenReturn(15);
        PersonnelBO personnelBO1 = getPersonnelBO(11);
        PersonnelCustomFieldEntity customField1 = CustomFieldUtils.getPersonnelCustomField(1, "Ans1", personnelBO1);
        PersonnelCustomFieldEntity customField2 = CustomFieldUtils.getPersonnelCustomField(1, "Ans2", personnelBO1);
        PersonnelCustomFieldEntity customField3 = CustomFieldUtils.getPersonnelCustomField(1, "Ans3", personnelBO1);
        List<CustomFieldForMigrationDto> personnelResponses1 = asList(personnelFieldEntityToMigrationDto(customField1), personnelFieldEntityToMigrationDto(customField2), personnelFieldEntityToMigrationDto(customField3));
        when(personnelDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(personnelResponses1);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 15, personnelResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        PersonnelBO personnelBO2 = getPersonnelBO(22);
        PersonnelCustomFieldEntity customField4 = CustomFieldUtils.getPersonnelCustomField(2, "Ans11", personnelBO2);
        PersonnelCustomFieldEntity customField5 = CustomFieldUtils.getPersonnelCustomField(2, "Ans22", personnelBO2);
        PersonnelCustomFieldEntity customField6 = CustomFieldUtils.getPersonnelCustomField(2, "Ans33", personnelBO2);
        List<CustomFieldForMigrationDto> personnelResponses2 = asList(personnelFieldEntityToMigrationDto(customField4), personnelFieldEntityToMigrationDto(customField5), personnelFieldEntityToMigrationDto(customField6));
        when(personnelDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(personnelResponses2);
        when(questionnaireMigrationMapper.map(QUESTION_GROUP_ID, 15, personnelResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForPersonnel();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.PERSONNEL);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(personnelDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(personnelDao, times(3)).retrieveCustomFieldEntitiesForPersonnel();
        verify(questionnaireMigrationMapper, times(2)).map(eq(QUESTION_GROUP_ID), eq(15), Matchers.<List<CustomFieldForMigrationDto>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }
    */

    private QuestionGroupDto getQuestionGroupDto(String questionGroupTitle, String questionTitle, String event, String source) {
        QuestionGroupDtoBuilder questionGroupDtoBuilder = new QuestionGroupDtoBuilder();
        QuestionDto questionDto = new QuestionDtoBuilder().withText(questionTitle).withType(QuestionType.FREETEXT).build();
        SectionDto sectionDto = new SectionDtoBuilder().withName("Misc").withOrder(0).withQuestions(asList(questionDto)).build();
        questionGroupDtoBuilder.withTitle(questionGroupTitle).withEventSource(event, source).withSections(asList(sectionDto));
        return questionGroupDtoBuilder.build();
    }

    private QuestionGroupInstanceDto getQuestionGroupInstanceDto(String response, Integer creatorId, Integer entityId) {
        QuestionGroupInstanceDtoBuilder instanceBuilder = new QuestionGroupInstanceDtoBuilder();
        QuestionGroupResponseDtoBuilder responseBuilder = new QuestionGroupResponseDtoBuilder();
        responseBuilder.withResponse(response).withSectionQuestion(999);
        QuestionGroupResponseDto questionGroupResponseDto = responseBuilder.build();
        instanceBuilder.withQuestionGroup(123).withCompleted(true).withCreator(creatorId).withEventSource(1).withEntity(entityId).withVersion(1).addResponses(questionGroupResponseDto);
        return instanceBuilder.build();
    }

}
