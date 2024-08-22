package com.walletgenerator;


import com.walletgenerator.generators.impl.EthAddressGenerator;
import com.walletgenerator.generators.impl.SolAddressGenerator;
import com.walletgenerator.generators.WalletAddressGenerator;
import com.walletgenerator.model.Wallet;
import lombok.extern.slf4j.Slf4j;

import static com.walletgenerator.utils.Constants.MNEMONIC;

import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {

        WalletAddressGenerator ethUtil = new EthAddressGenerator();
        WalletAddressGenerator solUtil = new SolAddressGenerator();

        List<Wallet> ethAddresses = ethUtil.generateAddresses(MNEMONIC);
        List<Wallet> solAddresses = solUtil.generateAddresses(MNEMONIC);


        log.info("ETH Addresses:");
        ethAddresses.forEach(eth ->
                log.info("Address: {} | Path: {}", eth.getAddress(), eth.getPath())
        );

        log.info("------------------------");


        log.info("SOL Addresses:");
        solAddresses.forEach(sol ->
                log.info("Address: {} | Path: {}", sol.getAddress(), sol.getPath())
        );
    }

}
