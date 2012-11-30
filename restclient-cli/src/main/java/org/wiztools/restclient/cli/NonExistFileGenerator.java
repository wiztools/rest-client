package org.wiztools.restclient.cli;

import java.io.File;

/**
 *
 * @author subwiz
 */
class NonExistFileGenerator {
    
    private final File outDir;
    private final String fileName;
    private final String extension;

    NonExistFileGenerator(File outDir, String fileName, String extension) {
        this.outDir = outDir;
        this.fileName = fileName;
        this.extension = extension;
    }
    
    File getFile() {
        final File outFile = new File(outDir, fileName + extension);
        if(outFile.exists()){
            System.err.println("File exists: " + outFile.getAbsolutePath());
            for(int i = 0; i< Integer.MAX_VALUE; i++){
                final File newOutFile = new File(outDir, fileName + "_" + i + extension);
                if(!outFile.exists()){
                    System.err.println("Using alternative: " + outFile.getAbsolutePath());
                    return newOutFile;
                }
            }
        }
        return outFile;
    }
}
