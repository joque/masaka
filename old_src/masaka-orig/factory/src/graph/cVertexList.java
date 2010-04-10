package graph;

import java.awt.Color;
import java.awt.Graphics;

class cVertexList {
	int n; // 0 means empty; 1 means one vertex; etc.
	cVertex head;
	cPointi v;

	cVertexList() {
		head = null;
		v = new cPointi();
		n = 0;
	}

	cVertexList(int i, int j) {
		v = new cPointi();
		v.x = i;
		v.y = j;
		v.z = i * i + j * j;
		// prev = next = null;
	}

	public cVertex GetElement(int index) {

		cVertex v = new cVertex();
		if (index <= n) {
			v = head;
			for (int i = 0; i < index; i++)
				v = v.next;

		} else
			v = new cVertex(10000, 10000);

		return v;
	}

	public cVertex MakeNullVertex() {
		cVertex v = new cVertex();
		InsertBeforeHead(v);
		return v;
	}

	public void InitHead(cVertex h) {
		head = new cVertex();
		head = h;
		head.next = head.prev = head;
		n = 1;
	}

	public void ClearVertexList() {
		if (head != null)
			head = null;
		n = 0;
	}

	/*
	 * Inserts newV before oldV
	 */
	public void InsertBeforeHead(cVertex ver) {
		if (head == null)
			InitHead(ver);
		else {
			InsertBefore(ver, head);
		}
	}

	public void InsertBefore(cVertex newV, cVertex oldV) {
		if (head == null)
			InitHead(newV);
	/*	else {
			oldV.prev.next = newV;
			newV.prev = oldV.prev;
			newV.next = oldV;
			oldV.prev = newV;
			n++;
		}*/
	}

	public void SetVertex(int x, int y) {
		cVertex v = new cVertex(x, y);
		InsertBeforeHead(v);
	}

	public void SetVertex3D(int x, int y, int z) {
		cVertex v = new cVertex(x, y, z);
		InsertBeforeHead(v);
	}

	/* Adds vertex, inserting in between vertices of the closes edge */
	public void AddVertex(int x, int y) {
		cVertex v = new cVertex(x, y);
		// gets vertex of 1st vertex of the closest edge to the point
		cVertex vNear = GetEdge(x, y);
		if (vNear != null)
			InsertBefore(v, vNear.next);
	}

	public void ResetVertex(cVertex resV, int x, int y) {
		resV.v.x = x;
		resV.v.y = y;
	}

	public void ResetVertex(cVertex resV, int x, int y, int vnum, boolean mark) {
		resV.v.x = x;
		resV.v.y = y;
		resV.vnum = vnum;
		resV.mark = mark;
	}

	public void Delete(cVertex ver) {
		if (head == head.next)
			head = null;
		else if (ver == head)
			head = head.next;

		ver.prev.next = ver.next;
		ver.next.prev = ver.prev;
		n--;
	}

	/*
	 * Makes a copy of present list
	 */
	public void ListCopy(cVertexList list) {
		cVertex temp1 = head, temp2;
		do {
			temp2 = new cVertex(); // Create a new vertex cell
			temp2.v = temp1.v; // Fill it with the same cPointi as in list
			temp2.mark = temp1.mark;
			temp2.ear = temp1.ear;
			// temp2.duplicate = temp1.duplicate;
			temp2.onhull = temp1.onhull;
			temp2.vnum = temp1.vnum;
			list.InsertBeforeHead(temp2);
			temp1 = temp1.next;
		} while (temp1 != head);
	}

	/*
	 * Reverses the vertices, in order to get a ccw orientation 1234 becomes
	 * 1432
	 */
	public void ReverseList() {
		cVertexList listcopy = new cVertexList();
		cVertex temp1, temp2;
		ListCopy(listcopy);
		this.ClearVertexList();

		// Fill this list in proper order:
		temp1 = listcopy.head;
		do {
			temp2 = new cVertex();
			temp2.v = temp1.v;
			InsertBeforeHead(temp2);
			temp1 = temp1.prev;
		} while (temp1 != listcopy.head);
		System.out.println("Reversing list...");
	}

	/*
	 * Makes the last element to be the head and head to be the last, e.g., 0123
	 * becomes 3210
	 */
	public void ReverseListCompletely() {
		cVertexList listcopy = new cVertexList();
		cVertex temp1, temp2;
		ListCopy(listcopy);
		this.ClearVertexList();

		// Fill this list in proper order:
		temp1 = listcopy.head.prev;
		do {
			temp2 = new cVertex();
			temp2.v = temp1.v;
			temp2.mark = temp1.mark;
			temp2.vnum = temp1.vnum;
			InsertBeforeHead(temp2);
			temp1 = temp1.prev;
		} while (temp1 != listcopy.head.prev);
		System.out.println("Reversing list completely...");
	}

	/*
	 * Returns the closest vertex to (x,y)
	 */
	public cVertex GetNearVertex(int x, int y) {
		cVertex vnear = null, vtemp = head;
		double dist = -1.0, dx, dy, mindist = 0.0;

		if (vtemp == null)
			return vnear;

		do {
			dx = vtemp.v.x - x;
			dy = vtemp.v.y - y;
			dist = dx * dx + dy * dy;

			// Initialize on first pass (when vnear==null);
			// otherwise update if new winner
			if (vnear == null || dist < mindist) {
				mindist = dist;
				vnear = vtemp;
			}
			vtemp = vtemp.next;
		} while (vtemp != head);

		return vnear;
	}

	/*
	 * Finds the vertex that was clicked on (in a given boundary)
	 */
	public cVertex FindVertex(int x, int y, int w, int h) {
		cVertex notfound = null;
		cVertex temp = head;

		if (n > 0) {
			do {
				temp = temp.next;
				if ((temp.v.x <= x + (w / 2)) && (temp.v.x >= x - (w / 2))
						&& (temp.v.y <= y + (h / 2))
						&& (temp.v.y >= y - (h / 2)))
					return temp;
			} while (temp != head);
		}
		return notfound;
	}

	/*
	 * Returns nearest edge to (x,y) by returning prior vertex
	 */
	public cVertex GetEdge(int x, int y) {
		cVertex vnear = null, vtemp = head;
		double mindist = 0.0, dist = -1.0;
		int k;
		cPointi p = new cPointi();

		// input query point
		p.x = x;
		p.y = y;

		if (vtemp == null)
			return vnear;

		do {
			dist = p.DistEdgePoint(vtemp.v, vtemp.next.v, p);
			// vtemp.v.PrintPoint();
			if (vnear == null || dist < mindist) {
				mindist = dist;
				vnear = vtemp;
			}
			vtemp = vtemp.next;
		} while (vtemp != head);

		return vnear;
	}

	/*
	 * Returns area of a polygon formed by the list of vertices
	 */
	public int AreaPoly2() {
		int sum = 0;
		cVertex a, p;

		p = head; /* Fixed */
		a = p.next; /* Moving. */
		do {
			sum += p.v.Area2(p.v, a.v, a.next.v);
			a = a.next;
		} while (a.next != head);
		return sum;
	}

	/*
	 * Determine if the polygon/list is oriented counterclockwise (ccw). (A more
	 * efficient method is possible, but here we use the available AreaPoly2())
	 */
	public int Ccw() {
		int sign = AreaPoly2();
		if (sign > 0)
			return 1;
		else
			return -1;
	}

	/*
	 * Returns true if polygon is covex, else returns false
	 */
	public boolean CheckForConvexity() {
		cVertex v = head;
		boolean flag = true;

		do {
			if (!v.v.LeftOn(v.v, v.next.v, v.next.next.v)) {
				flag = false;
				break;
			}
			v = v.next;
		} while (v != head);
		return flag;
	}

	/*
	 * QuickSort of elements using Compare2 function, used for computing
	 * Minkowski Convolution
	 */
	public void Sort2(int lo0, int hi0) {
		if (lo0 >= hi0)
			return;
		cVertex mid = new cVertex();
		mid = GetElement(hi0);

		int lo = lo0;
		int hi = hi0 - 1;

		while (lo <= hi) {
			while (lo <= hi && (Compare2(GetElement(lo), mid) != -1))
				lo++;

			while (lo <= hi && (Compare2(GetElement(hi), mid) != 1))
				hi--;

			if (lo < hi)
				Swap(GetElement(lo), GetElement(hi));
		}
		Swap(GetElement(lo), GetElement(hi0));

		Sort2(lo0, lo - 1);
		Sort2(lo + 1, hi0);
	}

	private void Swap(cVertex first, cVertex second) {
		cVertex temp;

		temp = new cVertex(first.v.x, first.v.y);
		temp.vnum = first.vnum;
		temp.mark = first.mark;

		ResetVertex(first, second.v.x, second.v.y, second.vnum, second.mark);
		ResetVertex(second, temp.v.x, temp.v.y, temp.vnum, temp.mark);
	}

	/*
	 * Function used for Sort2
	 */
	private int Compare2(cVertex tpi, cVertex tpj) {
		int a = 0; /* AreaSign result */
		int x = 0, y = 0; /* projections in 1st quadrant */
		cVertex pi, pj;
		pi = tpi;
		pj = tpj;
		cPointi Origin = new cPointi();

		/*
		 * A vector in the open upper halfplane is after a vector in the closed
		 * lower halfplane.
		 */
		if ((pi.v.y > 0) && (pj.v.y <= 0))
			return 1;
		else if ((pi.v.y <= 0) && (pj.v.y > 0))
			return -1;

		/*
		 * A vector on the x-axis and one in the lower halfplane are handled by
		 * the Left computation below.
		 */

		/* Both vectors on the x-axis requires special handling. */
		else if ((pi.v.y == 0) && (pj.v.y == 0)) {
			if ((pi.v.x < 0) && (pj.v.x > 0))
				return -1;
			if ((pi.v.x > 0) && (pj.v.x < 0))
				return 1;
			else if (Math.abs(pi.v.x) < Math.abs(pj.v.x))
				return -1;
			else if (Math.abs(pi.v.x) > Math.abs(pj.v.x))
				return 1;
			else
				return 0;
		}

		/*
		 * Otherwise, both in open upper halfplane, or both in closed lower
		 * halfplane, but not both on x-axis.
		 */
		else {

			a = Origin.AreaSign(Origin, pi.v, pj.v);
			if (a > 0)
				return -1;
			else if (a < 0)
				return 1;
			else { /* Begin collinear */
				x = Math.abs(pi.v.x) - Math.abs(pj.v.x);
				y = Math.abs(pi.v.y) - Math.abs(pj.v.y);

				if ((x < 0) || (y < 0))
					return -1;
				else if ((x > 0) || (y > 0))
					return 1;
				else
					/* points are coincident */
					return 0;
			} /* End collinear */
		}
	}

	/*
	 * Printing to the console:
	 */
	public void PrintVertices() {
		cVertex temp = head;
		int i = 1;
		if (head != null) {
			do {
				temp.PrintVertex(i);
				temp = temp.next;
				i++;
			} while (temp != head);
		}
	}

	public void PrintVertices3D() {
		cVertex temp = head;
		System.out.println("Printing vertices...");
		if (head != null) {
			do {
				temp.PrintVertex3D();
				temp = temp.next;
			} while (temp != head);
		}
	}

	public void PrintDetailed() {
		cVertex v = head;
		int i = 0;
		do {
			System.out.println("V" + i + ": primary=" + v.mark + " | vnum="
					+ v.vnum);
			// v.v.PrintPoint();
			v = v.next;
			i++;
		} while (v != head);
	}

	/*
	 * Drawing routines
	 */
	public void DrawPoints(Graphics g, int w, int h) {
		// vertex painting loop
		if (n == 0)
			System.out.println("No drawing is possible.");
		else {
			cVertex v = head;

			do {
				g.setColor(Color.blue);
				g.fillOval(v.v.x - (int) (w / 2), v.v.y - (int) (h / 2), w, h);
				v = v.next;
			} while (v != head.prev);
			g.fillOval(v.v.x - (int) (w / 2), v.v.y - (int) (h / 2), w, h);
		}
	}

	/*
	 * Draws first vertex of the list
	 */
	public void DrawHead(Graphics g, int w, int h) {
		cVertex v1 = head;
		if (head == null)
			return;
		g.setColor(Color.blue);
		g.fillOval(v1.v.x - (int) (w / 2), v1.v.y - (int) (h / 2), w, h);
	}

	/*
	 * Draws polygon, filled or unfilled, depending on the fill boolean variable
	 */
	public void DrawPolygon(Graphics g, int w, int h, Color inColor,
			Color vColor, boolean fill) {
		int xPoints[] = new int[n + 1];
		int yPoints[] = new int[n + 1];
		cVertex vtemp = head;
		int j = 0;
		if (head == null)
			return;
		do {
			xPoints[j] = vtemp.v.x;
			yPoints[j] = vtemp.v.y;
			j++;
			vtemp = vtemp.next;
		} while (vtemp != head);
		xPoints[n] = head.v.x;
		yPoints[n] = head.v.y;
		g.setColor(inColor);
		if (fill)
			g.fillPolygon(xPoints, yPoints, n);
		g.setColor(vColor);
		g.drawPolygon(xPoints, yPoints, n + 1);
		for (int k = 0; k < n; k++) {
			g.fillOval(xPoints[k] - (int) (w / 2), yPoints[k] - (int) (h / 2),
					w, h);
		}
	}

	/*
	 * Draws not closed polygon boundary
	 */
	public void DrawChain(Graphics g, int w, int h) {
		// vertex painting loop
		if (head == null)
			System.out.println("No drawing is possible.");
		else {
			cVertex v1 = head;
			cVertex v2;

			do {
				v2 = v1.next;
				g.setColor(Color.blue);
				if (n >= 2)
					g.drawLine(v1.v.x, v1.v.y, v2.v.x, v2.v.y);

				g
						.fillOval(v1.v.x - (int) (w / 2), v1.v.y
								- (int) (h / 2), w, h);
				g
						.fillOval(v2.v.x - (int) (w / 2), v2.v.y
								- (int) (h / 2), w, h);
				v1 = v1.next;
			} while (v1 != head.prev);
		}
	}
}
