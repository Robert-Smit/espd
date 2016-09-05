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

package eu.europa.ec.grow.espd.xml.response.award

import eu.europa.ec.grow.espd.domain.EspdDocument
import eu.europa.ec.grow.espd.domain.enums.criteria.OtherCriterion
import eu.europa.ec.grow.espd.xml.base.AbstractCriteriaFixture
/**
 * Created by ratoico on 2/29/16 at 11:53 AM.
 */
class SucontractingThirdPartiesResponseTest extends AbstractCriteriaFixture {

    def "05. should contain the 'Does the economic operator intend to subcontract any share of the contract to third parties' criterion"() {
        given:
        // exists is false but award criteria should always be present
        def espd = new EspdDocument(subcontractingThirdParties: new eu.europa.ec.grow.espd.domain.OtherCriterion(exists: false))

        when:
        def response = parseResponseXml(espd)
        def idx = getEoCriterionIndex(OtherCriterion.SUBCONTRACTING_THIRD_PARTIES)

        then: "CriterionID element"
        checkCriterionId(response, idx, "72c0c4b1-ca50-4667-9487-461f3eed4ed7")

        then: "CriterionTypeCode element"
        checkCriterionTypeCode(response, idx, "CRITERION.OTHER.EO_DATA.SUBCONTRACTS_WITH_THIRD_PARTIES")

        then: "CriterionName element"
        response.Criterion[idx].Name.text() == "Subcontracting third parties"

        then: "CriterionDescription element"
        response.Criterion[idx].Description.text() == "Does the economic operator intend to subcontract any share of the contract to third parties?"

        then: "check all the sub groups"
        response.Criterion[idx].RequirementGroup.size() == 1

        then: "G1"
        def g1 = response.Criterion[idx].RequirementGroup[0]
        g1.ID.text() == "d5fe5a71-7fd3-4910-b6f4-5cd2a4d23524"
        g1.@pi.text() == ""
        g1.RequirementGroup.size() == 1
        g1.Requirement.size() == 1
        checkRequirement(g1.Requirement[0], "7f18c64e-ae09-4646-9400-f3666d50af51", "Your answer", "INDICATOR")

        then: "G1.1"
        def g1_1 = g1.RequirementGroup[0]
        g1_1.ID.text() == "b638edf6-4f00-4e24-92c4-cf96846f2c17"
        g1_1.@pi.text() == "GROUP_FULFILLED.ON_TRUE"
        g1_1.RequirementGroup.size() == 0
        g1_1.Requirement.size() == 1
        checkRequirement(g1_1.Requirement[0], "999c7fe2-61cd-4e86-b76f-e280304dc8c9", "If yes and in so far as known, please list the proposed subcontractors:", "DESCRIPTION")
    }

    def "check the 'Indicator' requirement response"() {
        given:
        def espd = new EspdDocument(subcontractingThirdParties: new eu.europa.ec.grow.espd.domain.OtherCriterion(exists: true, answer: false))

        when:
        def response = parseResponseXml(espd)
        def idx = getEoCriterionIndex(OtherCriterion.SUBCONTRACTING_THIRD_PARTIES)

        then:
        def req = response.Criterion[idx].RequirementGroup[0].Requirement[0]
        checkRequirement(req, "7f18c64e-ae09-4646-9400-f3666d50af51", "Your answer", "INDICATOR")
        req.Response.size() == 1
        req.Response[0].Indicator.text() == "false"
    }

    def "check the 'Please describe them' requirement response"() {
        given:
        def espd = new EspdDocument(subcontractingThirdParties: new eu.europa.ec.grow.espd.domain.OtherCriterion(exists: true, description1: "descr 1"))

        when:
        def response = parseResponseXml(espd)
        def idx = getEoCriterionIndex(OtherCriterion.SUBCONTRACTING_THIRD_PARTIES)

        then:
        def g1_1 = response.Criterion[idx].RequirementGroup[0].RequirementGroup[0]
        def req = g1_1.Requirement[0]
        checkRequirement(req, "999c7fe2-61cd-4e86-b76f-e280304dc8c9", "If yes and in so far as known, please list the proposed subcontractors:", "DESCRIPTION")
        req.Response.size() == 1
        req.Response[0].Description.text() == "descr 1"
    }

}