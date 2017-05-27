import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ReadFile {

	static ArrayList<String> idList1 = new ArrayList<String>();
	static ArrayList<String> xList1 = new ArrayList<String>();
	static ArrayList<String> yList1 = new ArrayList<String>();

	public static void main(String[] args) {
		readDataset();
		System.out.println(idList1.get(0));
		System.out.println(xList1.get(0));
		System.out.println(yList1.get(0));
	}
	
	public static void readDataset(){
		String FILENAME = "dataset";
		BufferedReader br = null;
		FileReader fr = null;
		
		try{
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);
		
			String line = br.readLine();//read this line
			String line1 = null;
			
			while((line1 = br.readLine())!= null){
				String[] splited = line.split(" ");
				idList1.add(splited[0]);
				xList1.add(splited[1]);
				yList1.add(splited[2]);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
