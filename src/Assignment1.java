import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Assignment1 {

	private final static int B = 5;
	private final static int zeropoint4B=(int) Math.ceil(0.4*B); 
	static ArrayList<Node> nodeList = new ArrayList<Node>();
	//static ArrayList<Node> allNodes=new ArrayList<Node>();
	
	static HashMap<Integer,Node> allNodes = new HashMap<>();
	static HashMap<Integer,Integer> allNodesParentID = new HashMap<>();
	static int ID=1;
	
	public static void main(String[] args) {
		System.out.println("0.4B = "+zeropoint4B);
		//A list containing all nodes
		
		//assignment node id
		//int nodeID=1;

		//build an R-tree
		Point point1 = new Point(0,5,1);
		Point point2 = new Point(5,2,2);
		Point point3 = new Point(1,4,3);
		Point point4 = new Point(6,1,4);
		Point point5 = new Point(-1,3,5);
		Point point6 = new Point(8,-1,6);
		Point point7 = new Point(-5,-2,7);
		Point point8 = new Point(-4,-1,8);
		Point point9 = new Point(-4,-2,9);
		
		Point point10 = new Point(9,-2,10);
		Point point11 = new Point(18,-6,11);
		Point point12 = new Point(19,-5,12);
		Point point13 = new Point(21,-10,13);
		Point point14 = new Point(22,-16,14);
		Point point15 = new Point(22,-15,15);
		Point point16 = new Point(20,-17,16);
		Point point17 = new Point(12,-17,17);
		
//		Node node1= new Node();
		Node node1 = new Node(0, new ArrayList<Node>(),new ArrayList<Point>(),true,ID);
		allNodes.put(ID,node1);
		allNodesParentID.put(ID, 0);
		ID=ID+1;	
		
		
//		nodeNum=nodeNum+1;
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point1);
	//	System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());

		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point2);
	//	System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point3);
	//	System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point4);
	//	System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point5);
	//	System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());;
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point6);
	//	System.out.println(node1.x1+" "+node1.y1+" "+node1.x2+" "+node1.y2+" size: "+node1.points.size());

		
		System.out.println("Node 2's points (should be nothing)\n");
		allNodes.get(2).getAllPointsId();
		System.out.println("Node 2's nodes (should be 3,4)\n");
		allNodes.get(2).getAllNodesId();
		System.out.println("Node 3's points (should be 2,4,6)\n");
		allNodes.get(3).getAllPointsId();
		System.out.println("Node 4's points (should be 1,3,5)\n");
		allNodes.get(4).getAllPointsId();
		
		//test choose sub-tree
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point7);
		
		System.out.println("Node 3's points (should be 2,4,6)\n");
		allNodes.get(3).getAllPointsId();
		System.out.println("Node 4's points (should be 1,3,5,7)\n");
		allNodes.get(4).getAllPointsId();
		
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point8);
		
		System.out.println("Node 3's points (should be 2,4,6)\n");
		allNodes.get(3).getAllPointsId();
		System.out.println("Node 4's points (should be 1,3,5,7,8)\n");
		allNodes.get(4).getAllPointsId();
		
		
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point9);

		System.out.println("Node 5's points (should be 7,8,9)\n");
		allNodes.get(5).getAllPointsId();
		System.out.println("Node 6's points (should be 1,3,5)\n");
		allNodes.get(6).getAllPointsId();
		
		//test more points until the tree root node overflows
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point10);
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point11);
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point12);
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point13);
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point14);
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point15);
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point16);
		Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),point17);
		
		//there should be totally 6 nodes
		//CORRECT Allocation of points should be
		//2,4,6,10//7,8,9//1,3,5//11,12//13,14,15,16,17,18
		List<Integer> allnodesID = new ArrayList<Integer>(allNodes.keySet());
		System.out.println(Arrays.toString(allnodesID.toArray()));
		//print all nodes' points
		for(int i=0;i<allnodesID.size();i++){
			if(allNodes.get(allnodesID.get(i)).points.isEmpty()){
				System.out.println("node "+allnodesID.get(allnodesID.get(i))+"'s childnodes ");
				allNodes.get(allnodesID.get(i)).getAllNodesId();
			}else{
			System.out.println("node "+allnodesID.get(i));
			allNodes.get(allnodesID.get(i)).getAllPointsId();
			}
		}
		
	}
	
	//used to get the HashMap value sets by giving key, used to delete nodes in parent list who have same parent
	public static ArrayList<Integer> getkeySetsFromValue(HashMap<Integer,Integer> hm, int parentID){
		ArrayList<Integer> keySets = new ArrayList<Integer>();
		for (HashMap.Entry<Integer, Integer> entry : hm.entrySet()) {
			  if (entry.getValue().equals(parentID)) {
				  keySets.add(entry.getKey());
			  }
		}
		return keySets;
	}
	
	//used to get the HashMap value by giving key, used to find the node that doesn't have a parent (root)
	public static Object getKeyFromValue(HashMap hm, Object value) {
	    for (Object o : hm.keySet()) {
	      if (hm.get(o).equals(value)) {
	        return o;
	      }
	    }
	    return null;
	}
	
	//A method to check if a node in the list has specific id (0)
	public static boolean containsId(ArrayList<Node> parentNodes, int id) {
	    for (Node object : parentNodes) {
	        if (object.getID() == id) {
	            return true;
	        }
	    }
	    return false;
	}
	
	//Update all nodes' MBR (x1,y1,x2,y2) to be able to locate the insert point.
	public static void updateAllMBR(){
		//get all leafnodes and put
		ArrayList<Node> allLeafNodes = new ArrayList<Node>();
		for (Integer key : allNodes.keySet()) {
		    if(allNodes.get(key).asLeaf==true){
		    	allLeafNodes.add(allNodes.get(key));
		    }    
		}
		//print all leaf nodes
		System.out.println("all leaf node IDs: ");
		for (int i=0; i<allLeafNodes.size();i++) {
		    System.out.println("ID="+allLeafNodes.get(i).getID()); 
		}
		//update each leafnode's MBR and store their parent's id, stop updating process 
		//until the node doesn't have a parent (it's root)
		ArrayList<Integer> parentNodesID = new ArrayList<Integer>();
		
		//update all leafnodes first, get all leafnode's parent to the parent list
		//if the parent exists, ignore 
		for (int i=0; i<allLeafNodes.size();i++) {
			allLeafNodes.get(i).x1=allLeafNodes.get(i).points.get(0).x;
			allLeafNodes.get(i).y1=allLeafNodes.get(i).points.get(0).y;
			allLeafNodes.get(i).x2=allLeafNodes.get(i).points.get(0).x;
			allLeafNodes.get(i).y2=allLeafNodes.get(i).points.get(0).y;
			for(int j=1;j<allLeafNodes.get(i).points.size();j++){
				if(allLeafNodes.get(i).points.get(j).x<allLeafNodes.get(i).x1){
					allLeafNodes.get(i).x1=allLeafNodes.get(i).points.get(j).x;
				}
				if(allLeafNodes.get(i).points.get(j).x>allLeafNodes.get(i).x2){
					allLeafNodes.get(i).x2=allLeafNodes.get(i).points.get(j).x;
				}
				if(allLeafNodes.get(i).points.get(j).y>allLeafNodes.get(i).y1){
					allLeafNodes.get(i).y1=allLeafNodes.get(i).points.get(j).y;
				}
				if(allLeafNodes.get(i).points.get(j).y<allLeafNodes.get(i).y2){
					allLeafNodes.get(i).y2=allLeafNodes.get(i).points.get(j).y;
				}				
			}
			System.out.println("x1="+allLeafNodes.get(i).x1+", "+"y1="+allLeafNodes.get(i).y1+", "+"x2="+allLeafNodes.get(i).x2+", "+"y2="+allLeafNodes.get(i).y2);
			//if parent list doesn't have this node's parent node ID, add to the list
			if(!parentNodesID.contains((allLeafNodes.get(i).parentNodeID))){
				parentNodesID.add((allLeafNodes.get(i).parentNodeID));
			}
			else{
				System.out.println("node ID"+allLeafNodes.get(i).parentNodeID +" exists");
			}	
		}
		//while the parent id list doesn't have 0 (hasn't reach the root)
		while(!parentNodesID.contains(0)){
			//create a new parent nodes ID arrayList to replace the original one
			ArrayList<Integer> parentNodesID2= new ArrayList<Integer>();			
			//iterate the whole parent id list and update each parent node
			for(int id : parentNodesID){
				  allNodes.get(id).x1=allNodes.get(id).childNodes.get(0).x1;
				  allNodes.get(id).y1=allNodes.get(id).childNodes.get(0).y1;
				  allNodes.get(id).x2=allNodes.get(id).childNodes.get(0).x2;
				  allNodes.get(id).y2=allNodes.get(id).childNodes.get(0).y2;
				  //update this parent node's MBR
				  for(int j=1;j<allNodes.get(id).childNodes.size();j++){
					  if(allNodes.get(id).childNodes.get(j).x1<allNodes.get(id).x1){
						  allNodes.get(id).x1=allNodes.get(id).childNodes.get(j).x1;
					  }
					  if(allNodes.get(id).childNodes.get(j).x2>allNodes.get(id).x2){
						  allNodes.get(id).x2=allNodes.get(id).childNodes.get(j).x2;
					  }
					  if(allNodes.get(id).childNodes.get(j).y1>allNodes.get(id).y1){
						  allNodes.get(id).y1=allNodes.get(id).childNodes.get(j).y1;
					  }
					  if(allNodes.get(id).childNodes.get(j).y2<allNodes.get(id).y2){
						  allNodes.get(id).y2=allNodes.get(id).childNodes.get(j).y2;
					  }
				  }
				  //show its updated MBR
				  System.out.println("Parent node ID:"+ allNodes.get(id).getID()+" x1="+allNodes.get(id).x1+" y1="+allNodes.get(id).y1+" x2="+allNodes.get(id).x2+" y2="+allNodes.get(id).y2);
				   //if parent list doesn't have this node's parent node ID, add to the list
				  if(!parentNodesID2.contains((allNodes.get(id).parentNodeID))){
						parentNodesID2.add(allNodes.get(id).parentNodeID);
				  }
				  else{
						System.out.println("node ID"+allNodes.get(id).parentNodeID +" exists");
				  }
			}
			//delete all parent nodes ID in the original parent id list, change it to the second list
			parentNodesID.clear();
			parentNodesID=parentNodesID2;
					
		}
	}
	
	
	//Handle overflow
	public static void HandleOverflow(Node root){
		
		//Split first
		ArrayList<Node> twoSubLeafNodes = new ArrayList<Node>();
		ArrayList<Node> twoSubInternalNodes = new ArrayList<Node>();
		
		//**************check if this node is leaf node or not
		// then do different split
		if(root.asLeaf==true){
			twoSubLeafNodes=SplitLeaf(root);	
			
			if(root.parentNodeID==0){
				//remove the tree root first !!!!!should be two hashmaps all nodes+parent lists
				allNodes.remove(getKeyFromValue(allNodesParentID,0));
				allNodesParentID.remove(getKeyFromValue(allNodesParentID,0));
				
				// create a new node replacing root with no child nodes						
				allNodes.put(ID, new Node(0, new ArrayList<Node>(),new ArrayList<Point>(),false,ID));
				allNodesParentID.put(ID, 0);
				ID=ID+1;
				//add the two new split child nodes to all nodes list and parent list
				allNodes.put(ID, new Node(ID-1, new ArrayList<Node>(), twoSubLeafNodes.get(0).points, true,ID));	
				allNodesParentID.put(ID, ID-1);
				ID=ID+1;
				allNodes.put(ID, new Node(ID-2, new ArrayList<Node>(), twoSubLeafNodes.get(1).points, true,ID));
				allNodesParentID.put(ID, ID-2);
				ID=ID+1;
				//add two nodes to their parent node's child node list							
				allNodes.get(ID-3).childNodes.add(allNodes.get(ID-2));
				allNodes.get(ID-3).childNodes.add(allNodes.get(ID-1));
			}
			else{
				//this node has a parent (parentNodeID!=0)
				//get this node's parent
				Node parentNode = allNodes.get((root.parentNodeID));
				
				//*********Remove this node***************
				//*********follow this order**************
				//Remove this node's parent node from hashmap
				allNodesParentID.remove(root.id);
				//Remove this node from it's parent node's child node list
				parentNode.childNodes.remove(root);
				//Remove this node from all nodes list
				allNodes.remove(root.id);

				//create two new split leaf nodes
				//**********First Node********
				//1 all nodes list
				allNodes.put(ID, new Node(parentNode.id, new ArrayList<Node>(), twoSubLeafNodes.get(0).points, true,ID));
				//2 it's parent list
				allNodesParentID.put(ID, parentNode.id);
				//3 add to its parent node's child node list
				parentNode.childNodes.add(allNodes.get(ID));
				//4 itself child node list (no need)
				//5 it's child nodes' parent list (no need)
				//6 ID++
				ID=ID+1;
				
				//**********Second Node********
				//1 all nodes list
				allNodes.put(ID, new Node(parentNode.id, new ArrayList<Node>(), twoSubLeafNodes.get(1).points, true,ID));
				//2 it's parent list
				allNodesParentID.put(ID, parentNode.id);
				//3 add to its parent node's child node list
				parentNode.childNodes.add(allNodes.get(ID));
				//4 itself child node list (no need)
				//5 it's child nodes' parent list (no need)
				//6 ID++
				ID=ID+1;
				
				
				// Check if its parent node overflows
				if (parentNode.childNodes.size()>B){
					HandleOverflow(parentNode);
				}
				
				
				
			}	
			
		}
		else{
			updateAllMBR();			
			twoSubInternalNodes=SplitInternal(root);
			if(root.parentNodeID==0){
				
				System.out.println("*******Split Internal & Root Node*******");
				//create a new tree root node and delete the previous one
				//also cut all the connections of the previous root
				//remove process
				//1 cut the connections in parent list
				//find all nodes whose parent is this node and delete the relation
				for(int i=0;i<root.childNodes.size();i++){
					//reomove this ID and its value from parent HashMap
					allNodesParentID.remove(root.childNodes.get(i).getID());
				}
				//2 remove this node's id from parent list which is (root.id,0)
				allNodesParentID.remove(root.id);
				//3 remove this node from allNodes list
				allNodes.remove(root.id);
				
				//create a new tree root with parent 0, add this node to two lists
				Node newRoot= new Node(0,new ArrayList<Node>(),new ArrayList<Point>(),false,ID);
				allNodes.put(ID, newRoot);
				allNodesParentID.put(ID, 0);
				ID=ID+1;
				
				//create two new nodes
				//1 add to all nodes list
				Node internalNode1= new Node(ID-1, twoSubInternalNodes.get(0).childNodes, new ArrayList<Point>(), false,ID);
				allNodes.put(ID, internalNode1);	
				allNodesParentID.put(ID, ID-1);
				for(int j=0;j<internalNode1.childNodes.size();j++){
					internalNode1.childNodes.get(j).parentNodeID=internalNode1.id;
					allNodesParentID.put(internalNode1.childNodes.get(j).id, internalNode1.id);
				}
				newRoot.childNodes.add(internalNode1);
				ID=ID+1;
				
				Node internalNode2 = new Node(ID-2, twoSubInternalNodes.get(1).childNodes, new ArrayList<Point>(), false,ID);
				allNodes.put(ID, internalNode2);
				allNodesParentID.put(ID, ID-2);
				for(int j=0;j<internalNode2.childNodes.size();j++){
					internalNode2.childNodes.get(j).parentNodeID=internalNode2.id;
					allNodesParentID.put(internalNode2.childNodes.get(j).id, internalNode2.id);
				}
				newRoot.childNodes.add(internalNode2);
				ID=ID+1;
			
			}
			else{
				//This is not a root node, it's an internal node.  
				//no need to delete the root and create new root
				System.out.println("*******Split Internal NOT Root Node*******");
				
				
				
			}
			
		}
											
		//check if the 'root' is the tree's root
		//if yes, the 'root' is both the tree root and leaf node

	}
	
		//Insertion
		public static void Insertion(Node root, Point point){
			
			//check if the node(root) to be inserted is a leaf node
			if(root.getLeaf() == true){
				//add point to root
				root.addPoint2Node(point);
				
				//check if the node overflows
				if(root.points.size()>B){
					//handle overflow
					HandleOverflow(root);						
				}			
			}else{
			// the node(root) to be inserted is not a leaf node, which means
			// I'm gonna to choose the subtree of it, until I find the leaf node
			
			//internal node
			
				//Choose subtree function
				System.out.println("choose subtree for "+point.id);
				
				
				//************update all nodes' MBR first************
				updateAllMBR();
				//Since this node is definitely NOT a leaf node 
				//firstly look at its childnodes, and
				//find the next node to be inserted in by comparing the 
				// resulting MBRs
				ArrayList<Node> itsChildNodes = new ArrayList<Node>();
				itsChildNodes= root.childNodes;
				//iterate each child node and calculate the resulting MBR, 
				//storing the best node if it has a smaller increase of MBR perimeter
				//calculating the first node first and store it first
				float newX1=itsChildNodes.get(0).x1;
				float newY1=itsChildNodes.get(0).y1;
				float newX2=itsChildNodes.get(0).x2;
				float newY2=itsChildNodes.get(0).y2;
				float newPerimeter=0;
				float minIncrease =0;
				Node nextNode = new Node();
				
				if(point.x<itsChildNodes.get(0).x1){
					newX1=point.x;
				}
				if(point.x>itsChildNodes.get(0).x2){
					newX2=point.x;
				}
				if(point.y>itsChildNodes.get(0).y1){
					newY1=point.y;
				}
				if(point.y<itsChildNodes.get(0).y2){
					newY2=point.y;
				}
				//calculate MBR perimeter
				newPerimeter=(newX2-newX1)*2+(newY1-newY2)*2;
				minIncrease=newPerimeter-((itsChildNodes.get(0).x2-itsChildNodes.get(0).x1)*2+(itsChildNodes.get(0).y1-itsChildNodes.get(0).y2)*2);
				System.out.println("ID:"+itsChildNodes.get(0).getID()+"'s new perimeter is "+newPerimeter);
				System.out.println("ID:"+itsChildNodes.get(0).getID()+"'s min Increase is "+minIncrease);
				
				nextNode=itsChildNodes.get(0);	
				System.out.println("next node is: "+nextNode.id);
				
				for(int j=1;j<itsChildNodes.size();j++){
					newX1=itsChildNodes.get(j).x1;
					newY1=itsChildNodes.get(j).y1;
					newX2=itsChildNodes.get(j).x2;
					newY2=itsChildNodes.get(j).y2;
					
					if(point.x<itsChildNodes.get(j).x1){
						newX1=point.x;
					}
					if(point.x>itsChildNodes.get(j).x2){
						newX2=point.x;
					}
					if(point.y>itsChildNodes.get(j).y1){
						newY1=point.y;
					}
					if(point.y<itsChildNodes.get(j).y2){
						newY2=point.y;
					}
					newPerimeter=(newX2-newX1)*2+(newY1-newY2)*2;
					float increase=newPerimeter-((itsChildNodes.get(j).x2-itsChildNodes.get(j).x1)*2+(itsChildNodes.get(j).y1-itsChildNodes.get(j).y2)*2);
					if(increase<minIncrease){
						minIncrease=increase;
						nextNode=itsChildNodes.get(j);	
					}
					System.out.println("ID:"+itsChildNodes.get(j).getID()+"'s new perimeter is "+newPerimeter);
					System.out.println("ID:"+itsChildNodes.get(j).getID()+"'s min Increase is "+minIncrease);				
					System.out.println("next node is: "+nextNode.id);
				
					
				}
				Insertion(nextNode, point);
				
			}
		}
		//Split 1) Internal 2)Root but not leaf Node	
		public static ArrayList<Node> SplitInternal(Node internalnode){
			// this node must have child nodes, split it into two internal nodes.
			
			// initiate the smallest perimeter to be a negative integer
			float smallestPerimeter=-1;
			// initiate the best split Set S1 and S2
			ArrayList<Node> bestS1=new ArrayList<Node>();
			ArrayList<Node> bestS2=new ArrayList<Node>();
			//initiate two new internal nodes
			//these two nodes only have their child nodes, 
			//and have no relation to their parent or added to other list
			Node newInternalNode1 = new Node();
			Node newInternalNode2 = new Node();
			//firstly they are not leaf node
			newInternalNode1.asLeaf=false;
			newInternalNode2.asLeaf=false;
			//initiate final split two nodes list
			ArrayList<Node> twoNodesResult= new ArrayList<Node>();
			
			
			//******************//duplicate the following to sort X2
			
			//sort the points by X left boundaries values (X1)	
			ArrayList<Node> sortedX1Node= internalnode.childNodes;
			Collections.sort(sortedX1Node, new Comparator<Node>(){
				public int compare(Node node1, Node node2){
					int result =Float.compare(node1.x1, node2.x1);
					return result;
				}
			});
			System.out.println("Sorted nodes based on X1");
			for(int i=0;i<sortedX1Node.size();i++){			
				System.out.println("ID:"+sortedX1Node.get(i).id+" X1:"+sortedX1Node.get(i).x1+" X2:"+sortedX1Node.get(i).x2+" Y1:"+sortedX1Node.get(i).y1+" Y2:"+sortedX1Node.get(i).y2);
			}
			
			
			//calculate the perimeter sum of MBR(S1) and MBR(S2); 
			//record it if this is the best split so far
			for(int i=zeropoint4B;i<=B-zeropoint4B+1;i++){
				ArrayList<Node> S1X1=new ArrayList<Node>();
				ArrayList<Node> S2X1=new ArrayList<Node>();
				
				for(int j=0;j<i;j++){
					S1X1.add(sortedX1Node.get(j));
				}
				for(int j=i;j<(B+1);j++){
					S2X1.add(sortedX1Node.get(j));
				}
				System.out.println("S1X1\n");
				for(int j=0;j<S1X1.size();j++){
					
					System.out.println("ID:"+S1X1.get(j).id+" X1:"+S1X1.get(j).x1+" X2:"+S1X1.get(j).x2+" Y1:"+S1X1.get(j).y1+" Y2:"+S1X1.get(j).y2);
				}
				System.out.println("S2X1\n");
				for(int j=0;j<S2X1.size();j++){

					System.out.println("ID:"+S2X1.get(j).id+" X1:"+S2X1.get(j).x1+" X2:"+S2X1.get(j).x2+" Y1:"+S2X1.get(j).y1+" Y2:"+S2X1.get(j).y2);
					
				}
				
				//Since X1 is sorted, find the ***x2***,y1 y2 value of the MBR
				//why find x2, because it's not certain that the last node with largest x1
				//will also have the largest x2 as well
				//store the first node's Y2 Y1 in S1X1 to be the smallest/largest y value at first
				//store the first node's X2 in S1X1
				float minYS1X1=S1X1.get(0).y2;
				float maxYS1X1=S1X1.get(0).y1;
				float maxX2_S1X1=S1X1.get(0).x2;
				//find smallest perimeter, since x is sorted, find min and max y for S1 & S2.
				//since y1 is the max y for each node's MBR, only need to look at y1 for all 
				//nodes in S1X1 to get the maximum y
				//Same as y2
				for (int j=0;j<S1X1.size();j++){
					if(S1X1.get(j).y2<=minYS1X1){
						minYS1X1=S1X1.get(j).y2;
					}
					if(S1X1.get(j).y1>=maxYS1X1){
						maxYS1X1=S1X1.get(j).y1;
					}
					if(S1X1.get(j).x2>=maxX2_S1X1){
						maxX2_S1X1=S1X1.get(j).x2;
					}
				}
				System.out.println("min Y S1X1: "+minYS1X1);
				System.out.println("max Y S1X1: "+maxYS1X1);
				System.out.println("max X2 S1X1: "+maxX2_S1X1);
				
				//store the first node's Y in S2 to be the smallest/largest y value at first
				float minYS2X1=S2X1.get(0).y2;
				float maxYS2X1=S2X1.get(0).y1;
				float maxX2_S2X1=S2X1.get(0).x2;
				//find smallest perimeter, since x is sorted, find min and max y for S1 & S2.
				for (int j=0;j<S2X1.size();j++){
					if(S2X1.get(j).y2<=minYS2X1){
						minYS2X1=S2X1.get(j).y2;
					}
					if(S2X1.get(j).y1>=maxYS2X1){
						maxYS2X1=S2X1.get(j).y1;
					}
					if(S2X1.get(j).x2>=maxX2_S2X1){
						maxX2_S2X1=S2X1.get(j).x2;
					}
				}	
				System.out.println("min Y S2X1: "+minYS2X1);
				System.out.println("max Y S2X1: "+maxYS2X1);
				System.out.println("max X2 S2X1: "+maxX2_S2X1);
				
				
				
				
				float perimeterS1X1=(maxX2_S1X1 - S1X1.get(0).x1)*2+(maxYS1X1-minYS1X1)*2;
				float perimeterS2X1=(maxX2_S2X1 - S2X1.get(0).x1)*2+(maxYS2X1-minYS2X1)*2;
				float totalPerimeterX1=perimeterS1X1+perimeterS2X1;
				
				System.out.println("total perimeter on X1: "+totalPerimeterX1);
				
				
				if(smallestPerimeter==-1){ 				//no need to add
					smallestPerimeter=totalPerimeterX1;  //these lines
					bestS1=S1X1;							//for
					bestS2=S2X1;							//Y
				}else{									
					if(totalPerimeterX1<=smallestPerimeter){
						smallestPerimeter=totalPerimeterX1;
						bestS1=S1X1;
						bestS2=S2X1;
					}
				}					
			}
			
			System.out.println("smallest perimeter for X1: "+smallestPerimeter);
			System.out.println("best S1 so far for X1");
			for(int j=0;j<bestS1.size();j++){
				
				System.out.println("ID:"+bestS1.get(j).id+" X1"+bestS1.get(j).x1+" X2:"+bestS1.get(j).x2+" Y1:"+bestS1.get(j).y1+" Y2:"+bestS1.get(j).y2);
			}
			System.out.println("best S2 so far for X1");
			for(int j=0;j<bestS2.size();j++){
				
				System.out.println("ID:"+bestS2.get(j).id+" X1"+bestS2.get(j).x1+" X2:"+bestS2.get(j).x2+" Y1:"+bestS2.get(j).y1+" Y2:"+bestS2.get(j).y2);
			}
			
			
			
			
			
			
			
			newInternalNode1.childNodes=bestS1;
			newInternalNode2.childNodes=bestS2;
			twoNodesResult.add(newInternalNode1);
			twoNodesResult.add(newInternalNode2);		
			return twoNodesResult;
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
			for(int i=0;i<sortedXPoint.size();i++){
				System.out.println(sortedXPoint.get(i).getId()+" "+sortedXPoint.get(i).getX()+" "+sortedXPoint.get(i).getY());
			}
			
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
				System.out.println("S1\n");
				for(int j=0;j<S1.size();j++){
					
					System.out.println(S1.get(j).getId()+" "+S1.get(j).getX()+" "+S1.get(j).getY());
				}
				System.out.println("S2\n");
				for(int j=0;j<S2.size();j++){
					
					System.out.println(S2.get(j).getId()+" "+S2.get(j).getX()+" "+S2.get(j).getY());
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
				System.out.println("min Y S1: "+minYS1);
				System.out.println("max Y S1: "+maxYS1);
				
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
				System.out.println("min Y S2: "+minYS2);
				System.out.println("max Y S2: "+maxYS2);
				
				
				float perimeterS1X=(S1.get(S1.size()-1).getX()-S1.get(0).getX())*2+(maxYS1-minYS1)*2;
				float perimeterS2X=(S2.get(S2.size()-1).getX()-S2.get(0).getX())*2+(maxYS2-minYS2)*2;
				float totalPerimeterX=perimeterS1X+perimeterS2X;
				
				System.out.println("total perimeter: "+totalPerimeterX);
				
				
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
			
			System.out.println("smallest perimeter for x: "+smallestPerimeter);
			System.out.println("best S1 so far for x");
			for(int j=0;j<bestS1.size();j++){
				
				System.out.println(bestS1.get(j).getId()+" "+bestS1.get(j).getX()+" "+bestS1.get(j).getY());
			}
			System.out.println("best S2 so far for x");
			for(int j=0;j<bestS2.size();j++){
				
				System.out.println(bestS2.get(j).getId()+" "+bestS2.get(j).getX()+" "+bestS2.get(j).getY());
			}
			
			
			
			
			
			//sort the points by Y values
			ArrayList<Point> sortedYPoint= leafnode.points;
			Collections.sort(sortedYPoint, new Comparator<Point>(){
				public int compare(Point p1, Point p2){
					int result =Float.compare(p1.getY(), p2.getY());
					return result;
				}
			});
			
			System.out.println("sorted Y points");
			for(int i=0;i<sortedYPoint.size();i++){
				System.out.println(sortedYPoint.get(i).getId()+" "+sortedYPoint.get(i).getX()+" "+sortedXPoint.get(i).getY());
			}
			
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
				System.out.println("S1Y\n");
				for(int j=0;j<S1Y.size();j++){
					
					System.out.println(S1Y.get(j).getId()+" "+S1Y.get(j).getX()+" "+S1Y.get(j).getY());
				}
				System.out.println("S2Y\n");
				for(int j=0;j<S2Y.size();j++){
					
					System.out.println(S2Y.get(j).getId()+" "+S2Y.get(j).getX()+" "+S2Y.get(j).getY());
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
				System.out.println("min X S1Y: "+minXS1);
				System.out.println("max X S1Y: "+maxXS1);
				
				
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
				System.out.println("min X S2Y: "+minXS2);
				System.out.println("max X S2Y: "+maxXS2);
				
				
				float perimeterS1Y=(S1Y.get(S1Y.size()-1).getY()-S1Y.get(0).getY())*2+(maxXS1-minXS1)*2;
				float perimeterS2Y=(S2Y.get(S2Y.size()-1).getY()-S2Y.get(0).getY())*2+(maxXS2-minXS2)*2;
				float totalPerimeterY=perimeterS1Y+perimeterS2Y;
								
				System.out.println("total perimeter: "+totalPerimeterY);
				
				
				if(totalPerimeterY<=smallestPerimeter){
						smallestPerimeter=totalPerimeterY;
						bestS1=S1Y;
						bestS2=S2Y;
				}							
					
			}
			System.out.println("smallest perimeter for Y: "+smallestPerimeter);
			System.out.println("best S1 so far for Y");
			for(int j=0;j<bestS1.size();j++){
				
				System.out.println(bestS1.get(j).getId()+" "+bestS1.get(j).getX()+" "+bestS1.get(j).getY());
			}
			System.out.println("best S2 so far for Y");
			for(int j=0;j<bestS2.size();j++){
				
				System.out.println(bestS2.get(j).getId()+" "+bestS2.get(j).getX()+" "+bestS2.get(j).getY());
			}
			newLeafNode1.points=bestS1;
			newLeafNode2.points=bestS2;
			twoNodesResult.add(newLeafNode1);
			twoNodesResult.add(newLeafNode2);		
			return twoNodesResult;
		}
		
		
		
		
		//Node Class (rectangle)
		public static class Node{
			private float x1;
			private float y1;
			private float x2;
			private float y2;
			boolean asLeaf;
			
			ArrayList<Point> points = new ArrayList<Point>();
			//get all point's id for this node
			public void getAllPointsId(){
				for(int i=0;i<this.points.size();i++){
					System.out.println(this.points.get(i).getId());
				}
			}
			
			//the nodes in this node
			ArrayList<Node> childNodes = new ArrayList<Node>();
			//get all child nodes id for this node
			public void getAllNodesId(){
				for(int i=0;i<this.childNodes.size();i++){
					System.out.println(this.childNodes.get(i).getID());
				}
			}
			
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

