package png;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import png.modifier.AddSingleTransparentColorToPalette;

public class SetSinglePaletteColorToTransparent {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Missing arguments!");
            return;
        }
        int offset = args[0].charAt(0) == '#' ? 1 : 0;
        int color = Integer.parseInt(args[0].substring(offset), 16);
        try (InputStream in = getInputStream(args);
                OutputStream out = getOutputStream(args)) {
            PNGModifier modifier = new PNGModifier();
            modifier.addModifier("PLTE", new AddSingleTransparentColorToPalette(color));
            modifier.stream(in, out);
        }
    }

    private static InputStream getInputStream(String[] args) throws IOException {
        try {
            URL url = new URL(args[1]);
            return new BufferedInputStream(url.openStream());
        } catch (MalformedURLException e) {
            return new BufferedInputStream(new FileInputStream(new File(args[1])));
        }
    }

    private static OutputStream getOutputStream(String[] args) throws FileNotFoundException {
        if (args.length < 3) {
            return new BufferedOutputStream(System.out);
        }
        return new BufferedOutputStream(new FileOutputStream(new File(args[2])));
    }

}
