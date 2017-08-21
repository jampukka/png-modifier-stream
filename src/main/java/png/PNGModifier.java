package png;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import png.chunk.IHDR;
import png.modifier.ChunkModifier;
import png.util.Bytes;

public class PNGModifier {

    private static final byte[] PNG_SIGNATURE = new byte[] { (byte) 137, 80, 78, 71, 13, 10, 26, 10 };
    private static final byte[] IEND = "IEND".getBytes(StandardCharsets.US_ASCII);

    private final Map<String, ChunkModifier> typeToModifier = new HashMap<>();

    public PNGModifier addModifier(String type, ChunkModifier modifier) {
        if (typeToModifier.containsKey(type)) {
            throw new IllegalArgumentException(type + " already has a defined ChunkHandler!");
        }
        typeToModifier.put(type, modifier);
        return this;
    }

    public byte[] stream(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stream(in, baos);
        return baos.toByteArray();
    }

    public void stream(InputStream in, OutputStream out) throws IOException {
        // Check signature
        byte[] signature = new byte[8];
        Bytes.readFully(in, signature);
        if (!Arrays.equals(PNG_SIGNATURE, signature)) {
            throw new IllegalArgumentException("Invalid signature!");
        }
        out.write(signature);

        // Read IHDR
        int len = Bytes.readInt32(in);
        byte[] typeBytes = new byte[4];
        Bytes.readFully(in, typeBytes);
        if (!Arrays.equals(IHDR.TYPE, typeBytes)) {
            throw new IllegalArgumentException("IHDR must be first chunk!");
        }
        final IHDR ihdr = new IHDR(in, len);
        ihdr.writeTo(out);

        while (true) {
            // Read length and type
            len = Bytes.readInt32(in);
            typeBytes = new byte[4];
            Bytes.readFully(in, typeBytes);
            String type = new String(typeBytes, StandardCharsets.US_ASCII);

            ChunkModifier handler = typeToModifier.get(type);
            if (handler != null) {
                handler.handle(ihdr, len, typeBytes, in, out);
            } else {
                passChunk(len, typeBytes, in, out);
            }

            // If type was IEND then stop
            if (Arrays.equals(IEND, typeBytes)) {
                break;
            }
        }
    }

    public static void passChunk(int len, byte[] type, InputStream in, OutputStream out) throws IOException {
        Bytes.writeInt32(out, len);
        out.write(type);
        // Copy CRC as well
        int bytesToPass = len + 4;
        Bytes.copy(in, out, new byte[Math.min(bytesToPass, 8192)], bytesToPass);
    }

}
