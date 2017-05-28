import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ReadFile {

	static ArrayList<String> idList1 = new ArrayList<String>();
	static ArrayList<String> xList1 = new ArrayList<String>();
	static ArrayList<String> yList1 = new ArrayList<String>();
	static int numOfPts = 0;

	//RangeQuery
	static ArrayList<Float> rqX1 = new ArrayList<Float>();
	static ArrayList<Float> rqX2 = new ArrayList<Float>();
	static ArrayList<Float> rqY1 = new ArrayList<Float>();
	static ArrayList<Float> rqY2 = new ArrayList<Float>();
	
	//NNSearch
	static ArrayList<Float> nnqX = new ArrayList<Float>();
	static ArrayList<Float> nnqY = new ArrayList<Float>();
	
	/*
	public static void main(String[] args) {
		ReadNNSearchQuery("NNSearchTesting");
		System.out.println(nnqX.get(1));
		System.out.println(nnqY.get(1));
	}*/

	public static void readDataset(){
		String FILENAME = "dataset";
		FileReader fr = null;

		try{
			fr = new FileReader(FILENAME);
			BufferedReader reader = new BufferedReader(fr);

			//Read the first line
			String line1 = reader.readLine();
			numOfPts = Integer.parseInt(line1);

			String line = null;
			while((line = reader.readLine()) != null){
				String[] splited = line.split(" ");
				idList1.add(splited[0]);
				xList1.add(splited[1]);
				yList1.add(splited[2]);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void ReadRangeQuery(String filename){
		FileReader fr = null;
		BufferedReader br = null;
		try {

			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String line = "";
			while( (line=br.readLine()) != null) {
				String[] splited = line.split(" ");
				rqX1.add(Float.parseFloat(splited[0]));
				rqX2.add(Float.parseFloat(splited[1]));
				rqY1.add(Float.parseFloat(splited[2]));
				rqY2.add(Float.parseFloat(splited[3]));
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void ReadNNSearchQuery(String filename){
		FileReader fr = null;
		BufferedReader br = null;
		try {

			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String line = "";
			while( (line=br.readLine()) != null) {
				String[] splited = line.split(" ");
				nnqX.add(Float.parseFloat(splited[0]));
				nnqY.add(Float.parseFloat(splited[1]));
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
