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

package org.mifos.customers.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.struts.actionforms.CustSearchActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CustSearchAction extends SearchAction {

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("custSearchAction");
        security.allow("loadSearch", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("loadMainSearch", SecurityConstants.VIEW);
        security.allow("mainSearch", SecurityConstants.VIEW);
        security.allow("getHomePage", SecurityConstants.VIEW);
        security.allow("loadAllBranches", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("getOfficeHomePage", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;


        boolean isCenterHierarchyExist = ClientRules.getCenterHierarchyExists();

        if (StringUtils.isNotBlank(actionForm.getLoanOfficerId())) {

            Short loanOfficerId = Short.valueOf(actionForm.getLoanOfficerId());
            List<CustomerDetailDto> customerList = this.centerServiceFacade.retrieveCustomersUnderUser(loanOfficerId);

            SessionUtils.setCollectionAttribute(CustomerSearchConstants.CUSTOMERLIST, customerList, request);
            SessionUtils.setAttribute("GrpHierExists", isCenterHierarchyExist, request);
            SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDLOANOFFICER, request);
        }

        UserContext userContext = getUserContext(request);
        Short userBranchId = userContext.getBranchId();
        String officeName = retrieveOfficeName(actionForm, userBranchId);

        SessionUtils.setAttribute("isCenterHierarchyExists", isCenterHierarchyExist, request);
        SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, officeName, request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);
        return mapping.findForward(CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS);
    }

    private String retrieveOfficeName(CustSearchActionForm actionForm, Short userBranchId) {
        String officeName;
        if (StringUtils.isNotBlank(actionForm.getOfficeId())) {
            Short officeId = Short.valueOf(actionForm.getOfficeId());
            officeName = this.centerServiceFacade.retrieveOfficeName(officeId);
        } else {
            officeName = this.centerServiceFacade.retrieveOfficeName(userBranchId);
        }
        return officeName;
    }

    @TransactionDemarcate(conditionToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;

        if (StringUtils.isNotBlank(actionForm.getOfficeId())) {
            List<PersonnelBO> personnelList = new PersonnelPersistence().getActiveLoanOfficersUnderOffice(getShortValue(actionForm.getOfficeId()));
            SessionUtils.setCollectionAttribute(CustomerSearchConstants.LOANOFFICERSLIST, personnelList, request);
        }

        UserContext userContext = getUserContext(request);
        Short userBranchId = userContext.getBranchId();
        String officeName = retrieveOfficeName(actionForm, userBranchId);

        SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, officeName, request);
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);

        return mapping.findForward(CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadAllBranches(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setOfficeId("0");
        UserContext userContext = getUserContext(request);
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);

        loadMasterData(userContext.getId(), request, actionForm);
        return mapping.findForward(CustomerSearchConstants.LOADALLBRANCHES_SUCCESS);

    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getHomePage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setSearchString(null);

        cleanUpSearch(request);
        UserContext userContext = getUserContext(request);
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);
        loadMasterData(userContext.getId(), request, actionForm);

        fixUpReportSecurity();

        return mapping.findForward(CustomerConstants.GETHOMEPAGE_SUCCESS);
    }

    private static void fixUpReportSecurity() {
        // MIFOS-3108: the following line doesn't make too much sense (and causes permission problems).
        // ActivityMapper.getInstance().getActivityMap().put("/reportsUserParamsAction-loadAddList-" + 4, (short) -1);
        try {
            AuthorizationManager.getInstance().init();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setSearchString(null);
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        cleanUpSearch(request);
        return mapping.findForward(ActionForwards.loadSearch_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadMainSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String forward = null;
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        actionForm.setSearchString(null);
        actionForm.setOfficeId("0");
        cleanUpSearch(request);
        UserContext userContext = getUserContext(request);
        SessionUtils.setAttribute("isCenterHierarchyExists", ClientRules.getCenterHierarchyExists(), request);

        forward = loadMasterData(userContext.getId(), request, actionForm);
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward mainSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        Short officeId = getShortValue(actionForm.getOfficeId());
        String searchString = actionForm.getSearchString();
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        super.search(mapping, form, request, response);
        if (searchString == null || searchString.equals("")) {

            ActionErrors errors = new ActionErrors();

            errors.add(CustomerSearchConstants.NAMEMANDATORYEXCEPTION, new ActionMessage(
                    CustomerSearchConstants.NAMEMANDATORYEXCEPTION));

            request.setAttribute(Globals.ERROR_KEY, errors);
            return mapping.findForward(ActionForwards.mainSearch_success.toString());
        }

        if (officeId != null && officeId != 0) {
            addSeachValues(searchString, officeId.toString(), new OfficePersistence().getOffice(officeId).getOfficeName(), request);
        } else {
            addSeachValues(searchString, officeId.toString(), new OfficePersistence().getOffice(userContext.getBranchId()).getOfficeName(), request);
        }
        searchString = SearchUtils.normalizeSearchString(searchString);
        if (searchString.equals("")) {
            throw new CustomerException(CustomerSearchConstants.NAMEMANDATORYEXCEPTION);
        }
        QueryResult customerSearchResult = new CustomerPersistence().search(searchString, officeId, userContext.getId(), userContext.getBranchId());
        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, customerSearchResult, request);
        return mapping.findForward(ActionForwards.mainSearch_success.toString());

    }

    @TransactionDemarcate(conditionToken = true)
    public ActionForward getOfficeHomePage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Short loggedUserLevel = getUserContext(request).getLevelId();
        if (loggedUserLevel.equals(PersonnelLevel.LOAN_OFFICER.getValue())) {
            return loadMainSearch(mapping, form, request, response);
        }
        return preview(mapping, form, request, response);
    }

    private String loadMasterData(Short userId, HttpServletRequest request, CustSearchActionForm form) throws Exception {
        UserDetailDto userDetails = this.centerServiceFacade.retrieveUsersDetails(userId);
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);
        SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, userDetails.getOfficeName(), request);
        if (userDetails.isLoanOfficer()) {
            return loadLoanOfficer(personnel, request);
        }
        return loadNonLoanOfficer(personnel, request, form);

    }

    private String loadLoanOfficer(PersonnelBO personnel, HttpServletRequest request) throws Exception {
        /*
         * John W - this method is called by loadMasterData. loadMasterData is called by getHomePage, loadAllBranches
         * and loadMainSearch (which in turn is called by getOfficeHomePage). I couldn't find out where in the user
         * interface these public methods were used. I didn't delete because I wasn't sure.
         */

        // see centerServiceFacade.retrieveCustomersUnderUser(loanOfficerId) for replacing below code.
        List<CustomerBO> customerList = null;

        boolean isCenterHierarchyExist = ClientRules.getCenterHierarchyExists();

        if (isCenterHierarchyExist) {
            customerList = new CustomerPersistence().getActiveCentersUnderUser(personnel);
        } else {
            customerList = new CustomerPersistence().getGroupsUnderUser(personnel);
        }
        SessionUtils.setCollectionAttribute(CustomerSearchConstants.CUSTOMERLIST, customerList, request);
        SessionUtils.setAttribute("GrpHierExists", isCenterHierarchyExist, request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDLOANOFFICER, request);

        return CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS;
    }

    private String loadNonLoanOfficer(PersonnelBO personnel, HttpServletRequest request, CustSearchActionForm form)
            throws Exception {
        if (personnel.getOffice().getOfficeLevel().equals(OfficeLevel.BRANCHOFFICE)) {
            List<PersonnelBO> personnelList = new PersonnelPersistence().getActiveLoanOfficersUnderOffice(personnel.getOffice().getOfficeId());
            SessionUtils.setCollectionAttribute(CustomerSearchConstants.LOANOFFICERSLIST, personnelList, request);
            SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);
            form.setOfficeId(personnel.getOffice().getOfficeId().toString());

            return CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS;
        }

        SessionUtils.setCollectionAttribute(CustomerSearchConstants.OFFICESLIST, new OfficePersistence().getActiveBranchesUnderUser(personnel.getOfficeSearchId()), request);
        SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD, CustomerSearchConstants.LOADFORWARDNONBRANCHOFFICE, request);

        return CustomerSearchConstants.LOADFORWARDOFFICE_SUCCESS;
    }

    @Override
    @TransactionDemarcate(joinToken = true)
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.search(mapping, form, request, response);

        CustSearchActionForm actionForm = (CustSearchActionForm) form;
        UserContext userContext = getUserContext(request);

        String searchString = actionForm.getSearchString();
        if (searchString == null) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String officeName = this.centerServiceFacade.retrieveOfficeName(userContext.getBranchId());
        addSeachValues(searchString, userContext.getBranchId().toString(), officeName, request);
        searchString = SearchUtils.normalizeSearchString(searchString);

        if (StringUtils.isBlank(searchString)) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        if (actionForm.getInput() != null && actionForm.getInput().equals("loan")) {
            QueryResult groupClients = new CustomerPersistence().searchGroupClient(searchString, userContext.getId());
            SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, groupClients, request);
        } else if (actionForm.getInput() != null && actionForm.getInput().equals("savings")) {
            QueryResult customerForSavings = new CustomerPersistence().searchCustForSavings(searchString, userContext.getId());
            SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, customerForSavings, request);
        }
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        return actionForward;

    }
}