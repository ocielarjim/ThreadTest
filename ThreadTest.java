/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadImage;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 *
 * @author Ociel Jiménez
 */
public class ProcessLoad extends Thread {
    
    Global global = new Global();
    
    @Override
    public void run() {
        ArrayList<String> listFileProcessSuccessfull = new ArrayList<>();
        ArrayList<String> listFileProcessError = new ArrayList<>();
        ArrayList<String> listFileReprocess = null;
        ArrayList<String> listFileBadName = new ArrayList<>();
        if (!global.REPROCESS_PHOTO_ALLOWED)
            listFileReprocess = new ArrayList<>();
        
        System.out.println("inicio");
        
        // <editor-fold desc="1.0  LOAD FILE">
        System.out.println("Valida Directorios");
        this.validateDirectories();
        System.out.println("Obtiene el top de los archivos a procesar");
        String[] listFile = this.getListFile(global.DIRECTORY_PROCESS, global.TOP_FILE_LOAD);
        // </editor-fold>
        
        // <editor-fold desc="2.0  LOAD PHOTOS AND NORMALICE">
        System.out.println("Normaliza las fotografías 1 a 1");
        for (String nameFile : listFile)
        {
            try {
                // <editor-fold desc="2.1  VALIDATE IF LAST PROCESSED FILE">
                if (validateExistFile(nameFile) && !global.REPROCESS_PHOTO_ALLOWED)
                    throw new Exception(global.EXCEPTION_REPROCESS_FILE);
                // </editor-fold>
                
                // <editor-fold desc="2.2  VALIDATE IF NAME FILE IS CORRECT">
                if (!nameFile.matches("(\\d{9})\\l{3}"))
                    throw new Exception(global.EXCEPTION_NAME_FORMAT_INCORRECT);
                // </editor-fold>
                
                // <editor-fold desc="2.3  NORMALIZE PHOTO">
                this.normalizeImage(global.DIRECTORY_PROCESS + nameFile, global.DIRECTORY_NORMALIZE_PHOTO, nameFile);
                // </editor-fold>
                
                // <editor-fold desc="2.4  ADD TO LIST PROCESSED SUCCESSFULL">
                listFileProcessSuccessfull.add(nameFile);
                // </editor-fold>
            }
            catch (Exception e) {
                // <editor-fold desc="2.5  ADD TO LIST PROCESSED ERROR">
                if (e.getMessage().compareTo(global.EXCEPTION_REPROCESS_FILE) == 0)
                    listFileReprocess.add(nameFile);
                else if (e.getMessage().compareTo(global.EXCEPTION_NAME_FORMAT_INCORRECT) == 0)
                    listFileBadName.add(nameFile);
                else
                    listFileProcessError.add(nameFile);
                // </editor-fold>
            }
        }
        // </editor-fold>
        
        // <editor-fold desc="3.0  MOVE FILE">
        System.out.println("Mueve los archivos procesados correctamente a la carpeta Processed");
        this.moveFileProcessed(listFileProcessSuccessfull);
        System.out.println("Mueve los archivos procesados con errores a la carpeta Errors");
        this.moveFileError(listFileProcessError, global.GENERIC_FILE_ERROR);
        if (listFileReprocess != null)
            this.moveFileError(listFileReprocess, global.ERROR_REPROCESS_FILE);
        // </editor-fold>
        
        // <editor-fold desc="4.0  PRINT REPORT">
        // </editor-fold>
        System.out.println("fin");
    }
    
    // <editor-fold desc="METODOS Y FUNCIONES">
    /**
     * this method validate if exist directories of out
     */
    public void validateDirectories() {
        File processedDirectory = new File(global.DIRECTORY_PROCESSED);
        if (!processedDirectory.isDirectory())
            processedDirectory.mkdir();
        File errorsDirectory = new File(global.DIRECTORY_ERROR);
        if (!errorsDirectory.isDirectory())
            errorsDirectory.mkdir();
        File TMPDirectory = new File(global.DIRECTORY_NORMALIZE_PHOTO);
        if (!TMPDirectory.isDirectory())
            TMPDirectory.mkdir();
    }
    
    /**
     * This method get list name files in at range set
     * @param pPath is a path current files to load
     * @param pRange is a limit of load files
     * @return String[] with limit set in pRange
     */
    public String[] getListFile(String pPath, int pRange) {
        File directory = new File(pPath);
        String[] listFile;
        int loadPartFiles = directory.list().length;
        
        if (loadPartFiles >= pRange)
            loadPartFiles = pRange;
        
        listFile = new String[loadPartFiles];
        /*for (int i = 0; i < loadPartFiles; i++)
            listFile[i] = directory.list()[i];*/
        System.arraycopy(directory.list(), 0, listFile, 0, loadPartFiles);
        
        
        return listFile;
    }
    
    /**
     * Validate if exist file in directory to.
     * @param pNameFile
     * @return true or false
     */
    private boolean validateExistFile(String pNameFile) {
        File file = new File(global.DIRECTORY_PROCESSED + pNameFile);
        return file.isFile();
    }
    
    /**
     * This method return Object Image if is correct format file if not return null.
     * @param pPathName
     * @return 
     */
    private BufferedImage loadImage(String pPathName) throws IOException {
        BufferedImage bffImage = null;
        try {
            bffImage = ImageIO.read(new File(pPathName));
            return bffImage;
        }
        catch (IOException e) {
            throw e;
        }
    }
    
    /**
     * This method convert the original image to new image normalize.
     * @param pOriginalImagePath
     * @param pNewPath
     * @param pNameFile
     * @throws java.io.IOException 
     */
    public void normalizeImage(String pOriginalImagePath, String pNewPath, String pNameFile) throws IOException {
        try {
            BufferedImage bImage = loadImage(pOriginalImagePath);
            if(bImage.getHeight()>bImage.getWidth()){
                int heigt = (bImage.getHeight() * global.PHOTO_WIDTH) / bImage.getWidth();
                bImage = resize(bImage, global.PHOTO_WIDTH, heigt);
                int width = (bImage.getWidth() * global.PHOTO_HEIGHT) / bImage.getHeight();
                bImage = resize(bImage, width, global.PHOTO_HEIGHT);
            }else{
                int width = (bImage.getWidth() * global.PHOTO_HEIGHT) / bImage.getHeight();
                bImage = resize(bImage, width, global.PHOTO_HEIGHT);
                int heigt = (bImage.getHeight() * global.PHOTO_WIDTH) / bImage.getWidth();
                bImage = resize(bImage, global.PHOTO_WIDTH, heigt);
            }
            this.saveImage(bImage, pNewPath, pNameFile);
        }
        catch (IOException e) {
            throw e; 
        }
    }
 
    /**
     * This method is used from save image in new folder destiny
     * @param pImage
     * @param pPathName 
     */
    private void saveImage(BufferedImage pImage, String pPath, String pNameFile) {
        try {
            File file = new File(pPath + pNameFile);
            if (global.REPROCESS_PHOTO_ALLOWED && file.isFile())
                file = new File(pPath + this.renameErrorFile(pNameFile, "reprocess"));
            file.getParentFile().mkdirs();
            ImageIO.write(pImage, global.FORMAT_FILE_OUTPUT, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    /**
     * This method is used from resize photo
     * @param pImage
     * @param pNewWidth
     * @param pNewHeight
     * @return 
     */
    private BufferedImage resize(BufferedImage pImage, int pNewWidth, int pNewHeight) {
        int imageWidth = pImage.getWidth();
        int imageHeight = pImage.getHeight();
        BufferedImage bufim = new BufferedImage(pNewWidth, pNewHeight, pImage.getType());
        Graphics2D graphic = bufim.createGraphics();
        graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphic.drawImage(pImage, 0, 0, pNewWidth, pNewHeight, 0, 0, imageWidth, imageHeight, null);
        graphic.dispose();
        return bufim;
    }
    
    /**
     * This method moved files to folder processed 
     * @param pListFile 
     */
    private boolean moveFileProcessed(ArrayList<String> pListFile) {
        for (String nameFile : pListFile) {
            File fileOrigin = new File(global.DIRECTORY_PROCESS + nameFile);
            File fileTo = new File(global.DIRECTORY_PROCESSED + nameFile);
            if (global.REPROCESS_PHOTO_ALLOWED && fileTo.isFile())
                fileTo = new File(global.DIRECTORY_PROCESSED + renameErrorFile(nameFile, global.FILE_REPROCESS));
            fileOrigin.renameTo(fileTo);
            fileOrigin.delete();
        }
        return true;
    }
    
    /**
     * This method rename file out whit present file errors
     * @param pNameFile
     * @param pValueError
     * @return String with rename file
     */
    private String renameErrorFile (String pNameFile, String pValueError) {
        Date dateTime = new Date();
        String nameFile = pNameFile.split("[.]")[0];
        String formatFile = pNameFile.split("[.]")[1];
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        
        if (pValueError.compareTo(global.ERROR_NAME_FORMAT_INCORRECT) == 0)
            return pValueError + "_" + formatDate.format(dateTime) + "_" + nameFile + "." + formatFile;
        return nameFile + "_" + pValueError + "_" + formatDate.format(dateTime) + "." + formatFile;
    }
    
    /**
     * This method moved files to folder error
     * @param pListFile
     * @return 
     */
    private boolean moveFileError(ArrayList<String> pListFile, String pValueError) {
        for (String nameFile : pListFile) {
            File fileOrigin = new File(global.DIRECTORY_PROCESS + nameFile);
            File fileTo = new File(global.DIRECTORY_ERROR + this.renameErrorFile(nameFile, pValueError));
            fileOrigin.renameTo(fileTo);
            fileOrigin.delete();
        }
        return true;
    }
    // </editor-fold>
}
