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

package eu.europa.ec.grow.espd.domain.ubl;

import java.io.Serializable;

/**
 * Created by ratoico on 1/7/16 at 11:30 AM.
 */
public interface CcvCriterionType extends Serializable {

    /**
     * ESPD specific type used to build different types of criteria.
     *
     * @return
     */
    String getEspdType();

    /**
     * Code used in the criterion taxonomies.
     *
     * @return
     */
    String getCode();

}
