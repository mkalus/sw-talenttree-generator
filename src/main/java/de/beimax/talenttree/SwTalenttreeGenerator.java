package de.beimax.talenttree;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

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
 * Starter class. Call main()!
 */
public class SwTalenttreeGenerator {
    /**
     * my command line parser
     */
    private static CmdLineParser parser;

    /**
     * Main method to call TT PDF Generator
     * @param args
     */
    public static void main(String[] args) {
        // load generator
        try {
            // create generator
            PDFGenerator generator = new PDFGenerator();

            // create command line parser for generator
            SwTalenttreeGenerator.parser = new CmdLineParser(generator);

            try {
                // parse the arguments.
                parser.parseArgument(args);

                if (generator.getPrintHelp()) throw new Exception("Usage requested.");
            } catch (Exception e) {
                // if there's a problem in the command line,
                // you'll get this exception. this will report
                // an error message.
                System.err.println(e.getMessage());
                printHelp();
                System.exit(0);
            }

            System.err.println("Add -h option to print help.");

            // initialize stuff
            generator.initialize();

            // generate PDFs
            generator.generate();
        } catch (Exception e) {
            if (e.getMessage() == null)
                e.printStackTrace();
            else System.err.println("Error occured: " + e.getMessage());
        }
    }

    /**
     * Print help
     */
    protected static void printHelp() {
        System.err.println("java -jar sw-talenttree-generator.jar [options...]");
        // print the list of available options
        SwTalenttreeGenerator.parser.printUsage(System.err);
        System.err.println();

        // print option sample. This is useful some time
        System.err.println("  Example: java -jar sw-talenttree-generator.jar --data data.yaml --strings strings_de.txt");
    }
}
