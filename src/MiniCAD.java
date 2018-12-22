import java.awt.BorderLayout;
import javax.swing.JFrame;

public class MiniCAD extends JFrame{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args)  {
		MiniCAD miniCAD = new MiniCAD();
		miniCAD.setVisible(true);
	}
	
	public MiniCAD() {
		//father's construct 
		super();
		// set title
		setTitle("Mini CAD");
		// set size and position
		setSize(1300,700);
		setLocation(200, 100);
		// other settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		LogicManager logicManager = new LogicManager(this);
		add(BorderLayout.NORTH, logicManager.menuBar);
		add(BorderLayout.WEST, logicManager.toolBar);
		add(BorderLayout.CENTER, logicManager.drawPanel);
		add(BorderLayout.SOUTH, logicManager.statusbar);
		add(BorderLayout.EAST, logicManager.choosen_proprety);
	}
	
}
