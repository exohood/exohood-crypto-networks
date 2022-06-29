package io.exohood.binance.blockchain.services;

import io.exohood.blockchain.WrapperBlock;
import io.exohood.blockchain.WrapperTransaction;
import io.exohood.scanner.model.NewBlockEvent;
import io.exohood.scanner.services.LastBlockPersister;
import io.exohood.scanner.services.ScannerPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BinanceScanner extends ScannerPolling {
    private final AtomicInteger counter = new AtomicInteger(0);

    public BinanceScanner(BinanceNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        if (counter.incrementAndGet() == 10) {
            log.info("{}: 10 blocks received, the last {} ({})", network.getType(), block.getNumber(), block.getHash());
            counter.set(0);
        }

        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        if (block.getTransactions() == null) {
            log.warn("{}: block {} has no transactions.", network.getType(), block.getNumber());
            return;
        }
        block.getTransactions()
                .forEach(transaction -> {
                    transaction.getInputs().forEach(input -> addressTransactions.add(input, transaction));
                    transaction.getOutputs().forEach(output -> addressTransactions.add(output.getAddress(), transaction));
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });
        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
    }
}
