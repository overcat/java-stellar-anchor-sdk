package org.stellar.anchor.api.rpc.action;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class NotifyOffchainFundsReceivedRequest extends RpcActionParamsRequest {

  private Instant fundsReceivedAt;
  private String externalTransactionId;
  private String amountIn;
  private String amountOut;
  private String amountFee;
}
