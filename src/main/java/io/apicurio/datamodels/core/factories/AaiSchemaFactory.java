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

package io.apicurio.datamodels.core.factories;

import java.util.List;

import io.apicurio.datamodels.asyncapi.models.AaiDocument;
import io.apicurio.datamodels.asyncapi.models.AaiSchema;
import io.apicurio.datamodels.asyncapi.v2.models.Aai20Components;
import io.apicurio.datamodels.asyncapi.v2.models.Aai20Document;
import io.apicurio.datamodels.compat.JsonCompat;
import io.apicurio.datamodels.compat.NodeCompat;
import io.apicurio.datamodels.compat.RegexCompat;
import io.apicurio.datamodels.core.models.DocumentType;
import io.apicurio.datamodels.core.models.common.IDefinition;

/**
 * TODO: the functionality of this is duplicated in {@link OasSchemaFactory} - consider ways to share the logic.
 * @author laurent.broudoux@gmail.com
 */
public class AaiSchemaFactory {

   /**
    * Creates a new definition schema from a given example.  This method will analyze the example
    * object and create a new schema object that represents the example.  Note that this method
    * does not support arbitrarily complicated examples, and should be used as a starting point
    * for a schema, not a canonical one.
    * @param document
    * @param name
    * @param example
    */
   public static IDefinition createSchemaDefinitionFromExample(AaiDocument document, String name, Object example) {
      AaiSchema schema;

      if (document.getDocumentType() == DocumentType.asyncapi2) {
         Aai20Document doc = (Aai20Document) document;
         if (NodeCompat.isNullOrUndefined(doc.components)) {
            doc.components = doc.createComponents();
         }
         schema = ((Aai20Components) doc.components).createSchemaDefinition(name);
      } else {
         throw new RuntimeException("Only AsyncAPI 2 is currently supported.");
      }

      if (JsonCompat.isString(example)) {
         example = JsonCompat.parseJSON(JsonCompat.toString(example));
      }

      resolveAll(example, schema);
      schema.title = "Root Type for " + name;
      schema.description = "The root of the " + name + " type's schema.";
      return (IDefinition) schema;
   }

   private static void resolveType(Object thing, AaiSchema schema) {
      if (JsonCompat.isNumber(thing)) {
         Number value = JsonCompat.toNumber(thing);
         boolean isInteger = ("" + value).indexOf(".") == -1;
         if (isInteger) {
            schema.type = "integer";
            if (value.intValue() >= -2147483647 && value.intValue() <= 2147483647) {
               schema.format = "int32";
            } else if (value.longValue() >= -9223372036854775807l && value.longValue() <= 9223372036854775807l) {
               schema.format = "int64";
            }
         } else {
            schema.type = "number";
            schema.format = "double";
         }
      } else if (JsonCompat.isBoolean(thing)) {
         schema.type = "boolean";
      } else if (JsonCompat.isArray(thing)) {
         schema.type = "array";
      } else if (JsonCompat.isObject(thing)) {
         schema.type = "object";
      } else if (JsonCompat.isString(thing)) {
         String value = JsonCompat.toString(thing);
         schema.type = "string";
         if (RegexCompat.matches(value, "^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$")) {
            schema.format = "date";
         } else if (RegexCompat.matches(value, "^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])(\\D?([01]\\d|2[0-3])\\D?([0-5]\\d)\\D?([0-5]\\d)?\\D?(\\d{3})?([zZ]|([\\+-])([01]\\d|2[0-3])\\D?([0-5]\\d)?)?)?$")) {
            schema.format = "date-time";
         }
      } else if (JsonCompat.isNull(thing)) {
         schema.type = "string";
      }
   }
   
   private static void resolveAll(Object object, AaiSchema schema) {
      resolveType(object, schema);
      if (NodeCompat.equals(schema.type, "array")) {
         schema.items = schema.createItemsSchema();
         List<Object> list = JsonCompat.toList(object);
         if (list.size() > 0) {
            resolveAll(list.get(0), (AaiSchema) schema.items);
         }
      } else if (NodeCompat.equals(schema.type, "object")) {
         schema.type = "object";
         List<String> keys = JsonCompat.keys(object);
         for (String propName : keys) {
            AaiSchema pschema = (AaiSchema) schema.createPropertySchema(propName);
            schema.addProperty(propName, pschema);
            Object propValue = JsonCompat.getProperty(object, propName);
            resolveAll(propValue, pschema);
         }
      }
   }

}
