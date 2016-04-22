/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.struts;

import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * @author Samuel
 */
public class PreviewAction extends Action {
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
            
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PreviewForm previewForm = (PreviewForm)form;
        System.out.println("preview key: "+previewForm.getKey());
        System.out.println("preview file: "+previewForm.getFile());
        if (previewForm.getKey()!=null) {
            S3Object s3o = AmazonS3Manager.getS3Object(previewForm.getKey());
            if (previewForm.getFile()!=null) {
                System.out.println(TMP_DIR);
                // save s3 file to temp file
                File temp = new File(TMP_DIR+previewForm.getKey());
                int count = 0;
                InputStream is = s3o.getObjectContent();
                byte[] buf = new byte[1024];
                OutputStream os = new FileOutputStream(temp);
                while( (count = is.read(buf)) != -1)
                {
                   if( Thread.interrupted() )
                   {
                       throw new InterruptedException();
                   }
                   os.write(buf, 0, count);
                }
                os.close();
                is.close();
                
                // stream content to response
                ZipFile zipFile = new ZipFile(temp);
                response.setContentLength((int)zipFile.getEntry(previewForm.getFile()).getSize());
                String contentType = this.guessContentType(previewForm.getFile());
                System.out.println("contentType: "+contentType);
                // display html as text file
                if (contentType!=null && contentType.contains("html")) {
                    response.setContentType("text/plain");
                } else {
                    response.setContentType(contentType);
                }
                InputStream in = zipFile.getInputStream(zipFile.getEntry(previewForm.getFile()));
                buf = new byte[1024];
                OutputStream out = response.getOutputStream();
                count = 0;
                while ((count=in.read(buf))!=-1) {
                    out.write(buf, 0, count);
                }
                out.close();
                in.close();
            } else {
                response.setContentType(s3o.getObjectMetadata().getContentType());
                response.setContentLength((int)s3o.getObjectMetadata().getContentLength());
                InputStream in = s3o.getObjectContent();
                byte[] buf = new byte[1024];
                OutputStream out = response.getOutputStream();
                int count = 0;
                while ((count=in.read(buf))!=-1) {
                    out.write(buf, 0, count);
                }
                out.close();
                in.close();
            }
        }
        return null;
    }
    
    private String guessContentType(String name) {
        return URLConnection.guessContentTypeFromName(name);
    }
}
