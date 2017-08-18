package png.chunk;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import png.util.Bytes;

public class IHDR extends Chunk {

    public static final byte[] TYPE = "IHDR".getBytes(StandardCharsets.US_ASCII);

    public final int w;
    public final int h;
    public final int bitDepth;
    public final int colourType;
    public final int compressionMethod;
    public final int filtermethod;
    public final int interlaceMethod;

    @Override
    public byte[] getType() {
        return TYPE;
    }

    @Override
    public String isLengthValid(final int len) {
        if (len != 13) {
            return "IHDR length must be 13!";
        }
        return null;
    }

    public IHDR(InputStream in, final int len) throws IOException, IllegalArgumentException {
        super(in, len);
        int offset = 0;
        w = Bytes.readInt32(data, offset);
        offset += 4;
        h = Bytes.readInt32(data, offset);
        offset += 4;
        bitDepth = data[offset++] & 0xFF;
        colourType = data[offset++] & 0xFF;
        compressionMethod = data[offset++] & 0xFF;
        filtermethod = data[offset++] & 0xFF;
        interlaceMethod = data[offset++] & 0xFF;
    }

    public boolean isGreyscale() {
        return (colourType & 0x01) == 0;
    }

    public boolean isIndexed() {
        return colourType == 3;
    }

    public boolean isAlpha() {
        return colourType > 3;
    }

}
