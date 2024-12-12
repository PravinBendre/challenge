package com.dws.challenge.common;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class PaymentUtility {

    public static void printInfo(final BigDecimal amount, final String fromAccountID,
                                 final String toAccountID, final BigDecimal fromAccountBal,
                                 final BigDecimal toAccountBal) {

        var message = "{} transferred {} from {} to {}. From Account balance: {}. To Account balance: {}";

        // Get the current thread's name for logging purposes
        var threadName = Thread.currentThread().getName();

        // Ensure that parameters are not null and log accordingly
        if (amount == null || fromAccountID == null || toAccountID == null ||
                fromAccountBal == null || toAccountBal == null) {
            log.warn("Null parameter passed to printInfo: amount={}, fromAccountID={}, toAccountID={}, " +
                    "fromAccountBal={}, toAccountBal={}", amount, fromAccountID, toAccountID, fromAccountBal, toAccountBal);
        }

        // Log the message if debug level is enabled
        if (log.isDebugEnabled()) {
            log.debug(message, threadName, amount != null ? amount : "N/A",
                    fromAccountID != null ? fromAccountID : "N/A",
                    toAccountID != null ? toAccountID : "N/A",
                    fromAccountBal != null ? fromAccountBal : "N/A",
                    toAccountBal != null ? toAccountBal : "N/A");
        }
    }
}
