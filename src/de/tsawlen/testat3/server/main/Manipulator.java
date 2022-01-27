package de.tsawlen.testat3.server.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Manipulator {
	
	/**
	 * This method is responsable for searching and returning the searched line
	 * @param path
	 * @param lineNo
	 * @return String
	 */
	public String readLine(String path, int lineNo) {
		
		try {
			//get a Reader for the file
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String theLine = "";
			
			//read all lines till the searched line
			for(int i = 0; i < lineNo; i++) {
				//set the searched line
				theLine = reader.readLine();
			}
			//Close the reader
			reader.close();
			//check if line is out of bounds for the file
			if(theLine == null) {
				//return that the file does not exists
				theLine = "Line does not exist!";
			}
			return theLine;
		}catch(FileNotFoundException e) {
			//return an Error is the file does not exist
			return "The file does not exists!";
		} catch (IOException e) {
			//Return an Error when the Error is unknown 
			return "Status 500: Internal Server error!";
		}
		
	}
	
	/**
	 * The method responsible for writing a line
	 * @param path the path to the file
	 * @param lineNo the line to replace
	 * @param newLine the new line
	 * @return the Status of the operation
	 */
	public String writeLine(String path, int lineNo, String newLine) {
		try {
			//Create a variable for the file to change
			File file = new File(path);
			//Create a variable for the new file
			File newFile = new File(path + ".tmp");
			//get a reader on the old file
			BufferedReader reader = new BufferedReader(new FileReader(file));
			//create a writer for the new file
			PrintWriter writer = new PrintWriter(newFile);
			String line = "";
			//line counter
			int i = 1;
			//Run until the entire document is copied or the counter is below the line to replace, so that lines can be appended
			while(((line = reader.readLine()) != null) || i <= lineNo) {
				//check if current line is the line to replace
				if(i == lineNo) {
					//write the new line
					writer.println(newLine);
				}else {
					//if the line is not existing till now
					if(line == null) {
						//print an empty line
						writer.println("");
					}else {
						//When the line does exist reprint it to the new file
						writer.println(line);
					}
				}
				//increment the line counter
				i++;
			}
			//close the reader
			reader.close();
			//close the writer
			writer.close();
			//delete the old file
			file.delete();
			//rename the new file to the name of the old file
			newFile.renameTo(file); 
			//return status
			return "Überschrieben";
			
		}
		catch(FileNotFoundException e) {
			//return an Error is the file does not exist
			return "The file does not exists!";
		} catch (IOException e) {
			//Return an Error when the Error is unknown 
			return "Status 500: Internal Server error!";
		}
	}

}
