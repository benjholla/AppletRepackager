package repackager.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import repackager.Payload;

public class Main {

	private JFrame frmAppletRepackager;
	private JTextField codeText;
	private JTextField archiveText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmAppletRepackager.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAppletRepackager = new JFrame();
		frmAppletRepackager.setTitle("Applet Repackager");
		frmAppletRepackager.setBounds(100, 100, 550, 400);
		frmAppletRepackager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 550, 0 };
		gridBagLayout.rowHeights = new int[] { 241, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frmAppletRepackager.getContentPane().setLayout(gridBagLayout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints tabbedPaneGrid = new GridBagConstraints();
		tabbedPaneGrid.fill = GridBagConstraints.BOTH;
		tabbedPaneGrid.gridx = 0;
		tabbedPaneGrid.gridy = 0;
		frmAppletRepackager.getContentPane().add(tabbedPane, tabbedPaneGrid);

		JPanel appletPanel = new JPanel();
		tabbedPane.addTab("Applet", null, appletPanel, null);
		GridBagLayout appletPanelGrid = new GridBagLayout();

		appletPanelGrid.columnWeights = new double[] { 0.0, 1.0 };
		appletPanelGrid.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0 };
		appletPanel.setLayout(appletPanelGrid);

		JButton inputJarButton = new JButton("Input Jar");
		GridBagConstraints inputJarButtonGrid = new GridBagConstraints();
		inputJarButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		inputJarButtonGrid.insets = new Insets(0, 0, 5, 5);
		inputJarButtonGrid.gridx = 0;
		inputJarButtonGrid.gridy = 0;
		appletPanel.add(inputJarButton, inputJarButtonGrid);

		inputJarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		JLabel inputJarLabel = new JLabel("Select input jar path...");
		GridBagConstraints inputJarLabelGrid = new GridBagConstraints();
		inputJarLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		inputJarLabelGrid.insets = new Insets(0, 0, 5, 0);
		inputJarLabelGrid.gridx = 1;
		inputJarLabelGrid.gridy = 0;
		appletPanel.add(inputJarLabel, inputJarLabelGrid);

		JButton outputJarButton = new JButton("Output Jar");
		GridBagConstraints outputJarButtonGrid = new GridBagConstraints();
		outputJarButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		outputJarButtonGrid.insets = new Insets(0, 0, 5, 5);
		outputJarButtonGrid.gridx = 0;
		outputJarButtonGrid.gridy = 1;
		appletPanel.add(outputJarButton, outputJarButtonGrid);

		JLabel outputJarLabel = new JLabel("Select output jar path...");
		GridBagConstraints outputJarLabelGrid = new GridBagConstraints();
		outputJarLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		outputJarLabelGrid.insets = new Insets(0, 0, 5, 0);
		outputJarLabelGrid.gridx = 1;
		outputJarLabelGrid.gridy = 1;
		appletPanel.add(outputJarLabel, outputJarLabelGrid);

		JLabel appletHTMLLabel = new JLabel("Applet HTML");
		GridBagConstraints appletHTMLLabelGrid = new GridBagConstraints();
		appletHTMLLabelGrid.insets = new Insets(0, 0, 5, 5);
		appletHTMLLabelGrid.gridx = 0;
		appletHTMLLabelGrid.gridy = 2;
		appletPanel.add(appletHTMLLabel, appletHTMLLabelGrid);

		JTextArea appletHTMLTextArea = new JTextArea();
		GridBagConstraints appletHTMLTextAreaGrid = new GridBagConstraints();
		appletHTMLTextAreaGrid.insets = new Insets(0, 0, 5, 0);
		appletHTMLTextAreaGrid.fill = GridBagConstraints.BOTH;
		appletHTMLTextAreaGrid.gridx = 1;
		appletHTMLTextAreaGrid.gridy = 2;
		appletPanel.add(appletHTMLTextArea, appletHTMLTextAreaGrid);

		JLabel parsedFieldsLabel = new JLabel("Parsed Fields");
		GridBagConstraints parsedFieldsLabelGrid = new GridBagConstraints();
		parsedFieldsLabelGrid.insets = new Insets(0, 0, 5, 5);
		parsedFieldsLabelGrid.gridx = 0;
		parsedFieldsLabelGrid.gridy = 3;
		appletPanel.add(parsedFieldsLabel, parsedFieldsLabelGrid);

		JPanel parsedFieldsPanel = new JPanel();
		GridBagConstraints parsedFieldsPanelGrid = new GridBagConstraints();
		parsedFieldsPanelGrid.insets = new Insets(0, 0, 5, 0);
		parsedFieldsPanelGrid.fill = GridBagConstraints.BOTH;
		parsedFieldsPanelGrid.gridx = 1;
		parsedFieldsPanelGrid.gridy = 3;
		appletPanel.add(parsedFieldsPanel, parsedFieldsPanelGrid);

		JLabel codeLabel = new JLabel("code: ");
		parsedFieldsPanel.add(codeLabel);

		codeText = new JTextField();
		codeText.setEditable(false);
		parsedFieldsPanel.add(codeText);
		codeText.setColumns(10);

		JLabel archiveLabel = new JLabel("archive: ");
		parsedFieldsPanel.add(archiveLabel);

		archiveText = new JTextField();
		archiveText.setEditable(false);
		parsedFieldsPanel.add(archiveText);
		archiveText.setColumns(10);

		JButton addPayloadButton = new JButton("Add Payload");
		GridBagConstraints addPayloadButtonGrid = new GridBagConstraints();
		addPayloadButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		addPayloadButtonGrid.insets = new Insets(0, 0, 5, 5);
		addPayloadButtonGrid.gridx = 0;
		addPayloadButtonGrid.gridy = 4;
		appletPanel.add(addPayloadButton, addPayloadButtonGrid);

		JList<Payload> payloadList = new JList<Payload>();
		payloadList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		payloadList.setBorder(null);
		GridBagConstraints payloadListGrid = new GridBagConstraints();
		payloadListGrid.insets = new Insets(0, 0, 5, 0);
		payloadListGrid.fill = GridBagConstraints.BOTH;
		payloadListGrid.gridx = 1;
		payloadListGrid.gridy = 4;
		appletPanel.add(payloadList, payloadListGrid);

		JButton jdkPathButton = new JButton("JDK Path");
		GridBagConstraints jdkPathButtonGrid = new GridBagConstraints();
		jdkPathButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		jdkPathButtonGrid.insets = new Insets(0, 0, 5, 5);
		jdkPathButtonGrid.gridx = 0;
		jdkPathButtonGrid.gridy = 5;
		appletPanel.add(jdkPathButton, jdkPathButtonGrid);

		JLabel jdkPathLabel = new JLabel("Select JDK path...");
		GridBagConstraints jdkPathLabelGrid = new GridBagConstraints();
		jdkPathLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		jdkPathLabelGrid.insets = new Insets(0, 0, 5, 0);
		jdkPathLabelGrid.gridx = 1;
		jdkPathLabelGrid.gridy = 5;
		appletPanel.add(jdkPathLabel, jdkPathLabelGrid);

		JButton runButton = new JButton("Repack");
		GridBagConstraints runButtonGrid = new GridBagConstraints();
		runButtonGrid.fill = GridBagConstraints.HORIZONTAL;
		runButtonGrid.insets = new Insets(0, 0, 0, 5);
		runButtonGrid.gridx = 0;
		runButtonGrid.gridy = 6;
		appletPanel.add(runButton, runButtonGrid);

		JLabel statusLabel = new JLabel("Status: Awaiting input...");
		GridBagConstraints statusLabelGrid = new GridBagConstraints();
		statusLabelGrid.fill = GridBagConstraints.HORIZONTAL;
		statusLabelGrid.gridx = 1;
		statusLabelGrid.gridy = 6;
		appletPanel.add(statusLabel, statusLabelGrid);

		JTabbedPane manifestTab = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Manifest", null, manifestTab, null);
	}

}
