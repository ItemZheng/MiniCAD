import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class DrawBase implements Serializable{
	private static final long serialVersionUID = 1L;
	
	// define shapes 
	/* two point*/ 
	public final static int CHOOSE = 0;
	public final static int LINE = 1;
	public final static int RECTANGLE = 2;
	public final static int ELLIPSE = 3;
	public final static int FILLED_RECTANGLE = 4;
	public final static int FILLED_ELLIPSE = 5;
	/* many point*/
	public final static int MULTIPLE_LINE = 6;
	public final static int MULTIPLE_SHAPE = 7;
	/* text */
	public final static int TEXT = 8;
	
	public final static String type_name[] = {"", "Line", "Rectangle", "Ellipse", "Filled Rectangle", 
							"Filled Ellipse", "Multi-point Line", "Multi-point polygen", "Text"};
	
	/*POINT*/
	public final static int TWO_POINT = 2;
	public final static int MUL_POINT = 3;
	public final static int ONE_POINT = 1;
	
	/*Action*/
	public final static int LARGER = 0;
	public final static int SMALLER = 1;
	
	/*Arguments*/
	public final static float LARGER_SCALE = 1.1f;
	public final static float SMALLER_SCALE = 0.8f;
	private static final int CHOOSE_LIMIT = 8;
	
	// points
	protected ArrayList<Point> points = new ArrayList<Point>();
	/* 
	 * if use two points, just use first two points
	 * if use multiple points, use all points 
	 * if it is Text, use first point as position
	 * 
	 * */
	
	// type
	protected int type = LINE;
	
	// color
	protected Color color = new Color(0, 0, 0);
	
	// stroke of the pen size
	protected float pen_size = 1;
	
	//text content and font
	protected String content = "";
	protected Font font = null;
	// Constructor of 
	public DrawBase(Point start, int type) {
		// use default config
		pen_size = DefultConfig.pen_size;
		color = DefultConfig.color;
		font = DefultConfig.draw_font;
		
		// add points
		this.type = type;
		points.add(start);
	}
	
	// translate vector
	void translate(Point vector) {
		// update all points
		
		for (Point point : points) {
			point.x += vector.x;
			point.y += vector.y;
		}
	}

	// change type 
	void transTo(int type) {
		this.type = type;
	}
	
	// default resize function
	void resize(int op) {
		if(type == TEXT) {
			int font_size = font.getSize();
			if(op == LARGER) {
				font_size += 1;
				if(font_size < 80) {
					font = new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, font_size);
				}
			}
			else {
				font_size -= 1;
				if(font_size > 10) {
					font = new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, font_size);
				}
			}			
			return;
		}
		// judge 
		int defult_size = 2;
		if(type == MULTIPLE_LINE || type == MULTIPLE_SHAPE) {
			defult_size = points.size();
		}
		
		float total_x = 0;
		float total_y = 0;
		
		for (int i = 0; i < defult_size; i++) {
			Point point = points.get(i);
			total_x += point.x;
			total_y += point.y;
		}
		
		// center
		float cx = total_x / (float)defult_size;
		float cy = total_y / (float)defult_size;
		
		// ratio
		float ratio = LARGER_SCALE;
		if(op == SMALLER) {
			ratio = SMALLER_SCALE;
		}
		
		for(int i = 0; i < defult_size; i++) {
			Point point = points.get(i);			

			// new position
			point.x = (int)((point.x - cx) * ratio + cx);
			point.y = (int)((point.y - cy) * ratio + cy);
		}
	}
	
	// default if been chosen function
	boolean is_choosen(Point pos, Graphics2D graphics) {
		if(type == LINE) {
			// two points
			Point p1 = points.get(0);
			Point p2 = points.get(1);
			
			int x_valid = (pos.x - p1.x) * (pos.x - p2.x);
			int y_valid = (pos.y - p1.y) * (pos.y - p2.y);
			
			if(x_valid <= 0 && y_valid <= 0) {
				// calculate distance
				int delt_y = p2.y - p1.y;
				int delt_x = p2.x - p1.x;
				// compare distance
				if(delt_x == 0) {
					if(Math.abs(p2.x - pos.x) < CHOOSE_LIMIT) {
						return true;
					}
				}
				else {
					float k = delt_y / (float)delt_x;
					float b = p1.y - k * p1.x;   // y = k * x + b
					float dis = (pos.y - k * pos.x - b) / (float)Math.sqrt( 1 + k * k);
					if(Math.abs(dis) < CHOOSE_LIMIT) {
						return true;
					}
				}
				
			}
		}
		else if(type == RECTANGLE || type == FILLED_RECTANGLE) {
			// two points
			Point p1 = points.get(0);
			Point p2 = points.get(1);
			
			int x_valid = (pos.x - p1.x) * (pos.x - p2.x);
			int y_valid = (pos.y - p1.y) * (pos.y - p2.y);
			
			if(x_valid <= 0 && y_valid <= 0) {
				return true;
			}
		}
		else if(type == ELLIPSE || type == FILLED_ELLIPSE) {
			// two points
			Point p1 = points.get(0);
			Point p2 = points.get(1);
			
			// parament of a ellipse
			float cx = (p1.x + p2.x) / 2.0f;
			float cy = (p1.y + p2.y) / 2.0f;
			float a = Math.abs(p1.x - p2.x) / 2;
			float b = Math.abs(p1.y - p2.y) / 2;
			
			int x = pos.x;
			int y = pos.y;
			
			if(Math.pow(x-cx, 2)/Math.pow(a, 2) + Math.pow(y-cy, 2)/Math.pow(b, 2) <= 1) {
				return true;
			}
		}
		else if(type == MULTIPLE_LINE) {
			for(int i = 0; i < points.size() - 1; i++) {
				// two points
				Point p1 = points.get(i);
				Point p2 = points.get(i+1);
				
				int x_valid = (pos.x - p1.x) * (pos.x - p2.x);
				int y_valid = (pos.y - p1.y) * (pos.y - p2.y);
				
				if(x_valid <= 0 && y_valid <= 0) {
					// calculate distance
					int delt_y = p2.y - p1.y;
					int delt_x = p2.x - p1.x;
					// compare distance
					if(delt_x == 0) {
						if(Math.abs(p2.x - pos.x) < CHOOSE_LIMIT) {
							return true;
						}
					}
					else {
						float k = delt_y / (float)delt_x;
						float b = p1.y - k * p1.x;   // y = k * x + b
						float dis = (pos.y - k * pos.x - b) / (float)Math.sqrt( 1 + k * k);
						if(Math.abs(dis) < CHOOSE_LIMIT) {
							return true;
						}
					}
					
				}
			}
		}
		else if(type == MULTIPLE_SHAPE) {
			java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();  
			ArrayList<Point2D.Double> polygon = new ArrayList<Point2D.Double>();
			for(int i = 0; i < points.size(); i++) {
				Point2D.Double new_point = new Point2D.Double(points.get(i).x, points.get(i).y);
				polygon.add(new_point);
			}
			
			Point2D.Double first = polygon.get(0);  
	          p.moveTo(first.x, first.y);  
	          polygon.remove(0);  
	          for (Point2D.Double d : polygon) {  
	             p.lineTo(d.x, d.y);  
	          }
	          p.lineTo(first.x, first.y);  
	          p.closePath();  
	          
	          Point2D.Double point = new Point2D.Double(pos.x, pos.y);
	          return p.contains(point);  
		}
		else if(type == TEXT) {
			FontMetrics fMetrics = graphics.getFontMetrics(font);
			int width = fMetrics.stringWidth(content);
			int height = fMetrics.getHeight();
			
			Point p1 = points.get(0);
			
			int x_valid = (pos.x - p1.x) * (pos.x - p1.x - width);
			int y_valid = (pos.y - p1.y) * (pos.y - p1.y + height);
			
			if(x_valid <= 0 && y_valid <= 0) {
				return true;
			}
		}
		
		return false;
	}
	
	// default update
	void update(int index, Point pos) {
		if(index != 1) {
			return;
		}
		if(points.size() == 1) {
			points.add(pos);
		}
		else {
			points.set(index, pos);
		}
	}
	
	// draw
	void draw(Graphics2D graphics, Boolean choosed) {
		graphics.setStroke(new BasicStroke(pen_size));
		graphics.setColor(color);
		if(choosed) {
			graphics.setColor(new Color(255, 0, 0));
		}
		graphics.setFont(font);
		if(type == LINE) {
			graphics.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
		}
		else if(type == RECTANGLE || type == FILLED_RECTANGLE || type == ELLIPSE || type == FILLED_ELLIPSE) {
			int max_x = points.get(0).x > points.get(1).x ? points.get(0).x: points.get(1).x;
			int min_x = points.get(0).x <= points.get(1).x ? points.get(0).x: points.get(1).x;
			int max_y = points.get(0).y > points.get(1).y ? points.get(0).y: points.get(1).y;
			int min_y = points.get(0).y <= points.get(1).y ? points.get(0).y: points.get(1).y;
			
			if(type == RECTANGLE)
				graphics.drawRect(min_x, min_y, max_x-min_x, max_y-min_y);
			else if(type == FILLED_RECTANGLE)
				graphics.fillRect(min_x, min_y, max_x-min_x, max_y-min_y);
			else if(type == ELLIPSE)
				graphics.drawArc(min_x, min_y, max_x - min_x, max_y - min_y, 0, 360);
			else if(type == FILLED_ELLIPSE)
				graphics.fillArc(min_x, min_y, max_x - min_x, max_y - min_y, 0, 360);
		}
		else if(type == TEXT) {
			graphics.drawString(content, points.get(0).x, points.get(0).y);
		}
		else if(type == MULTIPLE_LINE) {
			for (Point point : points) {
				graphics.fillArc(point.x-3, point.y-3, 6, 6, 0, 360);
			}
			for(int i = 0; i < points.size() - 1; i++) {
				graphics.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
			}
		}
		else if(type == MULTIPLE_SHAPE) {
			for (Point point : points) {
				graphics.fillArc(point.x-3, point.y-3, 6, 6, 0, 360);
			}
			for(int i = 0; i < points.size(); i++) {
				graphics.drawLine(points.get(i).x, points.get(i).y, points.get((i+1) % points.size()).x, points.get((i+1) % points.size()).y);
			}
		}
	}

	//add point
	void add(Point point) {
		points.add(point);
	}

}