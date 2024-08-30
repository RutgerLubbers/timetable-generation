/*
 * Copyright 2015-2019 the original author or authors.
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

package org.hawaiiframework.logging.model;

import java.util.Arrays;

/**
 * This enum represents keys for data that is stored in the logging MDC.
 *
 * <p>NOTE: These logging fields should be in VFZ Elastic Common Schema 1.12.
 * See https://www.elastic.co/guide/en/ecs/1.12/index.html for the official ECS fields, use these first if they apply
 * to the field you are logging. If you cannot find a good type for your log, you may use the VFZ fields:
 *
 * <p>vz.geo_point.*
 * vz.ip.*
 * vz.bool.*
 * vz.date.*
 * vz.date_nanos.*
 * vz.double.*
 * vz.float.*
 * vz.integer.*
 * vz.keyword.*
 * vz.long.*
 * vz.short.*
 * vz.text.*
 *
 * <p>Replace * with the name of your field. Please note that you may not use a dot in this name, as this is a field
 * separator, and you may not introduce new fields. So e.g. vz.keyword.record_type is fine, vz.keyword.record.type
 * will result in the logging not being saved in Elasticsearch.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public enum KibanaLogFieldNames implements KibanaLogField {
  /**
   * The session id.
   */
  SESSION_ID("vz.keyword.session_id"),

  /**
   * The host name.
   */
  HOST_NAME("host.name"),

  /**
   * The software version.
   */
  SOFTWARE_VERSION("vz.keyword.software_version"),

  /**
   * The request id.
   */
  REQUEST_ID("vz.keyword.req_id"),
  /**
   * The request duration.
   */
  REQUEST_DURATION("vz.double.req_duration"),

  /**
   * The business transaction id.
   */
  BUSINESS_TX_ID("vz.keyword.business_tx_id"),

  /**
   * The transaction id.
   */
  TX_ID("vz.keyword.tx_id"),
  /**
   * The transaction type.
   */
  TX_TYPE("vz.keyword.tx_type"),
  /**
   * The transaction request ip address.
   */
  TX_REQUEST_IP("vz.ip.tx_request_ip"),

  /**
   * The transaction request method.
   */
  TX_REQUEST_METHOD("vz.keyword.tx_request_method"),
  /**
   * The transaction request uri.
   */
  TX_REQUEST_URI("vz.keyword.tx_request_uri"),
  /**
   * The transaction request size.
   */
  TX_REQUEST_SIZE("vz.long.tx_request_size"),

  /**
   * The transaction request headers.
   */
  TX_REQUEST_HEADERS("vz.text.tx_request_headers"),
  /**
   * The transaction request body.
   */
  TX_REQUEST_BODY("vz.text.tx_request_body"),

  /**
   * The transaction response size.
   */
  TX_RESPONSE_SIZE("vz.long.tx_response_size"),
  /**
   * The transaction response headers.
   */
  TX_RESPONSE_HEADERS("vz.text.tx_response_headers"),
  /**
   * The transaction response body.
   */
  TX_RESPONSE_BODY("vz.text.tx_response_body"),

  /**
   * The transaction duration.
   */
  TX_DURATION("vz.double.tx_duration"),
  /**
   * The transaction status.
   */
  TX_STATUS("vz.keyword.tx_status"),

  /** The HTTP status. */
  HTTP_STATUS("http.response.status_code"),

  /**
   * The call id.
   */
  CALL_ID("vz.keyword.call_id"),
  /**
   * The call type.
   */
  CALL_TYPE("vz.keyword.call_type"),

  /**
   * The call request method.
   */
  CALL_REQUEST_METHOD("vz.keyword.call_request_method"),
  /**
   * The call request uri.
   */
  CALL_REQUEST_URI("vz.keyword.call_request_uri"),
  /**
   * The call request size.
   */
  CALL_REQUEST_SIZE("vz.long.call_request_size"),

  /**
   * The call request headers.
   */
  CALL_REQUEST_HEADERS("vz.text.call_request_headers"),
  /**
   * The call request body.
   */
  CALL_REQUEST_BODY("vz.text.call_request_body"),

  /**
   * The call response size.
   */
  CALL_RESPONSE_SIZE("vz.long.call_response_size"),
  /**
   * The call response headers.
   */
  CALL_RESPONSE_HEADERS("vz.text.call_response_headers"),
  /**
   * The call response body.
   */
  CALL_RESPONSE_BODY("vz.text.call_response_body"),

  /**
   * The call duration.
   */
  CALL_DURATION("vz.double.call_duration"),
  /**
   * The call status.
   */
  CALL_STATUS("vz.keyword.call_status"),

  /**
   * The task id.
   */
  TASK_ID("vz.keyword.task_id"),

  /**
   * The username.
   */
  USER_NAME("user.name"),

  /**
   * THe log type.
   */
  LOG_TYPE("vz.keyword.log_type"),

  /**
   * The thread.
   */
  THREAD("process.thread.name"),
  /**
   * The level.
   */
  LEVEL("log.level"),
  /**
   * The timestamp.
   */
  TIMESTAMP("vz.keyword.timestamp"),
  /**
   * The log location.
   */
  LOG_LOCATION("log.origin.function"),

  /**
   * The log message.
   */
  MESSAGE("message");


  /**
   * The logging key for this MDC entry.
   */
  private final String fieldName;

  /**
   * Load the KibanaLogFieldNames object.
   */
  KibanaLogFieldNames(String fieldName) {
    this.fieldName = fieldName;
  }

  /**
   * Return the name of the logging field.
   */
  @Override
  public String getLogName() {
    return fieldName;
  }

  /**
   * Lookup method that does not throw an exception if the specified
   * key is not found.
   *
   * @param key the key to look for
   * @return the MdcKey with the given name, or null
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public static KibanaLogFieldNames fromKey(String key) {
    KibanaLogFieldNames result = null;
    if (key != null) {
      result = Arrays.stream(values()).filter(fieldName -> fieldName.matches(key)).findAny().orElse(null);
    }
    return result;
  }

}
