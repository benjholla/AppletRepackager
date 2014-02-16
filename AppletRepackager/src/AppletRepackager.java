
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class AppletRepackager {

	public static final String META_INF = "META-INF";
	
	public static class PayloadEntry {
		private String packageName;
		private String className;
		private String variableName;
		private File payloadFile;
		
		public PayloadEntry(File payloadFile, File baseDirectory) throws IOException {
			this.payloadFile = payloadFile;
			this.packageName = getPayloadPackage(payloadFile, baseDirectory);
			this.className = getPayloadClassName(payloadFile);
			this.variableName = getPayloadVariableName(this.className);
		}
		
		public String getPackageName() {
			return packageName;
		}

		public String getClassName() {
			return className;
		}

		public String getVariableName() {
			return variableName;
		}

		public File getPayloadFile() {
			return payloadFile;
		}
		
		private String getPayloadPackage(File payload, File workingDirectory) throws IOException {
			String result = "";
			if(!payload.getAbsolutePath().equals(workingDirectory.getAbsolutePath())){
				String payloadCanonicalPath = payload.getCanonicalPath();
				int relStart = workingDirectory.getCanonicalPath().length() + 1;
				int relEnd = payload.getCanonicalPath().length();
				String packageName = payloadCanonicalPath.substring(relStart,relEnd);
				if(!packageName.equals(payload.getName())){
					packageName = packageName.substring(0, packageName.lastIndexOf(payload.getName()));
					packageName = packageName.replaceAll(Matcher.quoteReplacement("" + File.separatorChar), Matcher.quoteReplacement("."));
					result = packageName;
				}
			}
			return result;
		}
		
		private String getPayloadClassName(File file){
			return file.getName().substring(0, file.getName().lastIndexOf(".java"));
		}
		
		private String getPayloadVariableName(String className){
			return Character.toLowerCase(className.charAt(0)) + className.substring(1);
		}
	}
	
	public static void generatePayloadInterface(File outputFile) throws Exception {
		FileWriter fw = new FileWriter(outputFile);
		fw.write("import java.awt.Graphics;\n");
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

	public static void generateWrapper(String targetClass, String wrapperName, PayloadEntry payloadInterface, ArrayList<PayloadEntry> payloads, File outputFile) throws Exception {
		FileWriter fw = new FileWriter(outputFile);
		
		fw.write("import java.applet.Applet;\n");
		fw.write("import java.awt.Graphics;\n");
		
		if(!payloadInterface.getPackageName().equals("")){
			fw.write("import " + payloadInterface.getPackageName() + "." + payloadInterface.getClassName() + ";\n");
		}

		for(PayloadEntry payload : payloads){
			if(!payload.getPackageName().equals("")){
				fw.write("import " + payload.getPackageName() + "." + payload.getClassName() + ";\n");
			}
		}
		
		fw.write("\n");
		fw.write("public class " + wrapperName + " extends Applet {\n");
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
		fw.write("			Class<?> c = Class.forName(\"" + targetClass + "\");\n");
		fw.write("			this.applet = (Applet) c.newInstance();\n");
		fw.write("		} catch (Exception e){}\n");
		for(PayloadEntry payload : payloads){
			fw.write("		try {\n");
			fw.write("			Class<?> c = Class.forName(\"" + payload + "\");\n");
			fw.write("			" + payload.getVariableName() + " = (Payload) c.newInstance();\n");
			fw.write("		} catch (Exception e){}\n");
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
	
	public static ArrayList<File> compileSourceFiles(ArrayList<File> sourceFiles) throws IOException {
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		
		if(javaCompiler == null){
			throw new RuntimeException("Could not find Java compiler, JDK may not be installed or classpath needs to be adjusted.");
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
