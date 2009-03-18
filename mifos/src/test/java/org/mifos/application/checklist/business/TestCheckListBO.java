package org.mifos.application.checklist.business;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.exceptions.CheckListException;
import org.mifos.application.checklist.util.helpers.CheckListType;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCheckListBO extends MifosIntegrationTest {

	public TestCheckListBO() throws SystemException, ApplicationException {
        super();
    }

    private CustomerCheckListBO customerCheckList = null;

	private AccountCheckListBO accountCheckList = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(customerCheckList);
		TestObjectFactory.cleanUp(accountCheckList);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testCreateCheckListForCustomer() throws Exception {
		CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(
				CustomerLevel.CENTER);
		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(
				CustomerStatus.CENTER_ACTIVE);
		customerCheckList = new CustomerCheckListBO(customerLevelEntity,
				customerStatusEntity, "Customer CheckList",
				CheckListConstants.STATUS_ACTIVE, getCheckListDetails(),
				(short) 1, (short) 1);
		customerCheckList.save();
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();

		customerCheckList = (CustomerCheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, customerCheckList.getChecklistId());

		assertEquals("Customer CheckList", customerCheckList.getChecklistName());
		assertEquals(CheckListConstants.STATUS_ACTIVE, customerCheckList
				.getChecklistStatus());
		assertEquals(3, customerCheckList.getChecklistDetails().size());
		assertEquals(CheckListType.CUSTOMER_CHECKLIST, customerCheckList
				.getCheckListType());

	}

	public void testCreateCheckListForProduct() throws Exception {
		ProductTypeEntity productTypeEntity = (ProductTypeEntity) TestObjectFactory
				.getObject(ProductTypeEntity.class, (short) 2);
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountState.SAVINGS_PARTIAL_APPLICATION);
		accountCheckList = new AccountCheckListBO(productTypeEntity,
				accountStateEntity, "Account CheckList", Short.valueOf("1"),
				getCheckListDetails(), Short.valueOf("1"), (short) 1);
		accountCheckList.save();
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();

		accountCheckList = (AccountCheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, accountCheckList.getChecklistId());

		assertEquals("Account CheckList", accountCheckList.getChecklistName());
		assertEquals(3, accountCheckList.getChecklistDetails().size());
		assertEquals(CheckListConstants.STATUS_ACTIVE, accountCheckList
				.getChecklistStatus());
		assertEquals(CheckListType.ACCOUNT_CHECKLIST, accountCheckList
				.getCheckListType());
	}

	public void testUpdateForNullCheckListName() throws Exception {
		customerCheckList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), CheckListConstants.STATUS_ACTIVE);
		try {
			customerCheckList.update(customerCheckList.getCustomerLevel(),
					customerCheckList.getCustomerStatus(), null,
					customerCheckList.getChecklistStatus(),
					getCheckListDetails(), (short) 1, (short) 1);
			fail();
		} catch (CheckListException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
		customerCheckList = (CustomerCheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, customerCheckList.getChecklistId());
	}

	public void testUpdateForNullCheckListDetails() throws Exception {
		customerCheckList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), CheckListConstants.STATUS_ACTIVE);
		try {
			customerCheckList.update(customerCheckList.getCustomerLevel(),
					customerCheckList.getCustomerStatus(), customerCheckList
							.getChecklistName(), customerCheckList
							.getChecklistStatus(), null, (short) 1, (short) 1);
			fail();
		} catch (CheckListException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
		customerCheckList = (CustomerCheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, customerCheckList.getChecklistId());
	}

	public void testUpdateForEmptyCheckListDetails() throws Exception {
		customerCheckList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), CheckListConstants.STATUS_ACTIVE);
		try {
			customerCheckList.update(customerCheckList.getCustomerLevel(),
					customerCheckList.getCustomerStatus(), customerCheckList
							.getChecklistName(), customerCheckList
							.getChecklistStatus(), new ArrayList<String>(),
					(short) 1, (short) 1);
			fail();
		} catch (CheckListException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
		customerCheckList = (CustomerCheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, customerCheckList.getChecklistId());
	}

	public void testUpdateCustomerCheckList() throws Exception {
		customerCheckList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), CheckListConstants.STATUS_ACTIVE);
		StaticHibernateUtil.closeSession();
		customerCheckList = (CustomerCheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, customerCheckList.getChecklistId());

		customerCheckList.update(customerCheckList.getCustomerLevel(),
				customerCheckList.getCustomerStatus(), "Customer CheckList",
				CheckListConstants.STATUS_INACTIVE, getCheckListDetails(),
				(short) 1, (short) 1);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		customerCheckList = (CustomerCheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, customerCheckList.getChecklistId());

		assertEquals("Customer CheckList", customerCheckList.getChecklistName());
		assertEquals(CheckListConstants.STATUS_INACTIVE, customerCheckList
				.getChecklistStatus());
		assertEquals(3, customerCheckList.getChecklistDetails().size());
		assertEquals(CheckListType.CUSTOMER_CHECKLIST, customerCheckList
				.getCheckListType());
	}

	public void testUpdateAccountCheckList() throws Exception {
		accountCheckList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
				CheckListConstants.STATUS_ACTIVE);
		StaticHibernateUtil.closeSession();
		accountCheckList = (AccountCheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, accountCheckList.getChecklistId());

		accountCheckList.update(accountCheckList.getProductTypeEntity(),
				accountCheckList.getAccountStateEntity(), "Account CheckList",
				CheckListConstants.STATUS_INACTIVE, getCheckListDetails(),
				(short) 1, (short) 1);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		accountCheckList = (AccountCheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, accountCheckList.getChecklistId());

		assertEquals("Account CheckList", accountCheckList.getChecklistName());
		assertEquals(CheckListConstants.STATUS_INACTIVE, accountCheckList
				.getChecklistStatus());
		assertEquals(3, accountCheckList.getChecklistDetails().size());
		assertEquals(CheckListType.ACCOUNT_CHECKLIST, accountCheckList
				.getCheckListType());
	}

	public void testSaveInValidConnection() throws CheckListException {
		ProductTypeEntity productTypeEntity = (ProductTypeEntity) TestObjectFactory
				.getObject(ProductTypeEntity.class, (short) 2);
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountState.SAVINGS_PARTIAL_APPLICATION);
		AccountCheckListBO accountCheckList = new AccountCheckListBO(productTypeEntity,
				accountStateEntity, "Account CheckList", Short.valueOf("1"),
				getCheckListDetails(), Short.valueOf("1"), (short) 1);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			accountCheckList.save();
			fail();
		} catch (CheckListException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}
	
	public void testCreateCheckListExceptionForCustomer() throws Exception {
		CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(
				CustomerLevel.CENTER);
		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(
				CustomerStatus.CENTER_ACTIVE);
		try {
			customerCheckList = new CustomerCheckListBO(customerLevelEntity,
					customerStatusEntity, null,
					CheckListConstants.STATUS_ACTIVE, getCheckListDetails(),
					(short) 1, (short) 1);
			fail();
		} catch (CheckListException ce) {
			assertTrue(true);
		}
	}
	
	public void testCreateCheckListExceptionForCustomerZeroDetails() throws Exception {
		CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(
				CustomerLevel.CENTER);
		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(
				CustomerStatus.CENTER_ACTIVE);
		try {
			customerCheckList = new CustomerCheckListBO(customerLevelEntity,
					customerStatusEntity, null,
					CheckListConstants.STATUS_ACTIVE, new ArrayList<String>(),
					(short) 1, (short) 1);
			fail();
		} catch (CheckListException ce) {
			assertTrue(true);
		}
	}



	public void testUpdateAccountCheckListInvalidState() throws Exception {
		AccountCheckListBO accountCheckList1 = TestObjectFactory
				.createAccountChecklist(ProductType.LOAN.getValue(),
						AccountState.LOAN_APPROVED,
						CheckListConstants.STATUS_ACTIVE);
		accountCheckList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
				CheckListConstants.STATUS_ACTIVE);
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountState.LOAN_APPROVED);
		StaticHibernateUtil.closeSession();
		accountCheckList = (AccountCheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, accountCheckList.getChecklistId());
		try {
			accountCheckList.update(accountCheckList.getProductTypeEntity(),
					accountStateEntity, "Account CheckList",
					CheckListConstants.STATUS_INACTIVE, getCheckListDetails(),
					(short) 1, (short) 1);
			fail();
		} catch (CheckListException ce) {
			assertTrue(true);
		} finally {
			TestObjectFactory.deleteChecklist(accountCheckList1);
			StaticHibernateUtil.closeSession();
		}
	}
	
	public void testUpdateCustomerCheckListInvalidState() throws Exception {
		CustomerCheckListBO customerCheckList1 = TestObjectFactory
				.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
						CustomerStatus.CENTER_INACTIVE.getValue(),
						CheckListConstants.STATUS_ACTIVE);
		customerCheckList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), CheckListConstants.STATUS_ACTIVE);
		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(
				CustomerStatus.fromInt(CustomerStatus.CENTER_INACTIVE
						.getValue()));
		StaticHibernateUtil.closeSession();
		customerCheckList = (CustomerCheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, customerCheckList.getChecklistId());
		try {
			customerCheckList.update(customerCheckList.getCustomerLevel(),
					customerStatusEntity, "Customer CheckList",
					CheckListConstants.STATUS_INACTIVE, getCheckListDetails(),
					(short) 1, (short) 1);
			fail();
		} catch (CheckListException ce) {
			assertTrue(true);
		} finally {
			TestObjectFactory.deleteChecklist(customerCheckList1);
			StaticHibernateUtil.closeSession();
		}
	}

	private List<String> getCheckListDetails() {
		List<String> details = new ArrayList();
		details.add("new detail1");
		details.add("new detail2");
		details.add("new detail3");
		return details;
	}
}
