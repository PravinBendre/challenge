package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.InvalidAmountException;
import com.dws.challenge.exception.LockException;
import com.dws.challenge.service.AccountTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountTransferServiceTest {

    private static final int MAX_RETRIES = 3; // Max number of retries for acquiring the lock
    private static final long RETRY_TIME = 50; // Retry time in milliseconds

    @Mock
    private Account fromAccount;

    @Mock
    private Account toAccount;

    @InjectMocks
    private AccountTransferService accountTransferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    // invalid amount to transfer (negative or zero)
    @Test
    public void testTransferInvalidAmount_ThrowsInvalidAmountException() {

        var invalidAmount = BigDecimal.ZERO;


        var thrown = assertThrows(InvalidAmountException.class, () -> {
            accountTransferService.transfer(fromAccount, toAccount, invalidAmount);
        });
        assertEquals("Amount must be greater than zero", thrown.getMessage());
    }

    // insufficient funds in the source account
    @Test
    public void testTransferInsufficientFunds_ThrowsInsufficientFundsException() throws Exception {

        var transferAmount = BigDecimal.valueOf(100);
        var fromAccountBalance = BigDecimal.valueOf(50);
        when(fromAccount.getBalance()).thenReturn(fromAccountBalance);
        when(fromAccount.getAccountId()).thenReturn("A1");
        when(toAccount.getAccountId()).thenReturn("A2");
        when(fromAccount.tryLockWithRetries(anyLong(), any(), anyInt())).thenReturn(true);
        when(toAccount.tryLockWithRetries(anyLong(), any(), anyInt())).thenReturn(true);


        var thrown = assertThrows(InsufficientFundsException.class, () -> {
            accountTransferService.transfer(fromAccount, toAccount, transferAmount);
        });
        assertTrue(thrown.getMessage().contains("Insufficient funds"));
    }

    // failed lock acquisition after retrying
    @Test
    public void testTransferLockAcquisitionFails_ThrowsLockException() throws Exception {

        var transferAmount = BigDecimal.valueOf(100);
        when(fromAccount.getAccountId()).thenReturn("A1");
        when(toAccount.getAccountId()).thenReturn("A2");
        when(fromAccount.tryLockWithRetries(anyLong(), any(), anyInt())).thenReturn(false);
        when(toAccount.tryLockWithRetries(anyLong(), any(), anyInt())).thenReturn(false);

        var thrown = assertThrows(LockException.class, () -> {
            accountTransferService.transfer(fromAccount, toAccount, transferAmount);
        });
        assertEquals("Failed to acquire locks on both accounts after 3 retries.", thrown.getMessage());
    }

    // successful transfer
    @Test
    public void testTransferSuccessfulTransfer_ReturnsTrue() throws Exception {

        var transferAmount = BigDecimal.valueOf(50);
        var fromAccountBalance = BigDecimal.valueOf(100);
        var toAccountBalance = BigDecimal.valueOf(50);

        when(fromAccount.getBalance()).thenReturn(fromAccountBalance);
        when(toAccount.getBalance()).thenReturn(toAccountBalance);
        when(fromAccount.getAccountId()).thenReturn("A1");
        when(toAccount.getAccountId()).thenReturn("A2");
        when(fromAccount.tryLockWithRetries(anyLong(), any(), anyInt())).thenReturn(true);
        when(toAccount.tryLockWithRetries(anyLong(), any(), anyInt())).thenReturn(true);

        var result = accountTransferService.transfer(fromAccount, toAccount, transferAmount);

        assertTrue(result);
        verify(fromAccount).setBalance(BigDecimal.valueOf(50));  // 100 - 50
        verify(toAccount).setBalance(BigDecimal.valueOf(100));  // 50 + 50
    }

    // when deposit amount is invalid (non-positive)
    @Test
    public void testTransferDepositInvalidAmount_ThrowsInvalidAmountException() throws Exception {
        var invalidAmount = BigDecimal.ZERO;

        var thrown = assertThrows(InvalidAmountException.class, () -> {
            accountTransferService.transfer(fromAccount, toAccount, invalidAmount);
        });
        assertEquals("Amount must be greater than zero", thrown.getMessage());
    }

    // InterruptedException during lock acquisition
    @Test
    public void testTransferInterruptedException_ThrowsLockException() throws Exception {

        var transferAmount = BigDecimal.valueOf(100);
        when(fromAccount.getAccountId()).thenReturn("A1");
        when(toAccount.getAccountId()).thenReturn("A2");
        when(fromAccount.tryLockWithRetries(anyLong(), any(), anyInt())).thenThrow(InterruptedException.class);

        LockException thrown = assertThrows(LockException.class, () -> {
            accountTransferService.transfer(fromAccount, toAccount, transferAmount);
        });
        assertEquals("Thread interrupted while attempting to acquire locks.", thrown.getMessage());
    }
}
