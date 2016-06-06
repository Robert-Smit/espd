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
<tiles:importAttribute name="lastYears"/>

<c:forEach var="count" items="${lastYears}" varStatus="loop">
	<div class="form-group">
		<div class="col-md-4">
		    <label class="control-label col-xs-3 small">${span18n['crit_year']}</label>
		    <div class="col-xs-9" style="margin-bottom: 5px;">
		        <form:select path="${field}.year${loop.count}" cssClass="form-control" >
					<form:option value="${null}" label="---"/>
					<form:options items="${lastYears}"/>
				</form:select>
		    </div>
	    </div>
	    <div class="col-md-8">
			<label class="control-label col-xs-3 small">${span18n['crit_amount_concerned']}</label>
			<div class="col-xs-5">
			    <form:input path="${field}.amount${loop.count}" number="true" cssClass="form-control"  data-i18n="crit_amount_concerned_placeholder" placeholder="${i18n['crit_amount_concerned_placeholder']}"/>
			</div>
			<div class="col-xs-4">
	            <tiles:insertDefinition name="currencies">
	                <tiles:putAttribute name="currencyField" value="${field}.currency${loop.count}"/>
	            </tiles:insertDefinition>
		    </div>
	    </div>
	</div>
</c:forEach>
