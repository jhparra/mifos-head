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

package org.mifos.application.customer.client.business.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestCaseInitializer;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.test.framework.util.DatabaseTestUtils;
import org.mifos.test.framework.util.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;

@ContextConfiguration(locations = { "classpath:integration-test-context.xml" })
public class StandardClientServiceIntegrationTest extends AbstractJUnit38SpringContextTests {

    private static final String CUSTOMER_ATTENDANCE = "CUSTOMER_ATTENDANCE";
    private StandardClientService clientService;
    private int client1Id = 123456;
    private AttendanceType client1Attendance = AttendanceType.PRESENT;
    private int client2Id = 123457;
    private AttendanceType client2Attendance = AttendanceType.ABSENT;
    private LocalDate meetingDate = new LocalDate("2009-02-14");

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DatabaseTestUtils databaseTestUtils;

    public void setUp() throws Exception {
        initializeMifosSoftware();

        clientService = new StandardClientService();
        StandardClientAttendanceDao clientAttendanceDao = new StandardClientAttendanceDao();
        clientService.setClientAttendanceDao(clientAttendanceDao);

        databaseTestUtils.deleteDataFromTables(dataSource, CUSTOMER_ATTENDANCE);
    }

    public void tearDown() throws Exception {
    }

    public void testGetClientAttendanceTwoIds() throws Exception {
        initializeData();
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, null));
        clientAttendanceDtos.add(getClientAttendanceDto(client2Id, meetingDate, null));
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService
                .getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(2, clientAttendance.size());
        Assert.assertEquals(client1Id, (int) clientAttendance.get(client1Id).getClientId());
        Assert.assertEquals(client1Attendance, clientAttendance.get(client1Id).getAttendance());
        Assert.assertEquals(client2Id, (int) clientAttendance.get(client2Id).getClientId());
        Assert.assertEquals(client2Attendance, clientAttendance.get(client2Id).getAttendance());
    }

    public void testSetClientAttendanceTwoIds() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, AttendanceType.PRESENT));
        clientAttendanceDtos.add(getClientAttendanceDto(client2Id, meetingDate, AttendanceType.ABSENT));
        clientService.setClientAttendance(clientAttendanceDtos);
        databaseTestUtils.verifyTable(getAttendanceDataSet().toString(), CUSTOMER_ATTENDANCE, dataSource);
    }

    public void testSetClientAttendanceReplaceOneId() throws Exception {
        initializeData();
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        AttendanceType expectedAttendance = AttendanceType.APPROVED_LEAVE;
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, expectedAttendance));
        clientService.setClientAttendance(clientAttendanceDtos);
        Map<Integer, ClientAttendanceDto> actualClientAttendanceDtos = clientService
                .getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(expectedAttendance, actualClientAttendanceDtos.get(client1Id).getAttendance());
        databaseTestUtils.verifyTable(getReplacedAttendanceDataSet().toString(), CUSTOMER_ATTENDANCE, dataSource);
    }

    public void testGetBulkEntryClientAttendance() throws SystemException, ApplicationException {

        new TestCaseInitializer().initialize();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        CenterBO center = TestObjectFactory.createCenter("Center", meeting);
        GroupBO group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        ClientBO client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        StaticHibernateUtil.closeSession();

        java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();

        ClientAttendanceBO clientAttendance = new ClientAttendanceBO();
        clientAttendance.setAttendance(AttendanceType.PRESENT);
        clientAttendance.setMeetingDate(meetingDate);
        ((ClientBO) client).addClientAttendance(clientAttendance);
        CustomerPersistence customerPersistence = new CustomerPersistence();
        customerPersistence.createOrUpdate(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        List<ClientAttendanceDto> clientAttendanceDtos = (List<ClientAttendanceDto>) clientService
                .getClientAttendanceList(meetingDate, client.getOffice().getOfficeId());

        Assert.assertEquals(clientAttendanceDtos.size(), 1);

    }

    private ClientAttendanceDto getClientAttendanceDto(int clientId, LocalDate meetingDate, AttendanceType attendance) {
        return new ClientAttendanceDto(clientId, meetingDate, attendance);
    }

    private void initializeMifosSoftware() {
        MifosLogManager.configureLogging();
        DatabaseSetup.initializeHibernate();
    }

    private void initializeData() throws DataSetException, IOException, SQLException, DatabaseUnitException {
        getAttendanceDataSet().insert(dataSource);
    }

    private SimpleDataSet getAttendanceDataSet() {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row("CUSTOMER", "CUSTOMER_ID=123456", "CUSTOMER_LEVEL_ID=1", "VERSION_NO=1",
                "DISCRIMINATOR=CLIENT");
        attendanceDataSet.row("CUSTOMER", "CUSTOMER_ID=123457", "CUSTOMER_LEVEL_ID=1", "VERSION_NO=1",
                "DISCRIMINATOR=CLIENT");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=1", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123456",
                "ATTENDANCE=1");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=2", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123457",
                "ATTENDANCE=2");
        return attendanceDataSet;
    }

    private SimpleDataSet getReplacedAttendanceDataSet() {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=1", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123456",
                "ATTENDANCE=3");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=2", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123457",
                "ATTENDANCE=2");
        return attendanceDataSet;
    }
}