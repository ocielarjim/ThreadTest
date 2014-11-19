/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loadphoto;

import java.io.*;

/**
 *
 * @author Ociel Jiménez
 * This class is used only for filter file in the directory process.
 */
public class FileFilter implements FilenameFilter {
    
    @Override
    public boolean accept(File pDirectory, String pFileName){
        File file = new File(pDirectory.getPath() + "\\" + pFileName);
        return file.isFile();
    }
}
