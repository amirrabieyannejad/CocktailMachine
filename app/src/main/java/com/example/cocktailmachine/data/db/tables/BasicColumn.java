package com.example.cocktailmachine.data.db.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.elements.SQLDataBaseElement;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @created Fr. 23.Jun 2023 - 12:32
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public abstract class BasicColumn<T extends SQLDataBaseElement> implements BaseColumns {
    static final String TAG = "BasicColumn";

    /**
     * gives name of table
     * @author Johanna Reidt
     * @return name of table
     */
    public abstract String getName();

    /**
     * gives names of columns in table
     * @author Johanna Reidt
     * @return names of columns in table as list of strings
     */
    public abstract List<String> getColumns();

    /**
     * gives types of columns in table
     * @author Johanna Reidt
     * @return types of columns in table as list of strings
     */
    public abstract List<String> getColumnTypes();
    public HashMap<String, String> getColumnsAndTypes(){
        HashMap<String, String> ct = new HashMap<>();
        List<String> c = getColumns();
        List<String> t = getColumnTypes();
        for (int i = 0; i < c.size(); i++) {
            ct.put(c.get(i), t.get(i));
        }
        return ct;
    }

    //TABLE BASICS
    public String createTableCmd(){
        String tablename = this.getName();
        HashMap<String, String> columns = this.getColumnsAndTypes();
        final StringBuilder res =  new StringBuilder( "CREATE TABLE "+tablename+"(");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            columns.forEach((columnname, datatype) -> {
                res.append(columnname);
                res.append(" ");
                res.append(datatype);
                res.append(",");
            });
        }else{
            res.append(Helper.stringAppender(columns));
        }
        res.deleteCharAt(res.length()-1);
        res.append(");");
        return res.toString();
    }

    /**
     * creates table
     * @author Johanna Reidt
     * @param db
     */
    public void createTable(SQLiteDatabase db){
        db.execSQL(createTableCmd());
    }

    /**
     * gives delete cmd for table
     * @author Johanna Reidt
     * @return delete cmd for table as string
     */
    public String deleteTableCmd(){
        return "DROP TABLE IF EXISTS "+this.getName()+";";
    }

    /**
     * deletes table
     * @author Johanna Reidt
     * @param db
     */
    public void deleteTable(SQLiteDatabase db){
        db.execSQL(deleteTableCmd());
    }

    //HELPER

    /**
     * reads with given crusor each given rows to a T element
     * @author Johanna Reidt
     * @param cursor
     * @return T element list
     */
    List<T> cursorToList(Cursor cursor){
       // Log.v(TAG, "cursorToList");
        List<T> res = new ArrayList<>();
        if(cursor.moveToFirst()) {
            res.add(makeElement(cursor));
            while (cursor.moveToNext()) {
                res.add(makeElement(cursor));
            }
        }
        cursor.close();
       // Log.v(TAG, "cursorToList : "+res);
        return res;
    }

    /**
     * reads with given crusor each given rows to a T element
     * @author Johanna Reidt
     * @param cursor
     * @return T element list
     */
    private List<Long> cursorToIDList(Cursor cursor){
       // Log.v(TAG, "cursorToList");
        List<Long> res = new ArrayList<>();
        int id_index = cursor.getColumnIndexOrThrow(_ID);
        if(cursor.moveToFirst()) {
            res.add(cursor.getLong(id_index));
            while (cursor.moveToNext()) {
                res.add(cursor.getLong(id_index));
            }
        }
        cursor.close();
       // Log.v(TAG, "cursorToList : "+res);
        return res;
    }

    String makeSelectionList(String column_name,
                                       List<? extends Object> ll)
            throws NoSuchColumnException {


        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        //String ttype = getColumnsAndTypes().get(column_name);

        StringBuilder builder = new StringBuilder();
        builder.append( "(");
        builder.append(ll.get(0));
        for(int i=1;i<ll.size();i++){
            builder.append(", ");
            builder.append(ll.get(i));
        }
        builder.append(")");
        return builder.toString();
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return ll.stream().map(Object::toString).toArray(String[] ::new);
        }else{
            return Helper.objToString(ll);
        }

         */
    }

    private String[] makeSelectionList(String column_name,
                                       String ll)
            throws NoSuchColumnException {
        /**
         if(!getColumns().contains(column_name)){
         throw new NoSuchColumnException(getName(), column_name);
         }
         String ttype = getColumnsAndTypes().get(column_name);
         String selection_list = "";
         if(ttype != null && ttype.toLowerCase().contains("text")){
         selection_list = ll.stream()
         .map(e-> "'"+e.toString()+"'")
         .collect(Collectors.joining(", ", "(", ")"));
         }else{
         selection_list = ll.stream()
         .map(Object::toString)
         .collect(Collectors.joining(", ", "(", ")"));
         }
         return selection_list;
         */

        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        String ttype = getColumnsAndTypes().get(column_name);

        return new String[]{ll};
    }

    //GET

    /**
     * get T element from table with given id
     * @author Johanna Reidt
     * @param db
     * @param id
     * @return T element with given id or null
     */
    @Nullable
    public T getElement(SQLiteDatabase db,
                        Long id){
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                BasicColumn._ID+" = " + id.toString(),
                null,
                null,
                null,
                null,
                "1",
                null);
        if(cursor.moveToFirst()) {
            T res = makeElement(cursor);
            cursor.close();
            return res;
        }
        return null;
    }


    public List<Long> getIDs(SQLiteDatabase db, List<Long> ids){
        try {
            return getIDsIn(db, _ID, new ArrayList<>(ids));
        } catch (NoSuchColumnException e) {
            return new ArrayList<>();
        }
        /*
        Cursor cursor = db.query(true,
                this.getName(),
                new String[]{_ID},
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        /*
        List<Long> ids = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                ids.add(cursor.getLong(cursor.getColumnIndexOrThrow(_ID)));
                while (cursor.moveToNext()) {
                    ids.add(cursor.getLong(cursor.getColumnIndexOrThrow(_ID)));
                }
            }
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getIDs", e);
            Log.getStackTraceString(e);
        }

         */
        /*
        List<Long> ids = cursorToIDList(cursor);
        cursor.close();
        return ids;

         */
    }

    public List<Long> getIDs(SQLiteDatabase db){
        Cursor cursor = db.query(true,
                this.getName(),
                new String[]{_ID},
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        /*
        List<Long> ids = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                ids.add(cursor.getLong(cursor.getColumnIndexOrThrow(_ID)));
                while (cursor.moveToNext()) {
                    ids.add(cursor.getLong(cursor.getColumnIndexOrThrow(_ID)));
                }
            }
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getIDs", e);
            Log.getStackTraceString(e);
        }

         */
        List<Long> ids = cursorToIDList(cursor);
        cursor.close();
        return ids;
    }

    public Iterator<T> getIterator(SQLiteDatabase db){

        return new Iterator<T>() {
            private final Iterator<Long> ids = BasicColumn.this.getIDs(db).listIterator();
            @Override
            public boolean hasNext() {
                return ids.hasNext();
            }

            @Override
            public T next() {
                return BasicColumn.this.getElement(db, ids.next());
            }
        };
    }




    /**
     * @author Johanna Reidt
     * @created Fr. 06.Okt 2023 - 15:39
     * @project CocktailMachine
     */
    public class DatabaseIterator implements Iterator<List<T>> {
        private static final String TAG = "DatabaseIterator";
        //private final BasicColumn<T> table;
        private final List<Long> ids;
        private int position = 0;
        private final int chunkSize;
        private final SQLiteDatabase readableDB;


        public DatabaseIterator(SQLiteDatabase readableDB){
            this.ids = BasicColumn.this.getIDs(readableDB);
            this.chunkSize = 30;
            this.readableDB = readableDB;
            //this.table = loadTable();
        }

        public DatabaseIterator(SQLiteDatabase readableDB,
                                int chunkSize){
            this.ids = BasicColumn.this.getIDs(readableDB);
            this.chunkSize = chunkSize;
            this.readableDB = readableDB;
            //this.table = loadTable();
        }

        @Override
        public boolean hasNext() {

            return this.position < this.ids.size();
        }

        @Override
        public List<T> next() {
            int oldPosition = this.position;
            this.position = this.position + this.chunkSize;
            if(this.position > this.ids.size()){
                this.position = this.ids.size();
            }
            List<Long> temp = this.ids.subList(
                    oldPosition,
                    this.position);
            return BasicColumn.this.getElements(this.readableDB, temp);
        }

        public void exclude(String columnName,
                            List<Object> exclude){
            try {
                List<Long> del = BasicColumn.this.getIDsIn(
                        readableDB,
                        columnName,
                        exclude);
                Long oldID = this.ids.get(position);
                this.ids.removeAll(del);
                this.position = this.ids.indexOf(oldID);
            } catch (NoSuchColumnException e) {
                Log.e(TAG, "exclude", e);
            }
        }

        public void include(String columnName,
                            List<Object> include){
            try {
                List<Long> del = BasicColumn.this.getIDsNotIn(
                        readableDB,
                        columnName,
                        include);
                Long oldID = this.ids.get(position);
                this.ids.removeAll(del);
                this.position = this.ids.indexOf(oldID);
            } catch (NoSuchColumnException e) {
                Log.e(TAG, "include", e);
            }
        }

        public void exclude(String columnName,
                            String needle){
            try {
                List<Long> del = BasicColumn.this.getIDsLike(
                        readableDB,
                        columnName,
                        needle);
                Long oldID = this.ids.get(position);
                this.ids.removeAll(del);
                this.position = this.ids.indexOf(oldID);
            } catch (NoSuchColumnException e) {
                Log.e(TAG, "include", e);
            }
        }

        public void include(String columnName,
                            String needle){
            try {
                List<Long> del = BasicColumn.this.getIDsNotLike(
                        readableDB,
                        columnName,
                        needle);
                Long oldID = this.ids.get(position);
                this.ids.removeAll(del);
                this.position = this.ids.indexOf(oldID);
            } catch (NoSuchColumnException e) {
                Log.e(TAG, "include", e);
            }
        }
    }




    public DatabaseIterator getChunkIterator(SQLiteDatabase db, int n){
        /*
        return new Iterator<List<T>>() {
            private final List<Long> ids = BasicColumn.this.getIDs(db);
            private int position = 0;
            @Override
            public boolean hasNext() {
                return position<ids.size();
            }

            @Override
            public List<T> next() {
                int oldPosition = position;
                position = position + n;
                if(position>ids.size()){
                    position = ids.size();
                }
                List<Long> temp = ids.subList(oldPosition, position);
                return BasicColumn.this.getElements(db, temp);
            }
        };

         */
        return new DatabaseIterator(db, n);
    }

    /**
     * get elements with given ids
     * @author Johanna Reidt
     * @param db
     * @param ids
     * @return list of elements with given ids
     */
    public List<T> getElements(SQLiteDatabase db,
                               List<Long> ids){
        String selection = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            selection = _ID+" in "+ids.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "(", ")"));
        }else {
            selection = _ID+" in "+Helper.objToString(ids, "(", ", ", ")");
        }

        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                selection,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }

    /**
     * get all elements in table
     * @author Johanna Reidt
     * @param db
     * @return all elements in table as list
     */
    public List<T> getAllElements(SQLiteDatabase db){
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }

    public List<T> getElementsLike(SQLiteDatabase db,
                                   String column_name,
                                   String likeThis)
            throws NoSuchColumnException {
        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" LIKE ?",
                new String[]{likeThis+"%"},
                null,
                null,
                null, null, null);
        return this.cursorToList(cursor);
    }

    public List<Long> getIDsLike(SQLiteDatabase db,
                                   String column_name,
                                   String likeThis)
            throws NoSuchColumnException {
        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" LIKE ?",
                new String[]{likeThis+"%"},
                null,
                null,
                null, null, null);
        return this.cursorToIDList(cursor);
    }

    public List<Long> getIDsNotLike(SQLiteDatabase db,
                                 String column_name,
                                 String likeThis)
            throws NoSuchColumnException {
        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" NOT LIKE ?",
                new String[]{likeThis+"%"},
                null,
                null,
                null,
                null,
                null
        );
        return this.cursorToIDList(cursor);
    }

    private List<T> getElementsSelectionOperator(SQLiteDatabase db,
                                                 String column_name,
                                                 List<Object> ll,
                                                 String selectionOperator)
            throws NoSuchColumnException {
       // Log.v(TAG, "getElementsSelectionOperator");

        if(ll.isEmpty()){
           // Log.v(TAG, "getElementsSelectionOperator ll is empty");
            return new ArrayList<>();
        }


       // Log.v(TAG, "getElementsSelectionOperator ll is not empty");
       // Log.v(TAG, "getElementsSelectionOperator ll: "+ ll);

        String selection = column_name+" "+selectionOperator+"  ";
       // Log.v(TAG, "getElementsSelectionOperator selection: "+selection);
        selection += makeSelectionList(column_name, ll);
       // Log.v(TAG, "getElementsSelectionOperator selection with List: "+ selection);


        Cursor cursor = db.query(true,

                this.getName(),
                getColumns().toArray(new String[0]),
                selection,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }

    private List<Long> getIDsSelectionOperator(SQLiteDatabase db,
                                                 String column_name,
                                                 List<Object> ll,
                                                 String selectionOperator)
            throws NoSuchColumnException {
       // Log.v(TAG, "getElementsSelectionOperator");

        if(ll.isEmpty()){
           // Log.v(TAG, "getElementsSelectionOperator ll is empty");
            return new ArrayList<>();
        }


        String selection = column_name+" "+selectionOperator+" ?";
       // Log.v(TAG, "getElementsSelectionOperator selection: "+selection);
        selection += makeSelectionList(column_name, ll);
       // Log.v(TAG, "getElementsSelectionOperator selection with List: "+ selection);


        Cursor cursor = db.query(true,

                this.getName(),
                getColumns().toArray(new String[0]),
                selection,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToIDList(cursor);
    }

    public List<T> getElementsIn(SQLiteDatabase db,
                                 String column_name,
                                 List<Object> ll)
            throws NoSuchColumnException {
        return getElementsSelectionOperator(db,
                column_name,
                ll,
                "IN");
    }

    public List<Long> getIDsIn(SQLiteDatabase db,
                                 String column_name,
                                 List<Object> ll)
            throws NoSuchColumnException {
        return getIDsSelectionOperator(db,
                column_name,
                ll,
                "IN");
    }

    public List<T> getElementsNotIn(SQLiteDatabase db,
                                    String column_name,
                                    List<Object> ll)
            throws NoSuchColumnException {
        return getElementsSelectionOperator(db,
                column_name,
                ll,
                "NOT IN");
    }

    public List<Long> getIDsNotIn(SQLiteDatabase db,
                                    String column_name,
                                    List<Object> ll)
            throws NoSuchColumnException {
        return getIDsSelectionOperator(db,
                column_name,
                ll,
                "NOT IN");
    }

    public List<T> getElementsWith(SQLiteDatabase db,
                                   String selection){
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                selection,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }

    public List<T> getElementsWith(SQLiteDatabase db,
                                   String column_name,
                                   String equalsThis)
            throws NoSuchColumnException {
        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" = "+equalsThis,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }

    public List<Long> getIDsWith(SQLiteDatabase db,
                                   String column_name,
                                   String equalsThis)
            throws NoSuchColumnException {
        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" = "+equalsThis,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToIDList(cursor);
    }


    //DELETE
    public void deleteElement(SQLiteDatabase db,
                              T element){
        if(element == null){
           // Log.v(TAG, "deleteElement: elm null");
            return;
        }
        deleteElement(db, element.getID());
    }

    public void deleteElement(SQLiteDatabase db,
                              long id){
        db.delete(getName(), this._ID+" = ?", new String[]{Long.toString(id)});
    }

    public void deleteElementsSelectionOperators(SQLiteDatabase db,
                                                 String column_name,
                                                 List<? extends Object> ll,
                                                 String selectionOperator)
            throws NoSuchColumnException {
        db.delete(this.getName(),
                column_name+" "+selectionOperator+" ?",
                new String[]{makeSelectionList(column_name, ll)});
    }

    public void deleteElements(SQLiteDatabase db, List<Long> ids) throws NoSuchColumnException {
        deleteElementsSelectionOperators(db, this._ID, ids, "IN");
    }

    public void deleteElementsIn(SQLiteDatabase db,
                                 String column_name,
                                 List<? extends Object> ll)
            throws NoSuchColumnException {
        deleteElementsSelectionOperators(db, column_name, ll, "IN");
    }

    public void deleteElementsNotIn(SQLiteDatabase db,
                                    String column_name,
                                    List<? extends Object> ll)
            throws NoSuchColumnException {
        deleteElementsSelectionOperators(db, column_name, ll, "NOT IN");
    }

    public void deleteElementsWith(SQLiteDatabase db,
                                   String column_name,
                                   String equalsThis)
            throws NoSuchColumnException {
        if(!getColumns().contains(column_name)){
            throw new NoSuchColumnException(getName(), column_name);
        }
        db.delete(this.getName(),
                column_name+" = "+equalsThis,
                null);
    }

    //ADD

    public long addElement(SQLiteDatabase db,
                           T element){
        return db.insertOrThrow(getName(), null, makeContentValues(element));
    }

    public List<Long> addElements(SQLiteDatabase db,
                                  List<T> elements){
        List<Long> ids = new ArrayList<>();
        for(T element: elements) {
            ids.add(db.insertOrThrow(getName(), null, makeContentValues(element)));
            element.setID(ids.get(ids.size()-1));
        }
        return ids;
    }


    //UPDATE
    public void updateElement(SQLiteDatabase db,
                              T element){
        db.update(getName(),
                makeContentValues(element),
                this._ID+" = ?",
                new String[]{Long.toString(element.getID())});
    }

    public void updateColumnsValuesSelectionOperator(SQLiteDatabase db,
                                                     ContentValues cv,
                                                     String where_column,
                                                     List<? extends Object> ll,
                                                     String selectionOperator)
            throws NoSuchColumnException {

        for(String key: cv.keySet()) {
            if (!getColumns().contains(key)) {
                throw new NoSuchColumnException(getName(), key);
            }
        }
        db.update(getName(),
                cv,
                where_column+" "+selectionOperator+" ?",
                new String[]{makeSelectionList(where_column, ll)});
    }

    public void updateColumnsValues(SQLiteDatabase db,
                                    ContentValues cv,
                                    String where_column,
                                    String equals_value)
            throws NoSuchColumnException {
        for(String key: cv.keySet()) {
            if (!getColumns().contains(key)) {
                throw new NoSuchColumnException(getName(), key);
            }
        }
        db.update(getName(),
                cv,
                where_column+" = ?",
                makeSelectionList(where_column, equals_value));
    }

    public void updateColumnsValues(SQLiteDatabase db,
                                    ContentValues cv,
                                    long id) throws NoSuchColumnException {
        updateColumnsValues(db, cv, this._ID, Long.toString(id));
    }

    public void updateColumnToConstant(SQLiteDatabase db,
                                       ContentValues cv)
            throws NoSuchColumnException {
        for (String key : cv.keySet()) {
            if (!getColumns().contains(key)) {
                throw new NoSuchColumnException(getName(), key);
            }
        }
        db.update(getName(),
                cv, null, null);
    }




    //TO IMPLEMENT
    public abstract T makeElement(Cursor cursor);

    public abstract ContentValues makeContentValues(T element);

    public ContentValues makeContentValuesWithID(T element){
        ContentValues cv = makeContentValues(element);
        cv.put(_ID, element.getID());
        return cv;
    }


}
