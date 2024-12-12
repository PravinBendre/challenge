package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.InvalidAccountException;
import com.dws.challenge.exception.InvalidAmountException;
import com.dws.challenge.exception.LockException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.dws.challenge.common.PaymentUtility.printInfo;

@Service
@Slf4j
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;
    private final AccountTransferService accountTransferService;
    private final NotificationService notificationService;

    public AccountsService(final AccountsRepository accountsRepository, AccountTransferService accountTransferService, final NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.accountTransferService = accountTransferService;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    public boolean transferAmount(
            final String fromAccountId,
            final String toAccountId,
            final BigDecimal amount
            ) throws InsufficientFundsException,
            InvalidAccountException,
            InvalidAmountException, LockException {

        // Check for non-negative amount upfront
        if (amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
        var fromAccount = getAccountOrThrow(fromAccountId);
        var toAccount = getAccountOrThrow(toAccountId);


        var isTransferSuccessFull = accountTransferService.transfer(fromAccount, toAccount, amount);
        if (isTransferSuccessFull) {
            // Notify the user about the transfer
            notifyTransfer(fromAccount, toAccount, amount);
            // Print the updated balances for information
            printInfo(amount, fromAccount.getAccountId(),
                    toAccount.getAccountId(), fromAccount.getBalance(),
                    toAccount.getBalance());

        }
        return isTransferSuccessFull;
    }

    /**
     * Helper method to fetch an account from the repository or throw an exception if not found.
     */
    private Account getAccountOrThrow(String accountId) throws InvalidAccountException {
        Account account = accountsRepository.getAccount(accountId);
        if (account == null) {
            log.warn("Account with id {} does not exist", accountId);
            throw new InvalidAccountException(String.format("Account does not exist for id %s", accountId));
        }
        return account;
    }

    /**
     * Notify about the successful transfer
     */
    private void notifyTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        String message = String.format("Amount: %d is transferred from account: %s to account: %s",
                amount.intValue(), fromAccount.getAccountId(), toAccount.getAccountId());
        notificationService.notifyAboutTransfer(fromAccount, message);
    }

}
