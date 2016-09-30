/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.lib.generator.text;

import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.lib.generator.DataGenerator;
import com.streamsets.pipeline.lib.generator.DataGeneratorException;

import java.io.IOException;
import java.io.Writer;

public class TextCharDataGenerator implements DataGenerator {
  private final String fieldPath;
  private final boolean recordSeparatorIfNull;
  private final Writer writer;
  private boolean closed;
  private final String recordSeparator;

  public TextCharDataGenerator(Writer writer, String fieldPath, boolean recordSeparatorIfNull, String recordSeparator)
      throws IOException {
    this.writer = writer;
    this.fieldPath = fieldPath;
    this.recordSeparatorIfNull = recordSeparatorIfNull;
    this.recordSeparator = recordSeparator;
  }

  //VisibleForTesting
  String getFieldPath() {
    return fieldPath;
  }

  //VisibleForTesting
  boolean isRecordSeparatorIfNull() {
    return recordSeparatorIfNull;
  }

  @Override
  public void write(Record record) throws IOException, DataGeneratorException {
    if (closed) {
      throw new IOException("Generator has been closed");
    }
    Field field = record.get(fieldPath);
    boolean fieldWritten = false;
    if (field != null && field.getValue() != null) {
      String value;
      try {
        value = field.getValueAsString();
      } catch (Exception ex) {
        throw new DataGeneratorException(Errors.TEXT_GENERATOR_00, record.getHeader().getSourceId(), fieldPath);
      }
      writer.write(value);
      fieldWritten = true;
    }

    if ((fieldWritten || recordSeparatorIfNull) && recordSeparator != null) {
      writer.write(recordSeparator);
    }
  }

  @Override
  public void flush() throws IOException {
    if (closed) {
      throw new IOException("Generator has been closed");
    }
    writer.flush();
  }

  @Override
  public void close() throws IOException {
    closed = true;
    writer.close();
  }
}
