package repackager;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

public class PayloadEntry {
		private String packageName;
		private String className;
		private String variableName;
		private File payloadSourceFile;
		private File payloadClassFile;
		
		public PayloadEntry(File payloadSourceFile, File baseDirectory) throws IOException {
			this.payloadSourceFile = payloadSourceFile;
			this.payloadClassFile = new File(payloadSourceFile.getAbsolutePath().replace(".java", ".class"));
			this.packageName = getPayloadPackage(payloadSourceFile, baseDirectory);
			this.className = getPayloadClassName(payloadSourceFile);
			this.variableName = getPayloadVariableName(this.className);
		}
		
		public void setPackageName(String packageName){
			this.packageName = packageName;
		}
		
		public String getPackageName() {
			return packageName;
		}

		public String getClassName() {
			return className;
		}
		
		public String getQualifiedClassName(){
			if(packageName.equals("")){
				return className;
			} else {
				return packageName + "." + className;
			}
		}

		public String getVariableName() {
			return variableName;
		}

		public File getSourceFile() {
			return payloadSourceFile;
		}
		
		public File getClassFile() {
			return payloadClassFile;
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
					if(packageName.endsWith("" + File.separatorChar)){
						packageName = packageName.substring(0, packageName.length()-1);
					}
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