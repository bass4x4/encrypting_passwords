package Backend;

public class RC4 {
    private final byte[] S;

    private int x = 0;
    private int y = 0;

    public RC4(byte[] key, boolean useEightBitMode) {
        int sBlockSize = useEightBitMode ? 256 : 65536;
        this.S = new byte[sBlockSize];
        init(key);
    }

    private void init(byte[] key) {
        for (int i = 0; i < S.length; i++) {
            S[i] = (byte) i;
        }
        int j = 0;
        for (int i = 0; i < S.length; i++) {
            j = (j + getIntFromByte(S[i]) + key[i % key.length]) % S.length;
            swap(S, i, j);
        }
    }

    private byte keyItem() {
        x = (x + 1) % S.length;
        y = (y + getIntFromByte(S[x])) % S.length;
        swap(S, x, y);
        return S[(getIntFromByte(S[x]) + getIntFromByte(S[y])) % S.length];
    }

    public byte[] Encode(byte[] data) {
        byte[] cipher = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            cipher[i] = (byte) (data[i] ^ keyItem());
        }
        return cipher;
    }

    private void swap(byte[] S, int i, int j) {
        byte c = S[i];
        S[i] = S[j];
        S[j] = c;
    }

    private int getIntFromByte(byte b) {
        return b & 0xff;
    }
}
