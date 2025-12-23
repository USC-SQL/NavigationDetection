package edu.usc.KFG.KFGUtilities;

public class Pair<L, R> {

    private L left;
    private R right;


    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() { return left; }
    public R getRight() { return right; }

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }

    @Override
    public String toString() {
        return "Pair [left=" + left.toString() + ", right=" + right.toString() + "]";
    }




//	@Override
//	public int compareTo(Pair<L, R> that) {
//	    int cmp = this.left.compareTo(that.left);
//	    if (cmp == 0)
//	        cmp = this.right.compareTo(that.right);
//	    return cmp;
//	}

}