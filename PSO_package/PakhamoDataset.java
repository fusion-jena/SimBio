package slib.sml.sm.core.metrics.ic.pso;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class PakhamoDataset {

	public static String[] PDataset1 = new String[150];
	public static String[] PDataset2 = new String[150];
	public static String[] PDataset3 = new String[150];
	public static String[] PDataset4 = new String[150];
	public static String[] PakhWordNT1 = new String[127];
	public static String[] PakhWordNT2 = new String[127];
	public static String[] PakhWordnet1 = new String[150];
	public static String[] PakhWordnet2 = new String[150];

	public static String[] readPakhamoDataset(int index) {
		String[] dataSet = new String[150];

		readPakhamoCSV();

		switch (index) {
		case 1: // Term1 of MeSH
			dataSet = PDataset1;
			break;
		case 2: // Term2 of MeSH
			dataSet = PDataset2;
			break;

		case 3: // Term1 of SNOMED
			dataSet = PDataset3;
			break;
		case 4: // Term2 of SNOMED
			dataSet = PDataset4;
			break;
		}

		return dataSet;
	}

	public static void readPakhamoCSV() {
		String csvFile = "PATH/TO/pakhomov_2010_dataset.csv";

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			int i = 0; // \t is separtor
			reader.readNext(); // first line in header of file, so we skip it
			while ((line = reader.readNext()) != null) {
				String[] te = line[0].split("\t");
				PDataset1[i] = te[2];
				PDataset2[i] = te[3];

				String[] a = te[4].split("\\;");
				if (a.length > 1) {
					PDataset3[i] = a[1];
				} else {
					PDataset3[i] = a[0];
				}

				String[] b = te[5].split("\\;");
				if (b.length > 1) {
					PDataset4[i] = b[1];
				} else {
					PDataset4[i] = b[0];
				}

				i++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String[] readPakhamoWordNet(int index) {
		String[] dataSet = new String[127];

		String csvFile = "PATH/TO/Pakhomov_Wordnet_mapping_file.csv";

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			int i = 0; // \t is separtor
			reader.readNext(); // first line in header of file, so we skip it
			while ((line = reader.readNext()) != null) {
				PakhWordNT1[i] = line[0].trim();
				PakhWordNT2[i] = line[1].trim();
				i++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		switch (index) {
		case 1: // Term1 of WordNet
			dataSet = PakhWordNT1;
			break;
		case 2: // Term2 of WordNet
			dataSet = PakhWordNT2;
			break;
		}

		return dataSet;
	}

	public static String[] readPakhamoWordNetComp(int index) {
		String[] dataSet = new String[150];

		String csvFile = "PATH/TO/Pakhomov_Wordnet.csv";

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			int i = 0; // \t is separtor
			reader.readNext(); // first line in header of file, so we skip it
			while ((line = reader.readNext()) != null) {
				if (line[0].equals("")) {
					PakhWordnet1[i] = null;
					PakhWordnet2[i] = null;
					i++;
				} else {
					PakhWordnet1[i] = line[0].trim();
					PakhWordnet2[i] = line[1].trim();
					i++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		switch (index) {
		case 1: // Term1 of WordNet
			dataSet = PakhWordnet1;
			break;
		case 2: // Term2 of WordNet
			dataSet = PakhWordnet2;
			break;
		}

		return dataSet;
	}
}