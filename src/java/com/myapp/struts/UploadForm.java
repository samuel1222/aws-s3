/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 *
 * @author Samuel
 */
public class UploadForm extends ActionForm {
    private FormFile file = null;

    public UploadForm() {
    }

    public FormFile getFile() {
        return this.file;
    }

    public void setFile(FormFile ff) {
        this.file = ff;
    }
}
