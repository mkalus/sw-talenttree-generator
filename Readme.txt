Star Wars PDF Talent Tree Generator
===================================
for RPG Edge of the Empire and Age of Rebellion

This program enables you to automatically create talent tree PDFs for the new role playing game of Star Wars by FFG.
You need Java to run the program.

Or download PDF directly:
English: https://www.beimax.de/fileadmin/downloads/starwars/Talents_SW_EotE.pdf
German:  https://www.beimax.de/fileadmin/downloads/starwars/Talente_SW_ARdI.pdf

Current status:
- runs on command line, no fancy GUI
- All talent trees from Edge if the Empire base game (German and English)
- Supplement "Dangerous Covenants" (German and English)
- Supplement "Enter the Unknown" (German and English)
- Supplement "Far Horizons" (English)
- Supplement "Flying Casual" (English)

Help would be appreciated :-)

Source code available on Github: https://github.com/mkalus/sw-talenttree-generator


Running Star Wars PDF Talent Tree Generator
===========================================

Download sw-talenttree-generator-all.zip and unzip it.
URL: https://www.beimax.de/fileadmin/downloads/starwars/sw-talenttree-generator-all.zip

There will be a jar file and several data files. Run the jar with -h option to see how the program works:
java -jar sw-talenttree-generator.jar -h

To create German PDFs using the data file data.yaml and language file strings_de.txt, run the program the following way:
java -jar sw-talenttree-generator.jar --data data.yaml --strings strings_de.txt

Without options, the program will try to create a file in your current language, if the file exists in the JAR.


Building Star Wars PDF Talent Tree Generator
============================================

Get the source from Github:
git clone https://github.com/mkalus/sw-talenttree-generator.git

Change into the directory:
cd sw-talenttree-generator

Build using Maven:
mvn package

You will find a ZIP file in the target directory called sw-talenttree-generator-all.zip. Voilà!
