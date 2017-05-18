import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.*;

public class Assignment1 {

	private final static int B = 5;
	private final static int zeropoint4B=(int) Math.ceil(0.4*B); 
	static ArrayList<Node> nodeList = new ArrayList<Node>();
	//static ArrayList<Node> allNodes=new ArrayList<Node>();
	
	static HashMap<Integer,Node> allNodes = new HashMap<>();
	static int ID=1;
	
	public static void main(String[] args) {
		System.out.println("0.4B = "+zeropoint4B);
		//A list containing all nodes
		
		//assignment node id
		//int nodeID=1;

		//build an R-tree
		Point point1 = new Point(0,5,1);
//		Point point2 = new Point(5,2,2);
//		Point point3 = new Point(1,4,3);
//		Point point4 = new Point(6,1,4);
//		Point point5 = new Point(-1,3,5);
//		Point point6 = new Point(8,-1,6);
		Node node1 = new Node(0, null,null,true,ID);
		allNodes.put(ID,node1);
		ID=ID+1;
		
//		nodeNum=nodeNum+1;
		Insertion(node1,point1);
		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());

//		Insertion(node1,point2);
//		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
//		Insertion(node1,point3);
//		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
//		Insertion(node1,point4);
//		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
//		Insertion(node1,point5);
//		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());;
//		Insertion(node1,point6);
//		System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
		node1.getAllId();
		
		
	}
	
		//Find the leaf node that contains the point
		
		//Insertion
		public static void Insertion(Node root, Point point){
			
			//check if the node(root) to be inserted is the leafnode
			if(root.getLeaf() == true){
				//add point to root
				root.addPoint2Node(point);
				
				//check if the node overflows
				if(root.points.size()>B){
					//handle overflow
					//split first
					ArrayList<Node> twoSubNodes=SplitLeaf(root);
					//System.out.println(twoSubNodes.get(0).points);
					
					//check if the 'root' is the tree's root
					//if yes, the 'root' is both the tree root and leaf node
					if(root.parentNodeID==0){
						// create a new node replacing root with no child nodes
						allNodes.put(ID, new Node(0, null,null,false,ID));
						ID=ID+1;
						//add the two new split child nodes to all nodes list
						allNodes.put(ID, new Node(ID-1, null, twoSubNodes.get(0).points, true,ID));	
						ID=ID+1;
						allNodes.put(ID, new Node(ID-2, null, twoSubNodes.get(1).points, true,ID));
						ID=ID+1;
						//add two nodes to their parent node's child node list						
						allNodes.get(allNodes.size()-3).childNodes.add(allNodes.get(allNodes.size()-2));
						allNodes.get(allNodes.size()-3).childNodes.add(allNodes.get(allNodes.size()-1));
						allNodes.remove(root);
					}else{
						//update MBR of 
					}
					
					
					
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
		public static ArrayList<Node> SplitLeaf(Node leafnode){
			
			// initiate the smallest perimeter to be a negative integer
			float smallestPerimeter=-1;
			// initiate the best split Set S1 and S2
			ArrayList<Point> bestS1=new ArrayList<Point>();
			ArrayList<Point> bestS2=new ArrayList<Point>();
			//initiate two new nodes
			Node newLeafNode1 = new Node();
			Node newLeafNode2 = new Node();
			newLeafNode1.asLeaf=true;
			newLeafNode2.asLeaf=true;
			//initiate final split two nodes list
			ArrayList<Node> twoNodesResult= new ArrayList<Node>();
			
			//******************//duplicate the following to sort y
			//sort the points by X values	
			ArrayList<Point> sortedXPoint= leafnode.points;
			Collections.sort(sortedXPoint, new Comparator<Point>(){
				public int compare(Point p1, Point p2){
					int result =Float.compare(p1.getX(), p2.getX());
					return result;
				}
			});
			
			//calculate the perimeter sum of MBR(S1) and MBR(S2); 
			//record it if this is the best split so far
			for(int i=zeropoint4B;i<=B-zeropoint4B+1;i++){
				ArrayList<Point> S1=new ArrayList<Point>();
				ArrayList<Point> S2=new ArrayList<Point>();
				
				for(int j=0;j<i;j++){
					S1.add(sortedXPoint.get(j));
				}
				for(int j=i;j<(B+1);j++){
					S2.add(sortedXPoint.get(j));
				}
				
				//store the first point's Y in S1 to be the smallest/largest y value at first
				float minYS1=S1.get(0).getY();
				float maxYS1=S1.get(0).getY();
				//find smallest perimeter, since x is sorted, find min and max y for S1 & S2.
				for (int j=0;j<S1.size();j++){
					if(S1.get(j).getY()<=minYS1){
						minYS1=S1.get(j).getY();
					}
					if(S1.get(j).getY()>=maxYS1){
						maxYS1=S1.get(j).getY();
					}
				}
				
				//store the first point's Y in S2 to be the smallest/largest y value at first
				float minYS2=S2.get(0).getY();
				float maxYS2=S2.get(0).getY();
				//find smallest perimeter, since x is sorted, find min and max y for S1 & S2.
				for (int j=0;j<S2.size();j++){
					if(S2.get(j).getY()<=minYS2){
						minYS2=S2.get(j).getY();
					}
					if(S2.get(j).getY()>=maxYS2){
						maxYS2=S2.get(j).getY();
					}
				}	
				
				float perimeterS1X=(S1.get(S1.size()-1).getX()-S1.get(0).getX())*2+(maxYS1-minYS1)*2;
				float perimeterS2X=(S2.get(S2.size()-1).getX()-S2.get(0).getX())*2+(maxYS2-minYS2)*2;
				float totalPerimeterX=perimeterS1X+perimeterS2X;
				
				if(smallestPerimeter==-1){ 				//no need to add
					smallestPerimeter=totalPerimeterX;  //these lines
					bestS1=S1;							//for
					bestS2=S2;							//Y
				}else{									
					if(totalPerimeterX<=smallestPerimeter){
						smallestPerimeter=totalPerimeterX;
						bestS1=S1;
						bestS2=S2;
					}
				}
					
			}
			
			

			//sort the points by Y values
			ArrayList<Point> sortedYPoint= leafnode.points;
			Collections.sort(sortedYPoint, new Comparator<Point>(){
				public int compare(Point p1, Point p2){
					int result =Float.compare(p1.getY(), p2.getY());
					return result;
				}
			});
			//calculate the perimeter sum of MBR(S1) and MBR(S2); 
			//record it if this is the best split so far
			for(int i=zeropoint4B;i<=B-zeropoint4B+1;i++){
				ArrayList<Point> S1Y=new ArrayList<Point>();
				ArrayList<Point> S2Y=new ArrayList<Point>();
				
				for(int j=0;j<i;j++){
					S1Y.add(sortedYPoint.get(j));
				}
				for(int j=i;j<(B+1);j++){
					S2Y.add(sortedYPoint.get(j));
				}
				
				//store the first point's X in S1 to be the smallest/largest x value at first
				float minXS1=S1Y.get(0).getX();
				float maxXS1=S1Y.get(0).getX();
				//find smallest perimeter, since Y is sorted, find min and max x for S1 & S2.
				for (int j=0;j<S1Y.size();j++){
					if(S1Y.get(j).getX()<=minXS1){
						minXS1=S1Y.get(j).getX();
					}
					if(S1Y.get(j).getX()>=maxXS1){
						maxXS1=S1Y.get(j).getX();
					}
				}
				
				//store the first point's X in S2 to be the smallest/largest X value at first
				float minXS2=S2Y.get(0).getX();
				float maxXS2=S2Y.get(0).getX();
				//find smallest perimeter, since Y is sorted, find min and max X for S1 & S2.
				for (int j=0;j<S2Y.size();j++){
					if(S2Y.get(j).getX()<=minXS2){
						minXS2=S2Y.get(j).getX();
					}
					if(S2Y.get(j).getX()>=maxXS2){
						maxXS2=S2Y.get(j).getX();
					}
				}	
				
				float perimeterS1Y=(S1Y.get(S1Y.size()-1).getY()-S1Y.get(0).getY())*2+(maxXS1-minXS1)*2;
				float perimeterS2Y=(S2Y.get(S2Y.size()-1).getX()-S2Y.get(0).getX())*2+(maxXS2-minXS2)*2;
				float totalPerimeterY=perimeterS1Y+perimeterS2Y;
				
				if(totalPerimeterY<=smallestPerimeter){
						smallestPerimeter=totalPerimeterY;
						bestS1=S1Y;
						bestS2=S2Y;
				}
				
					
			}
			
			newLeafNode1.points=bestS1;
			newLeafNode2.points=bestS2;
			twoNodesResult.add(newLeafNode1);
			twoNodesResult.add(newLeafNode2);		
			return twoNodesResult;
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
			//parent node
			private int parentNodeID;
			//Node ID
			private int id;
			
			public Node(){}
			
			public Node(int parentNodeID, ArrayList<Node> childNodes, ArrayList<Point> points, boolean asLeaf, int id){
				this.parentNodeID = parentNodeID;
				this.childNodes = childNodes;
				this.points = points;
				this.asLeaf = asLeaf;
				this.id=id;
			}
			
//			public Node(float x1, float y1, float x2, float y2, boolean asLeaf){
//				this.x1 = x1;
//				this.y1 = y1;
//				this.x2 = x2;
//				this.y2 = y2;
//				this.asLeaf = asLeaf;
//			}
			
			public void setID(int id){
				this.id = id;
			}
			
			public int getID(){
				return id;
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

