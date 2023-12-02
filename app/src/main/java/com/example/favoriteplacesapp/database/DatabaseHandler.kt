package com.example.favoriteplacesapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.favoriteplacesapp.models.FavoritePlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
    {

        companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable"

        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

        override fun onCreate(db: SQLiteDatabase?) {
            val CREATE_FAVORITE_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_IMAGE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_DATE + " TEXT,"
                    + KEY_LOCATION + " TEXT,"
                    + KEY_LATITUDE + " TEXT,"
                    + KEY_LONGITUDE + " TEXT)")
            db?.execSQL(CREATE_FAVORITE_PLACE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
            onCreate(db)
        }


        fun addHappyPlace(favoritePlace: FavoritePlaceModel): Long {
            val db = this.writableDatabase


            val contentValues = ContentValues()
            contentValues.put(KEY_TITLE, favoritePlace.title)
            contentValues.put(KEY_IMAGE, favoritePlace.image)
            contentValues.put(
                KEY_DESCRIPTION,
                favoritePlace.description
            )
            contentValues.put(KEY_DATE, favoritePlace.date)
            contentValues.put(KEY_LOCATION, favoritePlace.location)
            contentValues.put(KEY_LATITUDE, favoritePlace.latitude)
            contentValues.put(KEY_LONGITUDE, favoritePlace.longitude)

            val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)

            db.close()
            return result
        }

        @SuppressLint("Range")
        fun getFavoritePlacesList(): ArrayList<FavoritePlaceModel>{
            val favoritePlaceList = ArrayList<FavoritePlaceModel>()
            val selectQuery = "SELECT * FROM $TABLE_HAPPY_PLACE"
            val db = this.readableDatabase
            try{

                val cursor: Cursor = db.rawQuery(selectQuery, null)
                if(cursor.moveToFirst()){
                    do{
                        val place = FavoritePlaceModel(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                            cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                        )
                        favoritePlaceList.add(place)
                    }while(cursor.moveToNext())
                }

                cursor.close()
            }catch (e: SQLiteException){
                db.execSQL(selectQuery)
                return ArrayList()
            }
            return favoritePlaceList
        }
    }
