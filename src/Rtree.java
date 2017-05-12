import java.util.ArrayList;

public class Rtree {

	
	public static void main(String[] args) {
		
		
	}
	
	//Find the root
	
	//Insertion
	public void Insertion(Node root, Node node){
		if(root.getLeaf() == true){
			
		}
	}
	
	
	//rectangle
	public class Node{
		private float x1;
		private float y1;
		private float x2;
		private float y2;
		boolean asLeaf;
		
		ArrayList<Point> points = new ArrayList<Point>();
		
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
	}

	public class Point{
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
