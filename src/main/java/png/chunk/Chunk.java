package png.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

import png.util.Bytes;

public abstract class Chunk {

    public abstract byte[] getType();

    public final byte[] data;

    public String isLengthValid(final int len) {
        return null;
    }

    public Chunk(InputStream in, final int len) throws IllegalArgumentException, IOException {
        String msg = isLengthValid(len);
        if (msg != null) {
            throw new IllegalArgumentException("Illegal chunk length!"
                    + " Type: " + getTypeStr()
                    + " Message: " + msg);
        }

        data = new byte[len + 4];
        Bytes.readFully(in, data, 0, len + 4);

        CRC32 crc32 = new CRC32();
        crc32.update(getType(), 0, 4);
        crc32.update(data, 0, len);

        long crc = Bytes.readUInt32(data, len);
        if (crc != crc32.getValue()) {
            throw new IllegalArgumentException("CRC doesn't match! Type: " + getTypeStr());
        }
    }

    protected Chunk(byte[] data) {
        this.data = new byte[data.length + 4];
        System.arraycopy(data, 0, this.data, 0, data.length);
        updateCRC();
    }

    public void updateCRC() {
        CRC32 crc32 = new CRC32();
        crc32.update(getType());
        crc32.update(data);
        long crc = crc32.getValue();
        Bytes.writeUInt32(crc, data, data.length - 4);
    }

    public void writeTo(final OutputStream out) throws IOException {
        Bytes.writeInt32(out, data.length - 4);
        out.write(getType());
        out.write(data);
    }

    private String getTypeStr() {
        return new String(getType(), StandardCharsets.US_ASCII);
    }

}
