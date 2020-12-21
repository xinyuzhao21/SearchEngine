package retriever;

public class Entry implements Comparable<Entry> {
    private int key;
    private double value;

    public Entry(int key, double value)  {
            this.key = key;
            this.value = value;
        }

        // getters

    public double getValue() {
        return value;
    }

    public int getKey() {
        return key;
    }

    public int compareTo(Entry other) {
        return this.getValue()>(other.getValue())?1:-1;
    }

    public String toString(){
        return "("+getKey()+","+getValue()+")";
    }
}

