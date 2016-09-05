/*
 *
 * Copyright 2016 EUROPEAN COMMISSION
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package eu.europa.ec.grow.espd.xml.request.award

import eu.europa.ec.grow.espd.domain.EspdDocument
import eu.europa.ec.grow.espd.domain.enums.criteria.OtherCriterion
import eu.europa.ec.grow.espd.xml.base.AbstractCriteriaFixture
/**
 * Created by ratoico on 5/27/16.
 */
class EconomicOperatorParticipatingProcurementProcedureRequestTest extends AbstractCriteriaFixture {

    def "03. should contain the 'Is the economic operator participating in the procurement procedure together with others?' criterion"() {
        given:
        // exists is false but award criteria should always be present
        def espd = new EspdDocument(eoParticipatingProcurementProcedure: new eu.europa.ec.grow.espd.domain.OtherCriterion(exists: false))

        when:
        def request = parseRequestXml(espd)
        def idx = getEoCriterionIndex(OtherCriterion.EO_PARTICIPATING_PROCUREMENT_PROCEDURE)

        then: "CriterionID element"
        checkCriterionId(request, idx, "ee51100f-8e3e-40c9-8f8b-57d5a15be1f2")

        then: "CriterionTypeCode element"
        checkCriterionTypeCode(request, idx, "CRITERION.OTHER.EO_DATA.TOGETHER_WITH_OTHERS")

        then: "CriterionName element"
        request.Criterion[idx].Name.text() == "EO participating in procurement procedure"

        then: "CriterionDescription element"
        request.Criterion[idx].Description.text() == "Is the economic operator participating in the procurement procedure together with others?"

        then: "check all the sub groups"
        request.Criterion[idx].RequirementGroup.size() == 1

        then: "G1"
        def g1 = request.Criterion[idx].RequirementGroup[0]
        g1.ID.text() == "d939f2c6-ba25-4dc4-889c-11d1853add19"
        g1.@pi.text() == ""
        g1.RequirementGroup.size() == 1
        g1.Requirement.size() == 1
        checkRequirement(g1.Requirement[0], "7f18c64e-ae09-4646-9400-f3666d50af51", "Your answer", "INDICATOR")

        then: "G1.1"
        def g1_1 = g1.RequirementGroup[0]
        g1_1.ID.text() == "f5663c5a-d311-4ae4-be14-1575754be5f2"
        g1_1.@pi.text() == "GROUP_FULFILLED.ON_TRUE"
        g1_1.RequirementGroup.size() == 0
        g1_1.Requirement.size() == 3
        checkRequirement(g1_1.Requirement[0], "907fd62b-02f1-452c-81a8-785bedb0c536", "a) Please indicate the role of the economic operator in the group (leader, responsible for specific tasks...):", "DESCRIPTION")
        checkRequirement(g1_1.Requirement[1], "7c267f95-a3a7-49ef-abd9-e121dcd641a9",
                "b) Please identify the other economic operators participating in the procurement procedure together:", "DESCRIPTION")
        checkRequirement(g1_1.Requirement[2], "96f38793-4469-4153-aba6-c613282cdbdc",
                "c) Where applicable, name of the participating group:", "DESCRIPTION")
    }

}