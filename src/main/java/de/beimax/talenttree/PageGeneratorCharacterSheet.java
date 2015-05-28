package de.beimax.talenttree;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

/**
 * PDF Generator for Star Wars Talent sheets
 * (c) 2015 Maximilian Kalus [max_at_beimax_dot_de]
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
 * Generator for character sheet (front and back)
 */
public class PageGeneratorCharacterSheet extends AbstractPageGenerator {
    @Override
    public void generate() throws Exception {
        // front or back side?
        if ("front".equals(data.get("page"))) generateFront();
        else generateBack();
    }

    /**
     * generate front of character sheet
     * @throws Exception
     */
    protected void generateFront() throws Exception {
        generateLogo();

        float x, y;

        // generate characteristics
        float elementWidth = 65f;
        float space = (getUsablePageWidth() - elementWidth*6f)/5f;
        x = getLeftX();
        y = getTopY();

        String[] characteristics = {"Brawn", "Agility", "Intellect", "Cunning", "Willpower", "Presence"};

        for (int i = 0; i < characteristics.length; i++) {
            drawCharacteristic(characteristics[i], x + i*(elementWidth + space), y);
        }
    }

    /**
     * draw a single characteristic element
     * @param name to show (to translate)
     * @param x to start at
     * @param y to start at
     */
    protected void drawCharacteristic(String name, float x, float y) throws Exception {
        canvas.saveState();
        canvas.setColorFill(BaseColor.GRAY);

        // background forms
        drawSimpleCanvasForm(x, y, 65, 12);
        drawSimpleCanvasForm(x, y - 13, 65, 12);
        drawSimpleCanvasForm(x, y - 26, 65, 12);
        canvas.fill();

        canvas.setColorStroke(PDFGenerator.passiveColor);
        canvas.setColorFill(BaseColor.WHITE);
        canvas.setLineWidth(0.2f);
        drawSimpleCanvasForm(x, y - 39, 65, 15);
        canvas.fillStroke();

        canvas.setColorStroke(PDFGenerator.passiveColor);
        canvas.setColorFill(BaseColor.WHITE);
        canvas.setLineWidth(PDFGenerator.talentBoxStroke);
        canvas.circle(x + 32.5f, y - 22, 20f);
        canvas.fillStroke();

        canvas.setLineWidth(0.5f);
        canvas.circle(x + 32.5f, y - 22, 18f);
        canvas.stroke();

        canvas.setLineWidth(0.2f);
        canvas.circle(x + 32.5f, y - 22, 16.5f);
        canvas.stroke();

        canvas.restoreState();

        // draw legend text
        canvas.beginText();
        canvas.setFontAndSize(generator.getFontRegular(), 8f);
        canvas.setColorFill(BaseColor.BLACK);
        canvas.showTextAligned(Element.ALIGN_CENTER, getLocalizedString(name).toUpperCase(), x + 32.5f, y - 50.5f, 0);
        canvas.endText();
    }

    /**
     * Draw a simple canvas form - stroke and fill has to made in calling method
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void drawSimpleCanvasForm(float x, float y, float width, float height) {
        canvas.moveTo(x, y - 5);
        canvas.lineTo(x + 5, y);
        canvas.lineTo(x + width - 5, y);
        canvas.lineTo(x + width, y - 5);
        canvas.lineTo(x + width, y - height + 5);
        canvas.lineTo(x + width - 5, y - height);
        canvas.lineTo(x + 5, y - height);
        canvas.lineTo(x, y - height + 5);
        canvas.closePath();
    }

    /**
     * generate back of character sheet
     * @throws Exception
     */
    protected void generateBack() throws Exception {
        generateLogo();
    }

    /**
     * generate SW like logo
     * @throws Exception
     */
    protected void generateLogo() throws Exception {
        //TODO: logo
    }

    @Override
    public String getLocalizedSortKey() {
        return "00" + ("front".equals(data.get("page"))?"0":"1");
    }

    @Override
    public int compareTo(AbstractPageGenerator abstractPageGenerator) {
        return getLocalizedSortKey().compareTo(abstractPageGenerator.getLocalizedSortKey());
    }
}
