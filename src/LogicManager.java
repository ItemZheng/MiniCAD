import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
public class LogicManager{
	// menu bar to save or open
	public JMenuBar menuBar = null;
	public JToolBar toolBar = null;
	public DrawPanel drawPanel = null;
	public JLabel statusbar = null;
	private String type_name[] = {"Unchosen","Line", "Rectangle", "Ellipse", "Filled Rectangle", 
			"Filled Ellipse", "Multi-point Line", "Multi-point polygen", "Text"};
	// manager all element
	private DrawElemetManager draw_manager = new DrawElemetManager();
	
	// save all tool bar buttons
	private ArrayList<JButton> tool_buttons = new ArrayList<JButton>();
	private Font font = new Font("Consolas", Font.PLAIN, 15);

	private JFrame frame = null;
	JComboBox<String> types = null;
	JButton pensize_button = null;
	JButton color_button = null;
	JButton change_text_button = null;
	// the chosed one
	public DrawBase chosen_element = null;
	
	public JPanel choosen_proprety = null;
	
	// Init menuBar
	public LogicManager(JFrame frame) {
		this.frame = frame;
		
		// JMenuBar
		menuBar = new JMenuBar();
		menuBar.add(set_file_menu());
		menuBar.add(set_help_menu());
		
		// Tool Bar
		toolBar = new JToolBar();
		set_tool_bar(toolBar);
		
		// status
		statusbar = new JLabel("  Mode: Draw    Type:Line    Status: Not drawing");
		statusbar.setFont(DefultConfig.font);
		statusbar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		//the panel
		drawPanel = new DrawPanel(draw_manager, statusbar, this);
		
		DrawElemetManager.frame = frame;
		
		choosen_proprety = new JPanel(new GridLayout(20, 2, 3, 3));
		set_proprety_panel();
		update_property();
	}
	
	private JMenu set_file_menu() {
		// a new menu
		JMenu file_menu = new JMenu("File");
		JMenuItem open_item = new JMenuItem("Open...");
		open_item.setFont(font);
		JMenuItem save_item = new JMenuItem("Save");
		save_item.setFont(font);
		JMenuItem close_item = new JMenuItem("Close");
		close_item.setFont(font);
		JMenuItem exit_item = new JMenuItem("Exit");
		exit_item.setFont(font);
		
		//add action
		exit_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		//add action
		save_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				draw_manager.save();
			}
		});
		
		//close action
		close_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				draw_manager.close();
				drawPanel.close();
				chosen_element = null;
			}
		});
		
		open_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawPanel.close();
				chosen_element = null;
				draw_manager.open();
				drawPanel.refresh();
			}
		});
		
		//add item
		file_menu.add(open_item);
		file_menu.add(save_item);
		file_menu.add(close_item);
		file_menu.addSeparator();       // add a separator
		file_menu.add(exit_item);
		file_menu.setFont(font);
		
		return file_menu;
	}
	
	private JMenu set_help_menu() {
		// a new menu
		JMenu help_menu = new JMenu("Help");
		help_menu.setFont(font);
		
		JMenuItem help_item = new JMenuItem("help...");
		help_item.setFont(font);
		help_item.addActionListener(e->{
			if (java.awt.Desktop.isDesktopSupported()) {
				try {
					// a uri
					java.net.URI uri = java.net.URI.create("http://www.baidu.com");
					// get desktop
					java.awt.Desktop dp = java.awt.Desktop.getDesktop();
					// judge
					if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
						// open web
						dp.browse(uri);
					}
	 
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		help_menu.add(help_item);
		return help_menu;
	}
	
	private void set_tool_bar(JToolBar toolBar) {
		// set layout
		toolBar.setLayout(new GridLayout(18,1));
		toolBar.setFloatable(false);
		
		// add icon
		// Mouse
		URL mouse_pic = this.getClass().getResource("pic/mouse.png");
		Icon mouse_icon = new ImageIcon(mouse_pic);
		JButton mouse_button = new JButton();
		mouse_button.setToolTipText("Rectangle");
		mouse_button.setIcon(mouse_icon);
		mouse_button.setBorder(null);
		tool_buttons.add(mouse_button);
		
		// Line
		URL line_pic = this.getClass().getResource("pic/line.png");
		Icon line_icon = new ImageIcon(line_pic);
		JButton line_button = new JButton();
		line_button.setToolTipText("Line");
		line_button.setIcon(line_icon);
		line_button.setBorder(BorderFactory.createLineBorder(Color.gray));
		tool_buttons.add(line_button);
		
		// Rectangle
		URL rec_pic = this.getClass().getResource("pic/Rctangle.png");
		Icon rec_icon = new ImageIcon(rec_pic);
		JButton rec_button = new JButton();
		rec_button.setToolTipText("Rectangle");
		rec_button.setIcon(rec_icon);
		rec_button.setBorder(null);
		tool_buttons.add(rec_button);
		
		// Ellipse
		URL elli = this.getClass().getResource("pic/Ellipse.png");
		Icon elli_icon = new ImageIcon(elli);
		JButton elli_button = new JButton();
		elli_button.setToolTipText("Ellipse");
		elli_button.setIcon(elli_icon);
		elli_button.setBorder(null);
		tool_buttons.add(elli_button);
		
		// filled rectangle
		URL fill_rec = this.getClass().getResource("pic/Filled_Rctangle.png");
		Icon fill_rec_icon = new ImageIcon(fill_rec);
		JButton fill_rec_button = new JButton();
		fill_rec_button.setToolTipText("Filled Rectangle");
		fill_rec_button.setIcon(fill_rec_icon);
		fill_rec_button.setBorder(null);
		tool_buttons.add(fill_rec_button);
		
		// filled ellipse
		URL fill_elli = this.getClass().getResource("pic/Filled_Ellipse.png");
		Icon fill_elli_icon = new ImageIcon(fill_elli);
		JButton fill_elli_button = new JButton();
		fill_elli_button.setToolTipText("Filled Ellipse");
		fill_elli_button.setIcon(fill_elli_icon);
		fill_elli_button.setBorder(null);
		tool_buttons.add(fill_elli_button);
		
		//multiple points lines
		URL mul_line = this.getClass().getResource("pic/Multi_Line.png");
		Icon mul_line_icon = new ImageIcon(mul_line);
		JButton mul_line_button = new JButton();
		mul_line_button.setToolTipText("Multiple points line");
		mul_line_button.setIcon(mul_line_icon);
		mul_line_button.setBorder(null);
		tool_buttons.add(mul_line_button);
		
		//multiple lines shape
		URL mul_shape = this.getClass().getResource("pic/Shapes.png");
		Icon mul_shape_icon = new ImageIcon(mul_shape);
		JButton mul_shape_button = new JButton();
		mul_shape_button.setToolTipText("Multiple lines shape");
		mul_shape_button.setIcon(mul_shape_icon);
		mul_shape_button.setBorder(null);
		tool_buttons.add(mul_shape_button);
		
		//Text
		URL text = this.getClass().getResource("pic/Text.png");
		Icon text_icon = new ImageIcon(text);
		JButton text_button = new JButton();
		text_button.setToolTipText("Text");
		text_button.setIcon(text_icon);
		text_button.setBorder(null);
		tool_buttons.add(text_button);
		
		// add actions and add to bar
		for (JButton button : tool_buttons) {
			toolBar.add(button);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					set_shapes(button);
				}
			});
		}
		
		// change color
		JButton choose_color = new JButton("Color");
		choose_color.setBackground(DefultConfig.color);
		choose_color.setFont(font);
		choose_color.setToolTipText("Change color");
		choose_color.addActionListener(e->{
			try {
				Color color = JColorChooser.showDialog(frame, "Please choose color", DefultConfig.color);
				if(color != null) {
					DefultConfig.color = color;	
					choose_color.setBackground(color);
				}
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		toolBar.add(choose_color);
		
		// change font size
		JButton change_size = new JButton("Size: " + DefultConfig.pen_size);
		change_size.setFont(font);
		change_size.addActionListener(e->{
			change_pen_size(change_size);
		});
		toolBar.add(change_size);
		
		// Clear
		JButton clear_button = new JButton("Clear");
		clear_button.setFont(font);
		clear_button.addActionListener(e->{
			draw_manager.clear();
			chosen_element = null;
			update_property();
			drawPanel.refresh();
		});
		toolBar.add(clear_button);
		
	}
	
	private void set_shapes(JButton choosed) {
		chosen_element= null;
		draw_manager.set_choose(null);
		
		drawPanel.save_last_one();
		drawPanel.refresh();
		for (JButton button : tool_buttons) {
			if(button.equals(choosed)) {
				button.setBorder(BorderFactory.createLineBorder(Color.gray));
				DefultConfig.type = tool_buttons.indexOf(choosed);
			}
			else {
				button.setBorder(null);
			}
		}
		drawPanel.update_status();
		update_property();
	}

	private void change_pen_size(JButton change_size) {
		// read the new size of pen
		String s=JOptionPane.showInputDialog("Please input new pensize(0 < x < 40):");
		if(s != null) {
			// try to parse to float
			float pen = 0;
			try {
				pen = Float.parseFloat(s);
				if(pen > 0 && pen < 40) {
					//update
					DefultConfig.pen_size = Float.parseFloat(String.format("%.1f",pen));
					change_size.setText("Size: " + DefultConfig.pen_size);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void set_proprety_panel() {
		JLabel type_lable = new JLabel("types : ");
		type_lable.setFont(DefultConfig.font);
		choosen_proprety.add(type_lable);
		
		types = new JComboBox<String>(type_name);
		type_lable.setFont(DefultConfig.font);
		choosen_proprety.add(types);
		types.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chosen_element != null) {
					if(chosen_element.type == DrawBase.TEXT) {
						if(types.getSelectedIndex() != DrawBase.TEXT) {
							types.setSelectedItem(DrawBase.TEXT);
							JOptionPane.showMessageDialog(null, "Text type can not change to others!");
						}
					}
					else {
						if(types.getSelectedIndex() != DrawBase.CHOOSE && types.getSelectedIndex() != DrawBase.TEXT) {
							chosen_element.type = types.getSelectedIndex();
							drawPanel.refresh();
						}
						else {
							JOptionPane.showMessageDialog(null, "Can not change to text or unchosen!");
						}
					}
				}
			}
		});
		
		JLabel pensize_lable = new JLabel("pen size : ");
		pensize_lable.setFont(DefultConfig.font);
		choosen_proprety.add(pensize_lable);
		
		pensize_button = new JButton(DefultConfig.pen_size+"");
		pensize_button.setFont(DefultConfig.font);
		choosen_proprety.add(pensize_button);
		pensize_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chosen_element == null) {
					return;
				}
				String s=JOptionPane.showInputDialog("Please input new pensize(0 < x < 40):");
				if(s != null) {
					// try to parse to float
					float pen = 0;
					try {
						pen = Float.parseFloat(s);
						if(pen > 0 && pen < 40) {
							//update
							chosen_element.pen_size = Float.parseFloat(String.format("%.1f",pen));
							pensize_button.setText(chosen_element.pen_size+"");
							drawPanel.refresh();
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		JLabel color_lable = new JLabel("color : ");
		color_lable.setFont(DefultConfig.font);
		choosen_proprety.add(color_lable);
		
		color_button = new JButton("");
		color_button.setBackground(DefultConfig.color);
		choosen_proprety.add(color_button);
		color_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chosen_element == null) {
					return;
				}
				try {
					Color color = JColorChooser.showDialog(frame, "Please choose color", DefultConfig.color);
					if(color != null) {
						DefultConfig.color = color;	
						color_button.setBackground(color);
						chosen_element.color = color;
						drawPanel.refresh();
					}
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		
		JButton larger_button = new JButton("zoom in");
		larger_button.setFont(DefultConfig.font);
		choosen_proprety.add(larger_button);
		larger_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chosen_element != null) {
					chosen_element.resize(DrawBase.LARGER);
					drawPanel.refresh();
				}
			}
		});
		
		JButton small_button = new JButton("zoom out");
		small_button.setFont(DefultConfig.font);
		choosen_proprety.add(small_button);
		small_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chosen_element != null) {
					chosen_element.resize(DrawBase.SMALLER);
					drawPanel.refresh();
				}
			}
		});
		
		JButton delete_button = new JButton("delete");
		delete_button.setFont(DefultConfig.font);
		choosen_proprety.add(delete_button);
		delete_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chosen_element == null) {
					return;
				}
				draw_manager.remove(chosen_element);
				draw_manager.set_choose(null);
				chosen_element = null;
				update_property();
				drawPanel.refresh();
			}
		});
		
		change_text_button = new JButton("change text");
		change_text_button.setFont(DefultConfig.font);
		choosen_proprety.add(change_text_button);
		change_text_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chosen_element == null) {
					return;
				}
				String content = JOptionPane.showInputDialog("Please input new content:");
				chosen_element.content = content;
				drawPanel.refresh();
			}
		});
		
	}
	
	public void update_property() {
		if(chosen_element == null) {
			pensize_button.setText("Unchosen");
			color_button.setBackground(DefultConfig.color);
			types.setSelectedIndex(0);
			change_text_button.setVisible(false);
		}
		else {
			if(chosen_element.type == DrawBase.TEXT)
				change_text_button.setVisible(true);
			pensize_button.setText(chosen_element.pen_size+"");
			color_button.setBackground(chosen_element.color);
			types.setSelectedIndex(chosen_element.type);	
		}
	}
}
