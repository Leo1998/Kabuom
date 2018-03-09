package view.rendering;

import view.texture.Texture;
import view.texture.TextureRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BitmapFont {

    int lineHeight;
    int baseLine;
    int descent;
    int pages;
    Glyph[] glyphs;
    TextureRegion[] texturePages;

    class Glyph {
        int chr;
        int x, y, width, height;
        int xoffset, yoffset, xadvance;
        int[] kerning;
        int page;
        TextureRegion region;

        public int getKerning(int c) {
            if (kerning == null) return 0;
            return kerning[c];
        }

        void updateRegion(TextureRegion tex) {
            if (region == null)
                region = new TextureRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
            region.set(tex, x, y, width, height);
        }
    }

    public BitmapFont(URL fontDef, URL texture) throws IOException {
        this(fontDef, new Texture(texture));
    }

    public BitmapFont(URL fontDef, Texture texture) throws IOException {
        this(fontDef.openStream(), new TextureRegion(texture));
    }

    public BitmapFont(URL fontDef, TextureRegion texture) throws IOException {
        this(fontDef.openStream(), texture);
    }

    public BitmapFont(InputStream fontDef, TextureRegion texture) throws IOException {
        this(fontDef, new TextureRegion[]{texture});
    }

    public BitmapFont(InputStream fontDef, TextureRegion[] texturePages) throws IOException {
        this(fontDef, Charset.defaultCharset(), texturePages);
    }

    public BitmapFont(InputStream fontDef, Charset charset, TextureRegion[] texturePages) throws IOException {
        this.texturePages = texturePages;
        parseFont(fontDef, charset);
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public TextureRegion[] getTexturePages() {
        return texturePages;
    }

    public void drawText(Batch batch, CharSequence text, int x, int y) {
        drawText(batch, text, x, y, 0, text.length());
    }

    public void drawText(Batch batch, CharSequence text, int x, int y, int start, int end) {
        Glyph lastGlyph = null;
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c > glyphs.length || c < 0)
                continue;
            Glyph g = glyphs[c];
            if (g == null)
                continue;
            if (lastGlyph != null)
                x += lastGlyph.getKerning(c);
            lastGlyph = g;
            batch.draw(g.region, x + g.xoffset, y + g.yoffset, g.width, g.height);
            x += g.xadvance;
        }
    }

    public int getBaseline() {
        return baseLine;
    }

    public int getWidth(CharSequence text) {
        return getWidth(text, 0, text.length());
    }

    public int getWidth(CharSequence text, int start, int end) {
        Glyph lastGlyph = null;
        int width = 0;
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);
//TODO: make unsupported glyphs a bit cleaner...
            if (c > glyphs.length || c < 0)
                continue;
            Glyph g = glyphs[c];
            if (g == null)
                continue;
            if (lastGlyph != null)
                width += lastGlyph.getKerning(c);
            lastGlyph = g;
// width += g.width + g.xoffset;
// width += g.width + g.xoffset;
            width += g.xadvance;
        }
        return width;
    }

    private static String parse(String line, String tag) {
        tag += "=";
        int start = line.indexOf(tag);
        if (start == -1)
            return "";
        int end = line.indexOf(' ', start + tag.length());
        if (end == -1)
            end = line.length();
        return line.substring(start + tag.length(), end);
    }

    private static int parseInt(String line, String tag) throws IOException {
        try {
            return Integer.parseInt(parse(line, tag));
        } catch (NumberFormatException e) {
            throw new IOException("data for " + tag + " is corrupt/missing: " + parse(line, tag));
        }
    }

    protected void parseFont(InputStream fontFile, Charset charset) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(fontFile, charset), 512);
        String info = br.readLine();
        String common = br.readLine();
        lineHeight = parseInt(common, "lineHeight");
        baseLine = parseInt(common, "base");
        pages = parseInt(common, "pages");
//ignore file name, let user specify texture ...
        String line = "";
        ArrayList<Glyph> glyphsList = null;
        int maxCodePoint = 0;
        while (true) {
            line = br.readLine();
            if (line == null) break;
            if (line.startsWith("chars")) {
// System.out.println(line);
                int count = parseInt(line, "count");
                glyphsList = new ArrayList<Glyph>(count);
                continue;
            }
            if (line.startsWith("kernings "))
                break;
            if (!line.startsWith("char "))
                continue;
            Glyph glyph = new Glyph();
            StringTokenizer tokens = new StringTokenizer(line, " =");
            tokens.nextToken();
            tokens.nextToken();
            int ch = Integer.parseInt(tokens.nextToken());
            if (ch > Character.MAX_VALUE)
                continue;
            if (glyphsList == null) //incase some doofus deleted a line in the font def
                glyphsList = new ArrayList<Glyph>();
            glyphsList.add(glyph);
            glyph.chr = ch;
            if (ch > maxCodePoint)
                maxCodePoint = ch;
            tokens.nextToken();
            glyph.x = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            glyph.y = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            glyph.width = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            glyph.height = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            glyph.xoffset = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            glyph.yoffset = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            glyph.xadvance = Integer.parseInt(tokens.nextToken());

            tokens.nextToken();
            glyph.page = Integer.parseInt(tokens.nextToken());
            if (glyph.page > texturePages.length)
                throw new IOException("not enough texturePages supplied; glyph " + glyph.chr + " expects page index " + glyph.page);
            glyph.updateRegion(texturePages[glyph.page]);
            if (glyph.width > 0 && glyph.height > 0)
                descent = Math.min(baseLine + glyph.yoffset, descent);
        }
        glyphs = new Glyph[maxCodePoint + 1];
        for (Glyph g : glyphsList)
            glyphs[g.chr] = g;
        int kernCount = parseInt(line, "count");
        while (true) {
            line = br.readLine();
            if (line == null) break;
            if (!line.startsWith("kerning ")) break;
            StringTokenizer tokens = new StringTokenizer(line, " =");
            tokens.nextToken();
            tokens.nextToken();
            int first = Integer.parseInt(tokens.nextToken());
            tokens.nextToken();
            int second = Integer.parseInt(tokens.nextToken());
            if (first < 0 || first > Character.MAX_VALUE || second < 0 || second > Character.MAX_VALUE)
                continue;
            Glyph glyph = glyphs[first];
            tokens.nextToken();
            int offset = Integer.parseInt(tokens.nextToken());
            if (glyph.kerning == null) {
                glyph.kerning = new int[maxCodePoint + 1];
            }
            glyph.kerning[second] = offset;
        }
        try {
            fontFile.close();
            br.close();
        } catch (IOException e) {
        }
    }

    /**
     * Disposes all texture pages associated with this font.
     */
    public void dispose() {
        for (TextureRegion t : getTexturePages())
            t.getTexture().dispose();
    }

}
