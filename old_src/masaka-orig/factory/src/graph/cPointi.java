package graph;

class cPointi extends Thread {
	int x;
	int y;
	int z;

	cPointi() {
		x = y = z = 0;
	}

	cPointi(int x, int y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}

	cPointi(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/*
	 * Returns the distance of the input point from its perp. proj. to the e1
	 * edge. Uses method detailed in comp.graphics.algorithms FAQ
	 */
	public double DistEdgePoint(cPointi a, cPointi b, cPointi c) {
		double r, s;
		double length;
		double dproj = 0.0;
		length = Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));

		if (length == 0.0) {
			System.out.println("DistEdgePoint: Length = 0");
		}
		r = (((a.y - c.y) * (a.y - b.y)) - ((a.x - c.x) * (b.x - a.x)))
				/ (length * length);
		s = (((a.y - c.y) * (b.x - a.x)) - ((a.x - c.x) * (b.y - a.y)))
				/ (length * length);

		dproj = Math.abs(s * length);

		// System.out.println("XI = " + (a.x + r *(b.x-a.x))+" YI =
		// "+(a.y+r*(b.y-a.y)));
		if ((s != 0.0) && ((0.0 <= r) && (r <= 1.0)))
			return dproj;
		if ((s == 0.0) && Between(a, b, c))
			return 0.0;
		else {
			double ca = Dist(a, c);
			double cb = Dist(b, c);
			return Math.min(ca, cb);
		}
	}

	public double Dist(cPointi p, cPointi p1) // returns the distance of two
	// points
	{
		double l = Math.sqrt(Math.pow((p.x - p1.x), 2)
				+ Math.pow((p.y - p1.y), 2));
		return l;
	}

	public double Dist(cPointi p) // returns the distance of two points
	{
		double l = Math.sqrt(Math.pow((p.x - this.x), 2)
				+ Math.pow((p.y - this.y), 2));
		return l;
	}

	/*
	 * The signed area of the triangle det. by a,b,c; pos. if ccw, neg. if cw
	 */
	public int Area2(cPointi a, cPointi b, cPointi c) {
		int area = ((c.x - b.x) * (a.y - b.y)) - ((a.x - b.x) * (c.y - b.y));
		return area;
	}

	public int AreaSign(cPointi a, cPointi b, cPointi c) {
		double area2;

		area2 = (b.x - a.x) * (double) (c.y - a.y) - (c.x - a.x)
				* (double) (b.y - a.y);

		/* The area should be an integer. */
		if (area2 > 0.5)
			return 1;
		else if (area2 < -0.5)
			return -1;
		else
			return 0;
	}

	/*---------------------------------------------------------------------
	 *Returns true iff c is strictly to the left of the directed
	 *line through a to b.
	 */
	public boolean Left(cPointi a, cPointi b, cPointi c) {
		return AreaSign(a, b, c) > 0;
	}

	public boolean LeftOn(cPointi a, cPointi b, cPointi c) {
		return AreaSign(a, b, c) >= 0;
	}

	public boolean Collinear(cPointi a, cPointi b, cPointi c) {
		return AreaSign(a, b, c) == 0;
	}

	/*---------------------------------------------------------------------
	 *Returns true iff point c lies on the closed segement ab.
	 *First checks that c is collinear with a and b.
	 */
	public boolean Between(cPointi a, cPointi b, cPointi c) {
		cPointi ba, ca;

		if (!Collinear(a, b, c))
			return false;

		/* If ab not vertical, check betweenness on x; else on y. */
		if (a.x != b.x)
			return ((a.x <= c.x) && (c.x <= b.x))
					|| ((a.x >= c.x) && (c.x >= b.x));
		else
			return ((a.y <= c.y) && (c.y <= b.y))
					|| ((a.y >= c.y) && (c.y >= b.y));
	}

	public void Assigndi(cPointd p, cPointi a) {
		p.x = a.x;
		p.y = a.y;
	}

	/*---------------------------------------------------------------------
	  Returns TRUE iff point c lies on the closed segement ab.
	  Assumes it is already known that abc are collinear.
	  (This is the only difference with Between().)
	  ---------------------------------------------------------------------*/
	public boolean Between1(cPointi a, cPointi b, cPointi c) {
		cPointi ba, ca;

		/* If ab not vertical, check betweenness on x; else on y. */
		if (a.x != b.x)
			return ((a.x <= c.x) && (c.x <= b.x))
					|| ((a.x >= c.x) && (c.x >= b.x));
		else
			return ((a.y <= c.y) && (c.y <= b.y))
					|| ((a.y >= c.y) && (c.y >= b.y));
	}
}
