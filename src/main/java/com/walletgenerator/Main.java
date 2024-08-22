package com.walletgenerator;


import com.walletgenerator.generators.EthUtil;
import com.walletgenerator.generators.SolUtil;
import com.walletgenerator.generators.WalletUtil;
import com.walletgenerator.model.Wallet;
import static com.walletgenerator.utils.Constants.MNEMONIC;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        WalletUtil ethUtil = new EthUtil();
        WalletUtil solUtil = new SolUtil();

       List<Wallet> ethAddresses = ethUtil.generateAddresses(MNEMONIC);
       List<Wallet> solAddresses = solUtil.generateAddresses(MNEMONIC);


        System.out.println("ETH Addresses:");
        ethAddresses
                .forEach(eth ->
                        System.out.println("Address: " + eth.getAddress() + " | Path: " + eth.getPath())
                );

        System.out.println("------------------------");

        System.out.println("SOL Addresses:");
        solAddresses
                .forEach(sol ->
                System.out.println("Address: " + sol.getAddress() + " | Path: " + sol.getPath())
        );
    }

}