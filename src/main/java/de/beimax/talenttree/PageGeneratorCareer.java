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
public class PageGeneratorCareer extends PageGeneratorSimple {
    /**
     * add general descriptive text
     * @throws Exception
     */
    protected void addDescriptiveText() throws Exception {
        addSkillData(getLocalizedString("CareerSkills"), "skills");
        addSkillData(MessageFormat.format(getLocalizedString("BonusSkills"), getMappedLocalizedString("subheader")), "bonus_skills");
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
}
