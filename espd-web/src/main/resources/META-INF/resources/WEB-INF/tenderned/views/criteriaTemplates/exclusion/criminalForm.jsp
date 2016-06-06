<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%--
  ~
  ~ Copyright 2016 EUROPEAN COMMISSION
  ~
  ~ Licensed under the EUPL, Version 1.1 or – as soon they
  ~ will be approved by the European Commission - subsequent
  ~ versions of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~
  ~ You may obtain a copy of the Licence at:
  ~
  ~ https://joinup.ec.europa.eu/community/eupl/og_page/eupl
  ~
  ~ Unless required by applicable law or agreed to in
  ~ writing, software distributed under the Licence is
  ~ distributed on an "AS IS" basis,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
  ~ express or implied.
  ~ See the Licence for the specific language governing
  ~ permissions and limitations under the Licence.
  ~
  --%>

<tiles:importAttribute name="field"/>

<div class="form-group">
    <label class="control-label col-md-4 small" for="${field}-field1" data-i18n="crit_date_of_conviction"><s:message
            code='crit_date_of_conviction'/></label>
    <s:message code="crit_date_of_conviction_placeholder" var="dateOfconvictionPlaceholder"/>
    <div class="col-md-8">
        <form:input path="${field}.dateOfConviction" cssClass="form-control datepicker" id="${field}-field1"
                    placeholder="${dateOfconvictionPlaceholder}"/>
    </div>
</div>
<div class="form-group">
    <label class="control-label col-md-4 small" for="${field}-field2" data-i18n="crit_reason"><s:message
            code='crit_reason'/></label>

    <div class="col-md-8">
        <s:message code="crit_reason_placeholder" var="reasonPlaceholder"/>
        <form:textarea path="${field}.reason" cssClass="form-control" id="${field}-field2"
                       placeholder="${reasonPlaceholder}"/>
    </div>
</div>
<div class="form-group">
    <label class="control-label col-md-4 small" for="${field}-field3" data-i18n="crit_who_convicted">
            ${span18n["crit_who_convicted"]}
    </label>

    <div class="col-md-8">
        <s:message code="crit_who_convicted_placeholder" var="whoConvictedPlaceholder"/>
        <form:textarea path="${field}.convicted" cssClass="form-control" id="${field}-field3"
                       placeholder="${whoConvictedPlaceholder}"/>
    </div>
</div>
<div class="form-group">
    <label class="control-label col-md-4 small" for="${field}-field4"
           data-i18n="crit_length_period_exclusion"><s:message code='crit_length_period_exclusion'/></label>
    <div class="col-md-8">
        <s:message code="crit_length_period_exclusion_placeholder" var="periodLengthPlaceholder"/>
        <form:input path="${field}.periodLength" class="form-control" id="${field}-field4"
                    placeholder="${periodLengthPlaceholder}"/>
    </div>
</div>

