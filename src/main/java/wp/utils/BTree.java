package wp.utils;

import java.util.ArrayList;


public class BTree {

	private Node	root;
	private int	lowest;
	private int	highest;
	
	public BTree (int lowest, int highest) {
		this.lowest = lowest;
		this.highest = highest;

		if (lowest * 2 < highest)
			throw new IllegalArgumentException("lowest: " + lowest + " highest: " + highest);
		root = new Node(this);
	}
	
	public static class Node {
		private ArrayList<Integer>	keys;
		private ArrayList<Node>	children;
		private BTree	tree;
		private Node	parent;
		
		public Node (BTree tree) {
			this.tree = tree;
			this.keys = new ArrayList<Integer>();
		}
		
		public Node (BTree tree, Node parent, int theData) {
			this.tree = tree;
			this.keys = new ArrayList<Integer>();
			this.keys.add(theData);
			this.parent = parent;
		}
		
		public boolean	noChildren () {
			return (children == null || children.size() == 0);
		}
		
		public Node		getChildAt (int idx) {
			if (children == null || children.size() <= idx)
				return (null);
			return (children.get(idx));
		}
		
		public void	insert (int data) {
			if (keys.size() < tree.highest) {
				boolean	added = false;
				for (int i = 0; i < keys.size(); i++) {
					if (keys.get(i) > data) {
						Node	child = getChildAt(i);
						if (child == null) 
							keys.add(i, data);
						else 
							child.insert(data);
						
						added = true;
						break;
					}
				}
				
				if (!added) {
					// TODO
				}
			}
			else {
				int	medianIdx = keys.size() / 2;
				int	median = keys.get(medianIdx);
				if (parent == null) {
					parent = new Node(tree, null, median);
				}
				Node	left = new Node(tree);
				for (int i = 0; i < medianIdx; i++) {
					left.keys.add(keys.get(i));
					if (i < children.size())
						left.children.add(children.get(i));
				}
				for (int i = 0; i <= medianIdx; i++) {
					keys.remove(0);
					if (children.size() > 0)
						children.remove(0);
				}						
				
				parent.insert(left, this, median);
			}
		}
		
		private void	insert (Node left, Node right, int data) {
			
		}
		
		/*
		public void	insertBad (int data) {
			if (children == null) {
				children = new ArrayList<Node>();
			}
			
			int	numChildren = children.size();
			boolean	added = false;
			for (int i = 0; i < numChildren; i++) {
				Node	n = children.get(i);
				if (n.data > data) {
					if (numChildren < tree.highest) {
						if (n.noChildren()) {
							children.add(i, new Node(tree, this, data));
						}
						else {
							n.insert(data);
						}
						added = true;
						break;
					}
					else {
						split(data); // TODO split current node into two nodes, find the median value between children
						// and this new data which may recursively cause parents to split
						// lower than median goes to left, higher goes to right
					}
				}
			}
			if (!added) {
				if (numChildren < tree.highest) {
					Node	last = children.get(children.size() - 1);
					if (last.noChildren())
						children.add(new Node(tree, this, data));
					else
						last.insert(data);
				}
				else {
					split(data);
				}
			}
		}
		*/
		
		/**
		 * split means: produce another node, and add it to parent of this node. If that means too many
		 * children, recursively split up.
		 * @param data
		 */
		/*
		public void	split (int data) {
			
			int		numChildren = children == null ? 0 : children.size();
			int []	leafArr = new int [numChildren + 1];
			int		childrenIdx = 0;
			for (int i = 0; i < leafArr.length; i++) {
				if (i < numChildren) {
					int	nextChild = children.get(childrenIdx).data;
					if (data < nextChild)
						leafArr [i] = data;
					else {
						leafArr [i] = nextChild;
						childrenIdx++;
					}
				}
				else
					leafArr [i] = data; 
			}
			//int	median = numChildren % 2 == 1 ? leafArr [numChildren / 2 + 1] : 
			//	(leafArr [numChildren] + leafArr [numChildren + 1]) / 2;
			int	almostMedian = leafArr [numChildren / 2 + 1];
			
			if (parent == null) { // this is the root
				Node	newParent = new Node(tree, null, almostMedian);
				tree.root = newParent; // TODO
			}
			else {
				Node	left = new Node(tree, null, -1); // TODO
				Node	right = new Node(tree, null, -1); // TODO
				for (int i = 0; i < numChildren; i++) {
					int	nextChild = children.get(i).data;
					if (nextChild < almostMedian)
						left.insert(nextChild);
					else
						right.insert(nextChild);
				}
				parent.insert(almostMedian);
			}
			
		}
		*/
	}
		
	public Node	getRoot () {
		return (root);
	}

	public void	insert (int data) {
		root.insert(data);
	}

	/**
	 * This is a test.
	 * @param data
	 */
	public void	delete (int data) {
		
	}
}
