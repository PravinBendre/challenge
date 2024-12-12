package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.InvalidAmountException;
import com.dws.challenge.exception.LockException;
import com.dws.challenge.service.AccountTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class AccountsTransferServiceConcurrentTest {

    public static final BigDecimal ACC_A_BALANCE = new BigDecimal("1000.00");
    public static final BigDecimal ACC_B_BALANCE = new BigDecimal("500.00");
    private AccountTransferService transferService;
    private Account accountA;
    private Account accountB;

    @BeforeEach
    public void setUp() {
        // Set up the AccountTransferService and Accounts before each test
        transferService = new AccountTransferService();
        accountA = new Account("A", ACC_A_BALANCE);
        accountB = new Account("B", ACC_B_BALANCE);
    }

    @RepeatedTest(3)
    public void testTransferWithRepeatedRuns() throws InterruptedException {
        final int numThreads = 8;

        var latch = new CountDownLatch(numThreads);
        var executor = Executors.newFixedThreadPool(numThreads);
        var transferAmount = new BigDecimal("100.00");

        // Submit multiple transfer tasks to the executor (similar to testConcurrentTransfers)
        IntStream.range(0, numThreads).forEach(i -> executor.submit(() -> {
            try {
                // Perform a transfer of 100 from accountA to accountB
                transferService.transfer(accountA, accountB, transferAmount);
            } catch (InsufficientFundsException | InvalidAmountException | LockException e) {
                fail("Transfer failed with exception: " + e.getMessage());
            } finally {
                latch.countDown(); // Decrease the latch count to signal task completion
            }
        }));

        // Wait for all threads to finish their tasks
        latch.await();
        executor.shutdown();

        // Assert that accountA balance is reduced by the transfer amount * numThreads
        //expected result accountA's final balance: 200 (after 8 transfers of 100 each).
        assertEquals(ACC_A_BALANCE.subtract(transferAmount.multiply(new BigDecimal(numThreads))), accountA.getBalance());

        // Assert that accountB balance is increased by the transfer amount * numThreads
        // expected result accountB's final balance: 1300 (after receiving 8 transfers of 100 each).
        assertEquals(ACC_B_BALANCE.add(transferAmount.multiply(new BigDecimal(numThreads))), accountB.getBalance());
    }
}
