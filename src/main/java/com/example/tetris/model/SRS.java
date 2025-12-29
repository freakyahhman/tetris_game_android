package com.example.tetris.model;

import java.awt.Point;

public class SRS {

	private static final Point[][][] JLSTZ_KICKS = new Point[4][4][5]; // for other tetrominos
	private static final Point[][][] I_KICKS = new Point[4][4][5]; // for I

	static {
		JLSTZ_KICKS[0][1] = new Point[]{ p(0,0), p(-1,0), p(-1,-1), p(0,2), p(-1,2) };
		JLSTZ_KICKS[1][0] = new Point[]{ p(0,0), p(1,0), p(1,1), p(0,-2), p(1,-2) };
		JLSTZ_KICKS[1][2] = new Point[]{ p(0,0), p(1,0), p(1,1), p(0,-2), p(1,-2) };
		JLSTZ_KICKS[2][1] = new Point[]{ p(0,0), p(-1,0), p(-1,-1), p(0,2), p(-1,2) };
		JLSTZ_KICKS[2][3] = new Point[]{ p(0,0), p(1,0), p(1,-1), p(0,2), p(1,2) };
		JLSTZ_KICKS[3][2] = new Point[]{ p(0,0), p(-1,0), p(-1,1), p(0,-2), p(-1,-2) };
		JLSTZ_KICKS[3][0] = new Point[]{ p(0,0), p(-1,0), p(-1,1), p(0,-2), p(-1,-2) };
		JLSTZ_KICKS[0][3] = new Point[]{ p(0,0), p(1,0), p(1,-1), p(0,2), p(1,2) };


		I_KICKS[0][1] = new Point[]{ p(0,0), p(-2,0), p(1,0), p(-2,1), p(1,-2) };      
		I_KICKS[1][0] = new Point[]{ p(0,0), p(2,0), p(-1,0), p(2,-1), p(-1,2) };
		I_KICKS[1][2] = new Point[]{ p(0,0), p(-1,0), p(2,0), p(-1,-2), p(2,1) };
		I_KICKS[2][1] = new Point[]{ p(0,0), p(1,0), p(-2,0), p(1,2), p(-2,-1) };  
		I_KICKS[2][3] = new Point[]{ p(0,0), p(2,0), p(-1,0), p(2,-1), p(-1,2) };
		I_KICKS[3][2] = new Point[]{ p(0,0), p(-2,0), p(1,0), p(-2,1), p(1,-2) };
		I_KICKS[3][0] = new Point[]{ p(0,0), p(1,0), p(-2,0), p(1,2), p(-2,-1) };
		I_KICKS[0][3] = new Point[]{ p(0,0), p(-1,0), p(2,0), p(-1,-2), p(2,1) };
	}

	private static Point p(int x, int y) {
		return new Point(x, -y);
	}

	public static Point[] getKicks(char pieceType, int oldRotation, int newRotation) {
		if (pieceType == 'I') { 
			return I_KICKS[oldRotation][newRotation];
		} else if (pieceType == 'O') {
			return new Point[] { new Point(0,0) }; 
		} else {
			return JLSTZ_KICKS[oldRotation][newRotation];
		}
	}
}