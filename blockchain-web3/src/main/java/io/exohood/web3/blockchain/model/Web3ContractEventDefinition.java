package io.exohood.web3.blockchain.model;

import io.exohood.blockchain.ContractEventDefinition;
import lombok.Getter;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Web3ContractEventDefinition extends ContractEventDefinition {
    protected final List<WrapperType<?>> types;
    protected final String signature;
    protected final Event event;

    public Web3ContractEventDefinition(String name, List<WrapperType<?>> types) {
        super(name);
        this.types = types;
        this.event = new Event(
                name,
                types
                        .stream()
                        .map(WrapperType::getTypeReference)
                        .collect(Collectors.toList())
        );
        this.signature = EventEncoder.encode(event);
    }

    public List<WrapperType<?>> getTypes() {
        return this.types;
    }

    public String getSignature() {
        return this.signature;
    }

    public Event getEvent() {
        return this.event;
    }

}
