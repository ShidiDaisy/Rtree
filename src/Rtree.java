import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Rtree {
    
	//R-tree initials:
    private final static int B = 100;
    private final static int zeropoint4B=(int) Math.ceil(0.4*B);
    
    static ArrayList<Point> allPoints= new ArrayList<Point>();
    static HashMap<Integer,Node> allNodes = new HashMap<>();
    static HashMap<Integer,Integer> allNodesParentID = new HashMap<>();
    static int ID=1;
    
    //r query initials:
    static int sq_number_of_points=0;
    static int number_of_points=0;
    
    //nn query initials:
    static PriorityQueue<Entry> listH = new PriorityQueue<Entry>(10, Comparator.comparingDouble(Entry::getDist));
    static ArrayList<Rtree.Point> NN = new ArrayList<Rtree.Point>();
    static double nnDist = 0;
    static HashMap<Integer,Double> result = new HashMap<Integer,Double>();
    static double lowestDist = Double.POSITIVE_INFINITY;
    static double finalDist;
    
    public static void main(String[] args) {
        
        System.out.println("0.4B = "+zeropoint4B);
        //A list containing all nodes
        
        //		Node node1= new Node();
        Node node1 = new Node(0, new ArrayList<Node>(),new ArrayList<Point>(),true,ID);
        allNodes.put(ID,node1);
        allNodesParentID.put(ID, 0);
        ID=ID+1;
        
        //TESTING DRAFT
        /*
        //there should be totally 6 nodes
        //CORRECT Allocation of points should be
        //2,4,6,10//7,8,9//1,3,5//11,12//13,14,15,16,17,18
        List<Integer> allnodesID = new ArrayList<Integer>(allNodes.keySet());
        System.out.println(Arrays.toString(allnodesID.toArray()));
        
        //this time should have 9 nodes
        List<Integer> allnodesID_v2 = new ArrayList<Integer>(allNodes.keySet());
        System.out.println("\nAll existing Nodes now (should be 9):\n"+Arrays.toString(allnodesID_v2.toArray()));
        //show tree root id
        System.out.println("Now the Tree root id is:"+getKeyFromValue(allNodesParentID,0));
        */
        
        //*******************BUILD RTREE*******************//
        
        //Get data and build Rtree
        ReadFile.readDataset();
        
        for(int i = 0;i<ReadFile.numOfPts;i++){
        	float pX = Float.parseFloat(ReadFile.xList1.get(i));
        	float pY = Float.parseFloat(ReadFile.yList1.get(i));
        	
            Insertion(allNodes.get(getKeyFromValue(allNodesParentID,0)),new Point(pX,pY,i+1));
        }
       
        updateAllMBR();
        
        System.out.println("\n************Summary**************\n");
        //print all node parents list
        System.out.println("*********print all nodes' parents*********");
        for (Integer key : allNodesParentID.keySet()) {
            System.out.println("The node with ID: "+key + "'s parent is node with ID: " + allNodesParentID.get(key));
        }
        
        System.out.println("\n*********print all leaf node's points*********");
        //print all leaf node's points
        for (Integer key : allNodes.keySet()){
            if(allNodes.get(key).asLeaf==true){
                System.out.println("The points in Leaf Node ID: "+allNodes.get(key).id);
                allNodes.get(key).getAllPointsId();
            }
        }
        
        //******************************************************************//
        //******************************************************************//
        //******************************************************************//
        
        //Write Output
        try{
		    PrintWriter writer = new PrintWriter("RangeQueryResult", "UTF-8");
		    writer.println("************Range Query Result************");
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
        
        
        //perform range query
        ReadFile.ReadRangeQuery("RangeQueryTesting"); //<----------Change name of file
        ArrayList<Float> rqX1 = ReadFile.rqX1;
        ArrayList<Float> rqY1 = ReadFile.rqY1;
        ArrayList<Float> rqX2 = ReadFile.rqX2;
        ArrayList<Float> rqY2 = ReadFile.rqY2;
        ArrayList<Integer> rqResult = new ArrayList<Integer>();
        
        long startTimeR = System.nanoTime();
        for(int i=0; i<100; i++){
        	number_of_points=0;
        	
        	RQuery r1= new RQuery(rqX1.get(i),rqY1.get(i),rqX2.get(i),rqY2.get(i));
        	rangeQuery(allNodes.get(getKeyFromValue(allNodesParentID,0)),r1);
        	System.out.println("There are "+number_of_points+" points found");
        	rqResult.add(number_of_points);
        }
        
        long endTimeR   = System.nanoTime();
        long totalTimeR = endTimeR - startTimeR;
        long avgTimeR = totalTimeR/100;

        
        
        
        
        //Perform NNSearch        
        ReadFile.ReadNNSearchQuery("NNSearchTesting"); //<----------Read File
        ArrayList<Float> nnqX = ReadFile.nnqX;
        ArrayList<Float> nnqY = ReadFile.nnqY;
 
        long startTimeN = System.nanoTime();
        
        for(int i=0; i<100; i++){
        	Rtree.Point q = new Rtree.Point(nnqX.get(i),nnqY.get(i));
            Node treeroot=allNodes.get(getKeyFromValue(allNodesParentID,0));
            getMindist(q,treeroot); //Access root
            NNSearch(q);
            
            System.out.println("\n***********NN result: The id of point(s)***********\n");
            for(int j=0;j<NN.size();j++){
                
                System.out.println(NN.get(j).id);
                
            }
            
            NN.clear();
            nnDist = 0;
            lowestDist = Double.POSITIVE_INFINITY;
            result.clear();
            listH.clear();
        }
   
        long endTimeN   = System.nanoTime();
        long totalTimeN = endTimeN - startTimeN;
        
        
        // Insert all points into a list to perform SEQUENTIAL SCAN
        for (Map.Entry<Integer, Node> eachNode : allNodes.entrySet())
        {
            if(allNodes.get(eachNode.getKey()).asLeaf==true){
                for (int i=0; i<allNodes.get(eachNode.getKey()).points.size();i++){
                    allPoints.add(allNodes.get(eachNode.getKey()).points.get(i));
                }
            }
        }
        
        
        long startTimeSR = System.nanoTime();

        for(int j=0; j<100; j++){
        	sq_number_of_points=0;
        	
        	for (int i=0; i<allPoints.size();i++){
                float thisPointX=allPoints.get(i).x;
                float thisPointY=allPoints.get(i).y;
                if(thisPointX>=rqX1.get(j) && thisPointX<=rqX2.get(j) && thisPointY>=rqY2.get(j) && thisPointY<=rqY1.get(j)){
                    sq_number_of_points=sq_number_of_points+1;
                }
            }
        	
        	System.out.println("\nSequential Scan Range Query There are "+sq_number_of_points+" points found");
        }
        
        long endTimeSR   = System.nanoTime();
        long totalTimeSR = endTimeSR - startTimeSR;
        
        
        
        
        
        //NN Search Sequential Scan
        long startTimeSN = System.nanoTime();
        
        for(int j=0; j<100; j++){
        	
        	ArrayList<Point> S_NNresult=new ArrayList<Point>();
            float smallestDistance=(float) Math.sqrt((Math.pow((allPoints.get(0).x-nnqX.get(j)),2)+Math.pow((allPoints.get(0).y-nnqY.get(j)),2)));
            S_NNresult.add(allPoints.get(0));
            for(int i=1;i<allPoints.size();i++){
                float thisDistance=(float) Math.sqrt((Math.pow((allPoints.get(i).x-nnqX.get(j)),2)+Math.pow((allPoints.get(i).y-nnqY.get(j)),2)));
                if(thisDistance==smallestDistance){
                    S_NNresult.add(allPoints.get(i));
                }
                else if(thisDistance<smallestDistance){
                    S_NNresult.clear();
                    smallestDistance=thisDistance;
                    S_NNresult.add(allPoints.get(i));
                }
            }
            
            System.out.println("\nSequential Scan NN The nodes found are:");
            for(int i=0;i<S_NNresult.size();i++){
                System.out.println(S_NNresult.get(i).id);
            }
        }
        
        long endTimeSN   = System.nanoTime();
        long totalTimeSN = endTimeSN - startTimeSN;
        
        System.out.println("Sequential Scan Benchmark for 100 Range Queries (nano): "+totalTimeSR);
        System.out.println("Sequential Scan Benchmark for 1 Range Queries (nano): "+totalTimeSR/100);
        
        System.out.println("100 Range Query time (nano): "+totalTimeR);
        System.out.println("1 Range Query time (nano): "+totalTimeR/100);
        
        System.out.println("Sequential Scan Benchmark for 100 NN Query time (ms): "+totalTimeSN);
        System.out.println("Sequential Scan Benchmark for 1 NN Query time (ms): "+totalTimeSN/100);
        
        System.out.println("100 NN Query time (ms): "+totalTimeN);
        System.out.println("1 NN Query time (ms): "+totalTimeN/100);
        
        //Write Output File
        try{
		    PrintWriter writer = new PrintWriter("RangeQueryResult", "UTF-8");
		    writer.println("******Range Query Result******");
		    for(int i=1; i<=rqResult.size(); i++){
		    	writer.println("Query" + i + ": " + rqResult.get(i-1));
		    }
		    
		    writer.println("Total running time of answering all the 100 queries: " + totalTimeR);
		    writer.println("Average time of each query" + avgTimeR);
		    
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
    }
    
    
    
    
    
    
    
    
    
    
    
    
    //range query main function
    public static void rangeQuery(Node root, RQuery r){
        //System.out.println("\n***********Performing Range Query***********\n");
        if(root.asLeaf==true){
            for(int i=0;i<root.points.size();i++){
                if(root.points.get(i).x>=r.x1 && root.points.get(i).x<=r.x2&&root.points.get(i).y>=r.y2&&root.points.get(i).y<=r.y1){
                    number_of_points=number_of_points+1;
                    //queryPoints.add(root.points.get(i));
                }
            }
        }
        else{
            for(int j=0;j<root.childNodes.size();j++){
                if(isIntersect(root.childNodes.get(j),r)==true){
                    rangeQuery(root.childNodes.get(j),r);
                }
            }
        }
        
    }
    
    //used to check if two rectangle intersects (range query and MBR)
    public static boolean isIntersect(Node MBR, RQuery r){
        if((r.x2<MBR.x1)||(r.x1>MBR.x2)||(r.y2>MBR.y1)||(r.y1<MBR.y2)){
            return false;
        }
        else{
            return true;
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
        System.out.println("\n**********Update All MBR function called**********\n");
        
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
        System.out.println("\n**********Update MBR ends***********\n");
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
                System.out.println("\n************** Handle Overflow for Leaf & Root Starts**************\n");
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
                
                System.out.println("\n************** Handle Overflow Ends**************\n");
            }
            else{
                //this node has a parent (parentNodeID!=0)
                //get this node's parent
                System.out.println("\n************** Handle Overflow on Leaf Not Root Starts**************\n");
                
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
                
                System.out.println("\n************** Handle Overflows Ends**************\n");
                // Check if its parent node overflows
                if (parentNode.childNodes.size()>B){
                    System.out.println("********"+"Leaf Node "+root.id+"'s Parent Node "+parentNode.id+" Overflows"+"********");
                    HandleOverflow(parentNode);
                }
                
            }
            
        }
        else{
            updateAllMBR();
            twoSubInternalNodes=SplitInternal(root);
            if(root.parentNodeID==0){
                
                System.out.println("\n*******Handle Internal & Root Node Overflow with ID: "+root.id+"  Starts *********\n");
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
                
                //Node 1
                Node internalNode1= new Node(ID-1, twoSubInternalNodes.get(0).childNodes, new ArrayList<Point>(), false,ID);
                allNodes.put(ID, internalNode1);
                allNodesParentID.put(ID, ID-1);
                for(int j=0;j<internalNode1.childNodes.size();j++){
                    internalNode1.childNodes.get(j).parentNodeID=internalNode1.id;
                    allNodesParentID.put(internalNode1.childNodes.get(j).id, internalNode1.id);
                }
                newRoot.childNodes.add(internalNode1);
                ID=ID+1;
                
                //Node 2
                Node internalNode2 = new Node(ID-2, twoSubInternalNodes.get(1).childNodes, new ArrayList<Point>(), false,ID);
                allNodes.put(ID, internalNode2);
                allNodesParentID.put(ID, ID-2);
                for(int j=0;j<internalNode2.childNodes.size();j++){
                    internalNode2.childNodes.get(j).parentNodeID=internalNode2.id;
                    allNodesParentID.put(internalNode2.childNodes.get(j).id, internalNode2.id);
                }
                newRoot.childNodes.add(internalNode2);
                ID=ID+1;
                
                System.out.println("New node ID: "+ internalNode1.id+" and "+internalNode2.id);
                System.out.println("\n*******Handle Internal & Root Node Overflow Ends*********\n");
                
                
                
            }
            else{
                System.out.println("\n***********Handle Internal Not Root Overflow with ID" +root.id+" Starts ************\n");
                
                //This is not a root node, it's an internal node.
                //no need to delete the root and create new root
                
                
                //1 find all nodes whose parent is this node and delete the relation
                for(int i=0;i<root.childNodes.size();i++){
                    //reomove this ID and its value from parent HashMap
                    allNodesParentID.remove(root.childNodes.get(i).getID());
                }
                //find this node's parent
                Node itsparentNode= allNodes.get(root.parentNodeID);
                
                //remove its childnodes' parentNodeID, will replace it later
                //2 remove its parent's this child node
                System.out.println("Loop this node's parent nodes");
                for(int j=0;j<itsparentNode.childNodes.size();j++){
                    System.out.println("This node's parent's Childnode ID: "+itsparentNode.childNodes.get(j).id);
                    if(itsparentNode.childNodes.get(j).id==root.id){
                        System.out.println(itsparentNode.childNodes.get(j).id+" is this node, remove it!");
                        itsparentNode.childNodes.remove(itsparentNode.childNodes.get(j));
                    }
                }
                //Check its parent's child nodes list
                System.out.println("Check its parent's childnode list now:");
                itsparentNode.getAllNodesId();
                
                //3 remove this node's id from parent list which is (root.id,0)
                allNodesParentID.remove(root.id);
                
                //4 remove this node's from AllNodes List
                allNodes.remove(root.id);
                
                
                
                //create two new split nodes
                
                //Node 1
                //1 add to all nodes list
                Node internalNode1= new Node(itsparentNode.id, twoSubInternalNodes.get(0).childNodes, new ArrayList<Point>(), false,ID);
                allNodes.put(ID, internalNode1);
                
                //2 add itself to all nodes parent id list
                allNodesParentID.put(ID, itsparentNode.id);
                
                //3 add its child nodes to all nodes parent id list, and change their parent node id
                for(int j=0;j<internalNode1.childNodes.size();j++){
                    //change their parent node id
                    System.out.println("Change node: "+internalNode1.childNodes.get(j).id+"'s parent node id from "+internalNode1.childNodes.get(j).parentNodeID+" to "+internalNode1.id);
                    internalNode1.childNodes.get(j).parentNodeID=internalNode1.id;
                    System.out.println("Also change parent id in parent id HashMap");
                    allNodesParentID.put(internalNode1.childNodes.get(j).id, internalNode1.id);
                }
                //4 add this new node to it's parent node's child node
                itsparentNode.childNodes.add(internalNode1);
                
                ID=ID+1;
                
                
                
                //Node 2
                //1 add to all nodes list
                Node internalNode2= new Node(itsparentNode.id, twoSubInternalNodes.get(1).childNodes, new ArrayList<Point>(), false,ID);
                allNodes.put(ID, internalNode2);
                
                //2 add itself to all nodes parent id list
                allNodesParentID.put(ID, itsparentNode.id);
                
                //3 add its child nodes to all nodes parent id list, and change their parent node id
                for(int j=0;j<internalNode2.childNodes.size();j++){
                    //change their parent node id
                    System.out.println("Change node: "+internalNode2.childNodes.get(j).id+"'s parent node id from "+internalNode2.childNodes.get(j).parentNodeID+" to "+internalNode2.id);
                    internalNode2.childNodes.get(j).parentNodeID=internalNode2.id;
                    System.out.println("Also change parent id in parent id HashMap");
                    allNodesParentID.put(internalNode2.childNodes.get(j).id, internalNode2.id);
                }
                //4 add this new node to it's parent node's child node
                itsparentNode.childNodes.add(internalNode2);
                
                ID=ID+1;
                
                
                
                System.out.println("\nNew node ID: "+ internalNode1.id+" and "+internalNode2.id);
                
                System.out.println("\n***********Handle Internal Not Root Overflow Ends************\n");
                
                // Check if its parent node overflows
                if (itsparentNode.childNodes.size()>B){
                    System.out.println("********"+"This Internal Node's Parent Node "+itsparentNode.id+" Overflows"+"********");
                    HandleOverflow(itsparentNode);
                }
            }
            
        }
        
        //check if the 'root' is the tree's root
        //if yes, the 'root' is both the tree root and leaf node
        
    }
    
    //Insertion
    public static void Insertion(Node root, Point point){
        System.out.println("\n**********Insertion for point ID "+point.id+"************\n");
        //check if the node(root) to be inserted is a leaf node
        if(root.getLeaf() == true){
            //add point to root
            root.addPoint2Node(point);
            
            //check if the node overflows
            if(root.points.size()>B){
                //handle overflow
                System.out.println("********"+"Leaf Node "+root.id+ " Overflows"+"********");
                
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
            System.out.println("\n**********Insertion Ends************\n");
            
            Insertion(nextNode, point);
            
        }
    }
    
    
    //Split 1) Internal 2)Root but not leaf Node
    public static ArrayList<Node> SplitInternal(Node internalnode){
        System.out.println("\n**********Split Internal Node ID "+internalnode.id+" Starts ************\n");
        
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
        
        
        //******************//duplicate the following to sort X2,Y2,Y1
        
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
        
        
        //calculate the perimeter sum of MBR(S1X1) and MBR(S2X1);
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
            }
            else{
                if(totalPerimeterX1<=smallestPerimeter){
                    smallestPerimeter=totalPerimeterX1;
                    bestS1=S1X1;
                    bestS2=S2X1;
                }
            }
        }
        
        
        //Testing Print Lines on X1
        System.out.println("\nsmallest perimeter now (X1): "+smallestPerimeter);
        System.out.println("best S1 so far for X1");
        for(int j=0;j<bestS1.size();j++){
            
            System.out.println("ID:"+bestS1.get(j).id+" X1:"+bestS1.get(j).x1+" X2:"+bestS1.get(j).x2+" Y1:"+bestS1.get(j).y1+" Y2:"+bestS1.get(j).y2);
        }
        System.out.println("best S2 so far for X1");
        for(int j=0;j<bestS2.size();j++){
            
            System.out.println("ID:"+bestS2.get(j).id+" X1:"+bestS2.get(j).x1+" X2:"+bestS2.get(j).x2+" Y1:"+bestS2.get(j).y1+" Y2:"+bestS2.get(j).y2);
        }
        
        
        
        //Calculate the smallest right boundaries of MBRs on X dimensions (X2)
        //******************//duplicate the following to sort X2,Y2,Y1
        
        //sort the points by X right boundaries values (X2)
        ArrayList<Node> sortedX2Node= internalnode.childNodes;
        Collections.sort(sortedX2Node, new Comparator<Node>(){
            public int compare(Node node1, Node node2){
                int result =Float.compare(node1.x2, node2.x2);
                return result;
            }
        });
        System.out.println("Sorted nodes based on X2");
        for(int i=0;i<sortedX2Node.size();i++){
            System.out.println("ID:"+sortedX2Node.get(i).id+" X1:"+sortedX2Node.get(i).x1+" X2:"+sortedX2Node.get(i).x2+" Y1:"+sortedX2Node.get(i).y1+" Y2:"+sortedX2Node.get(i).y2);
        }
        
        
        //calculate the perimeter sum of MBR(S1X2) and MBR(S2X2);
        //record it if this is the best split so far
        for(int i=zeropoint4B;i<=B-zeropoint4B+1;i++){
            ArrayList<Node> S1X2=new ArrayList<Node>();
            ArrayList<Node> S2X2=new ArrayList<Node>();
            
            for(int j=0;j<i;j++){
                S1X2.add(sortedX2Node.get(j));
            }
            for(int j=i;j<(B+1);j++){
                S2X2.add(sortedX2Node.get(j));
            }
            System.out.println("\nthis round S1X2's nodes\n");
            for(int j=0;j<S1X2.size();j++){
                
                System.out.println("ID:"+S1X2.get(j).id+" X1:"+S1X2.get(j).x1+" X2:"+S1X2.get(j).x2+" Y1:"+S1X2.get(j).y1+" Y2:"+S1X2.get(j).y2);
            }
            System.out.println("\nthis round S2X2's nodes\n");
            for(int j=0;j<S2X2.size();j++){
                
                System.out.println("ID:"+S2X2.get(j).id+" X1:"+S2X2.get(j).x1+" X2:"+S2X2.get(j).x2+" Y1:"+S2X2.get(j).y1+" Y2:"+S2X2.get(j).y2);
                
            }
            
            //Since X2 is sorted, find the ***x1***,y1 y2 value of the MBR
            //why find x1, because it's not certain that the last node with largest x2
            //will also have the largest x1 as well
            //store the first node's Y2 Y1 in S1X1 to be the smallest/largest y value at first
            //store the first node's X1 in S1X1
            float minYS1X2=S1X2.get(0).y2;
            float maxYS1X2=S1X2.get(0).y1;
            float minX1_S1X2=S1X2.get(0).x1;
            //find smallest perimeter, since x is sorted, find min and max y for S1 & S2.
            //since y1 is the max y for each node's MBR, only need to look at y1 for all
            //nodes in S1X1 to get the maximum y
            //Same as y2
            for (int j=0;j<S1X2.size();j++){
                if(S1X2.get(j).y2<=minYS1X2){
                    minYS1X2=S1X2.get(j).y2;
                }
                if(S1X2.get(j).y1>=maxYS1X2){
                    maxYS1X2=S1X2.get(j).y1;
                }
                if(S1X2.get(j).x1<=minX1_S1X2){
                    minX1_S1X2=S1X2.get(j).x1;
                }
            }
            System.out.println("min Y S1X2: "+minYS1X2);
            System.out.println("max Y S1X2: "+maxYS1X2);
            System.out.println("min X1 S1X2: "+minX1_S1X2);
            
            
            
            //store the first node's y1y2x1 in S2X2
            float minYS2X2=S2X2.get(0).y2;
            float maxYS2X2=S2X2.get(0).y1;
            float minX1_S2X2=S2X2.get(0).x1;
            //find smallest perimeter, since x is sorted, find min and max y for S1 & S2.
            //since y1 is the max y for each node's MBR, only need to look at y1 for all
            //nodes in S1X1 to get the maximum y
            //Same as y2
            for (int j=0;j<S2X2.size();j++){
                if(S2X2.get(j).y2<=minYS2X2){
                    minYS2X2=S2X2.get(j).y2;
                }
                if(S2X2.get(j).y1>=maxYS2X2){
                    maxYS2X2=S2X2.get(j).y1;
                }
                if(S2X2.get(j).x1<=minX1_S2X2){
                    minX1_S2X2=S2X2.get(j).x1;
                }
            }
            System.out.println("min Y S2X2: "+minYS2X2);
            System.out.println("max Y S2X2: "+maxYS2X2);
            System.out.println("min X1 S2X2: "+minX1_S2X2);
            
            float perimeterS1X2=(S1X2.get(S1X2.size()-1).x2 - minX1_S1X2)*2+(maxYS1X2-minYS1X2)*2;
            float perimeterS2X2=(S2X2.get(S2X2.size()-1).x2 - minX1_S2X2)*2+(maxYS2X2-minYS2X2)*2;
            float totalPerimeterX2=perimeterS1X2+perimeterS2X2;
            
            System.out.println("total perimeter on X2: "+totalPerimeterX2);
            
												
            if(totalPerimeterX2<=smallestPerimeter){
                smallestPerimeter=totalPerimeterX2;
                bestS1=S1X2;
                bestS2=S2X2;
                
            }
        }
        
        
        //Testing Print Lines on X1
        System.out.println("\nsmallest perimeter now (X1,X2): "+smallestPerimeter);
        System.out.println("best S1 so far for (X1, X2)");
        for(int j=0;j<bestS1.size();j++){
            
            System.out.println("ID:"+bestS1.get(j).id+" X1:"+bestS1.get(j).x1+" X2:"+bestS1.get(j).x2+" Y1:"+bestS1.get(j).y1+" Y2:"+bestS1.get(j).y2);
        }
        System.out.println("best S2 so far for (X1, X2)");
        for(int j=0;j<bestS2.size();j++){
            
            System.out.println("ID:"+bestS2.get(j).id+" X1:"+bestS2.get(j).x1+" X2:"+bestS2.get(j).x2+" Y1:"+bestS2.get(j).y1+" Y2:"+bestS2.get(j).y2);
        }
        
        
        
        
        
        
        
        //Calculate the smallest down boundaries of MBRs on Y dimensions (Y2)
        //******************//duplicate the following to sort X2,Y2,Y1
        
        //sort the points by Y down boundaries values (Y2)
        ArrayList<Node> sortedY2Node= internalnode.childNodes;
        Collections.sort(sortedY2Node, new Comparator<Node>(){
            public int compare(Node node1, Node node2){
                int result =Float.compare(node1.y2, node2.y2);
                return result;
            }
        });
        System.out.println("********Sorted nodes based on Y2 (down boundaries)*********");
        for(int i=0;i<sortedY2Node.size();i++){
            System.out.println("ID:"+sortedY2Node.get(i).id+" X1:"+sortedY2Node.get(i).x1+" X2:"+sortedY2Node.get(i).x2+" Y1:"+sortedY2Node.get(i).y1+" Y2:"+sortedY2Node.get(i).y2);
        }
        
        
        //calculate the perimeter sum of MBR(S1Y2) and MBR(S2Y2);
        //record it if this is the best split so far
        for(int i=zeropoint4B;i<=B-zeropoint4B+1;i++){
            ArrayList<Node> S1Y2=new ArrayList<Node>();
            ArrayList<Node> S2Y2=new ArrayList<Node>();
            
            for(int j=0;j<i;j++){
                S1Y2.add(sortedY2Node.get(j));
            }
            for(int j=i;j<(B+1);j++){
                S2Y2.add(sortedY2Node.get(j));
            }
            System.out.println("\nS1Y2 this round nodes\n");
            for(int j=0;j<S1Y2.size();j++){
                
                System.out.println("ID:"+S1Y2.get(j).id+" X1:"+S1Y2.get(j).x1+" X2:"+S1Y2.get(j).x2+" Y1:"+S1Y2.get(j).y1+" Y2:"+S1Y2.get(j).y2);
            }
            System.out.println("\nS2Y2 this round nodes\n");
            for(int j=0;j<S2Y2.size();j++){
                
                System.out.println("ID:"+S2Y2.get(j).id+" X1:"+S2Y2.get(j).x1+" X2:"+S2Y2.get(j).x2+" Y1:"+S2Y2.get(j).y1+" Y2:"+S2Y2.get(j).y2);
                
            }
            
            //Since y2 is sorted, find the ***y1***,x1, x2 value of the MBR
            //why find y1, because it's not certain that the last node with largest y2
            //will also have the largest y1 as well
            //store the first node's X1 X2 in S1Y2 to be the smallest/largest y value at first
            //store the first node's Y1 in S1Y2
            float minXS1Y2=S1Y2.get(0).x1;
            float maxXS1Y2=S1Y2.get(0).x2;
            float maxY1_S1Y2=S1Y2.get(0).y1;
            
            //since x2 is the max x for each node's MBR, only need to look at x2 for all
            //nodes in S1Y2 to get the maximum x
            //Same as x1
            for (int j=0;j<S1Y2.size();j++){
                if(S1Y2.get(j).x1<=minXS1Y2){
                    minXS1Y2=S1Y2.get(j).x1;
                }
                if(S1Y2.get(j).x2>=maxXS1Y2){
                    maxXS1Y2=S1Y2.get(j).x2;
                }
                if(S1Y2.get(j).y1>=maxY1_S1Y2){
                    maxY1_S1Y2=S1Y2.get(j).y1;
                }
            }
            System.out.println("min X S1Y2: "+minXS1Y2);
            System.out.println("max X S1Y2: "+maxXS1Y2);
            System.out.println("min Y2 S1Y2: "+S1Y2.get(0).y2);
            System.out.println("max Y1 S1Y2: "+maxY1_S1Y2);
            
            
            //store the first node's X1 X2 in S2Y2 to be the smallest/largest y value at first
            //store the first node's Y1 in S2Y2
            float minXS2Y2=S2Y2.get(0).x1;
            float maxXS2Y2=S2Y2.get(0).x2;
            float maxY1_S2Y2=S2Y2.get(0).y1;
            
            //since x2 is the max x for each node's MBR, only need to look at x2 for all
            //nodes in S1Y2 to get the maximum x
            //Same as x1
            for (int j=0;j<S2Y2.size();j++){
                if(S2Y2.get(j).x1<=minXS2Y2){
                    minXS2Y2=S2Y2.get(j).x1;
                }
                if(S2Y2.get(j).x2>=maxXS2Y2){
                    maxXS2Y2=S2Y2.get(j).x2;
                }
                if(S2Y2.get(j).y1>=maxY1_S2Y2){
                    maxY1_S2Y2=S2Y2.get(j).y1;
                }
            }
            System.out.println("min X S2Y2: "+minXS2Y2);
            System.out.println("max X S2Y2: "+maxXS2Y2);
            System.out.println("min Y2 S2Y2: "+S2Y2.get(0).y2);
            System.out.println("max Y1 S2Y2: "+maxY1_S2Y2);
            
            
            float perimeterS1Y2=(maxXS1Y2 - minXS1Y2)*2+(maxY1_S1Y2 - S1Y2.get(0).y2)*2;
            float perimeterS2Y2=(maxXS2Y2 - minXS2Y2)*2+(maxY1_S2Y2 - S2Y2.get(0).y2)*2;
            float totalPerimeterY2=perimeterS1Y2+perimeterS2Y2;
            
            System.out.println("total perimeter on Y2: "+totalPerimeterY2);
            
            if(totalPerimeterY2<=smallestPerimeter){
                smallestPerimeter=totalPerimeterY2;
                bestS1=S1Y2;
                bestS2=S2Y2;
            }
            
        }
        
        
        //Testing Print Lines on Y2
        System.out.println("\nsmallest perimeter now (X1,X2,Y2): "+smallestPerimeter);
        System.out.println("best S1 nodes so far for X1,X2,Y2");
        for(int j=0;j<bestS1.size();j++){
            
            System.out.println("ID:"+bestS1.get(j).id+" X1:"+bestS1.get(j).x1+" X2:"+bestS1.get(j).x2+" Y1:"+bestS1.get(j).y1+" Y2:"+bestS1.get(j).y2);
        }
        System.out.println("best S2 nodes so far for X1,X2,Y2");
        for(int j=0;j<bestS2.size();j++){
            
            System.out.println("ID:"+bestS2.get(j).id+" X1:"+bestS2.get(j).x1+" X2:"+bestS2.get(j).x2+" Y1:"+bestS2.get(j).y1+" Y2:"+bestS2.get(j).y2);
        }
        
        
        
        
        
        
        //Calculate the smallest perimeter on sorted UP boundaries of MBRs on Y dimensions (Y1)
        //Calculate the smallest down boundaries of MBRs on Y dimensions (Y2)
        //******************//duplicate the following
        
        //sort the points by Y upper boundaries values (Y1), so don't know lower bound Y2
        ArrayList<Node> sortedY1Node= internalnode.childNodes;
        Collections.sort(sortedY1Node, new Comparator<Node>(){
            public int compare(Node node1, Node node2){
                int result =Float.compare(node1.y1, node2.y1);
                return result;
            }
        });
        System.out.println("********Sorted nodes based on Y1 (upper boundaries)*********");
        for(int i=0;i<sortedY1Node.size();i++){
            System.out.println("ID:"+sortedY1Node.get(i).id+" X1:"+sortedY1Node.get(i).x1+" X2:"+sortedY1Node.get(i).x2+" Y1:"+sortedY1Node.get(i).y1+" Y2:"+sortedY1Node.get(i).y2);
        }
        
        
        //calculate the perimeter sum of MBR(S1Y1) and MBR(S2Y1);
        //record it if this is the best split so far
        for(int i=zeropoint4B;i<=B-zeropoint4B+1;i++){
            ArrayList<Node> S1Y1=new ArrayList<Node>();
            ArrayList<Node> S2Y1=new ArrayList<Node>();
            
            for(int j=0;j<i;j++){
                S1Y1.add(sortedY1Node.get(j));
            }
            for(int j=i;j<(B+1);j++){
                S2Y1.add(sortedY1Node.get(j));
            }
            System.out.println("\nS1Y1 this round nodes");
            for(int j=0;j<S1Y1.size();j++){
                
                System.out.println("ID:"+S1Y1.get(j).id+" X1:"+S1Y1.get(j).x1+" X2:"+S1Y1.get(j).x2+" Y1:"+S1Y1.get(j).y1+" Y2:"+S1Y1.get(j).y2);
            }
            System.out.println("\nS2Y1 this round nodes");
            for(int j=0;j<S2Y1.size();j++){
                
                System.out.println("ID:"+S2Y1.get(j).id+" X1:"+S2Y1.get(j).x1+" X2:"+S2Y1.get(j).x2+" Y1:"+S2Y1.get(j).y1+" Y2:"+S2Y1.get(j).y2);
                
            }
            
            //Since y1 is sorted, find the ***y2***,x1, x2 value of the MBR
            //why find y2, because it's not certain that the last node with largest y1
            //will also have the largest y2 as well
            //store the first node's X1 X2 in S1Y1 to be the smallest/largest y value at first
            //store the first node's Y2 in S1Y1
            float minXS1Y1=S1Y1.get(0).x1;
            float maxXS1Y1=S1Y1.get(0).x2;
            float minY2_S1Y1=S1Y1.get(0).y2;
            
            //since x2 is the max x for each node's MBR, only need to look at x2 for all
            //nodes in S1Y2 to get the maximum x
            //Same as x1
            for (int j=0;j<S1Y1.size();j++){
                if(S1Y1.get(j).x1<=minXS1Y1){
                    minXS1Y1=S1Y1.get(j).x1;
                }
                if(S1Y1.get(j).x2>=maxXS1Y1){
                    maxXS1Y1=S1Y1.get(j).x2;
                }
                if(S1Y1.get(j).y2<=minY2_S1Y1){
                    minY2_S1Y1=S1Y1.get(j).y2;
                }
            }
            System.out.println("min X S1Y1: "+minXS1Y1);
            System.out.println("max X S1Y1: "+maxXS1Y1);
            System.out.println("min Y2 S1Y1: "+minY2_S1Y1);
            System.out.println("max Y1 S1Y1: "+S1Y1.get(S1Y1.size()-1).y1);
            
            
            //store the first node's X1 X2 in S2Y1 to be the smallest/largest y value at first
            //store the first node's Y2 in S2Y1
            float minXS2Y1=S2Y1.get(0).x1;
            float maxXS2Y1=S2Y1.get(0).x2;
            float minY2_S2Y1=S2Y1.get(0).y2;
            
            //since x2 is the max x for each node's MBR, only need to look at x2 for all
            //nodes in S2Y2 to get the maximum x
            //Same as x1
            for (int j=0;j<S2Y1.size();j++){
                if(S2Y1.get(j).x1<=minXS2Y1){
                    minXS2Y1=S2Y1.get(j).x1;
                }
                if(S2Y1.get(j).x2>=maxXS2Y1){
                    maxXS2Y1=S2Y1.get(j).x2;
                }
                if(S2Y1.get(j).y2<=minY2_S2Y1){
                    minY2_S2Y1=S2Y1.get(j).y2;
                }
            }
            System.out.println("min X S2Y1: "+minXS2Y1);
            System.out.println("max X S2Y1: "+maxXS2Y1);
            System.out.println("min Y2 S2Y1: "+minY2_S2Y1);
            System.out.println("max Y1 S2Y1: "+S2Y1.get(S2Y1.size()-1).y1);
            
            
            float perimeterS1Y1=(maxXS1Y1 - minXS1Y1)*2+(S1Y1.get(S1Y1.size()-1).y1 - minY2_S1Y1)*2;
            float perimeterS2Y1=(maxXS2Y1 - minXS2Y1)*2+(S2Y1.get(S2Y1.size()-1).y1 - minY2_S2Y1)*2;
            float totalPerimeterY1=perimeterS1Y1+perimeterS2Y1;
            
            System.out.println("total perimeter on Y1: "+totalPerimeterY1);
            
            if(totalPerimeterY1<=smallestPerimeter){
                smallestPerimeter=totalPerimeterY1;
                bestS1=S1Y1;
                bestS2=S2Y1;
            }
            
        }
        
        
        //Testing Print Lines on Y1
        System.out.println("\nsmallest perimeter now (X1,X2,Y2,Y1): "+smallestPerimeter);
        System.out.println("best S1 nodes so far for X1,X2,Y2,Y1");
        for(int j=0;j<bestS1.size();j++){
            
            System.out.println("ID:"+bestS1.get(j).id+" X1:"+bestS1.get(j).x1+" X2:"+bestS1.get(j).x2+" Y1:"+bestS1.get(j).y1+" Y2:"+bestS1.get(j).y2);
        }
        System.out.println("best S2 nodes so far for X1,X2,Y2,Y1");
        for(int j=0;j<bestS2.size();j++){
            
            System.out.println("ID:"+bestS2.get(j).id+" X1:"+bestS2.get(j).x1+" X2:"+bestS2.get(j).x2+" Y1:"+bestS2.get(j).y1+" Y2:"+bestS2.get(j).y2);
        }
        
        
        newInternalNode1.childNodes=bestS1;
        newInternalNode2.childNodes=bestS2;
        twoNodesResult.add(newInternalNode1);
        twoNodesResult.add(newInternalNode2);
        
        System.out.println("\n************** Split Internal Node Ends**************\n");
        
        return twoNodesResult;
        
        
    }
    
    
    
    //Split Leaf Node
    public static ArrayList<Node> SplitLeaf(Node leafnode){
        System.out.println("\n**********Split Leaf Node ID "+leafnode.id+" Starts ************\n");
        
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
        
        System.out.println("\n************** Split Leaf Node Ends**************\n");
        
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
        public Point(float x, float y){
            this.x = x;
            this.y = y;
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
        
        public int getId(){
            return id;
        }
        
        
    }
    
    
    public static class RQuery{
        
        private float x1;
        private float y1;
        private float x2;
        private float y2;
        public RQuery(float x1, float y1, float x2, float y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
    
    //listH entry
    public static class Entry{
    	private Node node;
    	private Double mindist;
    	
    	public Entry(Node node, Double mindist){
    		this.node = node;
    		this.setDist(mindist);
    	}


		public void setDist(Double mindist) {
			this.mindist = mindist;
		}

		public Double getDist() {
			return mindist;
		}
    }
    
    
    public static void NNSearch(Rtree.Point q){
        double ppDist = Double.POSITIVE_INFINITY;
        double thisPpDist = 0;
        ArrayList<Rtree.Point> curNN = new ArrayList<Rtree.Point>();
        
        //Access the first child in listH
        Rtree.Node u = new Rtree.Node();
        u = listH.poll().node;//??
        
        //If u is an intermediate node
        if(u.asLeaf == false){
            getMindist(q,u);
            //System.out.println(Arrays.asList(listH));
            NNSearch(q);
            
        }else{ //leaf node
            //Get the lowest value in listH
            double nextHVal = listH.peek().getDist();
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
            }else{
                NN.addAll(curNN);
                NNSearch(q);			
            }
        }
        
        finalDist=Math.sqrt(Math.pow(NN.get(0).getX() - q.getX(), 2) + Math.pow(NN.get(0).getY() - q.getY(), 2));
        for(int j=1;j<NN.size();j++){
            double thisDist=Math.sqrt(Math.pow(NN.get(j).getX() - q.getX(), 2) + Math.pow(NN.get(j).getY() - q.getY(), 2));
            if(thisDist<finalDist){
                for(int k=0;k<j;k++){
                    NN.remove(NN.get(k));
                }
            }
            else if(thisDist>finalDist){
                NN.remove(NN.get(j));
            }
            
        }
        
        
        
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
            listH.add(new Entry(n,mindist));
        }
    }
    
    
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
        .collect(Collectors.toMap(
                                  Map.Entry::getKey, 
                                  Map.Entry::getValue, 
                                  (e1, e2) -> e1, 
                                  LinkedHashMap::new
                                  ));
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

