package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.InvalidAmountException;
import com.dws.challenge.exception.LockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AccountTransferService {

    private static final int MAX_RETRIES = 3; // Max number of retries for acquiring the lock
    private static final long RETRY_TIME = 50; // Retry time in milliseconds

    /**
     * Transfers funds from one account to another.
     * Helper method to avoid deadlock by locking accounts in a consistent order
     *
     * @param fromAccount The account from which funds are withdrawn
     * @param toAccount   The account to which funds are deposited
     * @param amount      The amount to transfer
     * @return true if transfer is successful
     * @throws InsufficientFundsException if withdrawal exceeds available balance
     * @throws InvalidAmountException     if the amount is invalid
     * @throws LockException              if locks could not be acquired within the retry limits
     */
    public boolean transfer(final Account fromAccount, final Account toAccount,
                            final BigDecimal amount)
            throws InsufficientFundsException, InvalidAmountException, LockException {

        if (amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        // Array to hold the accounts, sorted to avoid deadlock
        Account[] accounts = {fromAccount, toAccount};

        // Sort the accounts array based on accountId to avoid deadlock
        Arrays.sort(accounts, Comparator.comparing(Account::getAccountId));

        // Lock the accounts in the sorted order
        Account firstLock = accounts[0];
        Account secondLock = accounts[1];

        boolean lockedFirst = false, lockedSecond = false;
        try {
            // Try to lock both accounts with retries
            lockedFirst = firstLock.tryLockWithRetries(RETRY_TIME, TimeUnit.MILLISECONDS, MAX_RETRIES);
            lockedSecond = secondLock.tryLockWithRetries(RETRY_TIME, TimeUnit.MILLISECONDS, MAX_RETRIES);

            if (lockedFirst && lockedSecond) {
                // Proceed with the transfer if both accounts are locked
                withdrawAmountFromAccount(fromAccount, amount);
                depositAmountToAccount(toAccount, amount);
            } else {
                throw new LockException("Failed to acquire locks on both accounts after " + MAX_RETRIES + " retries.");
            }
        } catch (InterruptedException e) {
            throw new LockException("Thread interrupted while attempting to acquire locks.");
        } finally {
            if (lockedFirst) firstLock.unlock();
            if (lockedSecond) secondLock.unlock();
        }
        return true;
    }

    /**
     * Withdraws an amount from an account.
     *
     * @param account The account to withdraw from
     * @param amount  The amount to withdraw
     * @throws InsufficientFundsException if the account balance is insufficient
     * @throws InvalidAmountException     if the amount is invalid
     */
    private void withdrawAmountFromAccount(final Account account, final BigDecimal amount
    ) throws InsufficientFundsException,
            InvalidAmountException {

        if (account.getBalance().compareTo(amount) < 0) {
            String msg = String.format("Insufficient funds: attempted to withdraw %s but account balance is %s",
                    amount, account.getBalance());
            throw new InsufficientFundsException(msg);
        }

        account.setBalance(account.getBalance()
                .subtract(amount));
    }

    /**
     * Deposits an amount to an account.
     *
     * @param toAccount The account to deposit to
     * @param amount    The amount to deposit
     * @throws InvalidAmountException if the amount is invalid
     */
    private void depositAmountToAccount(final Account toAccount, final BigDecimal amount) throws InsufficientFundsException,
            InvalidAmountException {

        if (amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        toAccount.setBalance(toAccount.getBalance().add(amount));
    }
}
