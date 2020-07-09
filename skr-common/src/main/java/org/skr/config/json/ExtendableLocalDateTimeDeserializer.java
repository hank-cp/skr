/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SuppressWarnings("unchecked")
public class ExtendableLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

    public static final String FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_COMPACT = "yyyyMMddHHmmss";

    public static final ExtendableLocalDateTimeDeserializer INSTANCE = new ExtendableLocalDateTimeDeserializer();

    private Map<String, DateTimeFormatter> formatters = new Hashtable<>();

    private ExtendableLocalDateTimeDeserializer() {
        super(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        registerFormatter(FORMAT_DEFAULT, DateTimeFormatter.ofPattern(FORMAT_DEFAULT));
        registerFormatter(FORMAT_TIMESTAMP, DateTimeFormatter.ofPattern(FORMAT_TIMESTAMP));
        registerFormatter(FORMAT_COMPACT, DateTimeFormatter.ofPattern(FORMAT_COMPACT));
    }

    public void registerFormatter(String name, DateTimeFormatter formatter) {
        formatters.put(name, formatter);
    }

    public void unregisterFormatter(String name) {
        formatters.remove(name);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            return super.deserialize(parser, context);
        } catch(DateTimeException | JsonMappingException ex) {
            if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
                String string = parser.getText().trim();
                for (DateTimeFormatter formatter : formatters.values()) {
                    try {
                        return LocalDateTime.parse(string, formatter);
                    } catch (DateTimeException ignored) {}
                }
            }
            throw ex;
        }
    }
}