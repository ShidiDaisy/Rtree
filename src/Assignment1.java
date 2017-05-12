import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.math.*;

public class Assignment1 {

	private final static int B = 3;
	private final static double zeropoint4B=Math.ceil(0.4*B); 
	static ArrayList<Node> nodeList = new ArrayList<Node>();
	
	public static void main(String[] args) {
		System.out.println("0.4B = "+zeropoint4B);
		//A list containing all nodes number	
	    //nodeList=new ArrayList<Node>();
		int nodeNum=0;
		//build a R-tree
		Point point1 = new Point(0,5,1);
		Point point2 = new Point(5,2,2);
		Node node1 = new Node();
		nodeNum++;
	//	nodeList.add(node1);
		node1.asLeaf=true;
		Insertion(node1,point1);
		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
		node1.getAllId();
		Insertion(node1,point2);
		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
		node1.getAllId();
	}
	
		//Find the root
		
		//Insertion
		public static void Insertion(Node root, Point point){
			
			if(root.getLeaf() == true){
				//add point to root
				root.addPoint2Node(point);
				//check if the node overflows
				if(root.points.size()>B){
					//handle overflow
					//split first
				}
					
				//update leaf size
				//if this is the first point
				if(root.points.size() == 1){
					root.x1 = point.x;
					root.x2 = point.x;
					root.y1 = point.y;
					root.y2 = point.y;
				}
				else{
					for(int i=0; i<root.points.size(); i++){
						if(root.points.get(i).x<=root.x1){
							root.x1=root.points.get(i).x;
						}
						if(root.points.get(i).x>=root.x2){
							root.x2=root.points.get(i).x;
						}
						if(root.points.get(i).y>=root.y1){
							root.y1=root.points.get(i).y;
						}
						if(root.points.get(i).y<=root.y2){
							root.y2=root.points.get(i).y;
						}
					}
				}
					//handle overflow function
				
				
			}else
			//internal node
			{
				//Choose subtree
			}
		}
			
		//Split Leaf Node
		public static void Split(Node leafnode){
			
			ArrayList<Point> sortedPoint= leafnode.points;
			Collections.sort(sortedPoint, new Comparator<Point>()){
				@Override 
				public float compare(Point p1, Point p2){
					return Float.valueOf(p1.getX()).compareTo(Float.valueOf(p2.getX()));
				}
			}
			
			ArrayList<Float> pointX = new ArrayList<Float>();
			
			for (int i=0;i<leafnode.points.size();i++){
				pointX.add(leafnode.points.get(i).getX());
			}
		}
		
		//Handle overflow
		public static void HandleOverflow(){
			//Split
		}
		
		
		
		//rectangle
		public static class Node{
			private float x1;
			private float y1;
			private float x2;
			private float y2;
			boolean asLeaf;
			
			ArrayList<Point> points = new ArrayList<Point>();
			
			public void getAllId(){
				for(int i=0;i<this.points.size();i++){
					System.out.println(this.points.get(i).getId());
				}
			}
			
			//the nodes in this node
			ArrayList<Node> childNodes = new ArrayList<Node>();
			
			public Node(){}
			
			public Node(float x1, float y1, float x2, float y2, boolean asLeaf){
				this.x1 = x1;
				this.y1 = y1;
				this.x2 = x2;
				this.y2 = y2;
				this.asLeaf = asLeaf;
			}
			
			public void setX1(float x1){
				this.x1 = x1;
			}
			
			public float getX1(){
				return x1;
			}
			
			public void setY1(float y1){
				this.y1 = y1;
			}
			
			public float getY1(){
				return y1;
			}
			
			public void setX2(float x2){
				this.x2 = x2;
			}
			
			public float getX2(){
				return x2;
			}
			
			public void setY2(float y2){
				this.y2 = y2;
			}
			
			public float getY2(){
				return y2;
			}
			
			public void setLeaf(boolean asLeaf){
				this.asLeaf = asLeaf;
			}
			
			public boolean getLeaf(){
				return asLeaf;
			}
			
			//add point to node (for leaf node)
			public void addPoint2Node(Point point){
				points.add(point);
			} 
			
			//add child to node (for normal node)
			public void addNode2Node(Node node){
				childNodes.add(node);
			} 
			
			
		}


		
		public static class Point{
			private float x;
			private float y;
			private int id;
			
			public Point(){}
			
			public Point(float x, float y, int id){
				this.x = x;
				this.y = y;
				this.id = id;
			}
			
			public void setX(float x){
				this.x = x;
			}
			
			public float getX(){
				return x;
			}
			
			public void setY(float y){
				this.y = y;
			}
			
			public float getY(){
				return y;
			}
			
			public void setId(int id){
				this.id = id;
			}
			
			public float getId(){
				return id;
			}
			
			
		}
	}

