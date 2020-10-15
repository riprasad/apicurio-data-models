/*
 * Copyright 2020 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.datamodels.core.validation.rules.invalid.format;

import io.apicurio.datamodels.asyncapi.models.AaiDocument;
import io.apicurio.datamodels.compat.RegexCompat;
import io.apicurio.datamodels.core.Constants;
import io.apicurio.datamodels.core.models.Document;
import io.apicurio.datamodels.core.validation.ValidationRule;
import io.apicurio.datamodels.core.validation.ValidationRuleMetaData;

/**
 * Implements the Invalid API ID Rule
 * @author eric.wittmann@gmail.com
 */
public class AaiInvalidApiIdRule extends ValidationRule {

    public static final String RFC3986_VALID_URI = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
    
    /**
     * Constructor.
     * @param ruleInfo
     */
    public AaiInvalidApiIdRule(ValidationRuleMetaData ruleInfo) {
        super(ruleInfo);
    }
    
    /**
     * @see io.apicurio.datamodels.combined.visitors.CombinedAllNodeVisitor#visitDocument(io.apicurio.datamodels.core.models.Document)
     */
    @Override
    public void visitDocument(Document node) {
        AaiDocument doc20 = (AaiDocument) node;
        if (hasValue(doc20.id)) {
            this.reportIfInvalid(isValidId(doc20.id), node, Constants.PROP_ID, map());
        }
    }

    static boolean isValidId(String id) {
        return RegexCompat.matches(id, RFC3986_VALID_URI);
    }

}
