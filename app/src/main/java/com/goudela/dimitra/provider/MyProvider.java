package com.goudela.dimitra.provider;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.util.Log;

import com.goudela.dimitra.provider.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class MyProvider extends DocumentsProvider {

    public static final String LOG_TAG = MyProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        final MatrixCursor cursor = new MatrixCursor(resolveRootProjection(projection));

        final MatrixCursor.RowBuilder row = cursor.newRow();
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, MyProvider.class.getName());
        row.add(DocumentsContract.Root.COLUMN_SUMMARY, "sdy61_ge5");
        row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_SUPPORTS_CREATE | DocumentsContract.Root.FLAG_SUPPORTS_SEARCH);
        row.add(DocumentsContract.Root.COLUMN_TITLE, "MyContentProvider");
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, "/");
        row.add(DocumentsContract.Root.COLUMN_MIME_TYPES, "*/*");
        row.add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, Integer.MAX_VALUE);
        row.add(DocumentsContract.Root.COLUMN_ICON, R.drawable.ic_launcher);

        return cursor;
    }

    private String[] resolveRootProjection(String[] projection) {

        if (projection == null || projection.length == 0) {
            return new String[] {
                    DocumentsContract.Root.COLUMN_ROOT_ID,
                    DocumentsContract.Root.COLUMN_MIME_TYPES,
                    DocumentsContract.Root.COLUMN_FLAGS,
                    DocumentsContract.Root.COLUMN_ICON,
                    DocumentsContract.Root.COLUMN_TITLE,
                    DocumentsContract.Root.COLUMN_SUMMARY,
                    DocumentsContract.Root.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Root.COLUMN_AVAILABLE_BYTES,
            };
        } else {
            return projection;
        }
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder)
            throws FileNotFoundException {
        MatrixCursor cursor = new MatrixCursor(resolveDocumentProjection(projection));

        String parentDocumentPath = getContext().getFilesDir().getPath() + "/" + parentDocumentId;
        File dir = new File(parentDocumentPath);
        for (File file : dir.listFiles()) {
            String documentId = parentDocumentId + "/" + file.getName();
            includeFile(cursor, documentId);
        }

        return cursor;
    }

    private String[] resolveDocumentProjection(String[] projection) {
        if (projection == null || projection.length == 0) {
            return new String[] {
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                    DocumentsContract.Document.COLUMN_FLAGS,
                    DocumentsContract.Document.COLUMN_SIZE,
            };
        } else {
            return projection;
        }
    }

    @Override
    public ParcelFileDescriptor openDocument(final String documentId, String mode,
                                             CancellationSignal signal) throws FileNotFoundException {
        final File file = new File(getContext().getFilesDir().getPath() + "/" + documentId);

        boolean isWrite = (mode.indexOf('w') != -1);
        if (isWrite) {
            int accessMode = ParcelFileDescriptor.MODE_READ_WRITE;
            try {
                Handler handler = new Handler(getContext().getMainLooper());
                return ParcelFileDescriptor.open(file, accessMode,
                        handler, new ParcelFileDescriptor.OnCloseListener() {
                            @Override
                            public void onClose(IOException e) {
                                Log.i(LOG_TAG, "A file with id " + documentId
                                        + " has been closed! Time to " + "update the server.");
                            }

                        });
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open document with id "
                        + documentId + " and mode " + mode);
            }
        } else {
            int accessMode = ParcelFileDescriptor.MODE_READ_ONLY;
            return ParcelFileDescriptor.open(file, accessMode);
        }
    }

    @Override
    public Cursor queryDocument(String documentId, String[] projection)
            throws FileNotFoundException {
        MatrixCursor cursor = new MatrixCursor(resolveDocumentProjection(projection));
        includeFile(cursor, documentId);

        return cursor;
    }

    @Override
    public String createDocument(String parentDocumentId, String mimeType, String displayName)
            throws FileNotFoundException {
        String filePath = getContext().getFilesDir().getPath() + "/" + parentDocumentId + "/"
                + displayName;
        try {
            boolean result = new File(filePath).createNewFile();
            if (!result)
                throw new RuntimeException("Failed to make new file");

            return parentDocumentId + "/" + displayName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDocument(String documentId) throws FileNotFoundException {
        String filePath = getContext().getFilesDir().getPath() + "/" + documentId;
        File file = new File(filePath);
        boolean result = file.delete();
        if (!result)
            throw new RuntimeException("Failed to delete the file, file path=" + filePath);
    }

    @Override
    public Cursor querySearchDocuments(String rootId, String query, String[] projection)
            throws FileNotFoundException {
        String rootDocumentPath = getContext().getFilesDir().getPath() + "/";

        MatrixCursor cursor = new MatrixCursor(resolveDocumentProjection(projection));
        List<File> searchResultList = IOUtil.find(rootDocumentPath, query);
        for (File file : searchResultList) {
            String documentId = file.getPath().replace(rootDocumentPath, "");
            includeFile(cursor, documentId);
        }

        return cursor;
    }

    private void includeFile(MatrixCursor cursor, String documentId) {
        String filePath = getContext().getFilesDir().getPath() + "/" + documentId;
        File file = new File(filePath);

        MatrixCursor.RowBuilder row = cursor.newRow();
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, documentId);
        if (file.isDirectory()) {
            row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, DocumentsContract.Document.MIME_TYPE_DIR);
            row.add(DocumentsContract.Document.COLUMN_FLAGS, DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE);
            row.add(DocumentsContract.Document.COLUMN_SIZE, 0);
        } else if (file.getName().endsWith(".txt")) {
            row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, "text/plain");
            row.add(DocumentsContract.Document.COLUMN_FLAGS, DocumentsContract.Document.FLAG_SUPPORTS_DELETE
                    | DocumentsContract.Document.FLAG_SUPPORTS_WRITE);
            row.add(DocumentsContract.Document.COLUMN_SIZE, file.length());
        } else {
            throw new RuntimeException("Unknown file type, file name=" + file.getName());
        }
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, file.getName());
        row.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, file.lastModified());
    }

}
