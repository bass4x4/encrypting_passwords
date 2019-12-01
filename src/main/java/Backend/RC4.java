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
            S[i] = (byte) i; //инициализация ключевой последовательности числами 1...256 (65536)
            // в зависимости от выбранного пользователем резмером S-блока - 8 (16) бит
        }
        int j = 0;
        for (int i = 0; i < S.length; i++) {
            j = (j + getIntFromByte(S[i]) + key[i % key.length]) % S.length; //перемешивание ключевой последовательности при помощи заданной пользователем парольной фразы
            swap(S, i, j);
        }
    }

    private byte nextByte() {
        x = (x + 1) % S.length; //генерация последующего байта гаммы
        y = (y + getIntFromByte(S[x])) % S.length;
        swap(S, x, y);
        return S[(getIntFromByte(S[x]) + getIntFromByte(S[y])) % S.length];
    }

    public byte[] Encode(byte[] data) {
        byte[] cipher = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            cipher[i] = (byte) (data[i] ^ nextByte()); //генерация результирующей последовательности путем выполнения операции XOR между
            //ключом и данными. Для алгоритма RC4 операчия Encode полностью аналогична операции Decode, т.е. :
            // M XOR K = C; C XOR K = M
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
    } //из-за устройства JAVA приходится приводить тип byte к типу int для корректного обращения к массиву для избежания неверных индексов
}
