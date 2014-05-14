package wp.core;

/**
 * Created by IntelliJ IDEA.
 * User: dhylbert
 * Date: 2009
 * Time: 11:09:49 AM
 */
public class BoundedIntQueue {

    public static class BoundedQueueException extends Exception {
        private static final long serialVersionUID = -5571546494391942853L;

        public static enum Type {
            FULL("Queue is full."),
            EMPTY("Queue is empty.");

            protected String msg;

            Type(String msg) {
                this.msg = msg;
            }

            public String getMsg() {
                return this.msg;
            }
        }

        protected Type type;

        public BoundedQueueException(Type type) {
            super(type.getMsg());
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }

    //
    // Begin: BoundedIntQueue Implementation
    //

    private int		size;
    private int		count = 0;
    public BoundedIntQueue (int size) {
    	this.size = size;
    }

    private Cell	head;
    private Cell	tail;
    
    public static class Cell {
    	public int	data;
    	public Cell	next;
    }
    
    public int	dequeue () throws BoundedQueueException {
    	if (head == null)
    		throw new BoundedQueueException(BoundedQueueException.Type.EMPTY);
    	Cell	next = head.next;
    	
    	int		val = head.data;
    	head = next;
    	count--;
    	return (val);
    }
    
    public void	enqueue (int x) throws BoundedQueueException {
    	if (count >= size)
    		throw new BoundedQueueException(BoundedQueueException.Type.FULL);
    	
    	Cell	newCell = new Cell();
    	newCell.data = x;
    	
    	if (head == null)
    		head = newCell;
    	if (tail == null)
    		tail = newCell;
    	else
    		tail.next = newCell;
    	
    	tail = newCell;
    	count++;
    }
        
    //
    // End: BoundedIntQueue Implementation


    public static void main(String args[]) {
        int queueSize = 3000;
        BoundedIntQueue queue = new BoundedIntQueue(queueSize);

        int x;
        try {
            //noinspection UnusedAssignment
            x = queue.dequeue();
            System.err.println("Empty Dequeue Test #1 Failed.");
            System.exit(-1);
            return;
        } catch (BoundedQueueException e) {
            switch (e.getType()) {
                case EMPTY:
                    System.out.println("Empty Dequeue Test #1 Passed.");
                    break;
                case FULL:
                    System.err.println("Empty Dequeue Test #1 Failed; unexpected exception.");
                    System.exit(-1);
                    return;
            }
        }

        int[] values = new int[queueSize];
        try {
            x = 7;
            for(int i = 0; i < queueSize; i++) {
                queue.enqueue(x);
                values[i] = x++;
            }
        } catch (BoundedQueueException e) {
            System.err.println("Unexpected Exception while enqueueing " + queueSize + " items into empty queue.");
            System.exit(-1);
            return;
        }
        
        try {
            queue.enqueue(x);
            System.err.println("Full Enqueue Test Failed.");
            System.exit(-1);
            return;
        } catch (BoundedQueueException e) {
            switch (e.getType()) {
                case EMPTY:
                    System.err.println("Empty Enqueue Test #1 Failed; unexpected exception.");
                    System.exit(-1);
                    return;
                case FULL:
                    System.out.println("Empty Enqueue Test #1 Passed.");
                    break;
            }
        }


        try {
            for(int i = 0; i < queueSize; i++) {
                int result = queue.dequeue();
                if(result != values[i]) {
                    System.err.println("Queue Order Error.");
                    System.exit(-1);
                    return;
                }
            }
        } catch (BoundedQueueException e) {
            System.err.println("Unexpected Exception while dequeueing " + queueSize + " items into empty queue.");
            System.exit(-1);
            return;
        }

        try {
            //noinspection UnusedAssignment
            x = queue.dequeue();
            System.err.println("Empty Dequeue Test #2 Failed.");
            System.exit(-1);
            return;
        } catch (BoundedQueueException e) {
            switch (e.getType()) {
                case EMPTY:
                    System.out.println("Empty Dequeue Test #2 Passed.");
                    break;
                case FULL:
                    System.err.println("Empty Dequeue Test #2 Failed; unexpected exception.");
                    System.exit(-1);
                    return;
            }
        }

        try {
            x = 70;
            for(int i = 0; i < (queueSize - (queueSize / 2)); i++) {
                queue.enqueue(x);
                values[i] = x++;
            }
        } catch (BoundedQueueException e) {
            System.err.println("Unexpected Exception while enqueueing " + queueSize + " items into empty queue.");
            System.exit(-1);
            return;
        }

        try {
            for(int i = 0; i < (queueSize - (queueSize / 2)); i++) {
                int result = queue.dequeue();
                if(result != values[i]) {
                    System.err.println("Queue Order Error.");
                    System.exit(-1);
                    return;
                }
            }
        } catch (BoundedQueueException e) {
            System.err.println("Unexpected Exception while dequeueing " + queueSize + " items into empty queue.");
            System.exit(-1);
            return;
        }        

        try {
            x = 700;
            for(int i = 0; i < queueSize; i++) {
                queue.enqueue(x);
                values[i] = x++;
            }
        } catch (BoundedQueueException e) {
            System.err.println("Unexpected Exception while enqueueing " + queueSize + " items into empty queue.");
            System.exit(-1);
            return;
        }

        try {
            for(int i = 0; i < queueSize; i++) {
                int result = queue.dequeue();
                if(result != values[i]) {
                    System.err.println("Queue Order Error.");
                    System.exit(-1);
                    return;
                }
            }
        } catch (BoundedQueueException e) {
            System.err.println("Unexpected Exception while dequeueing " + queueSize + " items into empty queue.");
            System.exit(-1);
            return;
        }


        System.out.println("All Tests Passed.");
        System.exit(0);
    }
}


