
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class AppletRepackager {

	public static final String META_INF = "META-INF";
	
	// some test code
	public static void main(String[] args) throws Exception {
//		File appletJarFile = new File("test.jar");
//		File extractedJarDirectory = ZipUtils.extractJar(appletJarFile);
//		File outputAppletJarFile = new File(appletJarFile.getAbsolutePath().replace(".jar", "") + "_repacked.jar");
//		unsign(extractedJarDirectory);
//		ZipUtils.compressJar(extractedJarDirectory, outputAppletJarFile);
//		System.out.println("Finished.");
		
		ArrayList<String> payloads = new ArrayList<String>();
		payloads.add("TestPayload");
		String targetClass = "Main";
		String wrapperName = "Wrapper";
		File sourceFile = new File(wrapperName + ".java");
		generateWrapper(targetClass, wrapperName, payloads, sourceFile);
//		File classFile = compileClass(sourceFile, payloads);
		
//		System.out.println(classFile);
	}
	
	private static void generatePayloadInterface(File outputFile) throws Exception {
		FileWriter fw = new FileWriter(outputFile);
		fw.write("import java.awt.Graphics;");
		fw.write("public interface Payload {");
		fw.write("	public void preInitPayload();");
		fw.write("	public void postInitPayload();");
		fw.write("	public void preStartPayload();");
		fw.write("	public void postStartPayload();");
		fw.write("	public void prePaintPayload(Graphics g);");
		fw.write("	public void postPaintPayload(Graphics g);");
		fw.write("	public void preStopPayload();");
		fw.write("	public void postStopPayload();");
		fw.write("	public void preDestroyPayload();");
		fw.write("	public void postDestroyPayload();");
		fw.write("}");
		fw.close();
	}
	
	private static void generateWrapper(String targetClass, String wrapperName, ArrayList<String> payloads, File outputFile) throws Exception {
		FileWriter fw = new FileWriter(outputFile);
		
		fw.write("import java.applet.Applet;\n");
		fw.write("import java.awt.Graphics;\n");
		fw.write("import Payload;\n");
		fw.write("\n");
		fw.write("public class " + wrapperName + " extends Applet {\n");
		fw.write("\n");
		fw.write("	private static final long serialVersionUID = 1L;\n");
		fw.write("\n");
		fw.write("	Applet applet = null;\n");
		fw.write("\n");
		for(String payload : payloads){
			fw.write("	" + "Payload " + classToVariableName(payload) + " = null;\n");
		}
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void init(){\n");
		fw.write("		try {\n");
		fw.write("			Class<?> c = Class.forName(\"" + targetClass + "\");\n");
		fw.write("				this.applet = (Applet) c.newInstance();\n");
		fw.write("		} catch (Exception e){}\n");
		for(String payload : payloads){
			fw.write("		try {\n");
			fw.write("			Class<?> c = Class.forName(\"" + payload + "\");\n");
			fw.write("				" + classToVariableName(payload) + " = (Payload) c.newInstance();\n");
			fw.write("		} catch (Exception e){}\n");
		}
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preInitPayload();\n");
		}
		fw.write("		this.applet.init();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postInitPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void start(){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preStartPayload();\n");
		}
		fw.write("		this.applet.start();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postStartPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void paint(Graphics g){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".prePaintPayload(g);\n");
		}
		fw.write("		this.applet.paint(g);\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postPaintPayload(g);\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void stop(){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preStopPayload();\n");
		}
		fw.write("		this.applet.stop();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postStopPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void destroy(){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preDestroyPayload();\n");
		}
		fw.write("		this.applet.destroy();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postDestroyPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("}\n");
		
		fw.close();
	}
	
	private static String classToVariableName(String className){
		return Character.toLowerCase(className.charAt(0)) + className.substring(1);
	}
	
//	private static File compileClass(File sourceFile){
//		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//		if(compiler.run(null, null, null, sourceFile.getPath()) == 0){
//			
//		} else {
//			throw new RuntimeException("Compile Error");
//		}
//		return new File(sourceFile.getAbsolutePath().replace(".java", ".class"));
//	}
	
	private static ArrayList<File> compileSourceFiles(ArrayList<File> sourceFiles) throws IOException {
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnostics, Locale.ENGLISH, Charset.forName("UTF-8"));
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFiles);
		javaCompiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
		    
		for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
			System.out.format("Error on line %d in %d%n", diagnostic.getLineNumber(), diagnostic.getSource().toString());
		}        
		 
		fileManager.close();
		
		ArrayList<File> classFiles = new ArrayList<File>();
		for(File sourceFile : sourceFiles){
			classFiles.add(new File(sourceFile.getAbsolutePath().replace(".java", ".class")));
		}
		return classFiles;
	}

}
