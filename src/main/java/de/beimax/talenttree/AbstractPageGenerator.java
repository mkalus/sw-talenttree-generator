package de.beimax.talenttree;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.util.Map;

/**
 * PDF Generator for Star Wars Talent sheets
 * (c) 2014 Maximilian Kalus [max_at_beimax_dot_de]
 *
 * This file is part of SWTalentTreeGenerator.
 *
 * SWTalentTreeGenerator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SWTalentTreeGenerator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SWTalentTreeGenerator.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Abstract class for single page generators
 */
public abstract class AbstractPageGenerator implements Comparable<AbstractPageGenerator> {
    /**
     * Current document
     */
    protected Document document;

    /**
     * Current writer
     */
    protected PdfWriter writer;

    /**
     * output canvas of current page
     */
    protected PdfContentByte canvas;

    /**
     * data of current document
     */
    protected Map data;

    /**
     * pointer to generator
     */
    protected PDFGenerator generator;

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setWriter(PdfWriter writer) {
        this.writer = writer;
    }

    public void setCanvas(PdfContentByte canvas) {
        this.canvas = canvas;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public void setGenerator(PDFGenerator generator) {
        this.generator = generator;
    }

    /**
     * Generate single data page
     * @throws Exception
     */
    abstract public void generate() throws Exception;

    /**
     * Get localized sort key for ordering
     * @return Sort key string
     */
    abstract public String getLocalizedSortKey();

    /**
     * get data structure's id key
     * @return
     * @throws Exception
     */
    public String getId()  throws Exception {
        return getString("id");
    }

    /**
     * Get string from YAML/data
     * @param key
     * @return String
     * @throws Exception
     */
    protected String getString(String key) throws Exception {
        Object o = data.get(key);
        if (o == null) throw new Exception("Data key " + key + " is empty!");
        return (String) o;
    }

    /**
     * Get a mapped localized string
     * @param key
     * @return
     * @throws Exception
     */
    protected String getMappedLocalizedString(String key) throws Exception {
        String mapped = getString(key);
        String local = generator.getStrings().getProperty(mapped);
        if (local == null) throw new Exception("No translation for " + mapped + " in key " + key + "!");
        return local;
    }

    /**
     * Get localized string without mapping
     * @param key
     * @return
     * @throws Exception
     */
    protected String getLocalizedString(String key) throws Exception {
        String local = generator.getStrings().getProperty(key);
        if (local == null) throw new Exception("No translation for " + key + "!");
        return local;
    }

    /**
     * Parse header properties of certain talent
     * @param key
     * @return
     * @throws Exception
     */
    protected HeaderProperties parseHeaderProperty(String key) throws Exception {
        HeaderProperties headerProperties = new HeaderProperties();
        // get localized element
        String localized = getLocalizedString(key + "Data");
        String[] parts = localized.split("\\|");

        if (parts.length < 4)
            throw new Exception("Line " + localized + " not correct for key " + key + "Data.");
        headerProperties.title = parts[0];
        headerProperties.page = parts[1];
        headerProperties.active = parts[2].equalsIgnoreCase("A")?true:false;
        headerProperties.status = parts[3].charAt(0);

        return headerProperties;
    }

    /**
     * Parse a string to PDF/itext phrase to be rendered later
     * @param key language key
     * @param fontSize size of font
     * @param narrowFonts use narrow fonts?
     * @return
     * @throws Exception
     */
    protected Phrase parseTextProperty(String key, float fontSize, boolean narrowFonts) throws Exception {
        // define fonts
        Font fontRegular, fontBold;
        if (narrowFonts) {
            fontRegular = new Font(generator.getFontCondensedRegular(), fontSize);
            fontBold = new Font(generator.getFontCondensedBold(), fontSize);
        } else {
            fontRegular = new Font(generator.getFontRegular(), fontSize);
            fontBold = new Font(generator.getFontBold(), fontSize);
        }
        Font fontSymbol = new Font(generator.getFontSymbol(), fontSize);

        Phrase phrase = new Phrase();
        phrase.setLeading(fontSize * 1.2f);

        // get localized element
        String localized = getLocalizedString(key);
        for (String part : localized.split("\\|")) {
            if (part.length() == 0) continue; // make sure to not fire index out of range
            char first = part.charAt(0);
            char last = part.charAt(part.length()-1);
            switch (first) {
                case '*': // bold
                    part = part.substring(1); // remove first char
                    if (last == ' ') part = part.substring(0, part.length() - 1);
                    phrase.add(new Chunk(part, fontBold));
                    if (last == ' ') phrase.add(new Chunk(" ", fontRegular));
                    break;
                case '#': // symbol font
                    part = part.substring(1); // remove first char
                    if (last == ' ') part = part.substring(0, part.length() - 1);
                    phrase.add(new Chunk(part, fontSymbol));
                    if (last == ' ') phrase.add(new Chunk(" ", fontRegular));
                    break;
                default: // all other cases
                    phrase.add(new Chunk(part, fontRegular));
            }
        }

        return phrase;
    }

    /**
     * get page width
     * @return
     */
    protected float getPageWidth() {
        return document.getPageSize().getWidth();
    }

    /**
     * get page height
     * @return
     */
    protected float getPageHeight() {
        return document.getPageSize().getHeight();
    }

    /**
     * get useable width for text/graphics
     * @return
     */
    protected float getUsablePageWidth() {
        return getPageWidth() - 2 * PDFGenerator.marginHorizontal;
    }

    /**
     * Topmost point
     * @return
     */
    protected float getTopY() {
        return document.getPageSize().getHeight() - PDFGenerator.marginVertical;
    }

    /**
     * Leftmost point
     * @return
     */
    protected float getLeftX() {
        return PDFGenerator.marginHorizontal;
    }

    /**
     * rightmost point
     * @return
     */
    protected float getRightX() {
        return document.getPageSize().getWidth() - PDFGenerator.marginHorizontal;
    }

    /**
     * add footer information to page, copyright, etc.
     */
    protected void addFooter() throws Exception {
        // draw legend text
        canvas.beginText();
        canvas.setFontAndSize(generator.getFontRegular(), 6f);
        canvas.setColorFill(BaseColor.BLACK);
        canvas.showTextAligned(Element.ALIGN_LEFT, getLocalizedString("copyright"), getLeftX(), PDFGenerator.marginVertical, 0);
        canvas.showTextAligned(Element.ALIGN_RIGHT, "Version " + getLocalizedString("version") + " • " + getLocalizedString("date") + " • " + getLocalizedString("game") + " • " + getLocalizedString("link"), getRightX(), PDFGenerator.marginVertical, 0);
        canvas.endText();
    }

    /**
     * Data structure holding header properties of a single entry
     */
    protected class HeaderProperties {
        public String title;
        public String page;
        public boolean active;
        public char status; //ranked, normal, etc.
    }
}
