import java.util.Comparator;

public class MindistComparator implements Comparator<Rtree.Entry> {

	@Override
	public int compare(Rtree.Entry o1, Rtree.Entry o2) {
		if(o1.getMindist() < o2.getMindist()){
			return -1;
		}
		
		if(o1.getMindist() > o2.getMindist()){
			return 1;
		}
		return 0;
	}
}
