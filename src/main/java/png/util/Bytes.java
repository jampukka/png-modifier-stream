package png.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Bytes {

    public static final byte FULL = (byte) 0xFF;

    public static final int readInt32(final InputStream in) throws IOException {
        final int a = in.read();
        final int b = in.read();
        final int c = in.read();
        final int d = in.read();
        return ((a << 24) | (b << 16) | (c << 8) | d);
    }

    public static final int readInt32(final byte[] b, int off) {
        return (  ((b[off++] & 0xFF) << 24)
                | ((b[off++] & 0xFF) << 16)
                | ((b[off++] & 0xFF) <<  8)
                | ((b[off]   & 0xFF) <<  0));
    }

    public static final long readUInt32(final byte[] b, int off) {
        return (  ((b[off++] & 0xFFL) << 24)
                | ((b[off++] & 0xFFL) << 16)
                | ((b[off++] & 0xFFL) <<  8)
                | ((b[off]   & 0xFFL) <<  0));
    }

    public static final void writeInt32(final OutputStream out, final int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>>  0) & 0xFF);
    }

    public static final void writeInt32(final int v, final byte[] buf, int off) {
        buf[off++] = (byte) ((v >>> 24) & 0xFF);
        buf[off++] = (byte) ((v >>> 16) & 0xFF);
        buf[off++] = (byte) ((v >>>  8) & 0xFF);
        buf[off]   = (byte) ((v >>>  0) & 0xFF);
    }

    public static final void writeUInt32(final long v, final byte[] buf, int off) {
        buf[off++] = (byte) ((v >>> 24) & 0xFF);
        buf[off++] = (byte) ((v >>> 16) & 0xFF);
        buf[off++] = (byte) ((v >>>  8) & 0xFF);
        buf[off]   = (byte) ((v >>>  0) & 0xFF);
    }

    public static final void readFully(final InputStream in, final byte[] b) throws IOException {
        readFully(in, b, 0, b.length);
    }

    public static final void readFully(final InputStream in, final byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            int n = in.read(b, off, len);
            if (n < 0) {
                throw new EOFException();
            }
            off += n;
            len -= n;
        }
    }

    public static final void copy(final InputStream in, final OutputStream out) throws IOException {
        copy(in, out, new byte[8192]);
    }

    public static final void copy(final InputStream in, final OutputStream out, final byte[] b) throws IOException {
        final int len = b.length;
        int n;
        while ((n = in.read(b, 0, len)) != -1) {
            out.write(b, 0, n);
        }
    }

    public static final void copy(final InputStream in, final OutputStream out, final byte[] b, int toCopy) throws IOException {
        final int len = b.length;
        while (toCopy > 0) {
            int read = in.read(b, 0, Math.min(len, toCopy));
            if (read < 0) {
                break;
            }
            out.write(b, 0, read);
            toCopy -= read;
        }
    }

}
