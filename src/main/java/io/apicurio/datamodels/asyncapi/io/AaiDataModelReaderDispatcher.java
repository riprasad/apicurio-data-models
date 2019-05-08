/*
 * Copyright 2019 Red Hat
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

package io.apicurio.datamodels.asyncapi.io;

import io.apicurio.datamodels.asyncapi.visitors.IAaiVisitor;
import io.apicurio.datamodels.core.io.DataModelReaderDispatcher;

/**
 * Async API implementation of a data model reader dispatcher.
 * @author eric.wittmann@gmail.com
 */
public class AaiDataModelReaderDispatcher extends DataModelReaderDispatcher implements IAaiVisitor {

    /**
     * Constructor.
     * @param reader
     * @param json
     */
    public AaiDataModelReaderDispatcher(Object json, AaiDataModelReader reader) {
        super(json, reader);
    }

}
