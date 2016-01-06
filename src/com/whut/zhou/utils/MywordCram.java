package com.whut.zhou.utils;

import java.awt.Shape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import processing.core.PApplet;
import processing.core.PImage;
import wordcram.Colorers;
import wordcram.ImageShaper;
import wordcram.Placers;
import wordcram.ShapeBasedPlacer;
import wordcram.Word;
import wordcram.WordCram;

public class MywordCram extends PApplet {
	static final int NUM = 90;
	Word[] wordArray = new Word[NUM];
	PImage image;
	int outside = color(0, 0, 0);
	int inside = color(204, 102, 0);

	public void settings() {
		//image = loadImage("C:\\MySoftware\\Java\\Workspace\\WebBestFriends\\WebContent\\images\\face.jpg");
		//size(image.width, image.height);
		size(700, 700);
	}

	public void setup() {

		background(255);
		//Shape imageShape = new ImageShaper().shape(image, outside);
		//ShapeBasedPlacer placer = new ShapeBasedPlacer(imageShape);

		wordArray = readerFile();
		WordCram wordcram = new WordCram(this);
		wordcram.fromWords(wordArray)
				.withPlacer(Placers.wave())
				//.withNudger(placer).maxNumberOfWordsToDraw(NUM).sizedByWeight(12, 80).withWordPadding(2)
				.withFont(processing.core.PFont.WHITESPACE).withColors(color(0), color(230, 0, 0), color(0, 0, 230))
				.sizedByWeight(8, 80)
				// .withColorer(Colorers.twoHuesRandomSats(this))
				.drawAll();

		saveFrame("C:\\MySoftware\\Java\\Workspace\\WebBestFriends\\WebContent\\images\\face.png");
		
		System.out.println("over");
	}

	public Word[] readerFile() {
		File file = new File("C:\\MySoftware\\Java\\WorkSpace\\WebBestFriends\\result.txt");
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String tempString = null;
			String temp[] = new String[NUM];
			int line = 1;
			int i = 0;
			while ((tempString = reader.readLine()) != null) {
				System.out.println("line " + line + ": " + tempString);
				line++;
				temp[i++] = tempString;
			}
			for (int j = 0; j < temp.length; j++) {
				String[] subString = temp[j].split("=");
				
				wordArray[j] = new Word(subString[0], Float.parseFloat(subString[1]));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return wordArray;

	}

	// "MywordCram" must match the name of your class (should include the package)
	public static void getWordCram() {
		PApplet.main(new String[] { "--present", "com.whut.zhou.utils.MywordCram" });
	}

}
