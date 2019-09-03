package com.github.wgx731.threading.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {

  public static final String PAY_TYPE = "PAYMENT";
  public static final String REFUND_TYPE = "REFUND";

  public static final String EMPTY_ORG_ID = "-";

  @JsonProperty(value = "transaction_id")
  private String id;

  @JsonProperty(value = "transaction_time")
  private String time;

  @JsonProperty(value = "transaction_amount")
  private BigDecimal amount;

  @JsonProperty(value = "transaction_type")
  private String type;

  @JsonProperty(value = "customer_name")
  private String customerName;

  @JsonProperty(value = "merchant_name")
  private String merchantName;

  @JsonProperty(value = "linked_transaction_id")
  private String orgId;

}
