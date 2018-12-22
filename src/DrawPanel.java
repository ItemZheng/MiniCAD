import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DrawPanel extends JPanel implements MouseListener,MouseMotionListener{
	private static final long serialVersionUID = 1L;
	
	// draw manager
	private DrawElemetManager draw_manager = null;
	private JLabel statusBar = null;
	private LogicManager logicManager = null;
	
	// the states of this CAD
	DrawBase drawing_element = null;
	
	// information about mouse 
	int clickedButton = -1;
	Point lastPos = null;
	
	public DrawPanel(DrawElemetManager drawElemetManager, JLabel statusBar, LogicManager logicManager) {
		super();
		// set background as white
		setBackground(new Color(255,255,255));
		addMouseListener(this);
		addMouseMotionListener(this);
		draw_manager = drawElemetManager;
		this.statusBar = statusBar;
		this.logicManager = logicManager;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		// begin set length
		if(clickedButton != -1) {
			// get the graphics
			if(clickedButton == MouseEvent.BUTTON1) {
				// left button
				if(use_point() == DrawBase.TWO_POINT) {
					drawing_element.update(1, arg0.getPoint());
					if(lastPos != arg0.getPoint()) {
						refresh();
					}
				}
				if(DefultConfig.type == DrawBase.CHOOSE) {
					// move
					if(draw_manager.get_choose() != null) {
						Point offset = new Point(arg0.getX()-lastPos.x, arg0.getY() - lastPos.y);
						draw_manager.get_choose().translate(offset);
						refresh();
					}
				}
			}
			else {
				//right button
				
			}
			
			lastPos = arg0.getPoint();
			update_status();
		}
		
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// set color
		if(arg0.getButton() == MouseEvent.BUTTON1) {
			// left button
			clickedButton = MouseEvent.BUTTON1;
			if(use_point() == DrawBase.TWO_POINT) {
				create_temp(arg0.getPoint());
			}
			else if(use_point() == DrawBase.ONE_POINT) {
				// read the new size of pen
				String s = JOptionPane.showInputDialog("Please input text content:");
				if(s != null) {
					create_temp(arg0.getPoint());
					drawing_element.content = s;
					save_last_one();
					refresh();
				}
			}
			else if(use_point() == DrawBase.MUL_POINT) {
				if(drawing_element == null) {
					create_temp(arg0.getPoint());
					refresh();
				}
				else {
					drawing_element.add(arg0.getPoint());
					refresh();
				}
			}
			
			if(DefultConfig.type == DrawBase.CHOOSE) {
				logicManager.chosen_element = draw_manager.choose(arg0.getPoint(), (Graphics2D)getGraphics());
				draw_manager.set_choose(logicManager.chosen_element);
				refresh();
				logicManager.update_property();
			}
			else {
				logicManager.chosen_element = null;
				draw_manager.set_choose(logicManager.chosen_element);
				logicManager.update_property();
			}
			lastPos = arg0.getPoint();
			update_status();
		}
		else {
			clickedButton = -1;
			save_last_one();
			refresh();
			update_status();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(use_point() == DrawBase.TWO_POINT) {
			save_last_one();
			update_status();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	private void create_temp(Point start) {
		drawing_element = new DrawBase(start, DefultConfig.type);
	}
	
	public void save_last_one() {
		if(drawing_element != null) {
			if(use_point() == DrawBase.TWO_POINT || use_point() == DrawBase.MUL_POINT) {
				if(drawing_element.points.size() >= 2) {
					draw_manager.add(drawing_element);
				}
			}
			else {
				draw_manager.add(drawing_element);
			}
			drawing_element = null;
		}
	}

	public int use_point() {
		if(DefultConfig.type == DrawBase.LINE || DefultConfig.type == DrawBase.ELLIPSE ||
				DefultConfig.type == DrawBase.RECTANGLE || DefultConfig.type == DrawBase.FILLED_ELLIPSE
				|| DefultConfig.type == DrawBase.FILLED_RECTANGLE) {
			return DrawBase.TWO_POINT;
		}
		else if(DefultConfig.type == DrawBase.MULTIPLE_LINE || DefultConfig.type == DrawBase.MULTIPLE_SHAPE) {
			return DrawBase.MUL_POINT;
		}
		else if(DefultConfig.type == DrawBase.TEXT) {
			return DrawBase.ONE_POINT;
		}
		return -1;
	}

	public void refresh() {
		Graphics2D graphics = (Graphics2D)getGraphics();
		this.paint(graphics); // clear
		draw_manager.draw(graphics);
		if(drawing_element != null) {
			if(DefultConfig.type != DrawBase.MULTIPLE_SHAPE) {
				drawing_element.draw(graphics, false);
			}
			else {
				for(Point point: drawing_element.points) {
					graphics.setStroke(new BasicStroke(DefultConfig.pen_size));
					graphics.setColor(DefultConfig.color);
					graphics.fillArc(point.x-3, point.y-3, 6, 6, 0, 360);
				}
			}
		}
			
	}
	
	public void update_status() {
		String status = "  ";
		// Mode
		if(DefultConfig.type == DrawBase.CHOOSE) {
			status += "Mode: Choose  ";
		}
		else {
			status += "Mode: Draw    ";
			//Type
			status += "Type: " + DrawBase.type_name[DefultConfig.type];
			
			if(drawing_element != null) {
				status += "    Status: Drawing";
			}
			else {
				status += "    Status: Not drawing";
			}
			
			if(DefultConfig.type == DrawBase.MULTIPLE_LINE || DefultConfig.type == DrawBase.MULTIPLE_SHAPE) {
				status += "    Tip: cliek right mouse button to end drawing";
			}
		}
		statusBar.setText(status);
	}
	
	public void close() {
		drawing_element = null;
		clickedButton = -1;
		lastPos = null;
		refresh();
		update_status();
	}
}