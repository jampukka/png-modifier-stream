package png.modifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import png.chunk.IHDR;

public abstract class ChunkModifier {

    public abstract void handle(IHDR ihdr, int len, byte[] type, InputStream in, OutputStream out) throws IOException;

}
