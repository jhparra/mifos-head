package org.mifos.application.productdefinition.business;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestProductCategoryBO extends MifosIntegrationTest {

	public TestProductCategoryBO() throws SystemException, ApplicationException {
        super();
    }

    public void testCreateProductCategory() 
	throws SystemException, ApplicationException {
		UserContext userContext=TestUtils.makeUser();
		List<ProductTypeEntity> productTypeList=new ProductCategoryBusinessService().getProductTypes();
		assertEquals(2,productTypeList.size());
		TestObjectFactory.updateObject(new ProductCategoryBO(userContext,productTypeList.get(0),"product category",null));
		ProductCategoryBO productCategoryBO=getProductCategory().get(2);
		assertEquals("1-003",productCategoryBO.getGlobalPrdCategoryNum());
		assertEquals("product category",productCategoryBO.getProductCategoryName());
		assertNull(productCategoryBO.getProductCategoryDesc());
		assertNotNull(productCategoryBO.getProductType());
		try {
			TestObjectFactory.updateObject(new ProductCategoryBO(userContext,
				productTypeList.get(0),"product category",null));
			fail();
		}
		catch (ProductDefinitionException expected) {
			assertEquals(ProductDefinitionConstants.DUPLICATE_CATEGORY_NAME,
				expected.getKey());
		}
		deleteProductCategory(productCategoryBO);
	}
	
	public void testUpdateProductCategory() 
	throws SystemException, ApplicationException {
		UserContext userContext= TestUtils.makeUser();
		List<ProductTypeEntity> productTypeList=new ProductCategoryBusinessService().getProductTypes();
		assertEquals(2,productTypeList.size());
		TestObjectFactory.updateObject(new ProductCategoryBO(userContext,productTypeList.get(0),"product category",null));
		ProductCategoryBO productCategoryBO=getProductCategory().get(2);
		PrdCategoryStatusEntity prdCategoryStatusEntity =(PrdCategoryStatusEntity)TestObjectFactory.getObject(PrdCategoryStatusEntity.class,Short.valueOf("0"));
		productCategoryBO.updateProductCategory("Category","Name changed",prdCategoryStatusEntity);
		TestObjectFactory.updateObject(productCategoryBO);
		productCategoryBO=getProductCategory().get(2);
		assertEquals("1-003",productCategoryBO.getGlobalPrdCategoryNum());
		assertEquals("Category",productCategoryBO.getProductCategoryName());
		assertNotNull(productCategoryBO.getProductCategoryDesc());
		assertNotNull(productCategoryBO.getProductType());
		assertNotNull("Name changed",productCategoryBO.getProductCategoryDesc());
		productCategoryBO.updateProductCategory("Category","Name changed",
			prdCategoryStatusEntity);
		// TODO: assert that it got updated.
		deleteProductCategory(productCategoryBO);
	}
	
	private List<ProductCategoryBO> getProductCategory() {
		return StaticHibernateUtil
			.getSessionTL()
			.createQuery(
				"from org.mifos.application.productdefinition.business.ProductCategoryBO pcb " +
				"order by pcb.productCategoryID")
			.list();
	}
	
	private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
		Session session = StaticHibernateUtil.getSessionTL();
		Transaction	transaction = StaticHibernateUtil.startTransaction();
		session.delete(productCategoryBO);
		transaction.commit();
	}

	
}
