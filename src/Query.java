import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Query {
//Node: x1<x2 , y1>y2 
	//Return by Rtree
	static Rtree.Node root = new Rtree.Node();
	
	//Map: (MBR(Node), mindist)
	static HashMap<Rtree.Node, Double> listH = new HashMap<Rtree.Node, Double>(); 
	static ArrayList<Rtree.Point> NN = new ArrayList<Rtree.Point>();
	static double nnDist = 0;
	static HashMap<Integer,Double> result = new HashMap<Integer,Double>();
	static double lowestDist = Double.POSITIVE_INFINITY;
	
	public static void main(String[] args) {
		//Return Tree root: parentnode.id = 0
		//Nodes (hashmap)
		//Parent Nodes (hashmap)
		
		//Test
		Rtree.Node r6 = new Rtree.Node();
		r6.setX1(1);
		r6.setX2(9);
		r6.setY1(10);
		r6.setY2(5);
		r6.asLeaf = false;

		Rtree.Node r7 = new Rtree.Node();
		r7.setX1(3);
		r7.setX2(10);
		r7.setY1(6);
		r7.setY2(1);
		r7.asLeaf = false;
		
		Rtree.Node r1 = new Rtree.Node();
		r1.setX1(1);
		r1.setX2(4);
		r1.setY1(10);
		r1.setY2(8);
		r1.asLeaf = true;
		
		Rtree.Node r2 = new Rtree.Node();
		r2.setX1(6);
		r2.setX2(9);
		r2.setY1(10);
		r2.setY2(5);
		r2.asLeaf = true;
		
		Rtree.Node r3 = new Rtree.Node();
		r3.setX1(3);
		r3.setX2(5);
		r3.setY1(6);
		r3.setY2(2);
		r3.asLeaf = true;
		
		Rtree.Node r4 = new Rtree.Node();
		r4.setX1(6);
		r4.setX2(8);
		r4.setY1(3);
		r4.setY2(2);
		r4.asLeaf = true;
		
		Rtree.Node r5 = new Rtree.Node();
		r5.setX1(9);
		r5.setX2(10);
		r5.setY1(4);
		r5.setY2(1);
		r5.asLeaf = true;
		
		root.childNodes.add(r7);
		root.childNodes.add(r6);
		r6.childNodes.add(r1);
		r6.childNodes.add(r2);
		r7.childNodes.add(r3);
		r7.childNodes.add(r4);
		r7.childNodes.add(r5);
		
		Rtree.Point p7 = new Rtree.Point(3,6,501);
		Rtree.Point p8 = new Rtree.Point(4,3,502);
		Rtree.Point p9 = new Rtree.Point(5,2,503);
		Rtree.Point p11 = new Rtree.Point(3,4,504);
		Rtree.Point p10 = new Rtree.Point(6,2,505);
		
		r3.points.add(p7);
		r3.points.add(p8);
		r3.points.add(p9);
		r3.points.add(p11);
		r4.points.add(p10);
		
		Rtree.Point q = new Rtree.Point(5.5f,2,500);
		
		//Main
		getMindist(q,root); //Access root
		HashMap<Integer, Double> nn = NNSearch(q);
		System.out.println(nn);
	}
	
	public static HashMap<Integer, Double> NNSearch(Rtree.Point q){
		double ppDist = Double.POSITIVE_INFINITY;
		double thisPpDist = 0;
		ArrayList<Rtree.Point> curNN = new ArrayList<Rtree.Point>();
		
		//Access the first child in listH
		Rtree.Node u = new Rtree.Node();
		u = listH.keySet().iterator().next();
		
		//Remove the first child in listH
		listH.remove(u);
		
		//If u is an intermediate node
		if(u.asLeaf == false){
			getMindist(q,u);
			//System.out.println(Arrays.asList(listH));
			NNSearch(q);

		}else{ //leaf node
			//Get the lowest value in listH
			double nextHVal = listH.values().iterator().next();
			//System.out.println(lowestMindist);
			
			//Get the closest points in this leaf node
			for(Rtree.Point point:u.points){
				thisPpDist = Math.sqrt(Math.pow(point.getX() - q.getX(), 2) + Math.pow(point.getY() - q.getY(), 2));
				if(thisPpDist < ppDist){
					ppDist = thisPpDist;
					curNN = new ArrayList<Rtree.Point>();
					curNN.add(point);
				}else if(thisPpDist == ppDist){
					curNN.add(point);
				}
			}
			
			if(ppDist < nextHVal){
				//NN found
				NN.addAll(curNN);
				nnDist = ppDist;
			}else if(ppDist == nextHVal){
				//Search next node
				NN.addAll(curNN);
				NNSearch(q);
			}else{
				NNSearch(q);
			}
		}
		
		for(Rtree.Point nn:NN){
			result.put(nn.getId(), nnDist);
		}
		
		return result;
	}
	
	public static void getMindist(Rtree.Point q, Rtree.Node node){
		
		//Calculate the mindist between q and each Root.childnodes
		for(Rtree.Node n:node.childNodes){
			
			double mindist = 0;
			
			if(q.getX()<n.getX1()){
				if(q.getY()>n.getY1()){
					mindist = Math.sqrt(Math.pow(n.getX1()-q.getX(),2) + Math.pow(q.getY() - n.getY1(), 2));
				}else if(q.getY()<=n.getY1() && q.getY() >= n.getY2()){
					mindist = n.getX1() - q.getX();
				}else if(q.getY()<n.getY2()){
					mindist = Math.sqrt(Math.pow(n.getX1()-q.getX(),2) + Math.pow(n.getY2() - q.getY(), 2));
				}else{
					System.out.println("Error 1.");
				}
			}else if(q.getX()>=n.getX1() && q.getX()<=n.getX2()){
				if(q.getY()>n.getY1()){
					mindist = q.getY() - n.getY1();
				}else if(q.getY()<=n.getY1() && q.getY()>=n.getY2()){
					mindist = 0;
				}else if(q.getY()<n.getY2()){
					mindist = n.getY2() - q.getY();
				}else{
					System.out.println("Error 2.");
				}
			}else if(q.getX()>n.getX2()){
				if(q.getY()>n.getY1()){
					mindist = Math.sqrt(Math.pow(q.getX()-n.getX2(),2) + Math.pow(q.getY() - n.getY1(), 2));
				}else if(q.getY()<=n.getY1() && q.getY()>=n.getY2()){
					mindist = q.getX() - n.getX2();
				}else if (q.getY()<n.getY2()){
					mindist = Math.sqrt(Math.pow(q.getX()-n.getX2(),2) + Math.pow(n.getY2()- q.getY(), 2));
				}else{
					System.out.println("Error 3.");
				}
			}else{
				System.out.println("Error 4.");
			} 
			//childnode, mindist
			listH.put(n, mindist);
		}
		
		//Sort the map according to the mindist
		listH = sortByValues(listH);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap<Rtree.Node, Double> sortByValues(HashMap<Rtree.Node, Double> map) { 
		List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }
}
