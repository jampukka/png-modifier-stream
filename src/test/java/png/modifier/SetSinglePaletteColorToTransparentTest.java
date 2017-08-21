package png.modifier;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import png.PNGModifier;

public class SetSinglePaletteColorToTransparentTest {

    @Test
    public void worksAsIntended() throws IOException {
        Color[] colors = new Color[] { Color.YELLOW, Color.CYAN, Color.MAGENTA };
        int w = colors.length;

        IndexColorModel icm = createIndexColorModel(colors);
        BufferedImage fg = new BufferedImage(w, 1, BufferedImage.TYPE_BYTE_INDEXED, icm);
        for (int x = 0; x < colors.length; x++) {
            fg.setRGB(x, 0, colors[x].getRGB());
        }

        byte[] png8 = writeToMemory(fg, "png");

        byte[] png8tRNS;
        BufferedImage trns;

        png8tRNS = new PNGModifier()
        .addModifier("PLTE", new SetSinglePaletteColorToTransparent(colors[0].getRGB()))
        .stream(new ByteArrayInputStream(png8));
        trns = ImageIO.read(new ByteArrayInputStream(png8tRNS));

        assertEquals(  0, new Color(trns.getRGB(0, 0), true).getAlpha());
        assertEquals(255, new Color(trns.getRGB(1, 0), true).getAlpha());
        assertEquals(255, new Color(trns.getRGB(2, 0), true).getAlpha());

        png8tRNS = new PNGModifier()
        .addModifier("PLTE", new SetSinglePaletteColorToTransparent(colors[1].getRGB()))
        .stream(new ByteArrayInputStream(png8));
        trns = ImageIO.read(new ByteArrayInputStream(png8tRNS));

        assertEquals(255, new Color(trns.getRGB(0, 0), true).getAlpha());
        assertEquals(  0, new Color(trns.getRGB(1, 0), true).getAlpha());
        assertEquals(255, new Color(trns.getRGB(2, 0), true).getAlpha());

        png8tRNS = new PNGModifier()
        .addModifier("PLTE", new SetSinglePaletteColorToTransparent(colors[2].getRGB()))
        .stream(new ByteArrayInputStream(png8));
        trns = ImageIO.read(new ByteArrayInputStream(png8tRNS));

        assertEquals(255, new Color(trns.getRGB(0, 0), true).getAlpha());
        assertEquals(255, new Color(trns.getRGB(1, 0), true).getAlpha());
        assertEquals(  0, new Color(trns.getRGB(2, 0), true).getAlpha());
    }

    private byte[] writeToMemory(BufferedImage bi, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, formatName, baos);
        return baos.toByteArray();
    }

    private IndexColorModel createIndexColorModel(Color[] colors) {
        int n = colors.length;
        byte[] r = new byte[n];
        byte[] g = new byte[n];
        byte[] b = new byte[n];
        for (int i = 0; i < colors.length; i++) {
            Color c = colors[i];
            r[i] = (byte) (c.getRed() & 0xFF);
            g[i] = (byte) (c.getBlue() & 0xFF);
            b[i] = (byte) (c.getGreen() & 0xFF);
        }
        return new IndexColorModel(8, n, r, g, b);
    }

}
