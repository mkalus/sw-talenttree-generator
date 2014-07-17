Star Wars PDF Talent Tree Generator
===================================
for RPG Edge of the Empire and Age of Rebellion

This program enables you to automatically create talent tree PDFs for the new role playing game of Star Wars by FFG.
You need Java to run the program.
Example file (German talents): https://dl.dropboxusercontent.com/u/9102471/StarWars/Talente_SW_ARdI.pdf

Current status:
- runs on command line, no fancy GUI
- German talent tree for Edge of the empire file finished (English, Age of Rebellion and other languages = TODO)
- only "normal" talent trees supported currently, no force sensitive ones or vintage ones (in the works)
- occupations from extensions are not included yet

Help would be appreciated :-)

Source code available on Github: https://github.com/mkalus/sw-talenttree-generator


Running Star Wars PDF Talent Tree Generator
===========================================

Download sw-talenttree-generator-all.zip and unzip it.
URL: https://dl.dropboxusercontent.com/u/9102471/StarWars/sw-talenttree-generator-all.zip

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