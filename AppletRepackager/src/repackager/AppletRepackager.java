package repackager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.jar.Manifest;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class AppletRepackager {

	public static final String META_INF = "META-INF";

	public static void repackageJar(String jdkPath, String code, String newCode, File archive, File newArchive, Manifest manifest, File... payloads) throws Exception {
		// extract jar and purge meta-inf data
		File extractedJarDirectory = new File(archive.getAbsolutePath().replace(".jar", ""));
		JarUtils.unjar(archive, extractedJarDirectory);
		JarUtils.purgeMetaInf(extractedJarDirectory); // unsign and clear manifest

		// generate the wrapper and payloads
		String qualifiedTargetClassName = code;
		
		String wrapperClassName = "";
		String wrapperPackageName = "";
		if(newCode.contains(".")){
			wrapperClassName = newCode.substring(newCode.lastIndexOf(".")+1, newCode.length());
			wrapperPackageName = newCode.substring(0, newCode.indexOf("."));
		} else {
			wrapperClassName = newCode;
			wrapperPackageName = "";
		}
		
		// generate the payload interface
		File payloadInterfaceFile = new File(extractedJarDirectory.getAbsolutePath() + File.separatorChar + "Payload.java");
		PayloadEntry payloadInterfaceEntry = new PayloadEntry(payloadInterfaceFile, extractedJarDirectory);
		AppletRepackager.generatePayloadInterface(payloadInterfaceEntry, payloadInterfaceFile);
		
		// copy over the test payload and add it to the ordered list of payloads to execute
		ArrayList<PayloadEntry> payloadEntries = new ArrayList<PayloadEntry>();
		for(File payloadSource : payloads){
			File payloadFile = new File(extractedJarDirectory.getAbsolutePath() + File.separatorChar + payloadSource.getName());
			if(payloadFile.exists()){
				payloadFile.delete();
			}
			Files.copy(payloadSource.toPath(), payloadFile.toPath());
			PayloadEntry payloadEntry = new PayloadEntry(payloadFile, extractedJarDirectory);
			payloadEntries.add(payloadEntry);
		}

		// generate the wrapper
		File wrapperFile = new File(extractedJarDirectory.getAbsolutePath() + File.separatorChar + wrapperClassName + ".java");
		AppletRepackager.generateWrapper(wrapperClassName, wrapperPackageName, qualifiedTargetClassName, payloadInterfaceEntry, payloadEntries, wrapperFile);

		// compile the source files
		ArrayList<File> sourceFiles = new ArrayList<File>();
		sourceFiles.add(payloadInterfaceFile);
		for(PayloadEntry payloadEntry : payloadEntries){
			sourceFiles.add(payloadEntry.getSourceFile());
		}
		sourceFiles.add(wrapperFile);
		
		try {
			AppletRepackager.compileSourceFiles(sourceFiles, jdkPath);
			
			// clean up the source files
			payloadInterfaceEntry.getSourceFile().delete();
			wrapperFile.delete();
			for(PayloadEntry payload : payloadEntries){
				payload.getSourceFile().delete();
			}
			
			JarUtils.jar(extractedJarDirectory, newArchive, manifest);
		} finally {			
			// clean up the temp directory
			JarUtils.delete(extractedJarDirectory);
		}
	}
	
	public static void generatePayloadInterface(PayloadEntry payloadInterface, File outputFile) throws Exception {
		FileWriter fw = new FileWriter(outputFile);
		if(!payloadInterface.getPackageName().equals("")){
			fw.write("package " + payloadInterface.getPackageName() +";\n\n");
		}
		fw.write("import java.awt.Graphics;\n\n");
		fw.write("public interface Payload {\n");
		fw.write("	public void preInitPayload();\n");
		fw.write("	public void postInitPayload();\n");
		fw.write("	public void preStartPayload();\n");
		fw.write("	public void postStartPayload();\n");
		fw.write("	public void prePaintPayload(Graphics g);\n");
		fw.write("	public void postPaintPayload(Graphics g);\n");
		fw.write("	public void preStopPayload();\n");
		fw.write("	public void postStopPayload();\n");
		fw.write("	public void preDestroyPayload();\n");
		fw.write("	public void postDestroyPayload();\n");
		fw.write("}\n");
		fw.close();
	}

	public static void generateWrapper(String wrapperClassName, String wrapperPackageName, String qualifiedTargetClassName, PayloadEntry payloadInterface, ArrayList<PayloadEntry> payloads, File outputFile) throws Exception {
		FileWriter fw = new FileWriter(outputFile);
		
		if(qualifiedTargetClassName.endsWith(".class")){
			qualifiedTargetClassName = qualifiedTargetClassName.substring(0, qualifiedTargetClassName.length() - 6);
		}
		
		if(!wrapperPackageName.equals("")){
			fw.write("package " + wrapperPackageName +";\n\n");
		}
		
		fw.write("import java.applet.Applet;\n");
		fw.write("import java.awt.Graphics;\n");
		
		if(!payloadInterface.getPackageName().equals("") && !payloadInterface.getPackageName().equals(wrapperPackageName)){
			fw.write("import " + payloadInterface.getQualifiedClassName() + ";\n");
		}
		
		for(PayloadEntry payload : payloads){
			if(!payload.getPackageName().equals("") && !payload.getPackageName().equals(wrapperPackageName)){
				fw.write("import " + payload.getQualifiedClassName() + ";\n");
			}
		}
		
		fw.write("\n");
		fw.write("public class " + wrapperClassName + " extends Applet {\n");
		fw.write("\n");
		fw.write("	private static final long serialVersionUID = 1L;\n");
		fw.write("\n");
		fw.write("	Applet applet = null;\n");
		fw.write("\n");
		for(PayloadEntry payload : payloads){
			fw.write("	" + "Payload " + payload.getVariableName() + " = null;\n");
		}
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void init(){\n");
		fw.write("		try {\n");
		fw.write("			Class<?> c = Class.forName(\"" + qualifiedTargetClassName + "\");\n");
		fw.write("			this.applet = (Applet) c.newInstance();\n");
		fw.write("		} catch (Exception e){e.printStackTrace();}\n");
		for(PayloadEntry payload : payloads){
			fw.write("		try {\n");
			fw.write("			Class<?> c = Class.forName(\"" + payload.getQualifiedClassName() + "\");\n");
			fw.write("			" + payload.getVariableName() + " = (Payload) c.newInstance();\n");
			fw.write("		} catch (Exception e){e.printStackTrace();}\n");
		}
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".preInitPayload();\n");
		}
		fw.write("		this.applet.init();\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".postInitPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void start(){\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".preStartPayload();\n");
		}
		fw.write("		this.applet.start();\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".postStartPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void paint(Graphics g){\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".prePaintPayload(g);\n");
		}
		fw.write("		this.applet.paint(g);\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".postPaintPayload(g);\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void stop(){\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".preStopPayload();\n");
		}
		fw.write("		this.applet.stop();\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".postStopPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void destroy(){\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".preDestroyPayload();\n");
		}
		fw.write("		this.applet.destroy();\n");
		for(PayloadEntry payload : payloads){
			fw.write("		" + payload.getVariableName() + ".postDestroyPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("}\n");
		
		fw.close();
	}
	
	public static ArrayList<File> compileSourceFiles(ArrayList<File> sourceFiles, String jdkPath) throws IOException {
		System.setProperty("java.home", jdkPath);
		
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		
		if(javaCompiler == null){
			throw new RuntimeException("Could not find Java compiler.");
		}
		
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnostics, Locale.ENGLISH, Charset.forName("UTF-8"));
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFiles);
		javaCompiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
		    
		for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
			System.err.format("Error on line %d in %d%n", diagnostic.getLineNumber(), diagnostic.getSource().toString());
		}        
		 
		fileManager.close();
		
		ArrayList<File> classFiles = new ArrayList<File>();
		for(File sourceFile : sourceFiles){
			classFiles.add(new File(sourceFile.getAbsolutePath().replace(".java", ".class")));
		}
		return classFiles;
	}

}
