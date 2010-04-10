package graph;

import java.util.*;

public class Node {

	int	delta_plus;	/* edge starts from this node */
	int	delta_minus;	/* edge terminates at this node */
	int	dist;		/* distance from the start node */
	int	prev;		/* previous node of the shortest path */
	int	succ,pred;	/* node in Sbar with finite dist. */
	int	pw;
	int	dx;
	int	dy;
	
	int	w;
	int	h;
	int	x;
	int	y;
	int totalDistanceIfFollowedThisNode = 0;
	String	name;
	int OrderWanted;
	boolean alreadySupplied = false;
	boolean alreadySuppliedAndBalanceDeducted = false;
	double angle;
	int shortestDist;//direct rout
	Vector<String> nodeNames = new Vector<String>();
}
	