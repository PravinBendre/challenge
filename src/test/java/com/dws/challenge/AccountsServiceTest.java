package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InvalidAccountException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {
    @Autowired
    private AccountsService accountsService;
    private final String Account11 = "Id-130";
    private final String Account12 = "Id-131";

    private NotificationService notificationService;

    @Test
    void addAccount() {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    void addAccount_failsOnDuplicateId() {
        String uniqueId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueId);
        this.accountsService.createAccount(account);

        try {
            this.accountsService.createAccount(account);
            fail("Should have failed when adding duplicate account");
        } catch (DuplicateAccountIdException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
        }
    }

    @Test
    public void transferAmountFromAcc1ToAcc2_thenFromAcc1BalanceShouldBeDebitedAndToAcc2BalanceCredited() throws             InvalidAccountException {
      this.accountsService.createAccount(new Account(Account11,
                new BigDecimal("8000")));
        this.accountsService.createAccount(new Account(Account12,
                new BigDecimal("16000")));
        accountsService.transferAmount(Account11, Account12,
                new BigDecimal("500"));
        assertTrue(accountsService.getAccount(Account11).getBalance()
                .equals(new BigDecimal("7500")));
        assertTrue(accountsService.getAccount(Account12).getBalance()
                .equals(new BigDecimal("16500")));

    }


}
