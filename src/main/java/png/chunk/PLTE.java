package png.chunk;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PLTE extends Chunk {

    public static final byte[] TYPE = "PLTE".getBytes(StandardCharsets.US_ASCII);

    public final int numEntries;

    public PLTE(final InputStream in, final int len) throws IOException, IllegalArgumentException {
        super(in, len);
        numEntries = len / 3;
    }

    @Override
    public byte[] getType() {
        return TYPE;
    }

    @Override
    public String isLengthValid(final int len) {
        if (len % 3 != 0) {
            return "PLTE length must be a multiple of 3!";
        }
        return null;
    }

    public int getNumEntries() {
        return numEntries;
    }

}
