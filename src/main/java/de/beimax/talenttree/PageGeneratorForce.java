package de.beimax.talenttree;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;

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
 * Generator for force powers
 */
public class PageGeneratorForce extends PageGeneratorSimple {
    /**
     * Add help/legend
     * @throws Exception
     */
    protected void addLegend() throws Exception {
        canvas.saveState();

        // draw arrows
        float x = getRightX();
        float y = getTopY() - 20;

        drawLegendArrow(PDFGenerator.passiveColor, x, y);

        // draw ranked
        drawRanked(PDFGenerator.passiveColor, x, y - 15);

        canvas.restoreState();

        // draw legend text
        canvas.beginText();
        canvas.setFontAndSize(generator.getFontRegular(), 10f);
        canvas.setColorFill(BaseColor.BLACK);
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("ForcePowerWedge"), x - 20, y - 7, 0);
        canvas.showTextAligned(Element.ALIGN_RIGHT, getLocalizedString("Ranked"), x - 20, y - 24, 0);
        canvas.endText();
    }
}
