<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <form:form id="espdform" role="form" class="form-horizontal" action="rest" method="post" data-toggle="validator" enctype="multipart/form-data">
        <input type="text" name="callbackURL" value="http://localhost:8085/mock/tenderned">
        <input type="text" name="accessToken" value="1111">
        <input type="text" name="lang" value="nl">
        <input type="text" name="agent" value="ca">
        <input type="text" name="tedReceptionId" value="1234">
        <input type="text" name="ojsNumber" value="13245674987">
        <input type="text" name="country" value="nl">
        <input type="text" name="name" value="Janna">
        <input type="text" name="procedureTitle" value="titel beschrijving">
        <input type="text" name="procedureShortDesc" value="1234">
        <input type="text" name="fileRefByCA" value="referenceca">
        <input type="text" name="noUpload" value="true">
        <input type="text" name="noMergeESPDs" value="true">
        <input type="file" name="attachment" path="attachment"/>
        <input type="submit" name="submit">
        <div class="form-group">
        </div>
    </form:form>
