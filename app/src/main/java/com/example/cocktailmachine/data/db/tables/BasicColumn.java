package com.example.cocktailmachine.data.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.cocktailmachine.data.db.elements.DataBaseElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BasicColumn<T extends DataBaseElement> implements BaseColumns {
    public abstract String getName();
    public abstract List<String> getColumns();
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
    public String createTable(){
        String tablename = this.getName();
        HashMap<String, String> columns = this.getColumnsAndTypes();
        final StringBuilder res =  new StringBuilder( "CREATE TABLE "+tablename+"(");
        columns.forEach((columnname, datatype) -> {
            res.append(columnname);
            res.append(" ");
            res.append(datatype);
            res.append(",");
        });
        res.deleteCharAt(res.length()-1);
        res.append(");");
        return res.toString();
    }

    public String deleteTable(){
        return "DROP TABLE IF EXISTS "+this.getName()+";";
    }

    //HELPER
    private List<T> cursorToList(Cursor cursor){
        cursor.moveToFirst();
        List<T> res = new ArrayList<>();
        res.add(makeElement(cursor));
        while(cursor.moveToNext()){
            res.add(makeElement(cursor));
        }
        cursor.close();
        return res;
    }

    private String[] makeSelectionList(String column_name,
                                           List<? extends Object> ll)
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

        return ll.stream().map(Object::toString).toArray(String[] ::new);
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
    public T getElement(SQLiteDatabase db,
                        Long id){
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
                BasicColumn._ID+" = " + id.toString(),
                null,
                null,
                null,
                null,
                "1");
        cursor.moveToFirst();
        T res = makeElement(cursor);
        cursor.close();
        return res;
    }

    public List<T> getElements(SQLiteDatabase db,
                               List<Long> ids){
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
                "id in "+ids.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ", "(", ")")),
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }

    public List<T> getAllElements(SQLiteDatabase db){
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
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
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" LIKE ?",
                new String[]{likeThis},
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }

    private List<T> getElementsSelectionOperator(SQLiteDatabase db,
                                        String column_name,
                                        List<Object> ll,
                                        String selectionOperator)
            throws NoSuchColumnException {
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" "+selectionOperator+" ?",
                makeSelectionList(column_name, ll),
                null,
                null,
                null);
        return this.cursorToList(cursor);
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

    public List<T> getElementsNotIn(SQLiteDatabase db,
                                    String column_name,
                                    List<Object> ll)
            throws NoSuchColumnException {
        return getElementsSelectionOperator(db,
                column_name,
                ll,
                "NOT IN");
    }

    public List<T> getElementsWith(SQLiteDatabase db,
                                   String selection){
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
                selection,
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
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
                column_name+" = "+equalsThis,
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }


    //DELETE
    public void deleteElement(SQLiteDatabase db,
                              T element){
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
        db.delete(getName(),
                column_name+" "+selectionOperator+" ?",
                makeSelectionList(column_name, ll));
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
                makeSelectionList(where_column, ll));
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
