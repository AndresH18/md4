import java.util.Arrays;
import java.util.List;

public class Md4 {
    private static final int BLOCK_LENGTH = 64;
    private static final int A = 0x67452301;
    private static final int B = 0xefcdab89;
    private static final int C = 0x98badcfe;
    private static final int D = 0x10325476;

    private long count;
    private final int[] context = new int[4];
    private final int[] extra = new int[16];
    private final byte[] buffer = new byte[BLOCK_LENGTH];

    public Md4() {
        init();
    }

    public synchronized byte[] digest(byte[] content) {
        update(content, content.length);

        var bufferIndex = (int) (count % BLOCK_LENGTH);
        var paddingLength = (bufferIndex < 56) ? 56 - bufferIndex : 120 - bufferIndex;

        var tail = new byte[paddingLength + 8];
        tail[0] = (byte) 0x80;

        for (var i = 0; i < 8; i++) {
            tail[paddingLength + i] = (byte) ((count * 8) >>> (8 * i));
        }

        update(tail, tail.length);

        var result = new byte[16];
        for (var i = 0; i < 4; i++) {
            for (var j = 0; j < 4; j++) {
                result[i * 4 + j] = (byte) (context[i] >>> (8 * j));
            }
        }

        init();
        return result;
    }

    private void update(byte[] bytes, int length) {
        var bufferIndex = (int) (count % BLOCK_LENGTH);
        count += length;
        var partialLength = BLOCK_LENGTH - bufferIndex;
        var i = 0;

        if (length >= partialLength) {
            System.arraycopy(bytes, 0, buffer, bufferIndex, partialLength);
            transform(buffer, 0);
            i = partialLength;
            while (i + BLOCK_LENGTH - 1 < length) {
                transform(bytes, i);
                i += BLOCK_LENGTH;
            }
            bufferIndex = 0;
        }

        if (i < length) {
            System.arraycopy(bytes, i, buffer, bufferIndex, length - i);
        }
    }

    private void transform(byte[] buffer, int offset) {
        for (var i = 0; i < 16; i++) {
            extra[i] = (buffer[offset++] & 0xff)
                       | ((buffer[offset++] & 0xff) << 8)
                       | ((buffer[offset++] & 0xff) << 16)
                       | ((buffer[offset++] & 0xff) << 24);
        }

        var a = context[0];
        var b = context[1];
        var c = context[2];
        var d = context[3];

        for (var i : List.of(0, 4, 8, 12)) {
            a = f(a, b, c, d, extra[i], 3);
            d = f(d, a, b, c, extra[i + 1], 7);
            c = f(c, d, a, b, extra[i + 2], 11);
            b = f(b, c, d, a, extra[i + 3], 19);
        }

        for (var i : List.of(0, 1, 2, 3)) {
            a = g(a, b, c, d, extra[i], 3);
            d = g(d, a, b, c, extra[i + 4], 5);
            c = g(c, d, a, b, extra[i + 8], 9);
            b = g(b, c, d, a, extra[i + 12], 13);
        }

        for (var i : List.of(0, 2, 1, 3)) {
            a = h(a, b, c, d, extra[i], 3);
            d = h(d, a, b, c, extra[i + 8], 9);
            c = h(c, d, a, b, extra[i + 4], 11);
            b = h(b, c, d, a, extra[i + 12], 15);
        }

        context[0] += a;
        context[1] += b;
        context[2] += c;
        context[3] += d;
    }

    private void init() {
        count = 0;
        context[0] = A;
        context[1] = B;
        context[2] = C;
        context[3] = D;
        Arrays.fill(extra, 0);
        Arrays.fill(buffer, (byte) 0);
    }

    private static int rotate(int t, int s) {
        return t << s | t >>> (32 - s);
    }

    private static int f(int a, int b, int c, int d, int x, int s) {
        return rotate(a + ((b & c) | (~b & d)) + x, s);
    }

    private static int g(int a, int b, int c, int d, int x, int s) {
        return rotate(a + ((b & (c | d)) | (c & d)) + x + 0x5A827999, s);
    }

    private static int h(int a, int b, int c, int d, int x, int s) {
        return rotate(a + (b ^ c ^ d) + x + 0x6ED9EBA1, s);
    }
}