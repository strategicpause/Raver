package com.raver;
/**
 * 
 * @author Nick Peters
 *
 */
public class Vector2D {
	public double x;
	public double y;
	
	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vector2D vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	public void add(Vector2D other) {
		this.x += other.x;
		this.y += other.y;
	}
	
	public void sub(Vector2D other) {
		this.x -= other.x;
		this.y -= other.y;
	}
	
	public double dotProduct(Vector2D other) {
		return x * other.x + y * other.y;
	}
	
	public double length() {
		return Math.sqrt((x * x) + (y * y));
	}
	
	public void normalize() {
		double length = length();
		x /= length;
		y /= length;
	}
	
	public void mult(double scalar) {
		x *= scalar;
		y *= scalar;
	}
}
