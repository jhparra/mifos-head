
package org.mifos.application.personnel.persistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeTemplate;
import org.mifos.application.office.business.OfficeTemplateImpl;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelNotesEntity;
import org.mifos.application.personnel.business.PersonnelTemplate;
import org.mifos.application.personnel.business.PersonnelTemplateImpl;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonnelPersistence extends MifosIntegrationTest {
	
	public TestPersonnelPersistence() throws SystemException, ApplicationException {
        super();
    }

    private static final Short OFFICE_WITH_BRANCH_MANAGER = Short.valueOf("3");

	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO client;

	private CustomerBO group;
	
	private OfficeBO office;

	private OfficeBO branchOffice;

	private OfficeBO createdBranchOffice;
	
	PersonnelBO personnel;
	
	Name name;

	CustomerPersistence customerPersistence = new CustomerPersistence();
	PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    OfficePersistence officePersistence = new OfficePersistence();

    @Override
	public void setUp() throws Exception{
        initializeStatisticsService();
    }

	@Override
	public void tearDown() throws Exception {
		office = null;
		branchOffice = null;
		name = null;
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(personnel);
		TestObjectFactory.cleanUp(createdBranchOffice);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

    public void testCreatePersonnel()
            throws Exception {
        UserContext userContext = TestUtils.makeUser();
        OfficeTemplate officeTemplate =
                OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            OfficeBO office = officePersistence.createOffice(userContext, officeTemplate);
            PersonnelTemplate template = PersonnelTemplateImpl.createNonUniquePersonnelTemplate(office.getOfficeId());
            PersonnelBO personnel = personnelPersistence.createPersonnel(userContext, template);

            assertNotNull(personnel.getPersonnelId());
            assertTrue(personnel.isActive());
            assertFalse(personnel.isPasswordChanged());
        }
        finally {
            StaticHibernateUtil.rollbackTransaction();
        }
        assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testCreatePersonnelValidationFailure()
            throws Exception {
        UserContext userContext = TestUtils.makeUser();
        PersonnelTemplate template = PersonnelTemplateImpl.createNonUniquePersonnelTemplate(new Short((short) -1));
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            PersonnelBO personnel = personnelPersistence.createPersonnel(userContext, template);
            fail("Should not have been able to create personnel " + personnel.getDisplayName());
        } catch (ValidationException e) {
            assertTrue(e.getMessage().equals(PersonnelConstants.OFFICE));
        }
        finally {
            StaticHibernateUtil.rollbackTransaction();
        }
        assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testActiveLoanOfficersInBranch() throws Exception {
		List<PersonnelView> personnels = personnelPersistence
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), Short.valueOf("3"),
						PersonnelConstants.LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

	public void testNonLoanOfficerInBranch() throws Exception {
		List<PersonnelView> personnels = personnelPersistence
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), OFFICE_WITH_BRANCH_MANAGER,
						PersonnelConstants.NON_LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

	public void testIsUserExistSucess() throws Exception{
		assertTrue(personnelPersistence.isUserExist("mifos"));
	}

	public void testIsUserExistFailure() throws Exception{
		assertFalse(personnelPersistence.isUserExist("XXX"));
	}

	public void testIsUserExistWithGovernmentIdSucess()throws Exception {
		assertTrue(personnelPersistence.isUserExistWithGovernmentId("123"));
	}

	public void testIsUserExistWithGovernmentIdFailure()throws Exception {
		assertFalse(personnelPersistence.isUserExistWithGovernmentId("XXX"));
	}

	public void testIsUserExistWithDobAndDisplayNameSucess() throws Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertTrue(personnelPersistence.isUserExist("mifos", dateFormat
				.parse("1979-12-12")));
	}

	public void testIsUserExistWithDobAndDisplayNameFailure() throws Exception{
		assertFalse(personnelPersistence.isUserExist("mifos", new GregorianCalendar(
				1989, 12, 12, 0, 0, 0).getTime()));
	}
	
	public void testActiveCustomerUnderLO() throws Exception{
		createCustomers(
			CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		assertTrue(personnelPersistence.getActiveChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
	}
	
	public void testClosedCustomerUnderLO() throws Exception{
		createCustomers(
			CustomerStatus.GROUP_CLOSED, CustomerStatus.CLIENT_CANCELLED);
		center.changeStatus(CustomerStatus.CENTER_INACTIVE,null,"check inactive");
		center.update();
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		center = TestObjectFactory.getCenter(center.getCustomerId());
		assertTrue(!personnelPersistence.getActiveChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
	}
	
	public void testGetAllCustomerUnderLO() throws Exception{
		createCustomers(
			CustomerStatus.GROUP_CLOSED ,CustomerStatus.CLIENT_CANCELLED);
		assertTrue(personnelPersistence.getAllChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
		center.changeStatus(CustomerStatus.CENTER_INACTIVE,null,"check inactive");
		center.update();
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		center = TestObjectFactory.getCenter(center.getCustomerId());
		assertTrue(personnelPersistence.getAllChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
	}
	
	public void testGetAllPersonnelNotes() throws Exception {
		office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		StaticHibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		assertEquals(branchOffice.getOfficeId(), personnel.getOffice()
				.getOfficeId());
		createInitialObjects(branchOffice.getOfficeId(), personnel
				.getPersonnelId());

		PersonnelNotesEntity personnelNotes = new PersonnelNotesEntity(
				"Personnel notes created", new PersonnelPersistence()
						.getPersonnel(PersonnelConstants.SYSTEM_USER), personnel);
		personnel.addNotes(PersonnelConstants.SYSTEM_USER, personnelNotes);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		assertEquals(1,personnelPersistence.getAllPersonnelNotes(personnel.getPersonnelId()).getSize());
	}
	
	public void testSuccessfullGetPersonnel() throws Exception {
		branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		String oldUserName = personnel.getUserName();
		personnel = personnelPersistence.getPersonnelByUserName(personnel.getUserName());
		assertEquals(oldUserName,personnel.getUserName());
	}
	
	public void testGetPersonnelByGlobalPersonnelNum()throws Exception{
		assertNotNull(personnelPersistence.getPersonnelByGlobalPersonnelNum("1"));
	}
	
	public void testGetPersonnelRoleCount() throws Exception{
		Integer count = personnelPersistence.getPersonnelRoleCount((short)2);
		assertEquals(0,(int)count);
		count = personnelPersistence.getPersonnelRoleCount((short)1);
		assertEquals(3,(int)count);
	}
	
	
	public  void testSearch()throws Exception{
		branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		QueryResult queryResult = personnelPersistence.search(personnel.getUserName(),Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
	}
	public  void testSearchFirstNameAndLastName()throws Exception{
		branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		personnel = createPersonnelWithName(branchOffice, PersonnelLevel.LOAN_OFFICER,new Name("Rajender",null,"singh","saini"));
		QueryResult queryResult = personnelPersistence.search(personnel.getPersonnelDetails().getName().getFirstName()+" "+personnel.getPersonnelDetails().getName().getLastName(),Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
	}	
	public void  testGetActiveLoanOfficersUnderOffice()throws Exception{
		branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		List<PersonnelBO> loanOfficers = personnelPersistence.getActiveLoanOfficersUnderOffice(branchOffice.getOfficeId());
		assertNotNull(loanOfficers);
		assertEquals(2,loanOfficers.size());
	  assertEquals("loan officer",loanOfficers.get(0).getDisplayName());
	  assertEquals("XYZ",loanOfficers.get(1).getDisplayName());	
	}
	
	
	public void testGetSupportedLocale()throws Exception{
		//asserting only on not null as suppored locales can be added by user 
		assertNotNull(personnelPersistence.getSupportedLocales());
	}

	public void testGetAllPersonnel() throws Exception {		
		List<PersonnelBO> personnel = personnelPersistence.getAllPersonnel();		
		assertNotNull(personnel);
		assertEquals(3, personnel.size());
	}

	private void createInitialObjects(Short officeId, Short personnelId) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());

		center = TestObjectFactory.createCenter("Center", meeting, officeId, personnelId);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private PersonnelBO createPersonnel(OfficeBO office,
			PersonnelLevel personnelLevel) throws Exception {
		name = new Name("XYZ", null, null, null);
		return create(personnelLevel,name,PersonnelConstants.SYSTEM_USER, office);
	}
    
    private PersonnelBO createPersonnelWithName(OfficeBO office,
			PersonnelLevel personnelLevel,Name personnelName) throws Exception {
		return create(personnelLevel,personnelName,PersonnelConstants.SYSTEM_USER,office);
	}

    private PersonnelBO create(PersonnelLevel personnelLevel,Name name,Short createdBy,OfficeBO office)throws Exception{
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				CustomFieldType.NUMERIC));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		personnel = new PersonnelBO(personnelLevel, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", null, customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, createdBy);
		personnel.save();
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
  }
	
	private void createCustomers(CustomerStatus groupStatus, 
			CustomerStatus clientStatus) {
		meeting = TestObjectFactory.createMeeting(
				TestObjectFactory.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter(
				"group", groupStatus, center);
		client = TestObjectFactory.createClient("client",clientStatus,
				group,new Date());
	}
	
	public void testGetActiveBranchManagerUnderOffice() throws Exception {
		List<PersonnelBO> activeBranchManagersUnderOffice = new PersonnelPersistence()
				.getActiveBranchManagersUnderOffice(OFFICE_WITH_BRANCH_MANAGER,
						new RolesPermissionsPersistence().getRole(Short
								.valueOf("1")));
		assertNotNull(activeBranchManagersUnderOffice);
		assertEquals(2, activeBranchManagersUnderOffice.size());
	}	
	
	
}
