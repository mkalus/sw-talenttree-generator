package de.beimax.talenttree;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Default generator for generic pages
 */
public class PageGeneratorSimple extends AbstractPageGenerator {
    @Override
    public void generate() throws Exception {
        // add header and info paragraphs
        addLegend();
        addHeader();
        addDescriptiveText();

        // add talent paths
        addTalentPaths();
        // add talents
        addTalents();

        // write footer
        addFooter();
    }

    /**
     * Create basic header
     * @throws Exception
     */
    protected void addHeader() throws Exception {
        Font fontHeader = new Font(generator.getFontBold(), 18);
        document.add(new Paragraph(getMappedLocalizedString("header") + ": " + getMappedLocalizedString("subheader"), fontHeader));
    }

    /**
     * add general descriptive text
     * @throws Exception
     */
    protected void addDescriptiveText() throws Exception {
        Object key = data.get("descriptiveText");
        // generic description
        if (key != null) {
            Paragraph p = new Paragraph(parseTextProperty((String) key, 10f, false));
            p.setSpacingBefore(5f); // add some space before to make it look nicer
            document.add(p);
        }
    }

    /**
     * Add help/legend
     * @throws Exception
     */
    protected void addLegend() throws Exception {
        canvas.saveState();

        // draw arrows
        float x = getRightX();
        float y = getTopY() - 20;

        drawLegendArrow(PDFGenerator.activeColor, x, y);
        drawLegendArrow(PDFGenerator.passiveColor, x, y - 15);

        // draw ranked
        drawRanked(PDFGenerator.passiveColor, x, y - 30);

        canvas.restoreState();

        // draw legend text
        canvas.beginText();
        canvas.setFontAndSize(generator.getFontRegular(), 10f);
        canvas.setColorFill(BaseColor.BLACK);
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("Active"), x - 20, y - 7, 0);
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("Passive"), x - 20, y - 22, 0);
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("Ranked"), x - 20, y - 37, 0);
        canvas.endText();
    }

    /**
     * draw a legend arrow - offset to the right
     * @param color
     * @param x
     * @param y
     */
    protected void drawLegendArrow(BaseColor color, float x, float y) {
        canvas.setColorFill(color);

        canvas.moveTo(x - 4, y);
        canvas.lineTo(x, y - 4);
        canvas.lineTo(x - 4, y - 8);
        canvas.lineTo(x - 16, y - 8);
        canvas.lineTo(x - 16, y);
        canvas.fill();
    }

    /**
     * draw normal shape - offset to the right
     * @param color
     * @param x
     * @param y
     */
    protected void drawNormal(BaseColor color, float x, float y) {
        float offset = - 15.766f;
        canvas.setColorFill(color);

        // rhombus/diamond
        canvas.moveTo(x + 9.883f + offset, y);
        canvas.lineTo(x + 15.766f + offset, y - 5.883f);
        canvas.lineTo(x + 9.883f + offset, y - 11.765f);
        canvas.lineTo(x + 4 + offset, y - 5.883f);
        canvas.fill();
    }

    /**
     * draw ranked shape - offset to the right
     * @param color
     * @param x
     * @param y
     */
    protected void drawRanked(BaseColor color, float x, float y) {
        float offset = - 15.766f;
        canvas.setColorFill(color);

        // wedge
        canvas.moveTo(x + 5.883f + offset, y);
        canvas.lineTo(x + 6.883f + offset, y - 1);
        canvas.lineTo(x + 2 + offset, y - 5.883f);
        canvas.lineTo(x + 6.883f + offset, y - 10.765f);
        canvas.lineTo(x + 5.883f + offset, y - 11.765f);
        canvas.lineTo(x + offset, y - 5.883f);
        canvas.fill();

        // rhombus/diamond
        canvas.moveTo(x + 9.883f + offset, y);
        canvas.lineTo(x + 15.766f + offset, y - 5.883f);
        canvas.lineTo(x + 9.883f + offset, y - 11.765f);
        canvas.lineTo(x + 4 + offset, y - 5.883f);
        canvas.fill();
    }

    /**
     * Add talent paths
     * @throws Exception
     */
    protected void addTalentPaths() throws Exception {
        canvas.saveState();
        canvas.setColorStroke(PDFGenerator.lineColor);
        canvas.setLineWidth(PDFGenerator.talentPathStroke);
        try {
            int row = 0;
            for (Object oRow : (ArrayList) data.get("talent_paths")) {
                int col = 0;
                for (int path : (ArrayList<Integer>) oRow) {
                    addTalentPath(row, col, path);
                    col++;
                }
                row++;
            }
        } catch (Exception e) {
            throw new Exception("Error while creating talent paths in " + data.get("id") + ": " + e.getMessage());
        }
        canvas.restoreState();
    }

    /**
     * draw paths between talents
     * @param row
     * @param col
     * @param path
     * @throws Exception
     */
    protected void addTalentPath(int row, int col, int path) throws Exception {
        // ignore non valid paths
        if (path != 1) return;

        // calculate offsets
        float x = calculateColOffset(col);
        float y = calculateRowOffset((int) Math.floor(row / 2));

        // vertical or horizontal
        if (row % 2 == 0) { // horizontal
            // add half height of box
            y -= PDFGenerator.talentBoxHeight/2 + PDFGenerator.talentBoxStroke/2;
            x += PDFGenerator.talentBoxWidth - PDFGenerator.talentBoxStroke;
            canvas.moveTo(x, y);
            canvas.lineTo(x + calculateHorizontalSpacing() + 2*PDFGenerator.talentBoxStroke, y);
            canvas.stroke();
        } else { // vertical
            // add half width of box
            x += PDFGenerator.talentBoxWidth/2 - PDFGenerator.talentPathStroke/2;
            y -= PDFGenerator.talentBoxHeight;
            // vertical
            canvas.moveTo(x, y + PDFGenerator.verticalSpacing);
            canvas.lineTo(x, y - PDFGenerator.talentBoxStroke - PDFGenerator.wedgeOffset - PDFGenerator.talentBoxStroke); //  PDFGenerator.verticalSpacing - PDFGenerator.wedgeOffset - PDFGenerator.talentBoxStroke
            canvas.stroke();
        }
    }

    /**
     * Add talent boxes
     * @throws Exception
     */
    protected void addTalents() throws Exception {
        // prepare regex patters
        Pattern multiColsPattern = Pattern.compile("\\*([1-4])");
        Pattern customCostPattern = Pattern.compile("\\|([0-9]+)");

        // add talents
        try {
            int row = 0;
            for (Object oRow : (ArrayList) data.get("talents")) {
                int col = 0;
                for (String talent : (ArrayList<String>) oRow) {
                    // parse talent string
                    int multiCols = 1;
                    int customCost = -1;
                    // first find multiple columns
                    Matcher matcher = multiColsPattern.matcher(talent);
                    if (matcher.find()) {
                        // found multi column directive
                        multiCols = new Integer(matcher.group(1));
                        talent = matcher.replaceAll("");
                    }
                    // then find custom cost
                    matcher = customCostPattern.matcher(talent);
                    if (matcher.find()) {
                        // found multi column directive
                        customCost = new Integer(matcher.group(1));
                        talent = matcher.replaceAll("");
                    }
                    addTalent(row, col, talent, multiCols, customCost);
                    col += multiCols;
                }
                row++;
            }
        } catch (Exception e) {
            throw new Exception("Error while creating talent list in " + data.get("id") + ": " + e.getMessage());
        }
    }

    /**
     * Add single talent in box
     * @param row row to print talent in
     * @param col column to print talent in
     * @param key key for talent information
     * @param multiCols span multiple columns or 1
     * @param customCost custom cost of talent (instead of default) - 0 means box will not be printed
     */
    protected void addTalent(int row, int col, String key, int multiCols, int customCost) throws Exception {
        // get data
        HeaderProperties headerProperties = parseHeaderProperty(key);

        // define color
        BaseColor bgColor = headerProperties.active?PDFGenerator.activeColor:PDFGenerator.passiveColor;
        boolean headerTwoLine = headerProperties.title.contains("\n");

        // calculate offsets
        float x = calculateColOffset(col);
        float y = calculateRowOffset(row);

        // sanity check for multiple columns
        if (multiCols < 1) multiCols = 1;
        else if (multiCols > 4) multiCols = 4;

        // box width and height
        float talentBoxWidth = PDFGenerator.talentBoxWidth * multiCols + calculateHorizontalSpacing() * (multiCols-1);
        float talentBoxHeight = PDFGenerator.talentBoxHeight;

        // draw shapes
        canvas.saveState();
        // draw outer rectangle
        drawTalentRectangle(bgColor, x, y, talentBoxWidth, talentBoxHeight);
        // draw left footer shape
        float yFooterBoxOffset = y - talentBoxHeight + PDFGenerator.talentBoxStroke;
        if (customCost != 0) {
            drawFooterShape(bgColor, x + PDFGenerator.wedgeOffset + PDFGenerator.talentBoxStroke, y - talentBoxHeight + PDFGenerator.talentBoxStroke, 25);
        }
        // draw right footer shape
        drawFooterShape(bgColor, x + talentBoxWidth - PDFGenerator.wedgeOffset - PDFGenerator.talentBoxStroke - 50, y - talentBoxHeight + PDFGenerator.talentBoxStroke, 50);
        // draw header shape
        drawHeaderShape(bgColor, x + PDFGenerator.talentBoxStroke*2.5f, y - PDFGenerator.talentBoxStroke*2.5f, talentBoxWidth - PDFGenerator.talentBoxStroke*5, headerTwoLine, headerProperties.status);
        canvas.restoreState();

        // draw text
        canvas.beginText();
        canvas.setColorFill(BaseColor.WHITE);
        // title
        canvas.setFontAndSize(generator.getFontHeader(), 13);
        float offSetYTalentText;
        if (headerTwoLine) {
            String[] parts = headerProperties.title.toUpperCase().split("\\n");
            canvas.showTextAligned(Element.ALIGN_LEFT, parts[0], x + PDFGenerator.talentBoxStroke*3.5f, y - PDFGenerator.talentBoxStroke*2 - 14f, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, parts[1], x + PDFGenerator.talentBoxStroke*3.5f, y - PDFGenerator.talentBoxStroke*2 - 27f, 0);
            offSetYTalentText = y - PDFGenerator.talentBoxStroke*2 - 28f;
        }
        else {
            canvas.showTextAligned(Element.ALIGN_LEFT, headerProperties.title.toUpperCase(), x + PDFGenerator.talentBoxStroke*3.5f, y - PDFGenerator.talentBoxStroke*2 - 14f, 0);
            offSetYTalentText = y - PDFGenerator.talentBoxStroke*2 - 15f;
        }
        // mini text
        canvas.setFontAndSize(generator.getFontBold(), 6.25f);
        float textOffsetY = yFooterBoxOffset + 8 - 6.25f/2 + 0.5f;
        // draw page
        canvas.showTextAligned(Element.ALIGN_CENTER, headerProperties.page, x + PDFGenerator.wedgeOffset + PDFGenerator.talentBoxStroke + 25f/2, textOffsetY, 0);
        // draw costs
        if (customCost != 0) {
            if (customCost == -1) customCost = (row + 1) * 5;
            canvas.showTextAligned(Element.ALIGN_LEFT, getLocalizedString("Cost") + " " + customCost, x + talentBoxWidth - PDFGenerator.talentBoxStroke - 50, textOffsetY, 0);
        }
        canvas.endText();

        // draw talent text
        canvas.setColorFill(BaseColor.BLACK);
        PdfPTable table = getTalentCell(key, talentBoxWidth, 9f);
        // too large?
        float max = y - talentBoxHeight;
        if (table.getRowHeight(0) > offSetYTalentText - max - 2 * PDFGenerator.wedgeOffset)
            table = getTalentCell(key, talentBoxWidth, 8.5f); // create smaller cell
        if (table.getRowHeight(0) > offSetYTalentText - max - 2 * PDFGenerator.wedgeOffset)
            table = getTalentCell(key, talentBoxWidth, 7.5f); // create tiny cell
        table.writeSelectedRows(0, -1, x + PDFGenerator.talentBoxStroke*1.5f, offSetYTalentText, canvas);
    }

    /**
     * get celled talent text
     * @param key
     * @param talentBoxWidth
     * @param fontSize
     * @return
     * @throws Exception
     */
    protected PdfPTable getTalentCell(String key, float talentBoxWidth, float fontSize) throws Exception {
        // get phrase
        Phrase phrase = parseTextProperty(key, fontSize, true);

        // table text
        PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(talentBoxWidth - PDFGenerator.talentBoxStroke*3);
        table.setLockedWidth(true);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        table.completeRow();

        return table;
    }

    /**
     * Draw the talent rectangle
     * @param color
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void drawTalentRectangle(BaseColor color, float x, float y, float width, float height) {
        // settings
        canvas.setColorStroke(color);
        canvas.setLineWidth(PDFGenerator.talentBoxStroke);
        float halfStroke = PDFGenerator.talentBoxStroke;

        // draw shape
        canvas.moveTo(x + halfStroke, y - halfStroke);
        canvas.lineTo(x + width - halfStroke - PDFGenerator.wedgeOffset, y - halfStroke);
        canvas.lineTo(x + width - halfStroke, y - halfStroke - PDFGenerator.wedgeOffset);
        canvas.lineTo(x + width - halfStroke, y - height + halfStroke + PDFGenerator.wedgeOffset*2);
        canvas.lineTo(x + width - halfStroke - PDFGenerator.wedgeOffset, y - height + halfStroke + PDFGenerator.wedgeOffset);
        canvas.lineTo(x + halfStroke + PDFGenerator.wedgeOffset, y - height + halfStroke + PDFGenerator.wedgeOffset);
        canvas.lineTo(x + halfStroke, y - height + halfStroke + PDFGenerator.wedgeOffset * 2);
        canvas.closePathStroke();
    }

    /**
     * Draw a footer shape
     * @param color
     * @param x
     * @param y
     * @param width
     */
    protected void drawFooterShape(BaseColor color, float x, float y, float width) {
        // settings
        canvas.setColorStroke(color);
        canvas.setColorFill(color);
        canvas.setLineWidth(PDFGenerator.talentBoxStroke);

        // draw shape
        canvas.moveTo(x, y + PDFGenerator.wedgeOffset);
        canvas.lineTo(x + PDFGenerator.wedgeOffset, y + PDFGenerator.wedgeOffset*2);
        canvas.lineTo(x + width - PDFGenerator.wedgeOffset, y + PDFGenerator.wedgeOffset*2);
        canvas.lineTo(x + width, y + PDFGenerator.wedgeOffset);
        canvas.lineTo(x + width - PDFGenerator.wedgeOffset, y);
        canvas.lineTo(x + PDFGenerator.wedgeOffset, y);
        canvas.closePathFillStroke();
    }

    /**
     * draw header shape for talent box
     * @param color
     * @param x
     * @param y
     * @param width
     * @param headerTwoLine
     * @param status
     */
    protected void drawHeaderShape(BaseColor color, float x, float y, float width, boolean headerTwoLine, char status) {
        // settings
        canvas.setColorFill(color);

        // draw shape
        canvas.moveTo(x, y);
        canvas.lineTo(x + width - PDFGenerator.wedgeOffset, y);
        canvas.lineTo(x + width, y - PDFGenerator.wedgeOffset);
        if (headerTwoLine) {
            canvas.lineTo(x + width - PDFGenerator.wedgeOffset*2.5f, y - PDFGenerator.wedgeOffset*3.5f);
            canvas.lineTo(x, y - PDFGenerator.wedgeOffset*3.5f);
        } else {
            canvas.lineTo(x + width - PDFGenerator.wedgeOffset, y - PDFGenerator.wedgeOffset*2);
            canvas.lineTo(x, y - PDFGenerator.wedgeOffset*2);
        }
        canvas.fill();


        // draw shape to cross off when learned
        float shapeX = x + width - 2.2f;
        float shapeY = y - 2.2f;
        switch (status) {
            case 'R': // ranked
                drawRanked(BaseColor.WHITE, shapeX, shapeY);
                break;
            default:
                drawNormal(BaseColor.WHITE, shapeX, shapeY);
        }
    }

    /**
     * calculate offset x
     * @param col
     * @return
     */
    protected float calculateColOffset(int col) {
        float spacing = calculateHorizontalSpacing();
        return getLeftX() + col * (spacing + PDFGenerator.talentBoxWidth);
    }

    /**
     * calculate horizontal spacing between boxes
     * @return
     */
    protected float calculateHorizontalSpacing() {
        return (getUsablePageWidth() - 4 * PDFGenerator.talentBoxWidth) / 3;
    }

    /**
     * calculate offset y
     * @param row
     * @return
     */
    protected float calculateRowOffset(int row) {
        float spacing = PDFGenerator.verticalSpacing;
        return spacing/2 + PDFGenerator.marginVertical + (5 - row) * (PDFGenerator.talentBoxHeight + spacing);
    }
}
