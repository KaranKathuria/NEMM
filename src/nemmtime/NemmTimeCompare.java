package nemmtime;

import java.util.Comparator;

class NemmTimeCompare implements Comparator<NemmTime> {

    @Override
    public int compare(NemmTime t1, NemmTime t2) {
        // comparison logic based on tick index
    	int compareTo = t2.tickIndex > t1.tickIndex ? 1 : (t2.tickIndex < t1.tickIndex ? -1 : 0);
        return compareTo;
    }
}