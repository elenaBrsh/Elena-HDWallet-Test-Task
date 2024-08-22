package com.walletgenerator.generators;

import com.walletgenerator.model.Wallet;
import com.walletgenerator.utils.CommonUtil;
import org.bitcoinj.crypto.ChildNumber;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.sol4k.PublicKey;
import java.util.List;
import java.util.ArrayList;

import static com.walletgenerator.utils.Constants.DERIVATION_NUMBER;
import static com.walletgenerator.utils.Constants.ED_25519_SEED;
import static com.walletgenerator.utils.Constants.HMAC_SHA512_ALG;
import static com.walletgenerator.utils.Constants.NUM_ADDRESSES;
import static com.walletgenerator.utils.Constants.SOL_PATH;


public class SolUtil implements WalletUtil {

    @Override
    public List<Wallet> generateAddresses(String mnemonic) throws Exception {

        byte[] seed = CommonUtil.generateSeedFromMnemonic(mnemonic);

        List<Wallet> solanaAddresses = new ArrayList<>();

        Ed25519PrivateKeyParameters masterKey = deriveMasterKey(seed);

        for (int i = 0; i < NUM_ADDRESSES; i++) {

            String solPath = SOL_PATH + i + (i < DERIVATION_NUMBER ? "'" : "");

            List<ChildNumber> pathList = CommonUtil.parsePathWithHardAndSoft(solPath);

            Ed25519PrivateKeyParameters childKey = deriveKeyFromPath(masterKey, pathList);
            Ed25519PublicKeyParameters publicKey = childKey.generatePublicKey();

            solanaAddresses.add(buildWallet(childKey, publicKey, solPath));

        }

        return solanaAddresses;

    }

    private static Ed25519PrivateKeyParameters deriveMasterKey(byte[] seed) throws Exception {

        byte[] masterKey = hmacSha512(ED_25519_SEED.getBytes(), seed);

        return new Ed25519PrivateKeyParameters(masterKey, 0);

    }

    private static Ed25519PrivateKeyParameters deriveKeyFromPath(Ed25519PrivateKeyParameters masterKey, List<ChildNumber> pathList) throws Exception {

        Ed25519PrivateKeyParameters currentKey = masterKey;

        for (ChildNumber childNumber : pathList) {
            currentKey = deriveChildKey(currentKey, childNumber.getI());
        }

        return currentKey;

    }

    private static Ed25519PrivateKeyParameters deriveChildKey(Ed25519PrivateKeyParameters parentKey, int index) throws Exception {

        byte[] indexBytes = intToByteArray(index | 0x80000000); // Hardened
        byte[] data = concatenate(parentKey.getEncoded(), indexBytes);

        byte[] childKey = hmacSha512(parentKey.getEncoded(), data);

        return new Ed25519PrivateKeyParameters(childKey, 0);

    }

    private static byte[] hmacSha512(byte[] key, byte[] data) throws Exception {

        Mac hmac = Mac.getInstance(HMAC_SHA512_ALG);
        hmac.init(new SecretKeySpec(key, HMAC_SHA512_ALG));

        return hmac.doFinal(data);

    }

    private static byte[] intToByteArray(int value) {

        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
        };

    }

    private static byte[] concatenate(byte[] a, byte[] b) {

        byte[] result = new byte[a.length + b.length];

        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);

        return result;

    }

    private static Wallet buildWallet(Ed25519PrivateKeyParameters privateKey, Ed25519PublicKeyParameters publicKey, String path) {

        String privateKeyHex = Hex.toHexString(privateKey.getEncoded());
        String publicKeyBase58 = new PublicKey(publicKey.getEncoded()).toBase58();

        return Wallet.builder()
                .privateKey(privateKeyHex)
                .publicKey(publicKeyBase58)
                .address(publicKeyBase58)
                .path(path)
                .build();
    }

}
