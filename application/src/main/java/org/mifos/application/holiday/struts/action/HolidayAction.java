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

package org.mifos.application.holiday.struts.action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;

public class HolidayAction extends BaseAction {

    /*
     * left in to handle AJAX call to officeHierarchy
     */
    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("holidayAction");
        security.allow("load", SecurityConstants.CAN_DEFINE_HOLIDAY);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("getHolidays", SecurityConstants.VIEW);
        security.allow("addHoliday", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("officeHierarchy", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.CAN_DEFINE_HOLIDAY);
        return security;
    }

    /*
     * for AJAX call from createHolidays.js
     */
    public ActionForward officeHierarchy(@SuppressWarnings("unused") ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(this.officeServiceFacade.headOfficeHierarchy().toJSONString());
        out.flush();
        return null;
    }
}