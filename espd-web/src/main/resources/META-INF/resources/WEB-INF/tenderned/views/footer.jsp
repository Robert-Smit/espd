<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<spring:eval var="piwikEnabled" scope="page" expression='@espdConfiguration.piwikEnabled' />
<spring:eval var="piwikServer" scope="page" expression='@espdConfiguration.piwikServer' />
<spring:eval var="piwikId" scope="page" expression='@espdConfiguration.piwikId' />
<c:if test="${piwikEnabled == true}">
    <script type="text/javascript">
        var piwik = Piwik.getTracker("${piwikServer}", ${piwikId});
        piwik.enableLinkTracking(true);
        piwik.trackPageView();
    </script>
    <noscript><p><img src="${piwikServer}?idsite=${piwikId}" style="border:0;" alt="" /></p></noscript>
</c:if>
