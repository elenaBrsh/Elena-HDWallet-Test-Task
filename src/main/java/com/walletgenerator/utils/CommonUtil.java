package com.walletgenerator.utils;

import org.bitcoinj.crypto.ChildNumber;
import org.web3j.crypto.MnemonicUtils;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    public static byte[] generateSeedFromMnemonic(String mnemonic) {

        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        return seed;
    }

    public static List<ChildNumber> parsePathWithHardAndSoft(String path) {
        String[] components = path.split("/");
        List<ChildNumber> result = new ArrayList<>();

        for (String component : components) {
            if (component.equals("m")) {
                continue;
            }
            boolean hard = component.endsWith("'");
            int index = Integer.parseInt(hard ? component.substring(0, component.length() - 1) : component);
            result.add(new ChildNumber(index, hard));
        }

        return result;
    }
}
