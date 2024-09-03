import java.util.Arrays;
import java.util.List;

public class Md4 {
    private static final int BLOCK_LENGTH = 64;

    private long count;
    private final int[] context = new int[4];
    private final int[] extra = new int[16];
    private final byte[] buffer = new byte[BLOCK_LENGTH];

    public Md4() {
        engineReset();
    }

    public synchronized byte[] digest(byte[] content) {
        engineUpdate(content, content.length);

        final int bufferIndex = (int) (count % BLOCK_LENGTH);
        final int paddingLength = (bufferIndex < 56) ? 56 - bufferIndex : 120 - bufferIndex;

        byte[] tail = new byte[paddingLength + 8];
        tail[0] = (byte) 0x80;

        for (int i = 0; i < 8; i++) {
            tail[paddingLength + i] = (byte) ((count * 8) >>> (8 * i));
        }

        engineUpdate(tail, tail.length);

        byte[] result = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i * 4 + j] = (byte) (context[i] >>> (8 * j));
            }
        }

        engineReset();
        return result;
    }

    private void engineUpdate(byte[] messageBytes, int messageLength) {
        if (messageLength < 0 || (long) messageLength > (long) messageBytes.length) {
            throw new ArrayIndexOutOfBoundsException("Incorrect arguments for method engineUpdate");
        }

        int bufferIndex = (int) (count % BLOCK_LENGTH);
        count += messageLength;
        final int partialLength = BLOCK_LENGTH - bufferIndex;
        int i = 0;

        if (messageLength >= partialLength) {
            System.arraycopy(messageBytes, 0, buffer, bufferIndex, partialLength);
            transform(buffer, 0);
            i = partialLength;
            while (i + BLOCK_LENGTH - 1 < messageLength) {
                transform(messageBytes, i);
                i += BLOCK_LENGTH;
            }
            bufferIndex = 0;
        }

        if (i < messageLength) {
            System.arraycopy(messageBytes, i, buffer, bufferIndex, messageLength - i);
        }
    }

    private void transform(byte[] buffer, int offset) {
        for (int i = 0; i < 16; i++) {
            extra[i] = ((buffer[offset++] & 0xff)) |
                       ((buffer[offset++] & 0xff) << 8) |
                       ((buffer[offset++] & 0xff) << 16) |
                       ((buffer[offset++] & 0xff) << 24);
        }

        int a = context[0];
        int b = context[1];
        int c = context[2];
        int d = context[3];

        for (int i : List.of(0, 4, 8, 12)) {
            a = ff(a, b, c, d, extra[i], 3);
            d = ff(d, a, b, c, extra[i + 1], 7);
            c = ff(c, d, a, b, extra[i + 2], 11);
            b = ff(b, c, d, a, extra[i + 3], 19);
        }

        for (int i : List.of(0, 1, 2, 3)) {
            a = gg(a, b, c, d, extra[i], 3);
            d = gg(d, a, b, c, extra[i + 4], 5);
            c = gg(c, d, a, b, extra[i + 8], 9);
            b = gg(b, c, d, a, extra[i + 12], 13);
        }

        for (int i : List.of(0, 2, 1, 3)) {
            a = hh(a, b, c, d, extra[i], 3);
            d = hh(d, a, b, c, extra[i + 8], 9);
            c = hh(c, d, a, b, extra[i + 4], 11);
            b = hh(b, c, d, a, extra[i + 12], 15);
        }

        context[0] += a;
        context[1] += b;
        context[2] += c;
        context[3] += d;
    }

    private void engineReset() {
        count = 0;
        context[0] = 0x67452301;
        context[1] = 0xefcdab89;
        context[2] = 0x98badcfe;
        context[3] = 0x10325476;
        Arrays.fill(extra, 0);
        Arrays.fill(buffer, (byte) 0);
    }

    private static int rotate(int t, int s) {
        return t << s | t >>> (32 - s);
    }

    private static int ff(int a, int b, int c, int d, int x, int s) {
        return rotate(a + ((b & c) | (~b & d)) + x, s);
    }

    private static int gg(int a, int b, int c, int d, int x, int s) {
        return rotate(a + ((b & (c | d)) | (c & d)) + x + 0x5A827999, s);
    }

    private static int hh(int a, int b, int c, int d, int x, int s) {
        return rotate(a + (b ^ c ^ d) + x + 0x6ED9EBA1, s);
    }
}