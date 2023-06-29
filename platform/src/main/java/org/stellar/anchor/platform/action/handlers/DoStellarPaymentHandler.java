package org.stellar.anchor.platform.action.handlers;

import static org.stellar.anchor.api.sep.SepTransactionStatus.PENDING_ANCHOR;
import static org.stellar.anchor.api.sep.SepTransactionStatus.PENDING_STELLAR;
import static org.stellar.anchor.platform.action.dto.ActionMethod.DO_STELLAR_PAYMENT;

import java.util.HashSet;
import java.util.Set;
import javax.validation.Validator;
import org.springframework.stereotype.Service;
import org.stellar.anchor.api.exception.AnchorException;
import org.stellar.anchor.api.exception.BadRequestException;
import org.stellar.anchor.api.platform.PlatformTransactionData.Kind;
import org.stellar.anchor.api.sep.SepTransactionStatus;
import org.stellar.anchor.asset.AssetService;
import org.stellar.anchor.config.CustodyConfig;
import org.stellar.anchor.custody.CustodyService;
import org.stellar.anchor.platform.action.dto.ActionMethod;
import org.stellar.anchor.platform.action.dto.DoStellarPaymentRequest;
import org.stellar.anchor.platform.action.dto.RpcParamsRequest;
import org.stellar.anchor.platform.data.JdbcSep24Transaction;
import org.stellar.anchor.platform.data.JdbcSepTransaction;
import org.stellar.anchor.sep24.Sep24TransactionStore;
import org.stellar.anchor.sep31.Sep31TransactionStore;

@Service
public class DoStellarPaymentHandler extends ActionHandler<DoStellarPaymentRequest> {

  private final CustodyService custodyService;
  private final CustodyConfig custodyConfig;

  public DoStellarPaymentHandler(
      Sep24TransactionStore txn24Store,
      Sep31TransactionStore txn31Store,
      Validator validator,
      CustodyConfig custodyConfig,
      AssetService assetService,
      CustodyService custodyService) {
    super(txn24Store, txn31Store, validator, assetService);
    this.custodyService = custodyService;
    this.custodyConfig = custodyConfig;
  }

  @Override
  public void handle(RpcParamsRequest request) throws AnchorException {
    if (!custodyConfig.isCustodyIntegrationEnabled()) {
      throw new BadRequestException(
          String.format("Action[%s] requires enabled custody integration", getActionType()));
    }

    super.handle(request);
  }

  @Override
  public ActionMethod getActionType() {
    return DO_STELLAR_PAYMENT;
  }

  @Override
  protected SepTransactionStatus getNextStatus(
      JdbcSepTransaction txn, DoStellarPaymentRequest request) {
    return PENDING_STELLAR;
  }

  @Override
  protected Set<SepTransactionStatus> getSupportedStatuses(JdbcSepTransaction txn) {
    Set<SepTransactionStatus> supportedStatuses = new HashSet<>();

    switch (txn.getProtocol()) {
      case "24":
        JdbcSep24Transaction txn24 = (JdbcSep24Transaction) txn;
        switch (Kind.from(txn24.getKind())) {
          case DEPOSIT:
            if (txn24.getTransferReceivedAt() != null) {
              supportedStatuses.add(PENDING_ANCHOR);
            }
            break;
        }
        break;
    }

    return supportedStatuses;
  }

  @Override
  protected Set<String> getSupportedProtocols() {
    return Set.of("24");
  }

  @Override
  protected boolean isMessageRequired() {
    return false;
  }

  @Override
  protected void updateActionTransactionInfo(
      JdbcSepTransaction txn, DoStellarPaymentRequest request) throws AnchorException {
    custodyService.submitTransactionPayment(txn.getId(), null);
  }
}
