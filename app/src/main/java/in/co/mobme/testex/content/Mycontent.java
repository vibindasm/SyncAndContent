package in.co.mobme.testex.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by mobme on 24/7/14.
 */
public class Mycontent extends ContentProvider{

    static final String PROVIDER_NAME = "co.in.mobme";
    static final String URL_CONTACT = "content://" + PROVIDER_NAME + "/contact";
    public static final Uri CONTENT_URI = Uri.parse(URL_CONTACT);

    static final String URL_TEXT = "content://" + PROVIDER_NAME + "/text";
    public static final Uri TEXT_URI = Uri.parse(URL_TEXT);

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String MOB = "mobile";
    public static final String TEXT_ID="text_id";
    public static final String DATA_TEXT="text_data";

    private static HashMap<String, String> CONTACT_PROJECTION_MAP;
    private static HashMap<String, String> TEXT_DATA_PROJECTION_MAP;

    static final int CONTACT = 1;
    static final int CONTACT_ID = 2;
    static final int TEXT_DATA = 3;
    static final int TEXT_DATA_ID =4;
    private static UriMatcher uriMatch;
    static
    {
        uriMatch = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatch.addURI(PROVIDER_NAME,"contact",CONTACT);
        uriMatch.addURI(PROVIDER_NAME,"contact/#",CONTACT_ID);
        uriMatch.addURI(PROVIDER_NAME,"text",TEXT_DATA);
        uriMatch.addURI(PROVIDER_NAME,"text/#",TEXT_DATA_ID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Mobme";
    static final String CONTACT_TABLE_NAME = "contacts";
    static final String TEXT_DATA_TABLE_NAME = "textData";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + CONTACT_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " mobile NUMBER NOT NULL);";
    static final String CREATE_TEXT_DB_TABLE =
            " CREATE TABLE " + TEXT_DATA_TABLE_NAME +
                    " (text_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " text_data TEXT NOT NULL);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
            db.execSQL(CREATE_TEXT_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  CONTACT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " +  CREATE_TEXT_DB_TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatch.match(uri)) {
            case CONTACT:
                qb.setTables(CONTACT_TABLE_NAME);
                qb.setProjectionMap(CONTACT_PROJECTION_MAP);
                if (sortOrder == null || sortOrder == ""){
                    /**
                     * By default sort on student names
                     */
                    sortOrder = NAME;
                }
                break;
            case CONTACT_ID:
                qb.setTables(CONTACT_TABLE_NAME);
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                if (sortOrder == null || sortOrder == ""){
                    /**
                     * By default sort on student names
                     */
                    sortOrder = NAME;
                }
                break;
            case TEXT_DATA:
                qb.setTables(TEXT_DATA_TABLE_NAME);
                qb.setProjectionMap(TEXT_DATA_PROJECTION_MAP);
                if (sortOrder == null || sortOrder == ""){
                    /**
                     * By default sort on student names
                     */
                    sortOrder = TEXT_ID;
                }
                break;
            case TEXT_DATA_ID:
                qb.setTables(TEXT_DATA_TABLE_NAME);
                qb.appendWhere( TEXT_ID + "=" + uri.getPathSegments().get(1));
                if (sortOrder == null || sortOrder == ""){
                    /**
                     * By default sort on student names
                     */
                    sortOrder = TEXT_ID;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,
                null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatch.match(uri)){
            /**
             * Get all student records
             */
            case CONTACT:
                return "vnd.android.cursor.dir/vnd.example.students";
            /**
             * Get a particular student
             */
            case CONTACT_ID:
                return "vnd.android.cursor.item/vnd.example.students";
            /**
             * Get all text data
             */
            case TEXT_DATA:
                return "vnd.android.cursor.dir/vnd.example.text_data";
            /**
             * Get a particular student
             */
            case TEXT_DATA_ID:
                return "vnd.android.cursor.item/vnd.example.text_data";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID;
        Uri _uri;
        switch (uriMatch.match(uri)) {
            case CONTACT:
               rowID = db.insert(	CONTACT_TABLE_NAME, "", values);
                if (rowID > 0)
                {
                    _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case TEXT_DATA:
                rowID = db.insert(	TEXT_DATA_TABLE_NAME,"", values);
                if (rowID > 0)
                {
                    _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        /**
         * Add a new contact record
         */

        /**
         * If record is added successfully
         */

        throw new SQLException("Failed to add a record into " + uri);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatch.match(uri)){
            case CONTACT:
                count = db.delete(CONTACT_TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( CONTACT_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            case TEXT_DATA:
                count = db.delete(TEXT_DATA_TABLE_NAME, selection, selectionArgs);
                break;
            case TEXT_DATA_ID:
                String text_id = uri.getPathSegments().get(1);
                count = db.delete( TEXT_DATA_TABLE_NAME, TEXT_ID +  " = " + text_id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatch.match(uri)){
            case CONTACT:
                count = db.update(CONTACT_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case CONTACT_ID:
                count = db.update(CONTACT_TABLE_NAME, values, _ID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            case TEXT_DATA:
                count = db.update(TEXT_DATA_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case TEXT_DATA_ID:
                count = db.update(TEXT_DATA_TABLE_NAME, values, TEXT_ID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
