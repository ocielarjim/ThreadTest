/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadphoto;

import java.awt.Graphics2D;
import java.awt.Image;
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
 * @author Administrator
 */
public class TestThread extends Thread {
    
    int count = 0;
    boolean runwhile = true;
    public static int MAX_WIDTH=400;//Ancho máximo
    public static int MAX_HEIGHT=550;//Alto máximo
    
    @Override
    public void run() {
        ArrayList<String> listFileProcessSuccessfull = new ArrayList<String>();
        ArrayList<String> listFileProcessError = new ArrayList<String>();
        
        System.out.println("inicio");
        
        // <editor-fold desc="1.0  LOAD FILE">
        System.out.println("Valida Directorios");
        this.validateDirectories("C:\\fotos\\fotos");
        System.out.println("Obtiene el top 10 de los archivos a procesar");
        String[] listFile = this.getListFile("C:\\fotos\\fotos", 10);
        
        // </editor-fold>
        
        // <editor-fold desc="2.0  LOAD PHOTOS AND NORMALICE">
        
        System.out.println("Normaliza las fotografías 1 a 1");
        for (String nameFile : listFile)
        {
            try {
                this.normaliceImage("C:\\fotos\\fotos\\" + nameFile, "C:\\fotos\\fotos\\TMP\\" + nameFile);
                listFileProcessSuccessfull.add(nameFile);
            }
            catch (Exception e) {
                //log
                listFileProcessError.add(nameFile);
            }
        }
        
        // </editor-fold>
        
        // <editor-fold desc="3.0  MOVE FILE">
        
        System.out.println("Mueve los archivos procesados correctamente a la carpeta Processed");
        this.moveFileProcessed(listFileProcessSuccessfull);
        System.out.println("Mueve los archivos procesados con errores a la carpeta Errors");
        this.moveFileError(listFileProcessError);
        
        // </editor-fold>
        
        // <editor-fold desc="4.0  PRINT REPORT">
        // </editor-fold>
        System.out.println("fin");
    }
    
    // <editor-fold desc="METODOS Y FUNCIONES">
    /**
     * this method validate if exist directories of out
     * @param pPaht 
     */
    public void validateDirectories(String pPaht) {
        File processedDirectory = new File(pPaht + "\\Processed");
        if (!processedDirectory.isDirectory())
            processedDirectory.mkdir();
        File errorsDirectory = new File(pPaht + "\\Errors");
        if (!errorsDirectory.isDirectory())
            errorsDirectory.mkdir();
        File TMPDirectory = new File(pPaht + "\\TMP");
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
        for (int i = 0; i < loadPartFiles; i++)
            listFile[i] = directory.list()[i];
        
        return listFile;
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
     * This method convert the original image to new image normalice.
     * @param pOriginalImagePath
     * @param pNewImagePath 
     * @throws java.io.IOException 
     */
    public void normaliceImage(String pOriginalImagePath, String pNewImagePath) throws IOException {
        try {
            BufferedImage bImage = loadImage(pOriginalImagePath);
            if(bImage.getHeight()>bImage.getWidth()){
                int heigt = (bImage.getHeight() * MAX_WIDTH) / bImage.getWidth();
                bImage = resize(bImage, MAX_WIDTH, heigt);
                int width = (bImage.getWidth() * MAX_HEIGHT) / bImage.getHeight();
                bImage = resize(bImage, width, MAX_HEIGHT);
            }else{
                int width = (bImage.getWidth() * MAX_HEIGHT) / bImage.getHeight();
                bImage = resize(bImage, width, MAX_HEIGHT);
                int heigt = (bImage.getHeight() * MAX_WIDTH) / bImage.getWidth();
                bImage = resize(bImage, MAX_WIDTH, heigt);
            }
            this.saveImage(bImage, pNewImagePath);
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
    private void saveImage(BufferedImage pImage, String pPathName) {
        try {
            String format = "jpg";
            File file =new File(pPathName);
            file.getParentFile().mkdirs();
            ImageIO.write(pImage, format, file);
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
        int w = pImage.getWidth();
        int h = pImage.getHeight();
        BufferedImage bufim = new BufferedImage(pNewWidth, pNewHeight, pImage.getType());
        Graphics2D graphic = bufim.createGraphics();
        graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphic.drawImage(pImage, 0, 0, pNewWidth, pNewHeight, 0, 0, w, h, null);
        graphic.dispose();
        return bufim;
    }
    
    /**
     * This method moved files to folder processed 
     * @param pListFile 
     */
    private boolean moveFileProcessed(ArrayList<String> pListFile) {
        for (String nameFile : pListFile) {
            File fileOrigin = new File("C:\\fotos\\fotos\\" + nameFile);
            File fileTo = new File("C:\\fotos\\fotos\\Processed\\" + nameFile);
            fileOrigin.renameTo(fileTo);
            fileOrigin.delete();
        }
        return true;
    }
    
    /**
     * This method rename file out whit present file errors
     * @param pNameFile
     * @return String with rename file
     */
    private String renameErrorFile (String pNameFile) {
        Date dateTime = new Date();
        String nameFile = pNameFile.split("[.]")[0];
        String formatFile = pNameFile.split("[.]")[1];
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        
        return nameFile + "_error_" + formatDate.format(dateTime) + "." + formatFile;
    }
    
    /**
     * This method moved files to folder error
     * @param pListFile
     * @return 
     */
    private boolean moveFileError(ArrayList<String> pListFile) {
        for (String nameFile : pListFile) {
            File fileOrigin = new File("C:\\fotos\\fotos\\" + nameFile);
            File fileTo = new File("C:\\fotos\\fotos\\Errors\\" + this.renameErrorFile(nameFile));
            fileOrigin.renameTo(fileTo);
            fileOrigin.delete();
        }
        return true;
    }
    // </editor-fold>
}
