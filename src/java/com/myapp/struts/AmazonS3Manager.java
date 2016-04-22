package com.myapp.struts;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.ArrayList;
import java.util.List;
import org.apache.struts.upload.FormFile;

/**
 *
 * @author Samuel
 */
public class AmazonS3Manager {
    private static final String BUCKET_NAME = "samuel-data-bucket";
    private static final String AWS_CREDENTIALS="~/.aws/credentials";

    public static S3Object getS3Object(String key) {
        return AmazonS3Manager.getS3().getObject(new GetObjectRequest(BUCKET_NAME, key));
    }
    
    public static void putS3Object(FormFile file) throws Exception {
        if (file==null) return;
        String key = file.getFileName();
        ObjectMetadata om = new ObjectMetadata();
        om.setContentLength(file.getFileSize());
        om.setContentType(file.getContentType());
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, key, file.getInputStream(), om);
        //request.setCannedAcl(CannedAccessControlList.PublicRead); // enable public viewing
        AmazonS3Manager.getS3().putObject(request);;
    }
    
    public static List<MyFile> listS3Objects() {
        System.out.println("Listing objects");
        List<MyFile> files = new ArrayList<MyFile>();
        ObjectListing objectListing = AmazonS3Manager.getS3().listObjects(new ListObjectsRequest()
                .withBucketName(BUCKET_NAME));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            files.add(new MyFile(objectSummary));
        }
        return files;
    }
    
    public static AmazonS3 getS3() {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        System.out.println("Secret Key: "+credentials.getAWSSecretKey());
        System.out.println("Access Key: "+credentials.getAWSAccessKeyId());
        AmazonS3 s3 = new AmazonS3Client(credentials);
        Region usEast = Region.getRegion(Regions.US_EAST_1);
        s3.setRegion(usEast);
        return s3;
    }
}
