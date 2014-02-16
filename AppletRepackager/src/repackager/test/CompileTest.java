package repackager.test;

import java.io.File;
import java.util.ArrayList;

import repackager.AppletRepackager;
import repackager.PayloadEntry;

public class CompileTest {

	// compilation test
	public static void main(String[] args) throws Exception {
		File workingDirectory = new File("C:\\Users\\Ben\\Desktop\\source\\");
		File sourceDirectory = new File("C:\\Users\\Ben\\Desktop\\source\\sneaky\\hobbits\\");
		
		String qualifiedTargetClassName = "Main";
		
		String wrapperClassName = "Wrapper";
		String wrapperPackageName = "sneaky.hobbits";
		
		File payloadInterfaceFile = new File(sourceDirectory.getAbsolutePath() + File.separatorChar + "Payload.java");
		PayloadEntry payloadInterfaceEntry = new PayloadEntry(payloadInterfaceFile, workingDirectory);
		
		
		File testPayloadFile = new File(sourceDirectory.getAbsolutePath() + File.separatorChar + "TestPayload.java");
		PayloadEntry testPayloadEntry = new PayloadEntry(testPayloadFile, workingDirectory);
		testPayloadEntry.setPackageName("");
		
		ArrayList<PayloadEntry> payloadEntries = new ArrayList<PayloadEntry>();
		payloadEntries.add(testPayloadEntry);
		
		File wrapperFile = new File(sourceDirectory.getAbsolutePath() + File.separatorChar + wrapperClassName + ".java");
		
		AppletRepackager.generatePayloadInterface(payloadInterfaceEntry, payloadInterfaceFile);
		
		AppletRepackager.generateWrapper(wrapperClassName, wrapperPackageName, qualifiedTargetClassName, payloadInterfaceEntry, payloadEntries, wrapperFile);
		
		ArrayList<File> sourceFiles = new ArrayList<File>();
		sourceFiles.add(payloadInterfaceFile);
		sourceFiles.add(testPayloadFile);
		sourceFiles.add(wrapperFile);
		ArrayList<File> classFiles = AppletRepackager.compileSourceFiles(sourceFiles, "C:\\Program Files\\Java\\jdk1.7.0_51");
		
		System.out.println(classFiles.toString());
	}
	
}
