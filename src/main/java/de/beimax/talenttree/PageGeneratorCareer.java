package de.beimax.talenttree;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

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
 * Generator for default career sheets
 */
public class PageGeneratorCareer extends AbstractPageGenerator {
    @Override
    public void generate() throws Exception {
        // add header and info paragraphs
        addLegend();
        addHeader();
        addSkillData(getLocalizedString("CareerSkills"), "skills");
        addSkillData(MessageFormat.format(getLocalizedString("BonusSkills"), getMappedLocalizedString("subheader")), "bonus_skills");

        // add talent paths
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

        // add talents
        try {
            int row = 0;
            for (Object oRow : (ArrayList) data.get("talents")) {
                int col = 0;
                for (String talent : (ArrayList<String>) oRow) {
                    addTalent(row, col, talent);
                    col++;
                }
                row++;
            }
        } catch (Exception e) {
            throw new Exception("Error while creating talent list in " + data.get("id") + ": " + e.getMessage());
        }
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
     * Add skill data
     * @param prefix translated prefix (printed bold) without :
     * @param dataKey data key in YAML file, skills/bonus_skills
     * @throws Exception
     */
    protected void addSkillData(String prefix, String dataKey) throws Exception {
        Font fontRegular = new Font(generator.getFontRegular(), 10.5f);
        Font fontBold = new Font(generator.getFontBold(), 10.5f);

        // add career skills
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(prefix + ": ", fontBold));
        ArrayList<String> skills = new ArrayList<String>();
        //noinspection unchecked
        for (String key : (Iterable<String>) data.get(dataKey))
            skills.add(getLocalizedString(key));
        // sort localized
        Collections.sort(skills, Collator.getInstance());
        // build skill list
        StringBuilder sb = new StringBuilder();
        for (String skill : skills) {
            if (sb.length() != 0) sb.append(", ");
            sb.append(skill);
        }
        phrase.add(new Chunk(sb.toString(), fontRegular));
        Paragraph p = new Paragraph(phrase);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setIndentationRight(getUsablePageWidth() - PDFGenerator.headerTextMaxWidth);
        p.setLeading(13.2f);
        document.add(p);
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
            y -= PDFGenerator.talentBoxHeight + PDFGenerator.talentBoxStroke;
            // vertical
            canvas.moveTo(x, y + PDFGenerator.verticalSpacing);
            canvas.lineTo(x, y - PDFGenerator.talentBoxStroke - PDFGenerator.wedgeOffset); //  PDFGenerator.verticalSpacing - PDFGenerator.wedgeOffset - PDFGenerator.talentBoxStroke
            canvas.stroke();
        }
    }

    /**
     * Add single talent in box
     * @param row
     * @param col
     * @param key
     */
    protected void addTalent(int row, int col, String key) throws Exception {
        // get data
        HeaderProperties headerProperties = parseHeaderProperty(key);

        // define color
        BaseColor bgColor = headerProperties.active?PDFGenerator.activeColor:PDFGenerator.passiveColor;
        boolean headerTwoLine = headerProperties.title.contains("\n");

        // calculate offsets
        float x = calculateColOffset(col);
        float y = calculateRowOffset(row);

        // draw shapes
        canvas.saveState();
        // draw outer rectangle
        drawTalentRectangle(bgColor, x, y, PDFGenerator.talentBoxWidth, PDFGenerator.talentBoxHeight);
        // draw left footer shape
        float yFooterBoxOffset = y - PDFGenerator.talentBoxHeight + PDFGenerator.talentBoxStroke;
        drawFooterShape(bgColor, x + PDFGenerator.wedgeOffset + PDFGenerator.talentBoxStroke, y - PDFGenerator.talentBoxHeight + PDFGenerator.talentBoxStroke, 25);
        // draw right footer shape
        drawFooterShape(bgColor, x + PDFGenerator.talentBoxWidth - PDFGenerator.wedgeOffset - PDFGenerator.talentBoxStroke - 50, y - PDFGenerator.talentBoxHeight + PDFGenerator.talentBoxStroke, 50);
        // draw header shape
        drawHeaderShape(bgColor, x + PDFGenerator.talentBoxStroke*2.5f, y - PDFGenerator.talentBoxStroke*2.5f, PDFGenerator.talentBoxWidth - PDFGenerator.talentBoxStroke*5, headerTwoLine, headerProperties.status);
        canvas.restoreState();

        // draw text
        canvas.beginText();
        canvas.setColorFill(BaseColor.WHITE);
        // title
        canvas.setFontAndSize(generator.getFontHeader(), 13);
        float offSetYTalentText;
        if (headerTwoLine) {
            String[] parts = headerProperties.title.split("\\n");
            canvas.showTextAligned(Element.ALIGN_LEFT, parts[0], x + PDFGenerator.talentBoxStroke*3.5f, y - PDFGenerator.talentBoxStroke*2 - 14f, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, parts[1], x + PDFGenerator.talentBoxStroke*3.5f, y - PDFGenerator.talentBoxStroke*2 - 27f, 0);
            offSetYTalentText = y - PDFGenerator.talentBoxStroke*2 - 28f;
        }
        else {
            canvas.showTextAligned(Element.ALIGN_LEFT, headerProperties.title, x + PDFGenerator.talentBoxStroke*3.5f, y - PDFGenerator.talentBoxStroke*2 - 14f, 0);
            offSetYTalentText = y - PDFGenerator.talentBoxStroke*2 - 15f;
        }
        // mini text
        canvas.setFontAndSize(generator.getFontBold(), 6.25f);
        float textOffsetY = yFooterBoxOffset + 8 - 6.25f/2 + 0.5f;
        // draw page
        canvas.showTextAligned(Element.ALIGN_CENTER, headerProperties.page, x + PDFGenerator.wedgeOffset + PDFGenerator.talentBoxStroke + 25f/2, textOffsetY, 0);
        // draw costs
        canvas.showTextAligned(Element.ALIGN_LEFT, getLocalizedString("Cost") + " " + (row+1)*5, x + PDFGenerator.talentBoxWidth - PDFGenerator.talentBoxStroke - 50, textOffsetY, 0);
        canvas.endText();

        // draw talent text
        canvas.setColorFill(BaseColor.BLACK);
        PdfPTable table = getTalentCell(key, 0);
        // too large?
        float max = y - PDFGenerator.talentBoxHeight;
        if (table.getRowHeight(0) > offSetYTalentText - max - 2 * PDFGenerator.wedgeOffset)
            table = getTalentCell(key, 1); // create smaller cell
        if (table.getRowHeight(0) > offSetYTalentText - max - 2 * PDFGenerator.wedgeOffset)
            table = getTalentCell(key, 2); // create tiny cell
        table.writeSelectedRows(0, -1, x + PDFGenerator.talentBoxStroke*1.5f, offSetYTalentText, canvas);
    }

    /**
     * get celled talent text
     * @param key
     * @param sizeReduction
     * @return
     * @throws Exception
     */
    protected PdfPTable getTalentCell(String key, int sizeReduction) throws Exception {
        // get phrase
        Phrase phrase = parseTextProperty(key, sizeReduction);

        // table text
        PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(PDFGenerator.talentBoxWidth - PDFGenerator.talentBoxStroke*3);
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

    //TODO
    protected void drawTalentBox(PdfContentByte canvas,
                               float x, float y, boolean active) {
        // set state of canvas

        canvas.saveState();
        canvas.setColorStroke(active ? BaseColor.BLACK : BaseColor.GRAY);
        canvas.setColorFill(active ? BaseColor.BLACK : BaseColor.GRAY);
        canvas.setLineWidth(2.0f);

        // draw outer rectangle
        canvas.moveTo(x + 1, y - 1);
        canvas.lineTo(x + 102, y - 1);
        canvas.lineTo(x + 110, y - 11);
        canvas.lineTo(x + 110, y - 111);
        canvas.lineTo(x + 102, y - 119);
        canvas.lineTo(x + 9, y - 119);
        canvas.lineTo(x + 1, y - 111);
        canvas.closePathStroke();

        // draw left footer shape
        canvas.moveTo(x + 9, y - 119);
        canvas.lineTo(x + 17, y - 111);
        canvas.lineTo(x + 29, y - 111);
        canvas.lineTo(x + 37, y - 119);
        canvas.lineTo(x + 29, y - 127);
        canvas.lineTo(x + 17, y - 127);
        canvas.closePathFillStroke();

        // draw right footer shape
        canvas.moveTo(x + 102, y - 119);
        canvas.lineTo(x + 94, y - 111);
        canvas.lineTo(x + 58, y - 111);
        canvas.lineTo(x + 50, y - 119);
        canvas.lineTo(x + 58, y - 127);
        canvas.lineTo(x + 94, y - 127);
        canvas.closePathFillStroke();

        canvas.restoreState();

        canvas.beginText();
        canvas.setFontAndSize(generator.getFontBold(), 6.25f);
        canvas.setColorFill(BaseColor.WHITE);
        canvas.showTextAligned(Element.ALIGN_CENTER, "134", x + 23, y - 121.5f, 0);
        canvas.endText();

        canvas.setColorFill(BaseColor.BLACK);
        Phrase phrase = new Phrase();
        phrase.setFont(new Font(generator.getFontCondensedBold(), 20));

        //phrase.add("Hallo! ");
        //phrase.add(new Chunk("bb", new Font(generator.getFontSymbol(), 20)));
        //ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 200, 572, 0);
    }
}
