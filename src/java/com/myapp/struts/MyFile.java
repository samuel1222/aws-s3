/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.struts;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Samuel
 */
public class MyFile {
    private String key;
    private String name;
    private String namePath;
    private Map<String, MyFile> children; // Zip File
    private MyFile parent;
    
    public MyFile(S3ObjectSummary objectSummary) {
        this.key = objectSummary.getKey();
        this.name = this.key;
        this.parent = null;
        if (this.name.toLowerCase().endsWith(".zip")) {
            try {
                S3Object s3o = AmazonS3Manager.getS3Object(key);
                ZipInputStream zis = new ZipInputStream(s3o.getObjectContent());
                this.initZip(zis);
                zis.close();
            } catch (Exception e) {
                e.printStackTrace();
                this.children = null;
            }
        }
    }
    
    private MyFile() { }
    
    private void initZip(ZipInputStream zis) throws IOException {
        //this.children = new HashMap<String, MyFile>();
        Map<String, MyFile> collected = new HashMap<String, MyFile>();
        ZipEntry entry;
        while ((entry = zis.getNextEntry())!=null) {
            String entryName = entry.getName();
            MyFile f = new MyFile();
            f.name = entryName;
            f.namePath = entryName;
            f.key = key;
            
            collected.put(entryName, f);
            int slashIndex = entryName.lastIndexOf('/', entryName.length()-2);
            if(slashIndex < 0) {
                connectParent(this, f, entryName);
            } else {
                String parentName = entryName.substring(0, slashIndex+1);
                MyFile p = collected.get(parentName);
                this.connectParent(p, f, entryName.substring(slashIndex+1));
            }
            //System.out.println(entryName + " - "+slashIndex);
        }
    }
    
    private void connectParent(MyFile parent, MyFile child, String childName) {
        child.parent = parent;
        child.name = childName;
        if (parent.children==null) parent.children = new HashMap<String, MyFile>();
        parent.children.put(childName, child);
    }
    
    public String getNamePath() {
        return this.namePath;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String toHtmlList() {
        StringBuilder html = new StringBuilder();
        html.append("<li>");
        if (this.isDirectory()) {
            html.append(this.getName()).append("<ul>");
            List<MyFile> c = this.getChildren();
            for (int i = 0; i < c.size(); i++) {
                html.append(c.get(i).toHtmlList());
            }
            html.append("</ul>");
        }else {
            html.append("<a target=\"_blank\" href=\"").append(this.getLink()).append("\">").append(this.getName()).append("</a>");
        }
        html.append("</li>");
        return html.toString();
    }
    private String getLink() {
        return "preview.do?key="+this.key+(this.namePath==null?"":"&file="+this.namePath);
    }
    
    public boolean isDirectory() { 
        return this.children!=null; 
    }
    
    public List<MyFile> getChildren() {
        if (children==null) return null;
        return new ArrayList(this.children.values());
    }
}
