package com.msarangal.vocabmania;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mandeep on 22/6/2015.
 */
public class MySQLiteAdapter {

    MyHelper helper;
    SQLiteDatabase db;
    List<WordMeaning> data = new ArrayList<>();
    public static String KEY_ROWID = "rowid";
    public static String KEY_MARKS = "marks";
    public static String KEY_DATES = "dates";
    public static String KEY_ARRAY_LENGTH = "length";
    public static String KEY_RESULT = "result";
    Cursor cursor = null;
    int col1, col2, col3;
    String word = null, meaning = null, usage = null;

    public MySQLiteAdapter(Context context) {
        // TODO Auto-generated constructor stub
        helper = new MyHelper(context);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public Bundle checkfive_returnArray(String level) {

        db = helper.getWritableDatabase();
        String[] selectionArgs = {level};
        String[] columns = {MyHelper.ROWID, MyHelper.MARKS, MyHelper.LEVEL, MyHelper.TEST_DATE};
        cursor = db.query(MyHelper.TABLE_NAME2, columns, MyHelper.LEVEL + "=?", selectionArgs, null, null, null);
        Bundle bundleGraphData = new Bundle();
        //get the number of rows in resultset
        int i = cursor.getCount();

        // if number of rows where level equals GIVEN LEVEL is more than five
        if (i > 5) {
            int limite = 5;
            int k = 0;
            String[] rowidArrayString = new String[5];
            int[] rowidArray = new int[5];
            int[] marksArray = new int[5];
            String[] testDatesArray = new String[5];
            Cursor c = db.query(MyHelper.TABLE_NAME2, columns, MyHelper.LEVEL + "=?", selectionArgs, null, null, MyHelper.ROWID + " DESC", String.valueOf(limite));

            // get the 5 most recent rows
            while (c.moveToNext()) {
                col1 = c.getColumnIndex(MyHelper.ROWID);
                col2 = c.getColumnIndex(MyHelper.MARKS);
                col3 = c.getColumnIndex(MyHelper.TEST_DATE);
                int rowid = c.getInt(col1);
                int marks = c.getInt(col2);
                String tDate = c.getString(col3);
                rowidArrayString[k] = String.valueOf(rowid);
                rowidArray[k] = rowid;
                marksArray[k] = marks;
                testDatesArray[k] = tDate;
                k = k + 1;
            }

            //delete the ONE least recent row
            String args = TextUtils.join(", ", rowidArrayString);

            db.execSQL(String.format("DELETE FROM " + MyHelper.TABLE_NAME2 + " WHERE " + MyHelper.ROWID + " NOT IN (%s) AND " + MyHelper.LEVEL + " = '%s' ;", args, level));


            bundleGraphData.putInt(MySQLiteAdapter.KEY_ARRAY_LENGTH, 5);
            bundleGraphData.putIntArray(MySQLiteAdapter.KEY_ROWID, rowidArray);
            bundleGraphData.putStringArray(MySQLiteAdapter.KEY_DATES, testDatesArray);
            bundleGraphData.putIntArray(MySQLiteAdapter.KEY_MARKS, marksArray);
            // 1 means bundle has required data
            bundleGraphData.putInt(MySQLiteAdapter.KEY_RESULT, 1);

            return bundleGraphData;

        } else {
            int k = 0;
            Cursor c = db.query(MyHelper.TABLE_NAME2, columns, MyHelper.LEVEL + "=?", selectionArgs, null, null, MyHelper.ROWID + " DESC");
            int arrayLength = c.getCount();
            if (arrayLength != 0) {
                int[] rowidArray = new int[arrayLength];
                int[] marksArray = new int[arrayLength];
                String[] testDatesArray = new String[arrayLength];
                // get the most recent rows, whatever number it is...
                while (c.moveToNext()) {
                    col1 = c.getColumnIndex(MyHelper.ROWID);
                    col2 = c.getColumnIndex(MyHelper.MARKS);
                    col3 = c.getColumnIndex(MyHelper.TEST_DATE);
                    int rowid = c.getInt(col1);
                    int marks = c.getInt(col2);
                    String tDate = c.getString(col3);
                    rowidArray[k] = rowid;
                    marksArray[k] = marks;
                    testDatesArray[k] = tDate;
                    k = k + 1;
                }

                bundleGraphData.putInt(MySQLiteAdapter.KEY_ARRAY_LENGTH, arrayLength);
                bundleGraphData.putIntArray(MySQLiteAdapter.KEY_ROWID, rowidArray);
                bundleGraphData.putStringArray(MySQLiteAdapter.KEY_DATES, testDatesArray);
                bundleGraphData.putIntArray(MySQLiteAdapter.KEY_MARKS, marksArray);
                // 1 means bundle has required data
                bundleGraphData.putInt(MySQLiteAdapter.KEY_RESULT, 1);
                return bundleGraphData;
            } else {
                //no data found in the resultset
                bundleGraphData.putInt(MySQLiteAdapter.KEY_RESULT, 0);
                return bundleGraphData;
            }

        }
    }

    public long insertIntoValuesForGraph(int marks, String level) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyHelper.MARKS, marks);
        cv.put(MyHelper.LEVEL, level);
        cv.put(MyHelper.TEST_DATE, getDateTime());

        long result = db.insert(MyHelper.TABLE_NAME2, null, cv);
        return result;
    }

    public String getAllGraphValues() {
        String[] columns = {MyHelper.ROWID, MyHelper.MARKS, MyHelper.LEVEL, MyHelper.TEST_DATE};

        StringBuffer buffer = new StringBuffer();

        db = helper.getWritableDatabase();
        Cursor c = db.query(MyHelper.TABLE_NAME2, columns, null, null, null, null,
                null);

        while (c.moveToNext()) {
            col1 = c.getColumnIndex(MyHelper.ROWID);
            col2 = c.getColumnIndex(MyHelper.MARKS);
            col3 = c.getColumnIndex(MyHelper.LEVEL);
            int col4 = c.getColumnIndex(MyHelper.TEST_DATE);

            int rowid = c.getInt(col1);
            String marks = c.getString(col2);
            String level = c.getString(col3);
            String date = c.getString(col4);

            buffer.append(rowid + " " + marks + " " + level + " " + date + "\n");
        }

        return buffer.toString();

    }

    public long insert(String word, String meaning) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyHelper.WORD, word);
        cv.put(MyHelper.MEANING, meaning);
        long res = db.insert(MyHelper.TABLE_NAME1, null, cv);
        return res;
    }

    public String getAllData() {
        String[] columns = {MyHelper.FAV_ID, MyHelper.WORD, MyHelper.MEANING};

        StringBuffer buffer = new StringBuffer();

        db = helper.getWritableDatabase();
        Cursor c = db.query(MyHelper.TABLE_NAME1, columns, null, null, null, null,
                null);

        while (c.moveToNext()) {
            col1 = c.getColumnIndex(MyHelper.FAV_ID);
            col2 = c.getColumnIndex(MyHelper.WORD);
            col3 = c.getColumnIndex(MyHelper.MEANING);

            int favid = c.getInt(col1);
            String word = c.getString(col2);
            String meaning = c.getString(col3);

            buffer.append(favid + " " + word + " " + meaning + "\n");
        }

        return buffer.toString();

    }


    public List<WordMeaning> getAll() {
        String[] columns = {MyHelper.FAV_ID, MyHelper.WORD, MyHelper.MEANING};

        db = helper.getWritableDatabase();
        Cursor c = db.query(MyHelper.TABLE_NAME1, columns, null, null, null, null,
                null);

        while (c.moveToNext()) {
            col2 = c.getColumnIndex(MyHelper.WORD);
            col3 = c.getColumnIndex(MyHelper.MEANING);

            WordMeaning current = new WordMeaning();
            current.word = c.getString(col2);
            current.meaning = c.getString(col3);

            data.add(current);
        }

        return data;
    }

    public int delete(String word) {

        db = helper.getWritableDatabase();

        String[] whereArgs = {word};

        return db.delete(MyHelper.TABLE_NAME1, MyHelper.WORD + "=?", whereArgs);

    }

    public int update(int roll, String name) {
        ContentValues cv = new ContentValues();
        cv.put(MyHelper.WORD, name);

        db = helper.getWritableDatabase();
        String where = MyHelper.FAV_ID + "=" + roll;
        int r = db.update(MyHelper.TABLE_NAME1, cv, where, null);

        return r;
    }


    public int checkEasyRandom(String qNo) {
        db = helper.getWritableDatabase();
        String[] columns = {MyHelper.RANDE_ROW_ID, MyHelper.RANDE_TEST_NO};
        String[] selectionArgs = {qNo};

        Cursor cursor = db.query(MyHelper.RANDOMIZE_EASY, columns, MyHelper.RANDE_TEST_NO + "=?", selectionArgs, null,
                null, null);
        if (cursor.getCount() == 0) {
            return 0; //success
        } else {
            return 1; // keep trying
        }
    }

    public long checkTotalEntries(int lev) {
        db = helper.getWritableDatabase();

        if (lev == 0) {
            return DatabaseUtils.queryNumEntries(db, MyHelper.RANDOMIZE_EASY);
        } else if (lev == 1) {
            return DatabaseUtils.queryNumEntries(db, MyHelper.RANDOMIZE_MED);
        } else {
            return DatabaseUtils.queryNumEntries(db, MyHelper.RANDOMIZE_TOUGH);
        }

    }

    public long insertE(int questionNumber) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyHelper.RANDE_TEST_NO, questionNumber);

        long result = db.insert(MyHelper.RANDOMIZE_EASY, null, cv);
        return result;
    }


    public int checkMedRandom(String qNo) {
        db = helper.getWritableDatabase();
        String[] columns = {MyHelper.RANDM_ROW_ID};

        String[] selectionArgs = {qNo};
//
        Cursor cursor = db.query(MyHelper.RANDOMIZE_MED, columns, MyHelper.RANDM_TEST_NO + "=?", selectionArgs, null,
                null, null);

        if (cursor.getCount() == 0) {
            return 0; //success
        } else {
            return 1; // keep trying

        }
    }

    public long insertM(int questionNumber) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyHelper.RANDM_TEST_NO, questionNumber);

        long result = db.insert(MyHelper.RANDOMIZE_MED, null, cv);
        return result;
    }


    public int checkToughRandom(String qNo) {
        db = helper.getWritableDatabase();
        String[] columns = {MyHelper.RANDT_ROW_ID};

        String[] selectionArgs = {qNo};

        Cursor cursor = db.query(MyHelper.RANDOMIZE_TOUGH, columns, MyHelper.RANDT_TEST_NO + "=?", selectionArgs, null,
                null, null);

        if (cursor.getCount() == 0) {
            return 0; //success
        } else {
            return cursor.getCount(); // keep trying
        }
    }


    public long insertT(int questionNumber) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyHelper.RANDT_TEST_NO, questionNumber);

        long result = db.insert(MyHelper.RANDOMIZE_TOUGH, null, cv);
        return result;
    }


    //add revision questions
    public long insertRevisionQues(int level, String word, String meaning, String usage) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long resFromRevision;

        if (level == 0) {
            cv.put(MyHelper.EASY_WORD, word);
            cv.put(MyHelper.EASY_MEANING, meaning);
            cv.put(MyHelper.EASY_USAGE, usage);
            resFromRevision = db.insert(MyHelper.EASY_REVISION, null, cv);
            return resFromRevision;

        } else if (level == 1) {
            cv.put(MyHelper.MEDIUM_WORD, word);
            cv.put(MyHelper.MEDIUM_MEANING, meaning);
            cv.put(MyHelper.MEDIUM_USAGE, usage);
            resFromRevision = db.insert(MyHelper.MEDIUM_REVISION, null, cv);
            return resFromRevision;

        } else if (level == 2) {
            cv.put(MyHelper.TOUGH_WORD, word);
            cv.put(MyHelper.TOUGH_MEANING, meaning);
            cv.put(MyHelper.TOUGH_USAGE, usage);
            resFromRevision = db.insert(MyHelper.TOUGH_REVISION, null, cv);
            return resFromRevision;

        }

        return 0;
    }

    //get revision questions forward recyclerView

    public List<WordMeaning> getRevisionForRecyclerView(int r_level) {

        db = helper.getWritableDatabase();

        if (r_level == 0) {
            String[] columns = {MyHelper.EASY_WORD, MyHelper.EASY_MEANING};

            cursor = db.query(MyHelper.EASY_REVISION, columns, null, null, null,
                    null, null);

            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    col1 = cursor.getColumnIndex(MyHelper.EASY_WORD);
                    col2 = cursor.getColumnIndex(MyHelper.EASY_MEANING);

                    word = cursor.getString(col1);
                    meaning = cursor.getString(col2);

                    WordMeaning wm = new WordMeaning();
                    wm.word = word;
                    wm.meaning = meaning;

                    data.add(wm);

                }

                return data;
            } else
                return null;
        } else if (r_level == 1) {
            String[] columns = {MyHelper.MEDIUM_WORD, MyHelper.MEDIUM_MEANING};
            cursor = db.query(MyHelper.MEDIUM_REVISION, columns, null, null, null,
                    null, null);

            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    col1 = cursor.getColumnIndex(MyHelper.MEDIUM_WORD);
                    col2 = cursor.getColumnIndex(MyHelper.MEDIUM_MEANING);

                    word = cursor.getString(col1);
                    meaning = cursor.getString(col2);

                    WordMeaning wm = new WordMeaning();
                    wm.word = word;
                    wm.meaning = meaning;

                    data.add(wm);
                }


                return data;
            } else
                return null;

        } else if (r_level == 2) {
            String[] columns = {MyHelper.TOUGH_WORD, MyHelper.TOUGH_MEANING};
            cursor = db.query(MyHelper.TOUGH_REVISION, columns, null, null, null,
                    null, null);

            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    col1 = cursor.getColumnIndex(MyHelper.TOUGH_WORD);
                    col2 = cursor.getColumnIndex(MyHelper.TOUGH_MEANING);

                    word = cursor.getString(col1);
                    meaning = cursor.getString(col2);

                    WordMeaning wm = new WordMeaning();
                    wm.word = word;
                    wm.meaning = meaning;

                    data.add(wm);

                }

                return data;
            } else
                return null;
        }
        return null;
    }


    //get words forward revision
    public Bundle getRevisionQues(int r_level) {

        int arrayIndex = 0;
        Bundle revisionBundle = new Bundle();
        db = helper.getWritableDatabase();

        if (r_level == 0) {
            String[] columns = {MyHelper.EASY_WORD, MyHelper.EASY_MEANING, MyHelper.EASY_USAGE};

            cursor = db.query(MyHelper.EASY_REVISION, columns, null, null, null,
                    null, null);

            if (cursor.getCount() > 0) {
                String words[] = new String[cursor.getCount()];
                String meanings[] = new String[cursor.getCount()];
                String usages[] = new String[cursor.getCount()];

                while (cursor.moveToNext()) {

                    col1 = cursor.getColumnIndex(MyHelper.EASY_WORD);
                    col2 = cursor.getColumnIndex(MyHelper.EASY_MEANING);
                    col3 = cursor.getColumnIndex(MyHelper.EASY_USAGE);

                    word = cursor.getString(col1);
                    meaning = cursor.getString(col2);
                    usage = cursor.getString(col3);

                    words[arrayIndex] = word;
                    meanings[arrayIndex] = meaning;
                    usages[arrayIndex] = usage;

                    arrayIndex++;
                }

                revisionBundle.putStringArray("wordsR", words);
                revisionBundle.putStringArray("meaningsR", meanings);
                revisionBundle.putStringArray("usagesR", usages);
                revisionBundle.putInt("arraySize", cursor.getCount());

                return revisionBundle;
            } else
                return null;
        } else if (r_level == 1) {
            String[] columns = {MyHelper.MEDIUM_WORD, MyHelper.MEDIUM_MEANING, MyHelper.MEDIUM_USAGE};
            cursor = db.query(MyHelper.MEDIUM_REVISION, columns, null, null, null,
                    null, null);

            if (cursor.getCount() > 0) {
                String words[] = new String[cursor.getCount()];
                String meanings[] = new String[cursor.getCount()];
                String usages[] = new String[cursor.getCount()];


                while (cursor.moveToNext()) {

                    col1 = cursor.getColumnIndex(MyHelper.MEDIUM_WORD);
                    col2 = cursor.getColumnIndex(MyHelper.MEDIUM_MEANING);
                    col3 = cursor.getColumnIndex(MyHelper.MEDIUM_USAGE);

                    word = cursor.getString(col1);
                    meaning = cursor.getString(col2);
                    usage = cursor.getString(col3);

                    words[arrayIndex] = word;
                    meanings[arrayIndex] = meaning;
                    usages[arrayIndex] = usage;

                    arrayIndex++;

                }


                revisionBundle.putStringArray("wordsR", words);
                revisionBundle.putStringArray("meaningsR", meanings);
                revisionBundle.putStringArray("usagesR", usages);
                revisionBundle.putInt("arraySize", cursor.getCount());

                return revisionBundle;
            } else
                return null;

        } else if (r_level == 2) {
            String[] columns = {MyHelper.TOUGH_WORD, MyHelper.TOUGH_MEANING, MyHelper.TOUGH_USAGE};
            cursor = db.query(MyHelper.TOUGH_REVISION, columns, null, null, null,
                    null, null);

            if (cursor.getCount() > 0) {
                String words[] = new String[cursor.getCount()];
                String meanings[] = new String[cursor.getCount()];
                String usages[] = new String[cursor.getCount()];


                while (cursor.moveToNext()) {

                    col1 = cursor.getColumnIndex(MyHelper.TOUGH_WORD);
                    col2 = cursor.getColumnIndex(MyHelper.TOUGH_MEANING);
                    col3 = cursor.getColumnIndex(MyHelper.TOUGH_USAGE);

                    word = cursor.getString(col1);
                    meaning = cursor.getString(col2);
                    usage = cursor.getString(col3);

                    words[arrayIndex] = word;
                    meanings[arrayIndex] = meaning;
                    usages[arrayIndex] = usage;

                    arrayIndex++;
                }


                revisionBundle.putStringArray("wordsR", words);
                revisionBundle.putStringArray("meaningsR", meanings);
                revisionBundle.putStringArray("usagesR", usages);
                revisionBundle.putInt("arraySize", cursor.getCount());

                return revisionBundle;
            } else
                return null;
        }
        return null;
    }


    public int getToughRevision() {
        db = helper.getWritableDatabase();

        String[] columns = {MyHelper.TOUGH_WORD, MyHelper.TOUGH_MEANING};
        cursor = db.query(MyHelper.TOUGH_REVISION, columns, null, null, null,
                null, null);

        int i = cursor.getCount();

        return i;
    }


}

class MyHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "favoritewords";
    static final int DATABASE_VERSION = 8;

    //Easy Revision Table

    static final String EASY_REVISION = "easy_revision";
    static final String EASY_RID = "_id";
    static final String EASY_WORD = "easy_word";
    static final String EASY_MEANING = "easy_meaning";
    static final String EASY_USAGE = "easy_usage";
    static final String CREATE_EASY_REVISION = "create table " + EASY_REVISION + "(" + EASY_RID
            + " Integer PRIMARY KEY AUTOINCREMENT," + EASY_WORD + " VARCHAR(255) NOT NULL, " + EASY_MEANING + " VARCHAR(255) NOT NULL, " + EASY_USAGE + " VARCHAR(255) NOT NULL);";
    static final String DROP_EASY_REVISION = "DROP TABLE IF EXISTS " + EASY_REVISION;

    //Medium Revision Table

    static final String MEDIUM_REVISION = "medium_revision";
    static final String MEDIUM_RID = "_id";
    static final String MEDIUM_WORD = "med_word";
    static final String MEDIUM_MEANING = "med_meaning";
    static final String MEDIUM_USAGE = "med_usage";
    static final String CREATE_MEDIUM_REVISION = "create table " + MEDIUM_REVISION + "(" + MEDIUM_RID
            + " Integer PRIMARY KEY AUTOINCREMENT," + MEDIUM_WORD + " VARCHAR(255) NOT NULL, " + MEDIUM_MEANING + " VARCHAR(255) NOT NULL, " + MEDIUM_USAGE + " VARCHAR(255) NOT NULL);";
    static final String DROP_MEDIUM_REVISION = "DROP TABLE IF EXISTS " + MEDIUM_REVISION;

    //Tough Revision Table

    static final String TOUGH_REVISION = "tough_revision";
    static final String TOUGH_RID = "_id";
    static final String TOUGH_WORD = "tough_word";
    static final String TOUGH_MEANING = "tough_meaning";
    static final String TOUGH_USAGE = "tough_usage";
    static final String CREATE_TOUGH_REVISION = "create table " + TOUGH_REVISION + "(" + TOUGH_RID
            + " Integer PRIMARY KEY AUTOINCREMENT," + TOUGH_WORD + " VARCHAR(255) NOT NULL, " + TOUGH_MEANING + " VARCHAR(255) NOT NULL, " + TOUGH_USAGE + " VARCHAR(255) NOT NULL);";
    static final String DROP_TOUGH_REVISION = "DROP TABLE IF EXISTS " + TOUGH_REVISION;

    //favorites table
    static final String TABLE_NAME1 = "favorites";
    static final String FAV_ID = "favid";
    static final String WORD = "word";
    static final String MEANING = "meaning";
    static final String CREATE_FAVORITES = "create table " + TABLE_NAME1 + "(" + FAV_ID
            + " Integer PRIMARY KEY AUTOINCREMENT," + WORD + " VARCHAR(255) NOT NULL, " + MEANING + " VARCHAR(300) NOT NULL );";
    static final String DROP_TABLE1 = "DROP TABLE IF EXISTS " + TABLE_NAME1;
    Context context;

    //values forward graph table
    static final String TABLE_NAME2 = "valuesforgraph";
    static final String ROWID = "rowid";
    static final String MARKS = "marks";
    static final String LEVEL = "level";
    static final String TEST_DATE = "testdate";
    static final String CREATE_VALUESFORGRAPHS = "create table " + TABLE_NAME2 + "(" + ROWID
            + " Integer PRIMARY KEY AUTOINCREMENT," + MARKS + " Integer NOT NULL, " + LEVEL + " VARCHAR(5) NOT NULL, " + TEST_DATE + " DATETIME NOT NULL );";
    static final String DROP_TABLE2 = "DROP TABLE IF EXISTS " + TABLE_NAME2;


    //Randomize EASY table
    static final String RANDOMIZE_EASY = "RandomizeE";
    static final String RANDE_ROW_ID = "_id";
    static final String RANDE_TEST_NO = "randomTestNo";
    static final String CREATE_RANDOMIZE_EASY = "create table " + RANDOMIZE_EASY + "(" + RANDE_ROW_ID
            + " Integer PRIMARY KEY AUTOINCREMENT," + RANDE_TEST_NO + " Integer NOT NULL );";
    static final String DROP_TABLE_RAND_E = "DROP TABLE IF EXISTS " + RANDOMIZE_EASY;

    //Randomize MEDIUM table
    static final String RANDOMIZE_MED = "RandomizeM";
    static final String RANDM_ROW_ID = "_id";
    static final String RANDM_TEST_NO = "randomTestNo";
    static final String CREATE_RANDOMIZE_MED = "create table " + RANDOMIZE_MED + "(" + RANDM_ROW_ID
            + " Integer PRIMARY KEY AUTOINCREMENT," + RANDM_TEST_NO + " Integer NOT NULL );";
    static final String DROP_TABLE_RAND_M = "DROP TABLE IF EXISTS " + RANDOMIZE_MED;


    //Randomize TOUGH table
    static final String RANDOMIZE_TOUGH = "RandomizeT";
    static final String RANDT_ROW_ID = "_id";
    static final String RANDT_TEST_NO = "randomTestNo";
    static final String CREATE_RANDOMIZE_TOUGH = "create table " + RANDOMIZE_TOUGH + "(" + RANDT_ROW_ID
            + " Integer PRIMARY KEY AUTOINCREMENT," + RANDT_TEST_NO + " Integer NOT NULL );";
    static final String DROP_TABLE_RAND_T = "DROP TABLE IF EXISTS " + RANDOMIZE_TOUGH;


    public MyHelper(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        try {
            db.execSQL(CREATE_FAVORITES);
            db.execSQL(CREATE_VALUESFORGRAPHS);
            db.execSQL(CREATE_RANDOMIZE_EASY);
            db.execSQL(CREATE_RANDOMIZE_MED);
            db.execSQL(CREATE_RANDOMIZE_TOUGH);
            db.execSQL(CREATE_EASY_REVISION);
            db.execSQL(CREATE_MEDIUM_REVISION);
            db.execSQL(CREATE_TOUGH_REVISION);
            //Message.message(context, "ALL TABLES created");
        } catch (SQLException e) {
            // Message.message(context, " " + e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        try {
            db.execSQL(DROP_TABLE1);
            db.execSQL(DROP_TABLE2);
            db.execSQL(DROP_TABLE_RAND_E);
            db.execSQL(DROP_TABLE_RAND_M);
            db.execSQL(DROP_TABLE_RAND_T);
            db.execSQL(DROP_EASY_REVISION);
            db.execSQL(DROP_MEDIUM_REVISION);
            db.execSQL(DROP_TOUGH_REVISION);
            onCreate(db);
        } catch (Exception e) {
            //Message.message(context, " " + e);
        }

    }
}