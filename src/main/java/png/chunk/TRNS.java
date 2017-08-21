package png.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import png.util.Bytes;

public class TRNS extends Chunk {

    public static final byte[] TYPE = "tRNS".getBytes(StandardCharsets.US_ASCII);

    public TRNS(InputStream in, final int len) throws IOException, IllegalArgumentException {
        super(in, len);
    }

    private TRNS(byte[] data) {
        super(data);
    }

    public static TRNS createPaletteFullyTransparent(int i) {
        if (i < 0 && i > 255) {
            throw new IllegalArgumentException();
        }
        byte[] b = new byte[i + 1];
        Arrays.fill(b, Bytes.FULL);
        b[i] = 0;
        return new TRNS(b);
    }

    public static TRNS createAlphaPalette(final byte[] alphaValues) {
        if (alphaValues.length < 1 || alphaValues.length > 256) {
            throw new IllegalArgumentException();
        }
        // Try to minimize the alpha table
        int i = alphaValues.length - 1;
        for (; i >= 0; i--) {
            if (alphaValues[i] == Bytes.FULL) {
                break;
            }
        }
        return new TRNS(Arrays.copyOfRange(alphaValues, 0, i + 1));
    }

    @Override
    public byte[] getType() {
        return TYPE;
    }

}
