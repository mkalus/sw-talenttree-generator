package de.beimax.talenttree;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import java.util.ArrayList;

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
 * Generator for signature skill trees (from supplements)
 */
public class PageGeneratorSignature extends PageGeneratorSimple {
    /**
     * Create basic header
     * @throws Exception
     */
    protected void addHeader() throws Exception {
        Font fontHeader = new Font(generator.getFontBold(), 14);
        Font fontSubHeader = new Font(generator.getFontBold(), 18);
        document.add(new Paragraph(getMappedLocalizedString("header"), fontHeader));
        document.add(new Paragraph( getMappedLocalizedString("subheader"), fontSubHeader));
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
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("BaseAbility"), x - 20, y - 7, 0);
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("Upgrade"), x - 20, y - 22, 0);
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("Ranked"), x - 20, y - 37, 0);
        canvas.endText();
    }

    /**
     * Add talent paths
     * @throws Exception
     */
    protected void addTalentPaths() throws Exception {
        // add signature nodes at top
        canvas.saveState();
        canvas.setColorStroke(PDFGenerator.lineColor);
        canvas.setLineWidth(PDFGenerator.talentPathStroke);
        try {
            int col = 0;
            for (int path : (ArrayList<Integer>) data.get("signature_nodes")) {
                addSignatureNode(col, path);
                col++;
            }
        } catch (Exception e) {
            throw new Exception("Error while creating signature nodes in " + data.get("id") + ": " + e.getMessage());
        }
        canvas.restoreState();

        // call rest of paths
        super.addTalentPaths();
    }

    /**
     * draw signature node
     * @param col
     * @param path
     * @throws Exception
     */
    protected void addSignatureNode(int col, int path) throws Exception {
        // ignore non valid paths
        if (path != 1) return;

        // calculate offsets
        float x = calculateColOffset(col);
        float y = calculateRowOffset(-1);

        // add half width of box
        x += PDFGenerator.talentBoxWidth/2 - PDFGenerator.talentPathStroke/2;
        y -= PDFGenerator.talentBoxHeight;
        // vertical line
        canvas.moveTo(x, y + PDFGenerator.verticalSpacing);
        canvas.lineTo(x, y - PDFGenerator.talentBoxStroke - PDFGenerator.wedgeOffset - PDFGenerator.talentBoxStroke); //  PDFGenerator.verticalSpacing - PDFGenerator.wedgeOffset - PDFGenerator.talentBoxStroke
        canvas.stroke();

        // connector node element
        canvas.moveTo(x - PDFGenerator.talentBoxWidth/2, y + PDFGenerator.verticalSpacing + PDFGenerator.wedgeOffset);
        canvas.lineTo(x - PDFGenerator.talentBoxWidth/2 + PDFGenerator.wedgeOffset, y + PDFGenerator.verticalSpacing);
        canvas.lineTo(x + PDFGenerator.talentBoxWidth/2 - PDFGenerator.wedgeOffset, y + PDFGenerator.verticalSpacing);
        canvas.lineTo(x + PDFGenerator.talentBoxWidth/2, y + PDFGenerator.verticalSpacing + PDFGenerator.wedgeOffset);
        canvas.stroke();

        // draw diamond
        drawDiamond(x, y + PDFGenerator.verticalSpacing);
    }

    /**
     * draw a diamond shape
     * @param x
     * @param y
     */
    protected void drawDiamond(float x, float y) {
        float offset = - 15.766f/2 - PDFGenerator.talentPathStroke/2;
        canvas.setColorFill(BaseColor.WHITE);

        // rhombus/diamond
        canvas.moveTo(x + 9.883f + offset, y - offset/2);
        canvas.lineTo(x + 15.766f + offset, y - 5.883f - offset/2);
        canvas.lineTo(x + 9.883f + offset, y - 11.765f - offset/2);
        canvas.lineTo(x + 4 + offset, y - 5.883f - offset/2);
        canvas.closePathFillStroke();
    }

    /**
     * calculate offset y
     * @param row
     * @return
     */
    protected float calculateRowOffset(int row) {
        // move all boxes a bit down to add space for signature nodes
        return super.calculateRowOffset(row) - 50;
    }
}
