package com.raver;

import java.util.Random;
import android.graphics.Paint;
public interface Constants {
	public static final Paint CIRCLE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
	public static final Random RANDOM = new Random();
	public static final double MAX_VELOCITY = 8;
	public static final double MIN_VELOCITY = -8;
	public static final int NUM_PARTICLES = 12;
	// States
	public static final byte STOP = 0x00;
	public static final byte STARTCOLLIDE = 0x01;
	public static final byte COLLIDE = 0x02;
	public static final byte STARTFIGURE8 = 0x03;
	public static final byte FIGURE8 = 0x04;
	// Math Constants
	public static final double PI_2 = Math.PI / 2.0;
	public static final double PI_4 = Math.PI / 4.0;
	public static final double FIVEPI_2 = 2 * Math.PI + PI_2;
	public static final double INTERVAL = FIVEPI_2 / 3000;
	public static final double DISTANCE = FIVEPI_2 / NUM_PARTICLES;
	public static final double REVOLUTION = 8;
}