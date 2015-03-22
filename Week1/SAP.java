
public class SAP {

    private Digraph G;
    private class Vertex {
        private int id;
        private int depth;
        public Vertex(int id, int depth) {
            this.id = id;
            this.depth = depth;
        }
        public int getId() {
            return id;
        }
        public int getDepth() {
            return depth;
        }
        public String toString() {
            return String.format("id:%d, depth: %d", id, depth);
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (null == G) throw new java.lang.NullPointerException();
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Queue<Integer> lst1 = new Queue<Integer>();
        Queue<Integer> lst2 = new Queue<Integer>();
        lst1.enqueue(v);
        lst2.enqueue(w);
        return source(lst1, lst2)[1];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Queue<Integer> lst1 = new Queue<Integer>();
        Queue<Integer> lst2 = new Queue<Integer>();
        lst1.enqueue(v);
        lst2.enqueue(w);
        return source(lst1, lst2)[0];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return source(v, w)[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return source(v, w)[0];
    }

    private int[] source(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = new int[2];
        SeparateChainingHashST<Integer, Vertex> visitedVertex1 =
            new SeparateChainingHashST<Integer, Vertex>();
        SeparateChainingHashST<Integer, Vertex> visitedVertex2 =
            new SeparateChainingHashST<Integer, Vertex>();
        Queue<Vertex> q1 = new Queue<Vertex>();
        Queue<Vertex> q2 = new Queue<Vertex>();
        int depth1 = 1;
        int depth2 = 1;
        boolean isStop1 = false;
        boolean isStop2 = false;

        // initialization
        if (null == v || null == w) throw new java.lang.NullPointerException();
        for (int it : v) {
            checkRange(it);
            q1.enqueue(new Vertex(it, 0));
        }
        for (int it : w) {
            checkRange(it);
            q2.enqueue(new Vertex(it, 0));
        }

        int shortestAncestor = -1;
        int shortestDistance = Integer.MAX_VALUE;

        while (!isStop1 || !isStop2) {
            // v explores one deeper layer
            while (!isStop1) {
                // early stopping
                if (q1.isEmpty() || depth1 > shortestDistance) {
                    isStop1 = true;
                    break;
                }

                // iteration is over
                if (q1.peek().depth >= depth1) {
                    depth1++;
                    break;
                }

                //begin to process
                Vertex first = q1.dequeue();
                int id = first.getId();
                if (visitedVertex1.contains(id)) continue;

                // bingo
                if (visitedVertex2.contains(id)) {
                    int distance = visitedVertex2.get(id).getDepth() + first.getDepth();
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        shortestAncestor = id;
                    }
                }

                // adding its neighbours into queue
                for (int it : this.G.adj(id)) {
                    if (visitedVertex1.contains(it)) continue;
                    q1.enqueue(new Vertex(it, depth1));
                }

                // process over, mark it
                // System.out.println("1->" + first.toString() + " " + shortestDistance);
                visitedVertex1.put(id, first);
            }

            // w explores one deeper layer
            while (!isStop2) {
                // early stopping
                if (q2.isEmpty() || depth2 > shortestDistance) {
                    isStop2 = true;
                    break;
                }

                // iteration is over
                if (q2.peek().depth >= depth2) {
                    depth2++;
                    break;
                }

                // begin to precess
                Vertex first = q2.dequeue();
                int id = first.getId();
                if (visitedVertex2.contains(id)) continue;
                if (visitedVertex1.contains(id)) {
                    int distance = visitedVertex1.get(id).getDepth() + first.getDepth();
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        shortestAncestor = id;
                    }
                }

                // adding its neighbours into queue
                for (int it : this.G.adj(first.id)) {
                    if (visitedVertex2.contains(it)) continue;
                    q2.enqueue(new Vertex(it, depth2));
                }

                // process over, mark it
                // System.out.println("2->" + first.toString() + " " + shortestDistance);
                visitedVertex2.put(id, first);
            }
        }

        // fails
        if (shortestAncestor == -1) {
            result[0] = -1;
            result[1] = -1;
        } else {
            result[0] = shortestAncestor;
            result[1] = shortestDistance;
        }
        return result;
    }

    private void checkRange(int arg) {
        if (null == this.G) throw new java.lang.NullPointerException();
        if (arg >= this.G.V() || arg < 0) {
            throw new java.lang.IndexOutOfBoundsException();
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        System.out.println("graph constructs finished");
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            StdOut.printf("length = %d\n", length);
        }
    }
}
