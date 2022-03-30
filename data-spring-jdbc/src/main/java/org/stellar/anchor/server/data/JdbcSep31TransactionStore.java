package org.stellar.anchor.server.data;

import lombok.NonNull;
import org.stellar.anchor.exception.SepException;
import org.stellar.anchor.model.Sep31Transaction;
import org.stellar.anchor.sep31.Sep31TransactionStore;

public class JdbcSep31TransactionStore implements Sep31TransactionStore {
  private final JdbcSep31TransactionRepo transactionRepo;

  public JdbcSep31TransactionStore(JdbcSep31TransactionRepo transactionRepo) {
    this.transactionRepo = transactionRepo;
  }

  @Override
  public Sep31Transaction newTransaction() {
    return new JdbcSep31Transaction();
  }

  @Override
  public Sep31Transaction.Refunds newRefunds() {
    return new JdbcSep31Refunds();
  }

  @Override
  public Sep31Transaction.RefundPayment newRefundPayment() {
    return new JdbcSep31RefundPayment.JdbcRefundPayment();
  }

  @Override
  public Sep31Transaction findByTransactionId(@NonNull String transactionId) throws SepException {
    return transactionRepo.findById(transactionId).orElse(null);
  }

  @Override
  public Sep31Transaction save(Sep31Transaction transaction) throws SepException {
    if (!(transaction instanceof JdbcSep31Transaction)) {
      throw new SepException(
          transaction.getClass() + "  is not a sub-type of " + JdbcSep31Transaction.class);
    }
    return transactionRepo.save((JdbcSep31Transaction) transaction);
  }
}