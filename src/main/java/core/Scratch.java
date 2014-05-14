package wp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

public class Scratch {

	public static void main (String [] args) {
		
//		int [][]	matrix = createMatrix(8, 8);
//		matrix [2][3] = 1;
//		
//		dilate(matrix, 2);
//		
//		printMatrix(matrix);
//		
//		erode(matrix, 1);
//		
//		printMatrix(matrix);
		
		
//		printMaxSubarray(new int [] {1, -2, 3, 4, 2});
//		printMaxSubarray(new int [] {-2, 1, -3, 4, -1, 2, 1, -5, 4});
//		printMaxSubarray(new int [] {1, 2, 3, 4, 2});
//		printMaxSubarray(new int [] {1});
		
		
//		System.out.println(longestCommonSubstring("aabb", "abba"));
//		System.out.println(longestCommonSubstring("aabb", "aa"));
//		System.out.println(longestCommonSubstring("bb", "aa"));
//		System.out.println(longestCommonSubstring("a", "a"));
//		
		//System.out.println(balancePoint(new int [] {-5, 5, 2, 1, 1, 1}));
		//System.out.println(balancePoint(new int [] {0, 3}));
		//System.out.println(balancePoint(new int [] {1, 2, 3, 4, 5, 6}));
		//System.out.println(balancePoint(new int [] {2, -1, 3, 1}));
		
		
		TreeNode	node1 = new TreeNode("1");
		TreeNode	node2 = new TreeNode("2");
		TreeNode	node3 = new TreeNode("3");
		TreeNode	node4 = new TreeNode("4");
		TreeNode	node5 = new TreeNode("5");
		TreeNode	node6 = new TreeNode("6");
		TreeNode	node7 = new TreeNode("7");
		TreeNode	node8 = new TreeNode("8");
		TreeNode	node9 = new TreeNode("9");
		TreeNode	node10 = new TreeNode("10");
		
		node1.children = new TreeNode [] {node2, node3};
		node2.children = new TreeNode [] {node4, node5, node6};
		node5.children = new TreeNode [] {node7};
		node7.children = new TreeNode [] {node8, node9};
		node3.children = new TreeNode [] {node10};
		
		StringBuffer	fileBuf = new StringBuffer();
		storeTreeToFile(node1, fileBuf);
		System.out.println(fileBuf);
		
		TreeNode	test = loadTreeFromFile(fileBuf, new IdxHolder());
		
		TreeNode	mirrored = mirrorTree(test);
		
		fileBuf = new StringBuffer();
		storeTreeToFile(mirrored, fileBuf);
		System.out.println(fileBuf);
		
		
//		printReverseWords("this is a test.");
//		printReverseWords("test");
//		printReverseWords(" ");
//		printReverseWords("a bb");
//		printReverseWords("bb cc");
//		printReverseWords(" bb");
//		printReverseWords("cc ");
		
		
//		System.out.println(levenshteinDiff("kitten", "sitting"));
//		System.out.println(levenshteinDiff("", ""));
//		System.out.println(levenshteinDiff("kit", "sit"));
//		System.out.println(levenshteinDiff("kk", "ss"));
//		System.out.println(levenshteinDiff("ks", "ss"));
//		System.out.println(levenshteinDiff("what", "than"));
		
//		System.out.println("PRIME FACTORS FOR 2:");
//		printPrimeFactors(2);
//		System.out.println("PRIME FACTORS FOR 3:");
//		printPrimeFactors(3);
//		System.out.println("PRIME FACTORS FOR 4:");
//		printPrimeFactors(4);
//		System.out.println("PRIME FACTORS FOR 10:");
//		printPrimeFactors(10);
//		System.out.println("PRIME FACTORS FOR 7:");
//		printPrimeFactors(7);
//		System.out.println("PRIME FACTORS FOR 20:");
//		printPrimeFactors(20);
//		System.out.println("PRIME FACTORS FOR 66:");
//		printPrimeFactors(66);
//		System.out.println("PRIME FACTORS FOR 100:");
//		printPrimeFactors(100);
		
//		System.out.println(kthLargest(new int [] {1, 3, 5, 5, 4, 10}, 2));
//		System.out.println(kthLargest(new int [] {1, 3, 5, 5, 4, 10}, 1));
//		System.out.println(kthLargest(new int [] {1, 3, 5, 5, 4, 10}, 3));
//		System.out.println(kthLargest(new int [] {1, 3, 5, 5, 4, 10}, 6));
		
//		int [][]	arr = new int [10][10];
//		int [][]	distance = manhattanDistance(arr, 3, 4);
//		for (int i = 0; i < distance.length; i++) {
//			for (int j = 0; j < distance.length; j++)
//				System.out.print(distance [i][j] + " ");
//			System.out.println();
//		}
		
//		System.out.println(fromExcelColName("AA"));
//		System.out.println(fromExcelColName("B"));
//		System.out.println(fromExcelColName("AZ"));
//		System.out.println(fromExcelColName("AAA"));
//		System.out.println(fromExcelColName("D"));
//		System.out.println(fromExcelColName("DADA"));
		
		
//		System.out.println(excelColName(1));
//		System.out.println(excelColName(2));
//		System.out.println(excelColName(26));
//		System.out.println(excelColName(27));
//		System.out.println(excelColName(26 * 2));
//
//		System.out.println(excelColName(26 * 3));
//		System.out.println(excelColName(26 * 2 + 1));
//		System.out.println(excelColName(26 * 26));
//		System.out.println(excelColName(26 * 26 + 26 + 1));
		
		//causeDeadlock();
		
		int []	test1 = new int [] {1};
		int []	test2 = new int [] {1, 2};
		int []	test3 = new int [] {2, 1};
		int []	test4 = new int [] {1, 2, 3, 2, -7, 8};
		int []	test5 = new int [] {1, 2, 3, 2, 1, 0};
		
//		printArray(test1);
//		printBiggestSumBoundary(test1);
//		printLongestRun(test1);
//		printArray(test2);
//		printBiggestSumBoundary(test2);
//		printLongestRun(test2);
//		printArray(test3);
//		printBiggestSumBoundary(test3);
//		printLongestRun(test3);
//		printArray(test4);
//		printBiggestSumBoundary(test4);
//		printLongestRun(test4);
//		printArray(test5);
//		printBiggestSumBoundary(test5);
//		printLongestRun(test5);
	
	}
	
	public static int [][]	multiply (int [][] a, int [][] b) {
		if (a == null || b == null || a [0].length != b.length)
			throw new RuntimeException ("invalid array multiplication");
		
		int [][]	ret = new int [a[0].length][b.length];
		
		for (int i = 0; i < ret.length; i++) {
			for (int j = 0; j < ret [i].length; j++) {
				int	sum = 0;
				
				for (int k = 0; k < a [0].length; k++) {
					sum += a [i][k] * b [k][i];
				}
				ret [i][j] = sum;
			}
		}
		
		return (ret);
	}
	
	public static void	printArray (int [] arr) {
		System.out.print("Array is: ");
		if (arr == null)
			System.out.println("null");
		else
			for (int i = 0; i < arr.length; i++) {
				System.out.print(arr [i]);
				if (i < arr.length - 1)
					System.out.print(", ");
				else
					System.out.println();
			}
	}
	
	/**
	 * 1, 2, 3, 2, 0, -2, -3, 0
	 * longest run: 3...-3 (total is 6)
	 * @param data
	 */
	public static void	printLongestRun (int [] data) {
		if (data != null) {
			int	startIdx = 0;
			int	endIdx = 0;
			int	largestDiff = 0;
			int	largestDiffSoFar = 0;
			int	largestStartIdxSoFar = 0;
			int	largestEndIdxSoFar = 0;
			
			while (endIdx < data.length) {
				int	diff = Math.abs(data [startIdx] - data [endIdx]);
				if (diff >= largestDiff) {
					largestDiff = diff;
					if (largestDiff > largestDiffSoFar) {
						largestDiffSoFar = largestDiff;
						largestStartIdxSoFar = startIdx;
						largestEndIdxSoFar = endIdx;
					}
					endIdx++;
				}
				else {
					startIdx = endIdx - 1;
					largestDiff = 0;
				}
			}
			
			System.out.println ("biggest drop/rise: " + largestDiffSoFar + 
					" start index: " + largestStartIdxSoFar + " end index: " + largestEndIdxSoFar);
		}
	}
	
	public static class BinaryTree {
		private Node	root;
		
		public BinaryTree () {
			
		}
		
		public void	insert (int data) {
			if (root == null)
				root = new Node(data);
			else 
				root.insert(data);
		}
		
		public static class Node {
			
			private int	data;
			private Node	left;
			private Node	right;
			
			public Node (int data) {
				this.data = data;
			}
			
			public void	insert (int data) {
				if (data <= this.data) {
					if (left == null)
						left = new Node(data);
					else
						left.insert(data);
				}
				else {
					if (right == null)
						right = new Node(data);
					else
						right.insert(data);
				}
			}
		}
	}
	
	public static int	levenshteinDiff (String s1, String s2) {
	
		System.out.println("DIFFing: " + s1 + " and " + s2);
		
		int	s1Len = s1 == null ? 0 : s1.length();
		int	s2Len = s2 == null ? 0 : s2.length();
		if (s1Len == 0)
			return (s2Len);
		else if (s2Len == 0)
			return (s1Len);
		
		int [][]	matrix = new int [s1Len + 1][s2Len + 1];
		for (int i = 0; i <= s1Len; i++)
			matrix [i][0] = i;
		
		for (int i = 0; i <= s2Len; i++)
			matrix [0][i] = i;
		
		for (int i = 1; i <= s1Len; i++)
			for (int j = 1; j <= s2Len; j++) {
				char	c1 = s1.charAt(i - 1);
				char	c2 = s2.charAt(j - 1);
				int		cost;
				if (c1 == c2)
					cost = 0;
				else
					cost = 1;
				
				int	minVal = Math.min(matrix [i-1][j] + 1, matrix [i][j-1] + 1);
				minVal = Math.min(minVal, matrix [i-1][j-1] + cost);
				matrix [i][j] = minVal;
			}
		return (matrix [s1Len][s2Len]);
	}
	
	
	/**
	 * 10 -> 1 * 10 + 0
	 * 115 -> 10 * 10 + 1 * 10 + 5
	 * 
	 */
	/**
	 * the difference is: there is no zero as placeholder
	 * 1 -> A
	 * 2 -> B
	 * 26 -> Z
	 * 26 * 1 + 1 -> AA
	 * 28 -> AB
	 * 26 * 2 -> AZ
	 * 26 * 2 + 1 -> BA
	 * 26 * 2 + 26 -> BZ
	 * 26 * 3 -> BZ
	 * 26 * 3 + 1 -> CA
	 * 26 * 26 + 1 -> ZA
	 * 26 * 26 + 26 -> ZZ
	 * 26 * 26 + 26 + 1 -> AAA
	 * @param idx
	 * @return
	 */
	public static String	excelColName (int idx) {
		
		if (idx < 1)
			throw new IllegalArgumentException("Can't be less than 1");
		
		StringBuffer	buf = new StringBuffer();
		int	remainder = idx;
		int	divisor = 1;
		int	power = 0;
		while (divisor < idx) {
			divisor = (int)Math.pow(26, power++);
		}
		if (divisor > 1)
			divisor /= 26;
		
		double	logIdxBase26 = Math.log(idx) / Math.log(26);
		int		testDivisor = (int)Math.pow(26, (int)logIdxBase26);
		
		while (remainder > 26) {					
			/*
			 * divide with divisor.
			 * if no remainder...
			 * 
			 */
			int division = remainder / divisor;
			int	total = division * divisor;
			if (total == remainder) {
				division--;
			}
			
			buf.append((char)((int)'A' + (division - 1)));
			
			remainder -= (divisor * division);
			
			divisor /= 26;
		}
		if (remainder > 0) {
			buf.append((char)((int)'A' + (remainder - 1)));
		}
			
		return (buf.toString());				
	}
	
	public static int		fromExcelColName (String name) {
		if (StringUtils.isEmpty(name))
			throw new IllegalArgumentException(name);
		
		name = name.toUpperCase();
		int	val = 0;
		for (int i = 0; i < name.length(); i++) {
			int	mult = (int)Math.pow(26, name.length() - (i + 1));
			int	charVal = name.charAt(i) + 1 - 'A';
			val += mult * charVal;
		}
		return (val);
	}
	
	/**
	 * Find index such that prefix sum (sum before this index) == suffix sum (sum after this index).
	 * Best solution is O(n) supposedly. such an index may not exist.
	 * @param arr
	 * @return
	 */
	public static int	balancePoint (int [] arr) {
		if (arr == null)
			return (-1); // error index
		
		/**
		 * examples:
		 * 1, 2, 3 -> NONE
		 * 1, 2, 4, 3 -> index 2
		 * 2, 3 -> NONE
		 * 0, 3 -> index 1?
		 * 2, -1, 3, 1 -> index 2
		 * 
		 * -5, 5, 2, 1, 1, 1
		 * 
		 * Seems doable for non-negative numbers - march to center from both sides...
		 * but with negative? perhaps need to backtrack and do something when hitting negative number...?
		 */
		 
		// for non-negative numbers:
		int	prefixSum = 0;
		int	suffixSum = 0;
		int	prefixSumIndex = 0;
		int	suffixSumIndex = arr.length - 1;
		while (suffixSumIndex - prefixSumIndex > 0) {
			if (prefixSum > suffixSum) {
				suffixSum += arr [suffixSumIndex];
				suffixSumIndex--;
			}
			else if (suffixSum > prefixSum) {
				prefixSum += arr [prefixSumIndex];
				prefixSumIndex++;
			}
			else if (prefixSum == suffixSum) {
				if (arr [suffixSumIndex] > arr [prefixSumIndex]) {
					prefixSum += arr [prefixSumIndex];
					prefixSumIndex++;
				}
				else {
					suffixSum += arr [suffixSumIndex];
					suffixSumIndex--;
				}
			}
		}
		
		return (prefixSum == suffixSum ? prefixSumIndex : -1);
	}
	
	public static void	printReverseWords (String s) {
		if (s == null)
			return;
		
		char []	tmp = s.toCharArray();
		reverseString(tmp, 0, tmp.length);
		
		int	swi = 0;
		for (int i = 0; i < tmp.length; i++) {
			char	c = tmp [i];
			if (c == ' ') {			
				reverseString(tmp, swi, i);
				swi = i + 1;
			}
			else if (i == tmp.length - 1) {
				reverseString(tmp, swi, i + 1);
			}
		}
		
		System.out.println(new String(tmp));
	}
	
	
	public static void	reverseString (char [] s, int startIdx, int endIdx) {
		int		length = endIdx - startIdx;
		for (int i = 0; i < length / 2; i++) {
			char	tmpChar = s[i + startIdx];
			int		swapIdx = startIdx + length - (i + 1);
			s [i + startIdx] = s [swapIdx];
			s [swapIdx] = tmpChar;
		}
	}
	
	public static class LinkedList {
		private Link	head;
		private Link	tail;
		
		public void	remove (String data) {
			Link	tmp = head;
			while (tmp != null) {
				
				if (tmp.data != null && tmp.data.equals(data)) {
					Link	next = tmp.next;
					Link	previous = tmp.previous;
					if (previous == null) // this is the head
						head = next;
					else {
						previous.next = next;
					}
					if (next == null) // this is the tail
						tail = previous;
					else {
						next.previous = previous;
					}
					
				}
				tmp = head.next;
			}
		}
	}
	
	public static class Link {
		private Link	next;
		private Link	previous;
		private String	data;
		
		public Link (String data) {
			this.data = data;
		}
		
		
	}
	
	/**
	 * LRU cache:
	 * hashmap and linked list to keep track of order of entries added. LinkedHashMap.
	 * 
	 * 
	 * 
	 */
	
	
	/**
	 * 3, 4, -5, 6 ; sum: 7 --> prints "3, 4"
	 * @param arr
	 * @param sum
	 */
	public static void	printNumbersThatAddUpToSum (int [] arr, int sum) {
		if (arr == null)
			return;
		
		HashMap<Integer, Integer>	numToIdxMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < arr.length; i++)
			numToIdxMap.put(arr [i], i);
		
		for (int i = 0; i < arr.length; i++) {
			int	cur = arr [i];
			int	needed = sum - cur;
			Integer	idx = numToIdxMap.get(needed);
			if (idx != null && idx > i)
				System.out.println(cur + ", " + needed);
		}
	}
	
	public static int [][]	dilate (int [][] matrix, int k) {
		if (matrix == null)
			return (null);
		
		matrix = manhattanDistance(matrix);
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix [i].length; j++) {
				matrix [i][j] = matrix [i][j] <= k ? 1 : 0;
			}
		return (matrix);
	}
	
	public static int [][]	erode (int [][] matrix, int k) {
		if (matrix == null)
			return (null);
		
		//matrix = manhattanDistance(matrix);
		for (int cnt = 0; cnt < k; cnt++) {
			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix [i].length; j++) {
					if (i < matrix.length - 1 && matrix [i + 1][j] == 0)
						matrix [i][j] = 2;
					if (i > 0 && matrix [i - 1][j] == 0)
						matrix [i][j] = 2;
					if (j < matrix [i].length - 1 && matrix [i][j + 1] == 0)
						matrix [i][j] = 2;
					if (j > 0 && matrix [i][j - 1] == 0)
						matrix [i][j] = 2;
				}
			
			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix [i].length; j++) {
					if (matrix [i][j] == 2)
						matrix [i][j] = 0;
				}
		}
		return (matrix);
	}
	
	public static int [][]	createMatrix (int row, int col) {
		int [][]	matrix = new int [row][col];
		for (int i = 0; i < row; i++)
			matrix [i] = new int [col];
		return (matrix);
	}
	
	public static void	printMatrix (int [][] matrix) {
		System.out.println("MATRIX: ");
		if (matrix == null)
			System.out.println("NULL");
		else {
			for (int i = 0; i < matrix.length; i++) {
				System.out.print("[");
				for (int j = 0; j < matrix [i].length; j++) {
					System.out.print(matrix [i][j]);
					if (j < matrix [i].length - 1)
						System.out.print(" ");
				}
				System.out.println("]");
			}
		}
	}
	
	
	public static int [][]	manhattanDistance (int [][] matrix) {
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix [i].length; j++) {
				if (matrix [i][j] == 1) 
					matrix [i][j] = 0;
				else {
					matrix [i][j] = matrix.length + matrix [i].length;
					
					if (i > 0)
						matrix [i][j] = Math.min(matrix [i][j], matrix [i - 1][j] + 1);
					
					if (j > 0)
						matrix [i][j] = Math.min(matrix [i][j], matrix [i][j - 1] + 1);
				}
			}
		
		for (int i = matrix.length - 1; i >= 0; i--)
			for (int j = matrix [i].length - 1; j >= 0; j--) {
				if (i + 1 < matrix.length)
					matrix [i][j] = Math.min(matrix [i][j], matrix [i + 1][j] + 1);
				
				if (j + 1 < matrix [i].length)
					matrix [i][j] = Math.min(matrix [i][j], matrix [i][j + 1] + 1);
			}
		return (matrix);
	}
	
	public static String	longestCommonSubstring (String s1, String s2) {
		if (s1 == null || s2 == null)
			return (null);
		
		int	longest = 0;
		HashSet<String>	longestSet = new HashSet<String>();
		
		int			s1Len = s1.length();
		int			s2Len =s2.length();
		int [][]	matrix = new int [s1Len + 1][s2Len + 1];
		
		for (int i = 0; i < s1Len + 1; i++) {
			matrix [i] = new int [s2Len + 1];
		}
		
		for (int i = 1; i < s1Len + 1; i++) {
			for (int j = 1; j < s2Len + 1; j++) {
				if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
					if (i == 1 || j == 1) 
						matrix [i][j] = 1;
					else 
						matrix [i][j] = matrix [i - 1][j - 1] + 1;
					if (matrix [i][j] > longest) {
						longest = matrix [i][j];
						longestSet.clear();
					}
					if (matrix [i][j] == longest)
						longestSet.add(s1.substring(i - longest, i));
				}
			}
		}
		return (longestSet.size() == 0 ? null : longestSet.iterator().next());
	}
	
	public static TreeNode		mirrorTree (TreeNode t) {
		if (t == null)
			return (null);
		
		TreeNode	mirrored = new TreeNode();
		mirrored.data = t.data;
		
		if (t.children != null) {
			int	numChildren = t.children.length;
			mirrored.children = new TreeNode [numChildren];
			for (int i = 0; i < numChildren; i++) {
				mirrored.children [i] = mirrorTree(t.children [numChildren - (i + 1)]);
			}
		}
		return (mirrored);
	}
	
	public static void	storeTreeToFile (TreeNode t, StringBuffer fileBuf) {
		
		if (t != null) {
			fileBuf.append(t.data);
			
			if (t.children != null) {
				fileBuf.append("{");
				boolean	addedChildren = false;
				for (TreeNode node : t.children) {
					if (addedChildren)
						fileBuf.append("+");
					else
						addedChildren = true;
					storeTreeToFile(node, fileBuf);
				}
				fileBuf.append("}"); // end children
			}
		}
		
	}

	public static class IdxHolder {
		public int	idx;
	}
	
	public static TreeNode	loadTreeFromFile (StringBuffer fileBuf, IdxHolder ih) {
		
		TreeNode	t = null;
		
		int	childIdx = fileBuf.indexOf("{");
		if (childIdx > 0) {
			t = new TreeNode();
			t.data = fileBuf.substring(0, childIdx);
			ih.idx = childIdx + 1;
			t.children = loadChildrenFromFile(fileBuf, ih);
		}
		return (t);
	}
	
	public static TreeNode []	loadChildrenFromFile (StringBuffer fileBuf, IdxHolder ih) {
		
		ArrayList<TreeNode>	arrChildren = new ArrayList<TreeNode>();
		TreeNode	curChild = new TreeNode();
		int			curChildDataStartIdx = -1;
		boolean		done = false;
		while (ih.idx < fileBuf.length() && !done) {
			char	c = fileBuf.charAt(ih.idx++);
			
			switch (c) {
			case '{': // sub tree
				curChild.data = fileBuf.substring(curChildDataStartIdx, ih.idx - 1);
				curChild.children = loadChildrenFromFile(fileBuf, ih);
				arrChildren.add(curChild);
				curChild = new TreeNode();
				curChildDataStartIdx = -1;
				break;
				
			case '}': // end children
				if (curChildDataStartIdx != -1) { // finish adding child
					curChild.data = fileBuf.substring(curChildDataStartIdx, ih.idx - 1);
					arrChildren.add(curChild);
				}
				done = true;
				break;
				
			case '+': { // next child
				if (curChildDataStartIdx != -1) {
					curChild.data = fileBuf.substring(curChildDataStartIdx, ih.idx - 1);
					arrChildren.add(curChild);
					curChild = new TreeNode();
					curChildDataStartIdx = -1;
				}
			}
				break;
				
			default:
				if (curChildDataStartIdx == -1)
					curChildDataStartIdx = ih.idx - 1;
				break;
			}
			
		}
		
		TreeNode []	children;
		
		if (arrChildren.size() > 0) {
			children = new TreeNode [arrChildren.size()];
			arrChildren.toArray(children);
		}
		else
			children = null;
		
		return (children);
	}
	
	/**
	 * 1{2{5}+3+4}
	 * @param t
	 */
	public static void	printDepthFirst (TreeNode t) {
		if (t == null)
			System.out.println("null");
		else {
			TreeNode []	children = t.children;
			if (children != null)
				for (TreeNode node : children) {
					printDepthFirst(node);
				}
			
			System.out.println(t.data);
		}
	}
	
	public static class TreeNode {
		public TreeNode []	children;
		public String	data;
		
		public TreeNode (String data) {
			this.data = data;
		}
		
		public TreeNode () { }
		
		public String	toString () {
			// shallow:
			StringBuffer	buf = new StringBuffer ("DATA: " + data);
			if (children == null || children.length == 0)
				buf.append (" NO CHILDREN");
			else {
				buf.append (" CHILDREN:");
				for (int i = 0; i < children.length; i++)
					buf.append(" " + children [i].data);
			}
			buf.append("\n");
			
			return (buf.toString());
		}
	}
	
	/**
	 * 1, -2, 3, 4, 2
	 * max sub array of length N:
	 * @param arr
	 */
	public static void	printMaxSubarray (int [] arr) {
		if (arr == null)
			return;
		
		int curSum = 0;
		int maxSum = 0;
		int start = 0, end = 0;

		for (int i = 0; i < arr.length; i++) {
			curSum += arr[i];
			if (curSum > maxSum) {
				maxSum = curSum;
				end = i;
			} else if (curSum < 0) {
				start = i + 1;
				curSum = 0;
			}
		}
		
		System.out.println ("MAX TOTAL IS: " + maxSum + " start idx: " + start + " end idx: " + end);
	}
	
	
	/**
	 * 1, -3, 4, 6, 5, -7, 9
	 * @param ints
	 * @return
	 */
	public static void	printBiggestSumBoundary (int [] ints) {
		int	idx = -1;
		if (ints != null) {
			int	total = Integer.MIN_VALUE;
			for (int i = 0; i < ints.length; i++) {
				if (i < ints.length - 1) {
					int	curTotal = ints [i] + ints [i + 1];
					if (curTotal > total) {
						total = curTotal;
						idx = i;
					}
				}
			}
		}
		if (idx == -1)
			System.out.println ("Array is not large enough");
		else
			System.out.println ("Biggest sum boundary idx: " + idx + " values: " +ints [idx] + ", " + ints [idx + 1]);
	}
	
	
	public static void		printPrimeFactors (int val) {
		if (val < 2)
			return;
		
		boolean	hasFactors = false;
		
		if (val >= 3) {
			int	max = (int)(Math.sqrt(val) + 1);
			for (int i = 2; i <= max; i++) {
				if (val % i == 0) {
					int	factor = val / i;
					printPrimeFactors(i);
					printPrimeFactors(factor);
					hasFactors = true;
					break;
				}
			}
		}
		if (!hasFactors)
			System.out.println(val);
	}
	
	public static int		kthLargest (int [] values, int k) {
		if (values == null || values.length < k)
			throw new IllegalArgumentException ("k is too big: " + k);
		
		HashSet<Integer>	avoidIdxSet = new HashSet<Integer>();
		for (int j = 0; j < k; j++) {
			int	largest = Integer.MIN_VALUE;
			int	largestIdx = -1;
			for (int i = 0; i < values.length; i++) {
				if (values [i] >= largest && !avoidIdxSet.contains(i)) {
					largest = values [i];
					largestIdx = i;
				}
				
			}
			avoidIdxSet.add(largestIdx);
			if (j == k - 1)
				return (largest);
		}
		return (0);
	}
	
	
	static class ParserContext {
		public int	idx;
		public Stack<Character>	operStack = new Stack<Character>();
		
		public Character	getCurrentOperation () {
			if (operStack.isEmpty())
				return (null);
			return (operStack.peek());
		}
	}
	public static void		causeDeadlock () {
		final Friend	f1 = new Friend("joe");
		final Friend	f2 = new Friend("bill");
		
		new Thread(new Runnable () {
			public void run() {
				f1.bow(f2);
			}
		}).start();
		new Thread(new Runnable () {
			public void run() {
				f2.bow(f1);
			}
		}).start();
	}
	
	public static class Friend {
	
		private String	name;
		public Friend (String name) {
			this.name = name;
		}
		
		public String	getName () { return (name); }
		
		public synchronized	void	bow (Friend friend) {
			System.out.format("%s: %s has bowed to me!%n", 
                    this.name, friend.getName());

			friend.bowBack(this);
		}
		
		public synchronized void	bowBack (Friend friend) {
			System.out.format("%s: %s has bowed back to me!%n", 
                    this.name, friend.getName());
		}
	}
	
	/**
	 * 3 / 4
	 * 3 / 400
	 * 7 / 4
	 * 7 / 40
	 * 
	 * 
	 * 3 / 400
	 * consider 4
	 * 1
	 * 4 - 1 * 3
	 * 10
	 * 
	 * 
	 * 
	 * @param n1
	 * @param n2
	 * @param accuracy
	 */
	public static void		longDivision (int n1, int n2, int accuracy) {
		
		String	s1 = String.valueOf(n1);
		String	s2 = String.valueOf(n2);
		
		
		int	decCount = 0;
		StringBuffer	buf = new StringBuffer();
		
		
		do {
			int		s1Len = s1.length();
			int		s2Len = s2.length();
			int		numToTry = 0;
			if (s1Len <= s2Len) {
				numToTry = Integer.valueOf(s2.substring(0, s1Len));
				if (numToTry < n1) {
					numToTry = Integer.valueOf(s2.substring(0, s1Len + 1));
				}
			}
		
		}
		while (decCount < accuracy);
		
		
		int				mult = 1;
		int				val = n1;
		while ((val = mult * n1) < n2) {
			mult++;
		}
		
		System.out.println(buf.toString());
	}
}
