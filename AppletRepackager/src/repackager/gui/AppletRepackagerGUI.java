package repackager.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import repackager.Payload;

public class AppletRepackagerGUI {

	private JFrame frame;
	private JTextField codeText;
	private JTextField archiveText;
	
	private File inputAppletFile = null;
	private File outputAppletFile = null;
	private Element appletElement = null;
	
	private static final String APPLET = "applet";
	private static final String ARCHIVE = "archive";
	private static final String CODE = "code";
	
	private static final String JAR_EXTENSION = ".jar";
	private static final String DEFAULT_OUTPUT_SUFFIX = "_repacked.jar";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppletRepackagerGUI window = new AppletRepackagerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppletRepackagerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Applet Repackager");
		frame.setBounds(100, 100, 650, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 550, 0 };
		gridBagLayout.rowHeights = new int[] { 241, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints tabbedPaneGrid = new GridBagConstraints();
		tabbedPaneGrid.fill = GridBagConstraints.BOTH;
		tabbedPaneGrid.gridx = 0;
		tabbedPaneGrid.gridy = 0;
		frame.getContentPane().add(tabbedPane, tabbedPaneGrid);

		JPanel appletPanel = new JPanel();
		tabbedPane.addTab("Applet", null, appletPanel, null);
		GridBagLayout appletPanelGrid = new GridBagLayout();

		appletPanelGrid.columnWeights = new double[] { 0.0, 1.0 };
		appletPanelGrid.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0 };
		appletPanel.setLayout(appletPanelGrid);

		final JTextArea appletHTMLTextArea = new JTextArea();
		appletHTMLTextArea.setLineWrap(true);
		JScrollPane appletHTMLScroll = new JScrollPane(appletHTMLTextArea);
		appletHTMLScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints appletHTMLScrollGrid = new GridBagConstraints();
		appletHTMLScrollGrid.insets = new Insets(0, 0, 5, 0);
		appletHTMLScrollGrid.fill = GridBagConstraints.BOTH;
		appletHTMLScrollGrid.gridx = 1;
		appletHTMLScrollGrid.gridy = 0;
		appletPanel.add(appletHTMLScroll, appletHTMLScrollGrid);
		
		final JLabel parsedFieldsLabel = new JLabel("Parsed Fields");
		GridBagConstraints parsedFieldsLabelGrid = new GridBagConstraints();
		parsedFieldsLabelGrid.insets = new Insets(0, 0, 5, 5);
		parsedFieldsLabelGrid.gridx = 0;
		parsedFieldsLabelGrid.gridy = 1;
		appletPanel.add(parsedFieldsLabel, parsedFieldsLabelGrid);

		final JPanel parsedFieldsPanel = new JPanel();
		GridBagConstraints parsedFieldsPanelGrid = new GridBagConstraints();
		parsedFieldsPanelGrid.insets = new Insets(0, 0, 5, 0);
		parsedFieldsPanelGrid.fill = GridBagConstraints.BOTH;
		parsedFieldsPanelGrid.gridx = 1;
		parsedFieldsPanelGrid.gridy = 1;
		appletPanel.add(parsedFieldsPanel, parsedFieldsPanelGrid);

		final JLabel codeLabel = new JLabel("code: ");
		parsedFieldsPanel.add(codeLabel);

		codeText = new JTextField();
		codeText.setEditable(false);
		parsedFieldsPanel.add(codeText);
		codeText.setColumns(14);

		final JLabel archiveLabel = new JLabel("archive: ");
		parsedFieldsPanel.add(archiveLabel);

		archiveText = new JTextField();
		archiveText.setEditable(false);
		parsedFieldsPanel.add(archiveText);
		archiveText.setColumns(14);
		
		final JButton inputAppletButton = new JButton("Input Applet");
		GridBagConstraints inputAppletButtonGrid = new GridBagConstraints();
		inputAppletButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		inputAppletButtonGrid.insets = new Insets(0, 0, 5, 5);
		inputAppletButtonGrid.gridx = 0;
		inputAppletButtonGrid.gridy = 2;
		appletPanel.add(inputAppletButton, inputAppletButtonGrid);
		
		final JLabel inputAppletLabel = new JLabel("Select main applet JAR...");
		GridBagConstraints inputAppletLabelGrid = new GridBagConstraints();
		inputAppletLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		inputAppletLabelGrid.insets = new Insets(0, 0, 5, 0);
		inputAppletLabelGrid.gridx = 1;
		inputAppletLabelGrid.gridy = 2;
		appletPanel.add(inputAppletLabel, inputAppletLabelGrid);

		final JButton outputAppletButton = new JButton("Output Applet");
		GridBagConstraints outputAppletButtonGrid = new GridBagConstraints();
		outputAppletButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		outputAppletButtonGrid.insets = new Insets(0, 0, 5, 5);
		outputAppletButtonGrid.gridx = 0;
		outputAppletButtonGrid.gridy = 3;
		appletPanel.add(outputAppletButton, outputAppletButtonGrid);

		final JLabel outputAppletLabel = new JLabel("Set output applet JAR...");
		GridBagConstraints outputAppletLabelGrid = new GridBagConstraints();
		outputAppletLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		outputAppletLabelGrid.insets = new Insets(0, 0, 5, 0);
		outputAppletLabelGrid.gridx = 1;
		outputAppletLabelGrid.gridy = 3;
		appletPanel.add(outputAppletLabel, outputAppletLabelGrid);

		final JLabel appletHTMLLabel = new JLabel("Applet HTML");
		GridBagConstraints appletHTMLLabelGrid = new GridBagConstraints();
		appletHTMLLabelGrid.insets = new Insets(0, 0, 5, 5);
		appletHTMLLabelGrid.gridx = 0;
		appletHTMLLabelGrid.gridy = 0;
		appletPanel.add(appletHTMLLabel, appletHTMLLabelGrid);

		final JButton addPayloadButton = new JButton("Add Payload(s)");
		GridBagConstraints addPayloadButtonGrid = new GridBagConstraints();
		addPayloadButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		addPayloadButtonGrid.insets = new Insets(0, 0, 5, 5);
		addPayloadButtonGrid.gridx = 0;
		addPayloadButtonGrid.gridy = 4;
		appletPanel.add(addPayloadButton, addPayloadButtonGrid);

		final JList<Payload> payloadList = new JList<Payload>();
		payloadList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		payloadList.setBorder(null);
		GridBagConstraints payloadListGrid = new GridBagConstraints();
		payloadListGrid.insets = new Insets(0, 0, 5, 0);
		payloadListGrid.fill = GridBagConstraints.BOTH;
		payloadListGrid.gridx = 1;
		payloadListGrid.gridy = 4;
		appletPanel.add(payloadList, payloadListGrid);

		final JButton jdkPathButton = new JButton("JDK Path");
		GridBagConstraints jdkPathButtonGrid = new GridBagConstraints();
		jdkPathButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		jdkPathButtonGrid.insets = new Insets(0, 0, 5, 5);
		jdkPathButtonGrid.gridx = 0;
		jdkPathButtonGrid.gridy = 5;
		appletPanel.add(jdkPathButton, jdkPathButtonGrid);

		final JLabel jdkPathLabel = new JLabel("Select JDK path...");
		GridBagConstraints jdkPathLabelGrid = new GridBagConstraints();
		jdkPathLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		jdkPathLabelGrid.insets = new Insets(0, 0, 5, 0);
		jdkPathLabelGrid.gridx = 1;
		jdkPathLabelGrid.gridy = 5;
		appletPanel.add(jdkPathLabel, jdkPathLabelGrid);
		
		JLabel htmlPreviewLabel = new JLabel("HTML Preview");
		GridBagConstraints htmlPreviewLabelGrid = new GridBagConstraints();
		htmlPreviewLabelGrid.insets = new Insets(0, 0, 5, 5);
		htmlPreviewLabelGrid.gridx = 0;
		htmlPreviewLabelGrid.gridy = 6;
		appletPanel.add(htmlPreviewLabel, htmlPreviewLabelGrid);
		
		final JTextArea outputAppletHTMLTextArea = new JTextArea();
		outputAppletHTMLTextArea.setLineWrap(true);
		JScrollPane outputAppletHTMLTextAreaScroll = new JScrollPane(outputAppletHTMLTextArea);
		outputAppletHTMLTextAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints outputAppletHTMLScrollGrid = new GridBagConstraints();
		outputAppletHTMLScrollGrid.insets = new Insets(0, 0, 5, 0);
		outputAppletHTMLScrollGrid.fill = GridBagConstraints.BOTH;
		outputAppletHTMLScrollGrid.gridx = 1;
		outputAppletHTMLScrollGrid.gridy = 6;
		appletPanel.add(outputAppletHTMLTextAreaScroll, outputAppletHTMLScrollGrid);

		final JButton repackButton = new JButton("Repack Applet");
		GridBagConstraints repackButtonGrid = new GridBagConstraints();
		repackButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		repackButtonGrid.insets = new Insets(0, 0, 0, 5);
		repackButtonGrid.gridx = 0;
		repackButtonGrid.gridy = 7;
		appletPanel.add(repackButton, repackButtonGrid);

		final JLabel statusLabel = new JLabel("Status: Awaiting input...");
		GridBagConstraints statusLabelGrid = new GridBagConstraints();
		statusLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		statusLabelGrid.gridx = 1;
		statusLabelGrid.gridy = 7;
		appletPanel.add(statusLabel, statusLabelGrid);

		final JTabbedPane manifestTab = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Manifest", null, manifestTab, null);
		
		JTabbedPane codeSigningTab = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Code Signing", null, codeSigningTab, null);
		
		/////// gui events

		inputAppletButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new AppletFileFilter());
			    fc.setDialogTitle("Select Input Applet");
				int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            inputAppletFile = fc.getSelectedFile();
		            inputAppletLabel.setText(inputAppletFile.getAbsolutePath());
		            if(outputAppletFile == null){
		            	String inputPath = inputAppletFile.getAbsolutePath();
		            	if(inputPath.endsWith(JAR_EXTENSION)){
		            		outputAppletFile = new File(inputPath.substring(0, inputPath.lastIndexOf(JAR_EXTENSION)) + DEFAULT_OUTPUT_SUFFIX);
		            	} else {
		            		outputAppletFile = new File(inputPath + DEFAULT_OUTPUT_SUFFIX);
		            	}
			            outputAppletLabel.setText(outputAppletFile.getAbsolutePath());
		            }
		            validateInputs(outputAppletHTMLTextArea, statusLabel);
		        }
			}
		});
		
		outputAppletButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new AppletFileFilter());
			    fc.setDialogTitle("Select Output Applet");
				int returnVal = fc.showSaveDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		        	File outputFile = fc.getSelectedFile();
		        	if(!outputFile.getAbsolutePath().endsWith(JAR_EXTENSION)){
		        		outputFile = new File(outputFile.getAbsolutePath() + JAR_EXTENSION);
		        	}
		            outputAppletFile = outputFile;
		            outputAppletLabel.setText(outputAppletFile.getAbsolutePath());
		            validateInputs(outputAppletHTMLTextArea, statusLabel);
		        }
			}			
		});

		appletHTMLTextArea.getDocument().addDocumentListener(new DocumentListener() {

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	        	parseApplet();
	        }

	        @Override
	        public void insertUpdate(DocumentEvent e) {
	        	parseApplet();
	        }

	        @Override
	        public void changedUpdate(DocumentEvent arg0) {
	        	parseApplet();
	        }
	        
	        // TODO: could get really fancy with this an parse each applet
	        //       for now it just always takes the first applet element
	        private void parseApplet(){
				String html = appletHTMLTextArea.getText();
				Document doc = Jsoup.parseBodyFragment(html);
				appletElement = null;
				for(Element element : doc.getElementsByTag(APPLET)){
					appletElement = element;
					break;
				}
				if(appletElement != null){
					if(appletElement.attr(CODE) != null){
						codeText.setText(appletElement.attr(CODE));
					} else {
						codeText.setText("");
					}
					if(appletElement.attr(ARCHIVE) != null){
						archiveText.setText(appletElement.attr(ARCHIVE));
					} else {
						archiveText.setText("");
					}
					validateInputs(outputAppletHTMLTextArea, statusLabel);
				} else {
					codeText.setText("");
					archiveText.setText("");
					outputAppletHTMLTextArea.setText("");
				}
	        }
	    });
	}
	
	private void validateInputs(final JTextArea outputAppletHTMLTextArea, final JLabel statusLabel) {
		if(inputAppletFile != null && outputAppletFile != null){
			if(appletElement.attr(ARCHIVE) != null){
				for(String archive : appletElement.attr(ARCHIVE).split(",")){
					if(archive.endsWith(inputAppletFile.getName())){
						Element appletHtmlPreview = appletElement.clone();
						appletHtmlPreview.attr(ARCHIVE, appletHtmlPreview.attr(ARCHIVE).replace(archive, outputAppletFile.getName()));
						outputAppletHTMLTextArea.setText(appletHtmlPreview.toString());
						statusLabel.setText("Status: Ready...");
						return;
					}
				} 
			}
		}
		outputAppletHTMLTextArea.setText("");
		statusLabel.setText("Status: Awaiting input...");
	}

}