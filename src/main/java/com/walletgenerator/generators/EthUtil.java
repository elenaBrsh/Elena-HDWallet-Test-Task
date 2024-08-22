package com.walletgenerator.generators;

import com.walletgenerator.model.Wallet;
import com.walletgenerator.utils.CommonUtil;
import com.walletgenerator.utils.Constants;
import org.bitcoinj.crypto.ChildNumber;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.walletgenerator.utils.Constants.DERIVATION_NUMBER;
import static com.walletgenerator.utils.Constants.ETH_PATH;
import static com.walletgenerator.utils.Constants.NUM_ADDRESSES;


public class EthUtil implements WalletUtil {

    @Override
    public List<Wallet> generateAddresses(String mnemonic) {

        byte[] seed = CommonUtil.generateSeedFromMnemonic(mnemonic);

        List<Wallet> listEth = new ArrayList<>();

            for (int i = 0; i < NUM_ADDRESSES; i++) {

            String ethPath = ETH_PATH + i + (i < DERIVATION_NUMBER ? "'" : "");

            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);

            List<ChildNumber> ethChildNumberList = CommonUtil.parsePathWithHardAndSoft(ethPath);

            Bip32ECKeyPair derivedKeypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, Arrays.stream(ethChildNumberList.toArray(new ChildNumber[0]))
                    .mapToInt(ChildNumber::getI)
                    .toArray());

            Credentials credentials = Credentials.create(derivedKeypair);

            listEth.add(buildEth(derivedKeypair,credentials, ethPath));

        }

        return listEth;

    }
    private static Wallet buildEth(Bip32ECKeyPair derivedKeypair, Credentials credentials,String path) {

        return Wallet.builder()
                .privateKey(derivedKeypair.getPrivateKey().toString(16))
                .publicKey(derivedKeypair.getPublicKey().toString(16))
                .address(credentials.getAddress())
                .path(path)
                .build();

    }

}
