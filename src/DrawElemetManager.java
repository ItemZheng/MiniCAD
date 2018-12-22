import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

public class DrawElemetManager {
	
	private String file_path = "example.mcad";
	private boolean isRead = false;
	private boolean isSaved = true;
	
	// save all elements
	private ArrayList<DrawBase> elements = new ArrayList<DrawBase>();
	// the chosed one
	private DrawBase chosen_element = null;
	public static JFrame frame = null;
	
	// add an element
	void add(DrawBase drawBase) {
		elements.add(drawBase);
		isSaved = false;
	}
	
	// delete 
	void remove(DrawBase drawBase) {
		isSaved = false;
		elements.remove(drawBase);
	}
	
	void clear() {
		elements.clear();
		isSaved = false;
		chosen_element = null;
	}
	
	// choose element
	DrawBase choose(Point pos, Graphics2D graphics) {
		for (DrawBase drawBase : elements) {
			// test all element if can be choose
			if(drawBase.is_choosen(pos, graphics)) {
				isSaved = false;
				return drawBase;
			}
		}
		return null;
	}
	
	//draw element
	void draw(Graphics2D graphics) {
		// draw all element
		for (DrawBase drawBase : elements) {
			if(drawBase == chosen_element) {
				drawBase.draw(graphics, true);
			}
			else {
				drawBase.draw(graphics, false);
			}
		}
	}
	
	// set file path
	void set_path(String path) {
		this.file_path = path;
	}
	
	// save to local file
	void save() {
		if(!isRead) {
			// tip to set path
			JFileChooser fileChooser = new JFileChooser();
			FileSystemView fsv = FileSystemView.getFileSystemView();
			fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
			fileChooser.setDialogTitle("Choose directory to save...");
			fileChooser.setApproveButtonText("Save");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = fileChooser.showOpenDialog(frame);
			if (JFileChooser.APPROVE_OPTION == result) {
		    	   String path = fileChooser.getSelectedFile().getPath();
		    	   file_path = path;
		    	   isRead = true;
		    }
			else {
				return;
			}
		}
		if(isRead) {
			File file =new File(file_path);
		    try {
		    	// test if exist
		    	if(!file.exists()) {
		    		file.createNewFile();
		    	}
		    	// output
		    	FileOutputStream fileOutputStream = new FileOutputStream(file);
		        ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);
		        objectOutputStream.writeObject(elements);
		        objectOutputStream.flush();
		        objectOutputStream.close();
		        isSaved = true;
		    } catch (IOException e) {
		        System.out.println("Save to file failed!");
		        e.printStackTrace();
		    }	
		}
	}
	
	public void open() {
		String path = null;
		JFileChooser fileChooser = new JFileChooser();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
		fileChooser.setDialogTitle("Choose file...");
		fileChooser.setApproveButtonText("Open");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(frame);
		if (JFileChooser.APPROVE_OPTION == result) {
	    	   path = fileChooser.getSelectedFile().getPath();
	    }
		else {
			return;
		}
		if(path == null) {
			return;
		}
		// init
		ArrayList<DrawBase> new_elements = null;
        File file =new File(path);
        try {
        	// test if exist
	    	if(!file.exists()) {
	    		return ;
	    	}
        	// read file 
        	FileInputStream inFileInputStream = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(inFileInputStream);
            new_elements = (ArrayList<DrawBase>)(objIn.readObject());
            objIn.close();
            elements = new_elements;
            isRead = true;
            isSaved = true;
            file_path = path;
        } catch (IOException e) {
        	// error
            System.out.println("read object failed");
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
	}
	
	public void set_choose(DrawBase choosen) {
		chosen_element = choosen;
	}
	
	public DrawBase get_choose() {
		return chosen_element;
	}

	public void close() {
		if(!isSaved) {
			save();
		}
		
		if(isSaved) {
			isRead = false;
			isSaved = true;
			file_path = "example.mcad";
			elements.clear();
			chosen_element = null;
		}
	}

}
