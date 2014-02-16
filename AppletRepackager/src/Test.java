import java.io.File;
import java.util.ArrayList;

public class Test {

//	public static void main(String[] args) throws Exception {
//		File originalJar = new File("C:\\Users\\Ben\\Desktop\\test.jar");
//		File extractedJarDirectory = new File("C:\\Users\\Ben\\Desktop\\test");
//		File repackedJar = new File("C:\\Users\\Ben\\Desktop\\test2.jar");
//		JarUtils.unjar(originalJar, extractedJarDirectory);
//		JarUtils.purgeMetaInf(extractedJarDirectory);
//		JarUtils.jar(extractedJarDirectory, repackedJar, JarUtils.generateEmptyManifest());
//	}
	
	// some test code
	public static void main(String[] args) throws Exception {
		
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.7.0_51");
		
		File workingDirectory = new File("C:\\Users\\Ben\\Desktop\\source\\");
		
		String targetClass = "Main";
		String wrapperName = "Wrapper";
		
		File payloadInterfaceFile = new File(workingDirectory.getAbsolutePath() + File.separatorChar + "Payload.java");
		AppletRepackager.PayloadEntry payloadInterfaceEntry = new AppletRepackager.PayloadEntry(payloadInterfaceFile, workingDirectory);
		
		File testPayloadFile = new File(workingDirectory.getAbsolutePath() + File.separatorChar + "TestPayload.java");
		AppletRepackager.PayloadEntry testPayloadEntry = new AppletRepackager.PayloadEntry(testPayloadFile, workingDirectory);
		
		ArrayList<AppletRepackager.PayloadEntry> payloadEntries = new ArrayList<AppletRepackager.PayloadEntry>();
		payloadEntries.add(testPayloadEntry);
		
		File wrapperFile = new File(workingDirectory.getAbsolutePath() + File.separatorChar + wrapperName + ".java");
		
		AppletRepackager.generatePayloadInterface(payloadInterfaceFile);
		
		// String targetClass, String wrapperName, PayloadEntry payloadInterface, ArrayList<PayloadEntry> payloads, File outputFile
		AppletRepackager.generateWrapper(targetClass, wrapperName, payloadInterfaceEntry, payloadEntries, wrapperFile);
		
		ArrayList<File> sourceFiles = new ArrayList<File>();
		sourceFiles.add(payloadInterfaceFile);
		sourceFiles.add(testPayloadFile);
		sourceFiles.add(wrapperFile);
		ArrayList<File> classFiles = AppletRepackager.compileSourceFiles(sourceFiles);
		
		System.out.println(classFiles.toString());
	}

}
