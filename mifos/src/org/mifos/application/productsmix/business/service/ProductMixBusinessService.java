/**

 * ProductMixBusinessService.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.productsmix.business.service;



import java.util.List;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.application.productsmix.business.ProductMixBO;
import org.mifos.application.productsmix.persistence.ProductMixPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class ProductMixBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<ProductTypeEntity> getProductTypes() throws ServiceException {
		try {
			return new ProductCategoryPersistence().getProductTypes();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<LoanOfferingBO> getAllLoanOfferings(Short localeId)
			throws ServiceException {
		try {
			return new LoanPrdPersistence().getAllActiveLoanOfferings(localeId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<LoanOfferingBO> getLoanOfferingsNotMixed(Short localeId)
			throws ServiceException {
		try {
			return new LoanPrdPersistence().getLoanOfferingsNotMixed(localeId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<LoanOfferingBO> getLoanOfferings(Short localeId)
			throws ServiceException {
		try {
			return new LoanPrdPersistence().getAllActiveLoanOfferings(localeId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<LoanOfferingBO> getLoanOfferingsByID(Short localeId)
			throws ServiceException {
		try {
			return new LoanPrdPersistence().getAllActiveLoanOfferings(localeId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<SavingsOfferingBO> getAllSavingsProducts()
			throws ServiceException {
		try {
			return new SavingsPrdPersistence().getAllActiveSavingsProducts();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<SavingsOfferingBO> getSavingsOfferingsNotMixed(Short localeId)
			throws ServiceException {
		try {
			return new SavingsPrdPersistence().getSavingsOfferingsNotMixed(localeId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<PrdOfferingBO> getAllPrdOfferingsByType(String prdType)
			throws ServiceException {
		try {
			return new PrdOfferingPersistence().getAllPrdOffringByType(prdType);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<PrdOfferingBO> getAllowedPrdOfferingsByType(String prdId,String prdType)
			throws ServiceException {
		try {
			return new PrdOfferingPersistence().getAllowedPrdOfferingsByType(prdId,prdType);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<PrdOfferingBO> getAllowedPrdOfferingsForMixProduct(
			String prdId, String prdType) throws ServiceException {
		try {
			return new PrdOfferingPersistence().getAllowedPrdOfferingsForMixProduct(
					prdId, prdType);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}	
	public List<PrdOfferingBO> getNotAllowedPrdOfferingsByType(String prdId) throws ServiceException {
		try {
			return new PrdOfferingPersistence().getNotAllowedPrdOfferingsByType(
					prdId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<PrdOfferingBO> getNotAllowedPrdOfferingsForMixProduct(String prdId,
			String prdType) throws ServiceException {
		try {
			return new PrdOfferingPersistence().getNotAllowedPrdOfferingsForMixProduct(
					prdId, prdType);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	
	public ProductTypeEntity getProductTypes(Short prdtype)
			throws ServiceException {
		try {
			return new PrdOfferingPersistence().getProductTypes(prdtype);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}	

	public PrdOfferingBO getPrdOffering(Short prdofferingId)
			throws ServiceException {
		try {
			return new PrdOfferingPersistence().getPrdOffering(prdofferingId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}	
	
	public PrdOfferingBO getPrdOfferingByID(Short prdofferingId)
			throws ServiceException {
		try {
			return new PrdOfferingPersistence().getPrdOffringByID(prdofferingId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<ProductMixBO> getAllProductMix() throws ServiceException {
		try {
			return new ProductMixPersistence().getAllProductMix();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<PrdOfferingBO> getPrdOfferingMix() throws ServiceException {
		try {
			return new PrdOfferingPersistence().getPrdOfferingMix();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public ProductMixBO getPrdOfferingMixByPrdOfferingID(Short productID,Short notAllowedProductID) throws ServiceException {
		try {
			return new ProductMixPersistence().getPrdOfferingMixByPrdOfferingID(productID,notAllowedProductID);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	
	public List<ProductMixBO> getProductMixByID() throws ServiceException {
		try {
			return new ProductMixPersistence().getAllProductMix();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<PrdOfferingBO> getNotAllowedProducts(Short prdofferingId) throws ServiceException {
		try {
			return new ProductMixPersistence().getNotAllowedProducts(prdofferingId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

}
