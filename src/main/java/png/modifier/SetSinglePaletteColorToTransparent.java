package png.modifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import png.PNGModifier;
import png.chunk.IHDR;
import png.chunk.PLTE;
import png.chunk.TRNS;

public class SetSinglePaletteColorToTransparent extends ChunkModifier {

    private final byte r;
    private final byte g;
    private final byte b;

    public SetSinglePaletteColorToTransparent(int rgb) {
        this.r = (byte) ((rgb >>> 16) & 0xFF);
        this.g = (byte) ((rgb >>>  8) & 0xFF);
        this.b = (byte) ((rgb >>>  0) & 0xFF);
    }

    public void handle(IHDR ihdr, int len, byte[] type, InputStream in, OutputStream out) throws IOException {
        if (!ihdr.isIndexed() || !Arrays.equals(PLTE.TYPE, type)) {
            PNGModifier.passChunk(len, type, in, out);
            return;
        }

        final PLTE plte = new PLTE(in, len);
        plte.writeTo(out);

        final int i = findIndex(plte.data);
        if (i > 0) {
            // Write tRNS chunk
            TRNS.createPaletteFullyTransparent(i).writeTo(out);
        }
    }

    private int findIndex(final byte[] plte) {
        for (int i = 0; i < plte.length; i += 3) {
            if ((r == plte[i + 0])
                    && (g == plte[i + 1])
                    && (b == plte[i + 2])) {
                return i / 3;
            }
        }
        return -1;
    }

}
