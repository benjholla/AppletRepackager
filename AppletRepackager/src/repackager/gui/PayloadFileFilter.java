package repackager.gui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.filechooser.FileFilter;

public class PayloadFileFilter extends FileFilter {
	 
	private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
 
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
	private String readFile(File file) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	  return new String(encoded, Charset.defaultCharset());
	}
	
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } 
        
        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals("java")) {
            	try {
            		if(readFile(f).contains("implements Payload")){
                		return true;
                	}
            	} catch (Exception e){}
            }
        }
 
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Applet Files";
    }
}