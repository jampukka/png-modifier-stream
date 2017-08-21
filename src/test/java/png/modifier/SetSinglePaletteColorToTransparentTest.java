package png.modifier;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import png.PNGModifier;
import png.modifier.SetSinglePaletteColorToTransparent;

public class SetSinglePaletteColorToTransparentTest {

    @Test
    public void worksAsIntended() throws IOException {
        int w = 100;
        int h = 100;

        Color[] colors = new Color[] { Color.YELLOW, Color.CYAN, Color.MAGENTA };
        IndexColorModel icm = createIndexColorModel(colors);
        BufferedImage fg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, icm);
        for (int y = 0; y < h; y++) {
            int rgb = colors[y % colors.length].getRGB();
            for (int x = 0; x < w; x++) {
                fg.setRGB(x, y, rgb);
            }
        }

        byte[] png8 = writeToMemory(fg, "png");
        byte[] png8tRNS = new PNGModifier()
            .addModifier("PLTE", new SetSinglePaletteColorToTransparent(Color.CYAN.getRGB()))
            .stream(new ByteArrayInputStream(png8));

        BufferedImage trns = ImageIO.read(new ByteArrayInputStream(png8tRNS));

        BufferedImage merge = createRGBImageOfColor(w, h, Color.BLACK.getRGB());
        Graphics2D g2d = merge.createGraphics();
        g2d.drawImage(trns, new AffineTransform(), null);
        g2d.dispose();

        assertEquals("First row should be all yellow", Color.YELLOW.getRGB(), merge.getRGB(0, 0));
        assertEquals("Second row should be black - NOT cyan!", Color.BLACK.getRGB(), merge.getRGB(0, 1));
        assertEquals("Third row should be magenta", Color.MAGENTA.getRGB(), merge.getRGB(0, 2));
    }

    private byte[] writeToMemory(BufferedImage bi, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, formatName, baos);
        return baos.toByteArray();
    }

    private BufferedImage createRGBImageOfColor(final int w, final int h, final int rgb) {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                bi.setRGB(x, y, rgb);
            }
        }
        return bi;
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
