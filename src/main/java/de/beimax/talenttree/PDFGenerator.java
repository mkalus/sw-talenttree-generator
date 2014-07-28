package de.beimax.talenttree;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.kohsuke.args4j.Option;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

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
 * PDF Core generator and settings.
 */
public class PDFGenerator {
    /**
     * Constants
     */
    public static final float marginHorizontal = 48;

    public static final float marginVertical = 28;

    public static final float headerTextMaxWidth = 420; // maximum width of header text

    // talent box settings
    public static final BaseColor passiveColor = BaseColor.BLACK;
    public static final BaseColor activeColor = BaseColor.GRAY;
    public static final BaseColor lineColor = BaseColor.GRAY;

    public static final float talentBoxStroke = 2.0f;
    public static final float talentPathStroke = 4.0f;
    public static final float talentBoxWidth = 111f;
    public static final float talentBoxHeight = 128f;
    public static final float wedgeOffset = 8f;
    public static final float verticalSpacing = 10f;

    /**
     * header font
     */
    private BaseFont fontHeader;

    /**
     * Regular font
     */
    private BaseFont fontRegular;

    /**
     * Bold font
     */
    private BaseFont fontBold;

    /**
     * Regular condensed font
     */
    private BaseFont fontCondensedRegular;

    /**
     * Bold condensed font
     */
    private BaseFont fontCondensedBold;

    /**
     * Star wars symbol font
     */
    private BaseFont fontSymbol;

    @Option(name = "-h", usage = "print this help")
    private boolean printHelp = false;

    /**
     * Page size
     */
    @Option(name = "--page-size", usage = "page size (A4, letter)")
    private String pageSize = "A4";
    private Rectangle pageSizeValue;

    /**
     * Strings file
     */
    @Option(name = "--strings", usage = "translation file")
    private String stringsFile = null;
    private Properties strings;

    /**
     * Data file
     */
    @Option(name = "--data", usage = "data file")
    private String dataFile = null;
    private Iterable<Object> data; // list of data objects to render

    /**
     * language
     */
    @Option(name = "--lang", usage = "language (ignored when --strings is set)")
    private String language = null;

    /**
     * Constructor
     * @throws Exception
     */
    public PDFGenerator() throws Exception {
        // load fonts
        loadFonts();
    }

    public boolean getPrintHelp() {
        return printHelp;
    }

    public BaseFont getFontHeader() {
        return fontHeader;
    }

    public BaseFont getFontRegular() {
        return fontRegular;
    }

    public BaseFont getFontBold() {
        return fontBold;
    }

    public BaseFont getFontCondensedRegular() {
        return fontCondensedRegular;
    }

    public BaseFont getFontCondensedBold() {
        return fontCondensedBold;
    }

    public BaseFont getFontSymbol() {
        return fontSymbol;
    }

    public Properties getStrings() {
        return strings;
    }

    /**
     * list of generators (sorted)
     */
    protected PriorityQueue<AbstractPageGenerator> pageGenerators;

    /**
     * Load fonts
     * @throws Exception
     */
    protected void loadFonts() throws Exception {
        // try header font
        try {
            fontHeader = BaseFont.createFont(getClass().getResource("/fonts/LeagueGothic-Regular.otf").toString(), BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new Exception("Header font could not be loaded.");
        }

        // try regular font
        try {
            fontRegular = BaseFont.createFont(getClass().getResource("/fonts/LiberationSans-Regular.ttf").toString(), BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new Exception("Regular font could not be loaded.");
        }

        // try bold font
        try {
            fontBold = BaseFont.createFont(getClass().getResource("/fonts/LiberationSans-Bold.ttf").toString(), BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new Exception("Bold font could not be loaded.");
        }

        // try regular condensed font
        try {
            fontCondensedRegular = BaseFont.createFont(getClass().getResource("/fonts/LiberationSansNarrow-Regular.ttf").toString(), BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new Exception("Regular font could not be loaded.");
        }

        // try bold condensed font
        try {
            fontCondensedBold = BaseFont.createFont(getClass().getResource("/fonts/LiberationSansNarrow-Bold.ttf").toString(), BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new Exception("Bold font could not be loaded.");
        }

        // try symbol font
        try {
            fontSymbol = BaseFont.createFont(getClass().getResource("/fonts/EotE_Symbol-Regular_v1.otf").toString(), BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new Exception("Symbol font could not be loaded.");
        }
    }

    /**
     * Prepare PDF generation
     * @throws Exception
     */
    public void initialize() throws Exception {
        // calculate page size
        if (pageSize.equalsIgnoreCase("A4")) pageSizeValue = PageSize.A4;
        else if (pageSize.equalsIgnoreCase("letter")) pageSizeValue = PageSize.LETTER;
        else throw new Exception("Unknown page size.");

        // load properties file
        InputStream langStream;
        if (stringsFile != null) {
            File langFile = new File(stringsFile);
            if (!langFile.exists()) throw new Exception("Language file " + stringsFile + " not found.");
            langStream = new BufferedInputStream(new FileInputStream(langFile));
        } else {// load default
            // language from parameters
            if (this.language == null) this.language = Locale.getDefault().getLanguage();
            // resource for current locale?
            URL langResource = getClass().getResource("/strings_" + this.language + ".txt");
            if (langResource == null) langResource = getClass().getResource("/strings_en.txt"); // fallback to English
            langStream = langResource.openStream();
        }
        try {
            strings = new Properties();
            strings.load(new InputStreamReader(langStream, "utf-8")); // read as UTF-8 encoded stream
            langStream.close();
        } catch (Exception e) {
            throw new Exception("Error loading language file:" + e.getMessage());
        }

        // load data file from yaml
        InputStream dataStream;
        if (dataFile != null) {
            File dFile = new File(dataFile);
            if (!dFile.exists()) throw new Exception("Data file " + stringsFile + " not found.");
            dataStream = new BufferedInputStream(new FileInputStream(dFile));
        } else {// load default
            dataStream = getClass().getResourceAsStream("/data.yaml");
        }
        try {
            Yaml yaml = new Yaml();
            data = yaml.loadAll(dataStream);
        } catch (Exception e) {
            throw new Exception("Error loading data file:" + e.getMessage());
        }
    }

    /**
     * prepare and generate PDF
     * @throws Exception
     */
    public void generate() throws Exception {
        // create and sort generator objects
        createSortedList();

        // call PDF generation
        createPDF();
    }

    /**
     * create and sort PDF generator objects
     * @throws Exception
     */
    protected void createSortedList() throws Exception {
        // initialize queue
        this.pageGenerators = new PriorityQueue<>();

        // iterate data to create objects
        for (Object o : this.data) {
            Map data = (Map) o; // to map
            // check type
            String type = (String) data.get("type");
            if (type == null) throw new Exception("The following data contained no type: " + o.toString());
            // try to load class and create instance
            AbstractPageGenerator pageGenerator;

            try {
                Class c = Class.forName(type);
                pageGenerator = (AbstractPageGenerator) c.newInstance();
                if (pageGenerator == null) throw new Exception();
            }  catch (Exception e) {
                throw new Exception("Type " + type + " not valid in following data: " + o.toString());
            }

            // set data
            pageGenerator.setGenerator(this);
            pageGenerator.setData(data);

            this.pageGenerators.add(pageGenerator);
        }
    }

    /**
     * actual PDF generation
     * @throws Exception
     */
    protected void createPDF() throws Exception {
        // create PDF
        Document document = new Document(pageSizeValue, marginHorizontal, marginHorizontal, marginVertical, marginVertical);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(getFileName()));
        document.open();

        document.addAuthor("Maximilian Kalus");
        document.addCreator("Star Wars Talent Tree Generator, see https://github.com/mkalus/sw-talenttree-generator");
        document.addTitle(strings.getProperty("PDFTitle", "Star Wars Talent Trees"));

        // iterate pageGenerators to generate PDF
        while (this.pageGenerators.size() > 0) {
            // get element from queue
            AbstractPageGenerator pageGenerator = this.pageGenerators.poll();

            // new page, if needed
            document.newPage();
            PdfContentByte canvas = writer.getDirectContent();

            // fill data
            pageGenerator.setDocument(document);
            pageGenerator.setWriter(writer);
            pageGenerator.setCanvas(canvas);

            // generate page
            pageGenerator.generate();

            System.out.println("Generated: " + pageGenerator.getId());
        }

        // close and write document
        document.close();
    }

    /**
     * get file name to export
     * @return file name to write
     */
    protected String getFileName() {
        String fileName = strings.getProperty("FileName");
        if (fileName == null || fileName.length() == 0) fileName = "out";

        // TODO: special file names

        return fileName + ".pdf";
    }
}
