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

package org.mifos.customers.surveys.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.exceptions.PersonnelException;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.ppi.business.PPISurvey;
import org.mifos.customers.surveys.exceptions.SurveyExceptionConstants;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.customers.surveys.helpers.SurveyState;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SurveyIntegrationTest extends MifosIntegrationTestCase {

    private static final double DELTA = 0.00000001;

    @Before
    public void setUp() throws Exception {

        // Force loading the chart of accounts since this data would otherwise
        // not be present in a Mayfly database freshly initialized from
        // particular SQL scripts. Why does setUp blow away the database every
        // time, anyway? Must be working around *another* issue...
        FinancialInitializer.loadCOA();
    }

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();

    }

    @Test
    public void testCreateSurveyInstance() throws Exception {
        Survey survey = new Survey();
        SurveyInstance instance = survey.createSurveyInstance();
       Assert.assertTrue("Instance should be instance of SurveyInstance, not PpiSurveyInstance", !(PPISurvey.class
                .isInstance(survey)));
    }

    @Test
    public void testSurveyType() {
       Assert.assertEquals("client", SurveyType.CLIENT.getValue());
       Assert.assertEquals("group", SurveyType.GROUP.getValue());
       Assert.assertEquals("center", SurveyType.CENTER.getValue());
       Assert.assertEquals("loan", SurveyType.LOAN.getValue());
       Assert.assertEquals("savings", SurveyType.SAVINGS.getValue());
       Assert.assertEquals("all", SurveyType.ALL.getValue());

       Assert.assertEquals(SurveyType.CLIENT, SurveyType.fromString("client"));
       Assert.assertEquals(SurveyType.GROUP, SurveyType.fromString("group"));
       Assert.assertEquals(SurveyType.CENTER, SurveyType.fromString("center"));
       Assert.assertEquals(SurveyType.LOAN, SurveyType.fromString("loan"));
       Assert.assertEquals(SurveyType.SAVINGS, SurveyType.fromString("savings"));
    }

    @Test
    public void testRetrieveQuestionsByState() throws Exception {
        SurveysPersistence surveysPersistence = new SurveysPersistence();
        String questionText1 = "testGetQuestionsByState question 1";
        String questionText2 = "testGetQuestionsByState question 2";
        String questionText3 = "testGetQuestionsByState question 3";

        Question question1 = new Question(questionText1);
        question1.setNickname("Q1");
        Question question2 = new Question(questionText2);
        question2.setNickname("Q2");
        question2.setQuestionState(QuestionState.INACTIVE);
        Question question3 = new Question(questionText3);
        question3.setNickname("Q3");
        question3.setQuestionState(QuestionState.ACTIVE);

        surveysPersistence.createOrUpdate(question1);
        surveysPersistence.createOrUpdate(question2);
        surveysPersistence.createOrUpdate(question3);

        List<Question> results = surveysPersistence.retrieveGeneralQuestionsByState(QuestionState.ACTIVE);
       Assert.assertEquals(2, results.size());
       Assert.assertEquals(questionText1, results.get(0).getQuestionText());
        results = surveysPersistence.retrieveGeneralQuestionsByState(QuestionState.INACTIVE);
       Assert.assertEquals(questionText2, results.get(0).getQuestionText());
    }

    @Test
    public void testRetrieveInstances() throws Exception {
        SurveysPersistence surveysPersistence = new SurveysPersistence();
        SurveyInstance instance1 = makeSurveyInstance("survey1");
        SurveyInstance instance2 = new SurveyInstance();
        Survey survey2 = new Survey();
        survey2.setName("survey2");
        survey2.setState(SurveyState.ACTIVE);
        survey2.setAppliesTo(SurveyType.CLIENT);
        instance2.setSurvey(survey2);
        instance2.setCustomer(instance1.getCustomer());
        instance2.setDateConducted(DateUtils.getCurrentDateWithoutTimeStamp());
        instance2.setOfficer(instance1.getOfficer());
        instance2.setCreator(instance1.getCreator());

        StaticHibernateUtil.startTransaction();
        MeetingBO meeting = TestObjectFactory.getTypicalMeeting();
        StaticHibernateUtil.flushSession();

        CenterBO center = TestObjectFactory.createWeeklyFeeCenter("centerName", meeting);
        meeting.setMeetingPlace("somewhere");
        SurveyInstance instance3 = new SurveyInstance();
        instance3.setCustomer(center);
        instance3.setSurvey(survey2);
        instance3.setDateConducted(DateUtils.getCurrentDateWithoutTimeStamp());
        instance3.setOfficer(instance1.getOfficer());
        instance3.setCreator(instance1.getCreator());
        surveysPersistence.createOrUpdate(instance2);
        surveysPersistence.createOrUpdate(instance3);
        StaticHibernateUtil.flushSession();

        List<SurveyInstance> retrievedInstances = surveysPersistence.retrieveInstancesByCustomer(instance1
                .getCustomer());
       Assert.assertEquals(2, retrievedInstances.size());
       Assert.assertEquals("survey2", retrievedInstances.get(1).getSurvey().getName());

        retrievedInstances = surveysPersistence.retrieveInstancesByCustomer(center);
       Assert.assertEquals(1, retrievedInstances.size());
       Assert.assertEquals("survey2", retrievedInstances.get(0).getSurvey().getName());
        SurveyInstance retrievedInstance = retrievedInstances.get(0);
        CenterBO retrievedCenter = (CenterBO) retrievedInstance.getCustomer();
       Assert.assertEquals("somewhere", retrievedCenter.getCustomerMeeting().getMeeting().getMeetingPlace());

        retrievedInstances = surveysPersistence.retrieveInstancesBySurvey(instance1.getSurvey());
       Assert.assertEquals(1, retrievedInstances.size());
       Assert.assertEquals("survey1", retrievedInstances.get(0).getSurvey().getName());
        retrievedInstances = surveysPersistence.retrieveInstancesBySurvey(survey2);
       Assert.assertEquals(2, retrievedInstances.size());
       Assert.assertEquals("survey2", retrievedInstances.get(0).getSurvey().getName());
    }

    @Test
    public void testRetrieveQuestionsByAnswerType() throws Exception {
        SurveysPersistence surveysPersistence = new SurveysPersistence();
        String questionText1 = "testGetQuestionsByState question 1";
        String questionText2 = "testGetQuestionsByState question 2";
        String questionText3 = "testGetQuestionsByState question 3";
        String questionText4 = "testGetQuestionsByState question 4";
        Question question1 = new Question(questionText1, AnswerType.FREETEXT);
        question1.setNickname("name 1");
        Question question2 = new Question(questionText2, AnswerType.NUMBER);
        question2.setNickname("name 2");
        Question question3 = new Question(questionText3, AnswerType.DATE);
        question3.setNickname("name 3");
        Question question4 = new Question(questionText4, AnswerType.CHOICE);
        question4.setNickname("name 4");

        surveysPersistence.createOrUpdate(question1);
        surveysPersistence.createOrUpdate(question2);
        surveysPersistence.createOrUpdate(question3);
        surveysPersistence.createOrUpdate(question4);

        List<Question> results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.FREETEXT);
       Assert.assertEquals(1, results.size());
       Assert.assertEquals(questionText1, results.get(0).getQuestionText());

        results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.NUMBER);
       Assert.assertEquals(1, results.size());
       Assert.assertEquals(questionText2, results.get(0).getQuestionText());

        results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.DATE);
       Assert.assertEquals(1, results.size());
       Assert.assertEquals(questionText3, results.get(0).getQuestionText());

        results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.CHOICE);
       Assert.assertEquals(1, results.size());
       Assert.assertEquals(questionText4, results.get(0).getQuestionText());

    }

    @Test
    public void testGetSurveysByType() throws Exception {
        SurveysPersistence surveysPersistence = new SurveysPersistence();
        Survey survey1 = new Survey("Survey 1", SurveyState.ACTIVE, SurveyType.CLIENT);
        Survey survey2 = new Survey("Survey 2", SurveyState.ACTIVE, SurveyType.GROUP);
        Survey survey3 = new Survey("Survey 3", SurveyState.ACTIVE, SurveyType.CENTER);
        Survey survey4 = new Survey("Survey 4", SurveyState.ACTIVE, SurveyType.LOAN);
        Survey survey5 = new Survey("Survey 5", SurveyState.ACTIVE, SurveyType.SAVINGS);
        Survey survey6 = new Survey("Survey 6", SurveyState.ACTIVE, SurveyType.ALL);

        surveysPersistence.createOrUpdate(survey1);
        surveysPersistence.createOrUpdate(survey2);
        surveysPersistence.createOrUpdate(survey3);
        surveysPersistence.createOrUpdate(survey4);
        surveysPersistence.createOrUpdate(survey5);
        surveysPersistence.createOrUpdate(survey6);

        List<Survey> allOnlyResults = surveysPersistence.retrieveSurveysByType(SurveyType.ALL);
       Assert.assertEquals(1, allOnlyResults.size());
       Assert.assertEquals(survey6.getName(), allOnlyResults.get(0).getName());

        List<Survey> clientResults = surveysPersistence.retrieveSurveysByType(SurveyType.CLIENT);
       Assert.assertEquals(2, clientResults.size());
       Assert.assertEquals(survey1.getName(), clientResults.get(0).getName());

        List<Survey> loanResults = surveysPersistence.retrieveSurveysByType(SurveyType.LOAN);
       Assert.assertEquals(2, loanResults.size());
       Assert.assertEquals(survey4.getName(), loanResults.get(0).getName());

        List<Survey> allResults = surveysPersistence.retrieveAllSurveys();
       Assert.assertEquals(6, allResults.size());

    }

    @Test
    public void testCreateSurvey() throws Exception {
        Survey survey = new Survey("testsurvey", SurveyState.ACTIVE, SurveyType.CLIENT);

        StaticHibernateUtil.startTransaction();
        SurveysPersistence surveysPersistence = new SurveysPersistence();
        surveysPersistence.createOrUpdate(survey);

        List result = StaticHibernateUtil.getSessionTL().createQuery("from " + Survey.class.getName()).list();
       Assert.assertEquals(1, result.size());
        Survey read_survey = (Survey) result.get(0);
        StaticHibernateUtil.flushSession();

       Assert.assertEquals("testsurvey", read_survey.getName());
       Assert.assertEquals(SurveyState.ACTIVE, read_survey.getStateAsEnum());
       Assert.assertEquals(SurveyType.CLIENT, read_survey.getAppliesToAsEnum());
    }

    @Test
    public void testCreateQuestion() {
        String questionText = "Why did the chicken cross the road?";

        Question question = new Question();
        question.setAnswerType(AnswerType.FREETEXT);
        question.setQuestionText(questionText);
        question.setNickname("Short Name Test");
        question.setQuestionState(QuestionState.ACTIVE);
        StaticHibernateUtil.getSessionTL().save(question);

        List result = StaticHibernateUtil.getSessionTL().createQuery("from " + Question.class.getName()).list();
       Assert.assertEquals(1, result.size());
        Question retrieved = (Question) result.get(0);
       Assert.assertEquals(questionText, retrieved.getQuestionText());
       Assert.assertEquals(AnswerType.FREETEXT, retrieved.getAnswerTypeAsEnum());
       Assert.assertEquals(QuestionState.ACTIVE, retrieved.getQuestionStateAsEnum());
    }

    @Test
    public void testRetrieveQuestions() throws Exception {
        String questionText1 = "test question 1";
        String questionText2 = "test question 2";
        String questionText3 = "test question 3";
        Question question1 = new Question(questionText1, AnswerType.FREETEXT);
        question1.setNickname("test name 1");
        Question question2 = new Question(questionText2, AnswerType.NUMBER);
        question2.setNickname("test name 2");
        Question question3 = new Question(questionText3, AnswerType.DATE);
        question3.setNickname("test name 3");
        question2.setQuestionState(QuestionState.INACTIVE);

        SurveysPersistence surveysPersistence = new SurveysPersistence();
        surveysPersistence.createOrUpdate(question1);
        surveysPersistence.createOrUpdate(question2);
        surveysPersistence.createOrUpdate(question3);

        List<Question> results = surveysPersistence.retrieveAllQuestions();
       Assert.assertEquals(3, results.size());
       Assert.assertEquals(questionText1, results.get(0).getQuestionText());
       Assert.assertEquals(questionText2, results.get(1).getQuestionText());
       Assert.assertEquals(questionText3, results.get(2).getQuestionText());

        results = surveysPersistence.retrieveGeneralQuestionsByState(QuestionState.ACTIVE);
       Assert.assertEquals(2, results.size());
       Assert.assertEquals(questionText1, results.get(0).getQuestionText());
       Assert.assertEquals(questionText3, results.get(1).getQuestionText());

    }

    @Test
    public void testRetrieveQuestionsByName() throws Exception {
        String name1 = "name1";
        Question question1 = new Question(name1, AnswerType.FREETEXT);
        question1.setNickname("test question text");

        SurveysPersistence surveysPersistence = new SurveysPersistence();
        surveysPersistence.createOrUpdate(question1);

        List<Question> results = surveysPersistence.retrieveQuestionsByText(name1);
       Assert.assertEquals(1, results.size());
       Assert.assertEquals(name1, results.get(0).getQuestionText());
    }

    public static SurveyInstance makeSurveyInstance(String surveyName) throws PersonnelException, PersistenceException,
            ValidationException {
        TestObjectFactory factory = new TestObjectFactory();
        ClientBO client = factory.createClient("Test Client " + surveyName, CustomerStatus.CLIENT_PARTIAL, null);

        Survey survey = new Survey();
        survey.setName(surveyName);
        survey.setState(SurveyState.ACTIVE);
        survey.setAppliesTo(SurveyType.CLIENT);

        OfficeBO office = factory.getOffice(TestObjectFactory.HEAD_OFFICE);
        Name name = new Name("XYZ", null, null, null);
        List<CustomFieldDto> customFieldDto = new ArrayList<CustomFieldDto>();
        customFieldDto.add(new CustomFieldDto((short) 9, "123456", CustomFieldType.NUMERIC.getValue()));
        Address address = new Address("abcd" + surveyName, "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Date date = DateUtils.getCurrentDateWithoutTimeStamp();
        String officerName = "Test Officer " + surveyName;
        PersonnelBO officer = new PersonnelBO(PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"),
                TestObjectFactory.TEST_LOCALE, "PASSWORD", officerName + System.currentTimeMillis(), "xyz@yahoo.com",
                null, customFieldDto, name, "govId" + surveyName, date, Integer.valueOf("1"), Integer.valueOf("1"),
                date, date, address, PersonnelConstants.SYSTEM_USER);

        SurveyInstance instance = new SurveyInstance();
        instance.setOfficer(officer);
        instance.setCreator(officer);
        instance.setSurvey(survey);
        instance.setCustomer(client);
        instance.setDateConducted(DateUtils.getCurrentDateWithoutTimeStamp());
        new SurveysPersistence().createOrUpdate(instance);
        StaticHibernateUtil.flushSession();
        return instance;
    }

    @Test
    public void testSurveyResponseWithChoices() throws Exception {
        SurveysPersistence persistence = new SurveysPersistence();
        SurveyInstance instance = makeSurveyInstance("Test choice type survey response");
        Survey survey = instance.getSurvey();
        String questionText = "Why did the chicken cross the road?";
        Question question = new Question(questionText, AnswerType.CHOICE);
        question.setNickname("Chicken Question");
        QuestionChoice choice1 = new QuestionChoice("To get to the other side.");
        QuestionChoice choice2 = new QuestionChoice("Exercise");
        List<QuestionChoice> choices = new LinkedList<QuestionChoice>();
        choices.add(choice1);
        choices.add(choice2);
        question.setChoices(choices);
        SurveyQuestion surveyQuestion = survey.addQuestion(question, false);
        StaticHibernateUtil.getSessionTL().save(question);
        SurveyResponse response = new SurveyResponse();
        response.setSurveyQuestion(surveyQuestion);
        response.setChoiceValue(choice1);
        response.setInstance(instance);
        StaticHibernateUtil.getSessionTL().save(response);
        List<SurveyResponse> responses = persistence.retrieveAllResponses();
       Assert.assertEquals(1, responses.size());
       Assert.assertEquals(choice1.getChoiceId(), responses.get(0).getChoiceValue().getChoiceId());
    }

    // this test was created because of problems persisting number survey
    // responses
    @Test
    public void testNumberSurveyResponse() throws Exception {
        SurveyInstance instance = makeSurveyInstance("Test number survey response");
        Survey survey = instance.getSurvey();
        String questionText = "Sample question with a numeric answer";
        Question question = new Question(questionText, AnswerType.NUMBER);
        question.setNickname("Sample Name");
        SurveyQuestion surveyQuestion = survey.addQuestion(question, false);
        StaticHibernateUtil.getSessionTL().save(question);
        SurveyResponse response = new SurveyResponse();
        response.setSurveyQuestion(surveyQuestion);
        response.setNumberValue(new Double(5));
        response.setInstance(instance);
        StaticHibernateUtil.getSessionTL().save(response);

        List<SurveyResponse> responses = new SurveysPersistence().retrieveAllResponses();
       Assert.assertEquals(1, responses.size());
       Assert.assertEquals(questionText, responses.get(0).getQuestion().getQuestionText());
    }

    @Test
    public void testSurveyResponseTypechecks() throws Exception {
        SurveyInstance instance = makeSurveyInstance("Test survey response typechecks");
        Survey survey = instance.getSurvey();

        String questionText = "Dummy question text";
        Question question = new Question(questionText, AnswerType.FREETEXT);
        question.setNickname("Short name");
        survey.addQuestion(question, true);

        String freetextAnswer = "Some answer";
        Date dateAnswer = new Date();
        BigDecimal numberAnswer = new BigDecimal("123.4");
        QuestionChoice choiceAnswer = new QuestionChoice("Some choice");
        question.addChoice(choiceAnswer);

        SurveyQuestion surveyQuestion = new SurveyQuestion();
        surveyQuestion.setQuestion(question);

        SurveyResponse response = new SurveyResponse(instance, surveyQuestion);
        try {
            response.setValue(dateAnswer);
            Assert.fail();
        } catch (ApplicationException e) {
           Assert.assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
        }

        try {
            response.setValue(choiceAnswer);
            Assert.fail();
        } catch (ApplicationException e) {
           Assert.assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
        }

        try {
            response.setValue(numberAnswer);
            Assert.fail();
        } catch (ApplicationException e) {
           Assert.assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
        }

        // verify date answertype check
        question.setAnswerType(AnswerType.DATE);

        try {
            response.setValue(freetextAnswer);
            Assert.fail();
        } catch (ApplicationException e) {
           Assert.assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
        }

        try {
            response.setValue(choiceAnswer);
            Assert.fail();
        } catch (ApplicationException e) {
           Assert.assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
        }

        try {
            response.setValue(numberAnswer);
            Assert.fail();
        } catch (ApplicationException e) {
           Assert.assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
        }

    }

    @Test
    public void testCreateRetrieveSurveyResponse() throws Exception {
        SurveysPersistence persistence = new SurveysPersistence();
        SurveyInstance instance1 = makeSurveyInstance("Test survey create response1");
        Survey survey = instance1.getSurvey();

        String questionText = "Text for testCreateSurveyResponse question";
        Question question = new Question(questionText, AnswerType.FREETEXT);
        question.setNickname("Short name uno");
        SurveyQuestion surveyQuestion = survey.addQuestion(question, true);

        SurveyResponse response1 = new SurveyResponse();
        response1.setSurveyQuestion(surveyQuestion);
        String response1Value = "response 1 value";
        response1.setStringValue(response1Value);
        response1.setInstance(instance1);

        persistence.createOrUpdate(response1);
        List<SurveyResponse> allResponses = persistence.retrieveAllResponses();
       Assert.assertEquals(1, allResponses.size());

        questionText = "text for second testCreateSurveyResponse question";
        Question question2 = new Question(questionText, AnswerType.NUMBER);
        question2.setNickname("Short name two");
        SurveyQuestion surveyQuestion2 = survey.addQuestion(question2, true);
        SurveyResponse response2 = new SurveyResponse();
        response2.setSurveyQuestion(surveyQuestion2);
        response2.setNumberValue(new Double(5));
        response2.setInstance(instance1);

        persistence.createOrUpdate(response2);
        allResponses = persistence.retrieveAllResponses();
       Assert.assertEquals(2, allResponses.size());

        SurveyInstance instance2 = makeSurveyInstance("Test survey create response2");
        SurveyResponse response3 = new SurveyResponse();
        response3.setInstance(instance2);
        response3.setSurveyQuestion(surveyQuestion2);
        response3.setStringValue("42");

        persistence.createOrUpdate(response3);
        allResponses = persistence.retrieveAllResponses();
       Assert.assertEquals(3, allResponses.size());

        List<SurveyResponse> responses = persistence.retrieveResponsesByInstance(instance1);
       Assert.assertEquals(2, responses.size());
       Assert.assertTrue(responses.contains(response1));
       Assert.assertTrue(responses.contains(response2));
       Assert.assertEquals(AnswerType.NUMBER, responses.get(1).getQuestion().getAnswerTypeAsEnum());
       Assert.assertEquals(5.0, responses.get(1).getNumberValue(), DELTA);
       Assert.assertEquals(response1Value, responses.get(0).getFreetextValue());

        responses = persistence.retrieveResponsesByInstance(instance2);
       Assert.assertEquals(1, responses.size());
       Assert.assertTrue(responses.contains(response3));
       Assert.assertEquals(AnswerType.NUMBER, responses.get(0).getQuestion().getAnswerTypeAsEnum());
       Assert.assertEquals(42.0, responses.get(0).getNumberValue(), DELTA);
        Assert.assertNull(responses.get(0).getFreetextValue());

        SurveyInstance retrievedInstance = persistence.getInstance(instance1.getInstanceId());
        responses = persistence.retrieveResponsesByInstance(retrievedInstance);
       Assert.assertEquals(2, responses.size());
    }

    @Test
    public void testSerialize() throws Exception {
        Survey survey = new Survey("my survey", SurveyState.ACTIVE, SurveyType.CLIENT);
        Question question = new Question("Can I be written to the session?");
        survey.addQuestion(question, false);

        TestUtils.assertCanSerialize(survey);
    }

}
