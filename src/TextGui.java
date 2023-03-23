import java.util.ArrayList;
import java.util.Stack;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.io.File;  
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class TextGui extends JFrame {
	
	//Gui Window
	private static final int FRAME_WIDTH = 650;
	private static final int FRAME_HEIGHT = 560;
	
	//Font Types
	private boolean typeListener;
	private static final String fontTypes[] = new String[] 
			{ "Arial","Calibri","Comic Sans MS",
				"Monospaced", "SansSerif", "Serif"};
	
	//Font Styles
	private JCheckBox italicCheckBox;
	private JCheckBox boldCheckBox;
	
	//Font Sizes
	private ArrayList<JRadioButton> fSizeButtons;
	private static final int fontSizes[] = new int[] { 8, 16, 24, 32, 40 };
	private JComboBox<String> facenameCombo;
	private ActionListener listener;
	
	//Text Area
	private JTextArea resultArea;
	
	//Data Structure for undo and redo functionality
	private Stack<FontOption> undoStack;
	private Stack<FontOption> redoStack;
	private boolean cleared;
	
	public TextGui() {
		typeListener = true;
		cleared = false;
		
		//Init stacks
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		
		// Construct menu
		createMenu();
		
		// Construct text area
		createTextArea();
		
		// Listener for font type and sizes
		listener = new ChoiceListener();
		
		// Construct Buttons
		createControlPanel();
		
		// Construct Text
		textAreaAction();
		
		// Construct GUI
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
	
	class ChoiceListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			afterClear();
			if(typeListener) textAreaAction();
		}
	}
	
	class ExitItemListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.exit(0);
		}
	}

	class OpenItemListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String fileName = "";

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			   e.printStackTrace();
			}

			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file == null) {
					return;
				}
				fileName = chooser.getSelectedFile().getAbsolutePath();
			}

			try {
				File myObj = new File(fileName);
				Scanner myReader = new Scanner(myObj);
				String data = "";
				while (myReader.hasNextLine()) {
				  data += myReader.nextLine() + '\n';
				}
				resultArea.setText(data);
				myReader.close();
			  } catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			  }
		}
	}

	class SaveItemListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String fileName = "";

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			   e.printStackTrace();
			}

			JFileChooser chooser = new JFileChooser();
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file == null) {
					return;
				}
				fileName = chooser.getSelectedFile().getAbsolutePath();
			}
			
			if(fileName != ""){
				try {
					FileWriter myWriter = new FileWriter(fileName+".txt");
					myWriter.write(resultArea.getText());
					myWriter.close();
				} catch (Exception e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
			}
			
		}
	}
	
	class undoItemListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			afterClear();
			if(undoStack.size() > 1) {
				FontOption current = undoStack.pop();
				FontOption prev = undoStack.peek();
				
				//Update text to most recent text after clear
				if(!current.isCleared() && prev.isCleared()) {
					current.setText(resultArea.getText());
					prev.setText(resultArea.getText());
				}
				addToStack(redoStack, current);
				updateOptions(prev);
			}
			else {
				FontOption prev = undoStack.peek();
				updateOptions(prev);
			}
		}
	}
	
	class redoItemListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(!redoStack.isEmpty()) {
				FontOption rem = redoStack.pop();
				addToStack(undoStack, getAreaFont());
				addToStack(undoStack, rem);
				updateOptions(rem);
			}
		}
	}
	
	class clearButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			addToStack(undoStack, getAreaFont(true));
			resultArea.setText("");
			addToStack(undoStack, getAreaFont(true));
			undoStack.add(getAreaFont(true));
			cleared = true;
		}
	}
	
	/**
	 * Creates Menu Bar containing File and Edit Menu
	 * 
	 */
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
	}
	
	/**
	 * Creates and adds text are to center of Gui
	 * 
	 * @return a panel containing 1 button
	 */
	private void createTextArea() {
		resultArea = new JTextArea(10, 10);
		resultArea.setLineWrap(true);
		resultArea.setEditable(true);
		resultArea.setText("");
		JScrollPane scrollPane = new JScrollPane(resultArea);
		scrollPane.setBorder(new TitledBorder(new EtchedBorder(), " "));
		add(scrollPane, BorderLayout.CENTER);
	}
	
	
	/**
	 * Creates the Clear Button.
	 * 
	 * @return a panel containing 1 button
	 */
	private JPanel createButton() {
		JButton button = new JButton("Clear");
		ActionListener listener = new clearButtonListener();
		button.addActionListener(listener);
		JPanel bp = new JPanel();
		bp.add(button);
		return bp;
	}
	
	/**
	 * Creates the File menu.
	 * 
	 * @return the menu
	 */
	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");

		//Create Menu Items

		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new OpenItemListener());

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new SaveItemListener());

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ExitItemListener());

		//Add Menu Items
		menu.add(openItem);
		menu.add(saveItem);
		menu.add(exitItem);
		return menu;
	}
	
	/**
	 * Creates the Edit menu.
	 * Undo and Redo
	 * 
	 * @return the menu
	 */
	private JMenu createEditMenu() {
		JMenu menu = new JMenu("Edit");
		JMenuItem undoItem = new JMenuItem("Undo");
		JMenuItem redoItem = new JMenuItem("Redo");
		undoItem.addActionListener(new undoItemListener());
		redoItem.addActionListener(new redoItemListener());
		menu.add(undoItem);
		menu.add(redoItem);
		return menu;
	}

	/**
	 * Creates the control panel to change the font.
	 */
	private void createControlPanel() {
		//Panel for Font options
		JPanel fontPanel = new JPanel();
		JPanel facenamePanel = createComboBox();
		JPanel styleGroupPanel = createCheckBoxes();
		JPanel sizeGroupPanel = createRadioButtons();
		JPanel clearButton = createButton();
		
		fontPanel.add(facenamePanel);
		fontPanel.add(styleGroupPanel);
		fontPanel.add(sizeGroupPanel);
		add(fontPanel, BorderLayout.NORTH);
		add(clearButton, BorderLayout.SOUTH);
	}

	/**
	 * Creates the combo box with the font style choices.
	 * 
	 * @return the panel containing the combo box
	 */
	private JPanel createComboBox() {
		facenameCombo = new JComboBox<String>();
		for(String s : fontTypes) {
			facenameCombo.addItem(s);
		}
		facenameCombo.setEditable(false);
		facenameCombo.addActionListener(listener);

		JPanel panel = new JPanel();
		panel.add(facenameCombo);
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Font"));
		return panel;
	}

	/**
	 * Creates the check boxes for selecting bold and italic styles.
	 * 
	 * @return the panel containing the check boxes
	 */
	private JPanel createCheckBoxes() {
		italicCheckBox = new JCheckBox("Italic");
		italicCheckBox.addActionListener(listener);

		boldCheckBox = new JCheckBox("Bold");
		boldCheckBox.addActionListener(listener);

		JPanel panel = new JPanel();
		panel.add(italicCheckBox);
		panel.add(boldCheckBox);
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Style"));

		return panel;
	}

	/**
	 * Creates the radio buttons to select the font size.
	 * 
	 * @return the panel containing the radio buttons
	 */
	private JPanel createRadioButtons() {
		
		fSizeButtons = new ArrayList<>();
		ButtonGroup group = new ButtonGroup();
		JPanel panel = new JPanel();
		
		for(int i = 0; i < fontSizes.length; i++) {
			fSizeButtons.add(new JRadioButton(fontSizes[i] + " pt."));
			fSizeButtons.get(i).addActionListener(listener);
			
			//Set last button to be default selected
			if(i == fontSizes.length - 3)
				fSizeButtons.get(i).setSelected(true);
			
			//Add buttons to button group and panel
			group.add(fSizeButtons.get(i));
			panel.add(fSizeButtons.get(i));
		}
		
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Size"));

		return panel;
	}
	
	/**
	 * Gets user choice for font name,
	 * style, and size and sets the 
	 * font of the text area
	 */
	private FontOption getAreaFont() {
		// Get font style
		int style = 0;
		if (italicCheckBox.isSelected()) {
			style = style + Font.ITALIC;
		}
		if (boldCheckBox.isSelected()) {
			style = style + Font.BOLD;
		}

		// Get font size
		int size = 0;		
		for(int i = 0; i < fontSizes.length; i++) {
			if(fSizeButtons.get(i).isSelected()) {
				size = fontSizes[i];
				break;
			}
		}
		
		return new FontOption(facenameCombo.getSelectedIndex(), 
								style, size,resultArea.getText());
	}
	
	private FontOption getAreaFont(boolean c) {
		FontOption op = getAreaFont();
		op.setCleared(true);
		return op;
	}

	/**
	 * Sets user choice for font name,
	 * style, and size and sets the 
	 * font of the text area
	 */
	private void setAreaFont(FontOption op) {
		// Set font of text field
		resultArea.setFont(new Font(facenameCombo.getItemAt(op.getFont()), 
										op.getStyle(), op.getSize()));
		resultArea.repaint(getBounds());
		resultArea.revalidate();
	}
	
	private void updateOptions(FontOption op) {
		
		//Update Font Selected
		typeListener = false;
		facenameCombo.setSelectedIndex(op.getFont());
		
		//Update Font Style Buttons
		switch (op.getStyle()) {
			case 0://None Selected
				italicCheckBox.setSelected(false);
        		boldCheckBox.setSelected(false);
				break;
        	case Font.ITALIC:
        		italicCheckBox.setSelected(true);
        		boldCheckBox.setSelected(false);
                 break;
        	case Font.BOLD:
        		italicCheckBox.setSelected(false);
        		boldCheckBox.setSelected(true);
                 break;
        	default://Both Selected
        		italicCheckBox.setSelected(true);
        		boldCheckBox.setSelected(true);
                 break;
		}
		
		//Update Font Size Buttons
		for(int i = 0; i < fontSizes.length; i++) {
			if(fontSizes[i] == op.getSize()) {
				fSizeButtons.get(i).setSelected(true);
				break;
			}
		}
		
		//Update Input Area
		if(op.isCleared()) resultArea.setText(op.getText());
		resultArea.setFont(new Font(facenameCombo.getItemAt(op.getFont()), 
				op.getStyle(), op.getSize()));
		resultArea.repaint(getBounds());
		resultArea.revalidate();
		
		typeListener = true;
	}
	
	private void afterClear() {
		if(cleared) {
			FontOption old = undoStack.pop();
			old.setText(resultArea.getText());
			old.setCleared(true);
			addToStack(undoStack,old);
			cleared = false;
		}
	}

	
	private void textAreaAction() {
		FontOption op = getAreaFont();
		setAreaFont(op);
		addToStack(undoStack, op);
		if(!redoStack.empty()) redoStack.clear();
	}
	
	private void addToStack(Stack<FontOption> s, FontOption op) {
		
		if(s.empty()) s.add(op);
		else if(s.peek().compareStyles(op) &&
				!s.peek().isCleared() && op.isCleared()) {
			s.pop();
			s.add(op);
		}
		else if(!(s.peek().equals(op))) s.add(op);
	}
	
	public void run(String title) {
		JFrame frame = new TextGui();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String pad = "                          ";
		frame.setTitle(pad + pad + pad + title);
		frame.setVisible(true);
	}
}
