package repackager.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import repackager.JarUtils;

public class AppletRepackagerGUI {

	private JFrame frame;
	
	private File inputAppletFile = null;
	private File outputAppletFile = null;
	private Element appletElement = null;
	
	private Manifest originalManifest = null;
	private Manifest preserveManifest = null;
	private Manifest removeManifest = null;
	private Manifest addManifest = null;
	
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
					window.frame.setLocationRelativeTo(null);
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
		frame.setBounds(100, 100, 650, 550);
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
		appletPanelGrid.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0 };
		appletPanel.setLayout(appletPanelGrid);

		final JTextArea appletHTMLTextArea = new JTextArea();
		appletHTMLTextArea.setLineWrap(true);
		JScrollPane appletHTMLScroll = new JScrollPane(appletHTMLTextArea);
		appletHTMLScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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

		final JTextField codeText = new JTextField();
		codeText.setEditable(false);
		parsedFieldsPanel.add(codeText);
		codeText.setColumns(14);

		final JLabel archiveLabel = new JLabel("archive: ");
		parsedFieldsPanel.add(archiveLabel);

		final JTextField archiveText = new JTextField();
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

		final JButton addRemovePayloadButton = new JButton("Add Payload");
		GridBagConstraints addPayloadButtonGrid = new GridBagConstraints();
		addPayloadButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		addPayloadButtonGrid.insets = new Insets(0, 0, 5, 5);
		addPayloadButtonGrid.gridx = 0;
		addPayloadButtonGrid.gridy = 4;
		appletPanel.add(addRemovePayloadButton, addPayloadButtonGrid);

		final DefaultListModel<File> payloads = new DefaultListModel<File>();
		final JList<File> payloadList = new JList<File>(payloads);
		payloadList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		payloadList.setBorder(null);
		JScrollPane payloadListScroll = new JScrollPane(payloadList);
		payloadListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints payloadListScrollGrid = new GridBagConstraints();
		payloadListScrollGrid.insets = new Insets(0, 0, 5, 0);
		payloadListScrollGrid.fill = GridBagConstraints.BOTH;
		payloadListScrollGrid.gridx = 1;
		payloadListScrollGrid.gridy = 4;
		appletPanel.add(payloadListScroll, payloadListScrollGrid);
		
		JLabel wrapperLabel = new JLabel("Qualified Wrapper");
		GridBagConstraints wrapperLabelGrid = new GridBagConstraints();
		wrapperLabelGrid.insets = new Insets(0, 0, 5, 5);
		wrapperLabelGrid.gridx = 0;
		wrapperLabelGrid.gridy = 5;
		appletPanel.add(wrapperLabel, wrapperLabelGrid);
		
		final JTextField wrapperText = new JTextField();
		wrapperText.setEnabled(false);
		wrapperText.setText("Wrapper.class");
		GridBagConstraints wrapperTextGrid = new GridBagConstraints();
		wrapperTextGrid.fill = GridBagConstraints.HORIZONTAL;
		wrapperTextGrid.insets = new Insets(0, 0, 5, 0);
		wrapperTextGrid.gridx = 1;
		wrapperTextGrid.gridy = 5;
		appletPanel.add(wrapperText, wrapperTextGrid);
		wrapperText.setColumns(10);

		final JButton jdkPathButton = new JButton("JDK Path");
		GridBagConstraints jdkPathButtonGrid = new GridBagConstraints();
		jdkPathButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		jdkPathButtonGrid.insets = new Insets(0, 0, 5, 5);
		jdkPathButtonGrid.gridx = 0;
		jdkPathButtonGrid.gridy = 6;
		appletPanel.add(jdkPathButton, jdkPathButtonGrid);

		final JLabel jdkPathLabel = new JLabel("Select JDK path...");
		GridBagConstraints jdkPathLabelGrid = new GridBagConstraints();
		jdkPathLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		jdkPathLabelGrid.insets = new Insets(0, 0, 5, 0);
		jdkPathLabelGrid.gridx = 1;
		jdkPathLabelGrid.gridy = 6;
		appletPanel.add(jdkPathLabel, jdkPathLabelGrid);
		
		JLabel htmlPreviewLabel = new JLabel("HTML Preview");
		GridBagConstraints htmlPreviewLabelGrid = new GridBagConstraints();
		htmlPreviewLabelGrid.insets = new Insets(0, 0, 5, 5);
		htmlPreviewLabelGrid.gridx = 0;
		htmlPreviewLabelGrid.gridy = 7;
		appletPanel.add(htmlPreviewLabel, htmlPreviewLabelGrid);
		
		final JTextArea outputAppletHTMLTextArea = new JTextArea();
		outputAppletHTMLTextArea.setEditable(false);
		outputAppletHTMLTextArea.setLineWrap(true);
		JScrollPane outputAppletHTMLTextAreaScroll = new JScrollPane(outputAppletHTMLTextArea);
		outputAppletHTMLTextAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outputAppletHTMLTextAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints outputAppletHTMLScrollGrid = new GridBagConstraints();
		outputAppletHTMLScrollGrid.insets = new Insets(0, 0, 5, 0);
		outputAppletHTMLScrollGrid.fill = GridBagConstraints.BOTH;
		outputAppletHTMLScrollGrid.gridx = 1;
		outputAppletHTMLScrollGrid.gridy = 7;
		appletPanel.add(outputAppletHTMLTextAreaScroll, outputAppletHTMLScrollGrid);

		final JButton repackButton = new JButton("Repack Applet");
		GridBagConstraints repackButtonGrid = new GridBagConstraints();
		repackButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		repackButtonGrid.insets = new Insets(0, 0, 0, 5);
		repackButtonGrid.gridx = 0;
		repackButtonGrid.gridy = 8;
		appletPanel.add(repackButton, repackButtonGrid);

		final JLabel statusLabel = new JLabel("Status: Awaiting input...");
		GridBagConstraints statusLabelGrid = new GridBagConstraints();
		statusLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		statusLabelGrid.gridx = 1;
		statusLabelGrid.gridy = 8;
		appletPanel.add(statusLabel, statusLabelGrid);
		
		final JPanel manifestTab = new JPanel();
		tabbedPane.addTab("Manifest", null, manifestTab, null);
		
		GridBagLayout manifestTabGrid = new GridBagLayout();
		manifestTabGrid.columnWidths = new int[]{0};
		manifestTabGrid.rowHeights = new int[]{0, 0};
		manifestTabGrid.columnWeights = new double[]{1.0};
		manifestTabGrid.rowWeights = new double[]{Double.MIN_VALUE, 1.0};
		manifestTab.setLayout(manifestTabGrid);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		manifestTab.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JButton selectPreserveManifestButton = new JButton("Select Manifest");
		GridBagConstraints selectPreserveManifestButtonGrid = new GridBagConstraints();
		selectPreserveManifestButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		selectPreserveManifestButtonGrid.insets = new Insets(0, 0, 5, 5);
		selectPreserveManifestButtonGrid.gridx = 0;
		selectPreserveManifestButtonGrid.gridy = 0;
		panel.add(selectPreserveManifestButton, selectPreserveManifestButtonGrid);
		
		final JLabel preserveManifestLabel = new JLabel("Select Manifest with related attributes to preserve...");
		GridBagConstraints preserveManifestLabelGrid = new GridBagConstraints();
		preserveManifestLabelGrid.anchor = GridBagConstraints.WEST;
		preserveManifestLabelGrid.insets = new Insets(0, 0, 5, 0);
		preserveManifestLabelGrid.gridx = 1;
		preserveManifestLabelGrid.gridy = 0;
		panel.add(preserveManifestLabel, preserveManifestLabelGrid);
		
		JButton selectRemoveManifestButton = new JButton("Select Manifest");
		GridBagConstraints selectRemoveManifestButtonGrid = new GridBagConstraints();
		selectRemoveManifestButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		selectRemoveManifestButtonGrid.insets = new Insets(0, 0, 5, 5);
		selectRemoveManifestButtonGrid.gridx = 0;
		selectRemoveManifestButtonGrid.gridy = 1;
		panel.add(selectRemoveManifestButton, selectRemoveManifestButtonGrid);
		
		final JLabel removeManifestLabel = new JLabel("Select Manifest with related attributes to remove...");
		GridBagConstraints removeManifestLabelGrid = new GridBagConstraints();
		removeManifestLabelGrid.anchor = GridBagConstraints.WEST;
		removeManifestLabelGrid.insets = new Insets(0, 0, 5, 0);
		removeManifestLabelGrid.gridx = 1;
		removeManifestLabelGrid.gridy = 1;
		panel.add(removeManifestLabel, removeManifestLabelGrid);
		
		JButton selectAddManifestButton = new JButton("Select Manifest");
		GridBagConstraints selectAddManifestButtonGrid = new GridBagConstraints();
		selectAddManifestButtonGrid.insets = new Insets(0, 0, 5, 5);
		selectAddManifestButtonGrid.anchor = GridBagConstraints.WEST;
		selectAddManifestButtonGrid.gridx = 0;
		selectAddManifestButtonGrid.gridy = 2;
		panel.add(selectAddManifestButton, selectAddManifestButtonGrid);
		
		final JLabel addManifestLabel = new JLabel("Select Manifest with attributes to add/overwrite...");
		GridBagConstraints addManifestLabelGrid = new GridBagConstraints();
		addManifestLabelGrid.insets = new Insets(0, 0, 5, 0);
		addManifestLabelGrid.anchor = GridBagConstraints.WEST;
		addManifestLabelGrid.gridx = 1;
		addManifestLabelGrid.gridy = 2;
		panel.add(addManifestLabel, addManifestLabelGrid);
		
		JButton resetManifestButton = new JButton("Reset");
		GridBagConstraints resetManifestButtonGrid = new GridBagConstraints();
		resetManifestButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		resetManifestButtonGrid.insets = new Insets(0, 0, 5, 5);
		resetManifestButtonGrid.gridx = 0;
		resetManifestButtonGrid.gridy = 3;
		panel.add(resetManifestButton, resetManifestButtonGrid);
		
		JLabel filler = new JLabel("");
		GridBagConstraints fillerGrid = new GridBagConstraints();
		fillerGrid.fill = GridBagConstraints.HORIZONTAL;
		fillerGrid.insets = new Insets(0, 0, 5, 0);
		fillerGrid.gridx = 1;
		fillerGrid.gridy = 3;
		panel.add(filler, fillerGrid);
		
		JLabel previewManifestLabel = new JLabel("Manifest Preview");
		GridBagConstraints previewManifestLabelGrid = new GridBagConstraints();
		previewManifestLabelGrid.insets = new Insets(0, 0, 0, 5);
		previewManifestLabelGrid.gridx = 0;
		previewManifestLabelGrid.gridy = 4;
		panel.add(previewManifestLabel, previewManifestLabelGrid);
		
		JLabel filler2 = new JLabel("");
		GridBagConstraints filler2Grid = new GridBagConstraints();
		filler2Grid.gridx = 1;
		filler2Grid.gridy = 4;
		panel.add(filler2, filler2Grid);
		
		final JTextArea manifestPreviewTextArea = new JTextArea();
		manifestPreviewTextArea.setEditable(false);
		manifestPreviewTextArea.setLineWrap(true);
		JScrollPane manifestPreviewScroll = new JScrollPane(manifestPreviewTextArea);
		manifestPreviewScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		manifestPreviewScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints manifestPreviewScrollGrid = new GridBagConstraints();
		manifestPreviewScrollGrid.insets = new Insets(0, 0, 5, 0);
		manifestPreviewScrollGrid.fill = GridBagConstraints.BOTH;
		manifestPreviewScrollGrid.gridx = 0;
		manifestPreviewScrollGrid.gridy = 1;
		manifestTab.add(manifestPreviewScroll, manifestPreviewScrollGrid);
		
		JPanel codeSigningTab = new JPanel();
		tabbedPane.addTab("Code Signing", null, codeSigningTab, null);

		/////// gui events
		
		resetManifestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preserveManifest = null;
				preserveManifestLabel.setText("Select Manifest with related attributes to preserve...");
				removeManifest = null;
				removeManifestLabel.setText("Select Manifest with related attributes to remove...");
				addManifest = null;
				addManifestLabel.setText("Select Manifest with attributes to add/overwrite...");
				previewOutputManifest(manifestPreviewTextArea);
			}
		});
		
		selectPreserveManifestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ManifestFileFilter());
			    fc.setDialogTitle("Select Input Manifest");
				int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File inputManifestFile = fc.getSelectedFile();
		            try {
						preserveManifest = JarUtils.getManifestFromFile(inputManifestFile);
						previewOutputManifest(manifestPreviewTextArea);
						preserveManifestLabel.setText("Preserve: " + inputManifestFile.getAbsolutePath());
					} catch (Exception ex){
						JOptionPane.showMessageDialog(frame, "Invalid Manifest");
					}
		        }
			}
		});
		
		selectRemoveManifestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ManifestFileFilter());
			    fc.setDialogTitle("Select Input Manifest");
				int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File inputManifestFile = fc.getSelectedFile();
		            try {
		            	removeManifest = JarUtils.getManifestFromFile(inputManifestFile);
						previewOutputManifest(manifestPreviewTextArea);
						removeManifestLabel.setText("Remove: " + inputManifestFile.getAbsolutePath());
					} catch (Exception ex){
						JOptionPane.showMessageDialog(frame, "Invalid Manifest");
					}
		        }
			}
		});
		
		selectAddManifestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ManifestFileFilter());
			    fc.setDialogTitle("Select Input Manifest");
				int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File inputManifestFile = fc.getSelectedFile();
		            try {
		            	addManifest = JarUtils.getManifestFromFile(inputManifestFile);
						previewOutputManifest(manifestPreviewTextArea);
						addManifestLabel.setText("Add: " + inputManifestFile.getAbsolutePath());
					} catch (Exception ex){
						JOptionPane.showMessageDialog(frame, "Invalid Manifest");
					}
		        }
			}
		});
		
		inputAppletButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new AppletFileFilter());
			    fc.setDialogTitle("Select Input Applet");
				int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            inputAppletFile = fc.getSelectedFile();
		            inputAppletLabel.setText(inputAppletFile.getAbsolutePath());
	            	String inputPath = inputAppletFile.getAbsolutePath();
	            	if(inputPath.endsWith(JAR_EXTENSION)){
	            		outputAppletFile = new File(inputPath.substring(0, inputPath.lastIndexOf(JAR_EXTENSION)) + DEFAULT_OUTPUT_SUFFIX);
	            	} else {
	            		outputAppletFile = new File(inputPath + DEFAULT_OUTPUT_SUFFIX);
	            	}
		            outputAppletLabel.setText(outputAppletFile.getAbsolutePath());
		            try {
		            	originalManifest = JarUtils.getManifest(inputAppletFile);
		            	previewOutputManifest(manifestPreviewTextArea);
		            } catch (Exception ex){
		            	ex.printStackTrace();
		            }
		            validateInputs(outputAppletHTMLTextArea, wrapperText, statusLabel);
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
		            validateInputs(outputAppletHTMLTextArea, wrapperText, statusLabel);
		        }
			}			
		});
		
		addRemovePayloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(payloadList.isSelectionEmpty()){
					final JFileChooser fc = new JFileChooser();
					fc.setFileFilter(new PayloadFileFilter());
				    fc.setDialogTitle("Select Payload Source File");
					int returnVal = fc.showOpenDialog(frame);
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			        	File selectedPayload = fc.getSelectedFile();
			            payloads.addElement(selectedPayload);
			        }
				} else {
					payloads.remove(payloadList.getSelectedIndex());
				}
				wrapperText.setEnabled(!payloads.isEmpty());
			}
		});
		
		payloadList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(payloadList.isSelectionEmpty()){
					addRemovePayloadButton.setText("Add Payload");
				} else {
					addRemovePayloadButton.setText("Remove Payload");
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
	        
	        // always just takes the first applet element
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
					validateInputs(outputAppletHTMLTextArea, wrapperText, statusLabel);
				} else {
					codeText.setText("");
					archiveText.setText("");
					outputAppletHTMLTextArea.setText("");
				}
	        }
	    });
	}
	
	private Manifest calculateOutputManifest(){
		Manifest manifest = null;
		if(originalManifest != null){
			manifest = originalManifest;
			if(preserveManifest != null){
				// TODO: Implement
			}
			if(removeManifest != null){
				// TODO: Implement
			}
			if(addManifest != null){
				// TODO: Implement
			}
		}
		return manifest;
	}
	
	private void previewOutputManifest(final JTextArea manifestPreviewTextArea) {
		Manifest manifest = calculateOutputManifest();
		String manifestString = "";
		if(manifest != null){
			Attributes attributes = manifest.getMainAttributes();
			for(Entry<Object,Object> attribute : attributes.entrySet()){
			manifestString += attribute.getKey().toString() + ": " + attribute.getValue().toString() + "\n";
			}
		}
		manifestPreviewTextArea.setText(manifestString.trim());
	}
	
	private void validateInputs(final JTextArea outputAppletHTMLTextArea, final JTextField wrapperText, final JLabel statusLabel) {
		if(inputAppletFile != null && outputAppletFile != null){
			if(!wrapperText.getText().equals("")){
				if(appletElement != null && appletElement.attr(ARCHIVE) != null){
					for(String archive : appletElement.attr(ARCHIVE).split(",")){
						if(archive.endsWith(inputAppletFile.getName())){
							Element appletHtmlPreview = appletElement.clone();
							appletHtmlPreview.attr(ARCHIVE, appletHtmlPreview.attr(ARCHIVE).replace(archive, outputAppletFile.getName()));
							appletHtmlPreview.attr(CODE, wrapperText.getText());
							outputAppletHTMLTextArea.setText(appletHtmlPreview.toString());
							statusLabel.setText("Status: Ready...");
							return;
						}
					} 
				}
			}
		}
		outputAppletHTMLTextArea.setText("");
		statusLabel.setText("Status: Awaiting input...");
	}
}
