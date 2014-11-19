/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadphoto;

/**
 *
 * @author Ociel Jiménez
 */
public class Global {
    
    // <editor-fold desc="DIRECTORIES">
    public final String DIRECTORY_PROCESS = "C:\\fotos\\fotos\\";
    public final String DIRECTORY_PROCESSED = "C:\\fotos\\fotos\\Processed\\";
    public final String DIRECTORY_ERROR = "C:\\fotos\\fotos\\Errors\\";
    public final String DIRECTORY_NORMALIZE_PHOTO = "C:\\fotos\\fotos\\NormalizedPhoto\\";
    // </editor-fold>
    
    // <editor-fold desc="PROPERTIES IMAGE NORMALICE">
    public final int PHOTO_WIDTH = 400;
    public final int PHOTO_HEIGHT = 550;
    public final String FORMAT_FILE_OUTPUT = "jpg";
    public final boolean REPROCESS_PHOTO_ALLOWED = false;
    // </editor-fold>
    
    // <editor-fold desc="PROPERTIES PROCESS">
    public final int TOP_FILE_LOAD = 10;
    public final String REG_EXP_FILE_NAME = "\\d{9}[(\\..)]\\w{3}";
    public final String WILDCARD_RENAME_ERROR = "ddMMyyyyhhmmss";
    // </editor-fold>
    
    // <editor-fold desc="ERROR IN LOAD FILE">
    public final String EXCEPTION_REPROCESS_FILE = "File previus processed";
    public final String ERROR_REPROCESS_FILE = "file_exist";
    public final String EXCEPTION_NAME_FORMAT_INCORRECT = "Name format incorrect";
    public final String ERROR_NAME_FORMAT_INCORRECT = "bad_name";
    public final String ERROR_GENERIC_FILE = "error";
    public final String ERROR_FILE_REPROCESS = "reprocess";
    public final String EXCEPTION_INCOPATIBLE_FILE = "Archivo dañado o incompatible";
    // </editor-fold>
}
