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

package eu.europa.ec.grow.espd.xml;

import com.google.common.collect.ImmutableMap;
import eu.europa.ec.grow.espd.domain.enums.criteria.ExclusionCriterion;

import java.util.Map;

/**
 * This class contains the method criminalListCA_NL which creates a map of the exclusions
 * specifically for NL procedures.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class CriteriaTemplatesNL {

    protected static final Map[] criminalListCA_NL = new Map[] {
            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "criminalConvictions").
                    put("title_code", "crit_eu_title_grounds_criminal_conv").
                    put("description_code", "crit_eu_text_grounds_criminal_conv").
                    put("is_always_checked", "false").
                    put("default_value", "false").
                    put("is_disabled", "false").
                    put("criterion", ExclusionCriterion.PARTICIPATION_CRIMINAL_ORGANISATION).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "corruption").
                    put("title_code", "crit_eu_title_corruption").
                    put("description_code", "crit_eu_text_corruption").
                    put("criterion", ExclusionCriterion.CORRUPTION).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "fraud").
                    put("title_code", "crit_eu_title_fraud").
                    put("description_code", "crit_eu_text_fraud").
                    put("criterion", ExclusionCriterion.FRAUD).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "terroristOffences").
                    put("title_code", "crit_eu_title_terrorist").
                    put("description_code", "crit_eu_text_terrorist").
                    put("criterion", ExclusionCriterion.TERRORIST_OFFENCES).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "moneyLaundering").
                    put("title_code", "crit_eu_title_money_laundering").
                    put("description_code", "crit_eu_text_money_laundering").
                    put("criterion", ExclusionCriterion.MONEY_LAUNDERING).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "childLabour").
                    put("title_code", "crit_eu_title_child_labour").
                    put("description_code", "crit_eu_text_child_labour").
                    put("criterion", ExclusionCriterion.CHILD_LABOUR).build()
    };

    protected static final Map[] taxesListCA_NL = new Map[] {
            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "paymentTaxes").
                    put("title_code", "crit_eu_title_payment_taxes").
                    put("description_code", "crit_eu_text_payment_taxes").
                    put("criterion", ExclusionCriterion.PAYMENT_OF_TAXES).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "paymentSocialSecurity").
                    put("title_code", "crit_eu_title_payment_social_security").
                    put("description_code", "crit_eu_text_payment_social_security").
                    put("criterion", ExclusionCriterion.PAYMENT_OF_SOCIAL_SECURITY).build()
    };

    protected static final Map[] insolvencyListCA_NL = new Map[] {
            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "breachingObligationsEnvironmental").
                    put("title_code", "crit_eu_title_breaching_obligations_environmental").
                    put("description_code", "crit_eu_text_breaching_obligations_environmental").
                    put("criterion", ExclusionCriterion.BREACHING_OF_OBLIGATIONS_ENVIRONMENTAL).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "breachingObligationsSocial").
                    put("title_code", "crit_eu_title_breaching_obligations_social").
                    put("description_code", "crit_eu_text_breaching_obligations_social").
                    put("criterion", ExclusionCriterion.BREACHING_OF_OBLIGATIONS_SOCIAL).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "breachingObligationsLabour").
                    put("title_code", "crit_eu_title_breaching_obligations_labour").
                    put("description_code", "crit_eu_text_breaching_obligations_labour").
                    put("criterion", ExclusionCriterion.BREACHING_OF_OBLIGATIONS_LABOUR).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "bankruptcy").
                    put("title_code", "crit_eu_title_bankrupt").
                    put("description_code", "crit_eu_text_bankrupt").
                    put("criterion", ExclusionCriterion.BANKRUPTCY).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "insolvency").
                    put("title_code", "crit_eu_title_insolvency").
                    put("description_code", "crit_eu_text_insolvency").
                    put("criterion", ExclusionCriterion.INSOLVENCY).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "arrangementWithCreditors").
                    put("title_code", "crit_eu_title_arrangement_creditors").
                    put("description_code", "crit_eu_text_arrangement_creditors").
                    put("criterion", ExclusionCriterion.ARRANGEMENT_WITH_CREDITORS).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "analogousSituation").
                    put("title_code", "crit_eu_title_national_bankruptcy").
                    put("description_code", "crit_eu_text_national_bankruptcy").
                    put("criterion", ExclusionCriterion.ANALOGOUS_SITUATION).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "assetsAdministeredByLiquidator").
                    put("title_code", "crit_eu_title_liquidator").
                    put("description_code", "crit_eu_text_liquidator").
                    put("criterion", ExclusionCriterion.ASSETS_ADMINISTERED_BY_LIQUIDATOR).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "businessActivitiesSuspended").
                    put("title_code", "crit_eu_title_suspended_business").
                    put("description_code", "crit_eu_text_suspended_business").
                    put("criterion", ExclusionCriterion.BUSINESS_ACTIVITIES_SUSPENDED).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "guiltyGrave").
                    put("title_code", "crit_eu_title_guilty_misconduct").
                    put("description_code", "crit_eu_text_guilty_misconduct").
                    put("criterion", ExclusionCriterion.GUILTY_OF_PROFESSIONAL_MISCONDUCT).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "agreementsWithOtherEO").
                    put("title_code", "crit_eu_title_agreement_economic").
                    put("description_code", "crit_eu_text_agreement_economic").
                    put("criterion", ExclusionCriterion.AGREEMENTS_WITH_OTHER_EO).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "conflictInterest").
                    put("title_code", "crit_eu_title_conflict_interest").
                    put("description_code", "crit_eu_text_conflict_interest").
                    put("criterion", ExclusionCriterion.CONFLICT_OF_INTEREST_EO_PROCUREMENT_PROCEDURE).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "involvementPreparationProcurement").
                    put("title_code", "crit_eu_title_involvement").
                    put("description_code", "crit_eu_text_involvement").
                    put("criterion", ExclusionCriterion.DIRECT_INVOLVEMENT_PROCUREMENT_PROCEDURE).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "earlyTermination").
                    put("title_code", "crit_eu_title_early_termination").
                    put("description_code", "crit_eu_text_early_termination").
                    put("criterion", ExclusionCriterion.EARLY_TERMINATION).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "checkTemplate").
                    put("field", "guiltyMisinterpretation").
                    put("title_code", "crit_eu_title_guilty_misinterpretation").
                    put("description_code", "crit_eu_text_guilty_misinterpretation").
                    put("criterion", ExclusionCriterion.GUILTY_OF_MISINTERPRETATION).build()
    };

    /**
     * TOP LEVEL EXCLUSION CA for NL procedures
     */
    public static final Map[] exclusionCA_NL = new Map[] {
            ImmutableMap.<String, Object>builder().
                    put("template", "euCriteriaListTemplate").
                    put("id", "ca-criminal-convictions-section").
                    put("title_code", "crit_top_title_grounds_criminal_conv").
                    put("subtitle_code", "crit_eu_main_title_grounds_criminal_conv").
                    put("criteriaList", criminalListCA_NL).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "euCriteriaListTemplate").
                    put("id", "ca-payment-of-taxes-section").
                    put("title_code", "crit_top_title_grounds_payment_taxes").
                    put("subtitle_code", "crit_eu_main_title_payment_taxes").
                    put("criteriaList", taxesListCA_NL).build(),

            ImmutableMap.<String, Object>builder().
                    put("template", "euCriteriaListTemplate").
                    put("id", "ca-insolvency-section").
                    put("title_code", "crit_top_title_insolvency_conflicts").
                    put("subtitle_code", "crit_eu_main_breaching_obligations").
                    put("criteriaList", insolvencyListCA_NL).build(),
    };


    private CriteriaTemplatesNL() {

    }
}
