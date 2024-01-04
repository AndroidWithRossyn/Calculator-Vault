package com.example.vault.documents.util;

import android.content.Context;

import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.util.DocumentFolderDAL;
import com.example.vault.storageoption.StorageOptionsCommon;

public class DocumentsFolderGalleryMethods {
    public void AddFolderToDatabase(Context context, String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.DOCUMENTS);
        sb.append(str);
        documentFolder.setFolderLocation(sb.toString());
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(context);
        try {
            documentFolderDAL.OpenWrite();
            documentFolderDAL.AddDocumentFolder(documentFolder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentFolderDAL.close();
            throw th;
        }
        documentFolderDAL.close();
    }

    public void UpdateAlbumInDatabase(Context context, int i, String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setId(i);
        documentFolder.setFolderName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.DOCUMENTS);
        sb.append(str);
        documentFolder.setFolderLocation(sb.toString());
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(context);
        try {
            documentFolderDAL.OpenWrite();
            documentFolderDAL.UpdateFolderName(documentFolder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentFolderDAL.close();
            throw th;
        }
        documentFolderDAL.close();
    }
}
