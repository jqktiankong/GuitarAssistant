package com.example.jqk.guitarassistant.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jqk.guitarassistant.music.Song;
import com.greendaodemo.gen.DaoMaster;
import com.greendaodemo.gen.DaoSession;
import com.greendaodemo.gen.SongDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DBManager {
    private static final String dbName = "song_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper mOpenHelper;
    private Context mContext;

    public DBManager(Context context) {
        this.mContext = context;
        mOpenHelper = new DaoMaster.DevOpenHelper(mContext, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     *
     * @return
     */
    private SQLiteDatabase getReadableDatabase() {
        if (mOpenHelper == null) {
            mOpenHelper = new DaoMaster.DevOpenHelper(mContext, dbName, null);
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mOpenHelper == null) {
            mOpenHelper = new DaoMaster.DevOpenHelper(mContext, dbName, null);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db;
    }

    /**
     * 插入一条记录
     *
     * @param song
     */
    public void insertSong(Song song) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao SongDao = daoSession.getSongDao();
        long a = SongDao.insert(song);
        Log.d("123", "插入结果 = " + a);
    }

    /**
     * 插入用户集合
     *
     * @param songs
     */
    public void insertSongList(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao SongDao = daoSession.getSongDao();
        SongDao.insertInTx(songs);
    }

    /**
     * 删除一条记录
     *
     * @param song
     */
    public void deleteSong(Song song) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao SongDao = daoSession.getSongDao();
        SongDao.delete(song);
    }

    /**
     * 清空所有记录
     */
    public void clearSongs() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao SongDao = daoSession.getSongDao();
        SongDao.deleteAll();
    }

    /**
     * 批量删除
     *
     * @param list
     */
    public void deleteSongs(List<Song> list) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao SongDao = daoSession.getSongDao();
        SongDao.deleteInTx(list);
    }


    /**
     * 更新一条记录
     *
     * @param Song
     */
    public void updateSong(Song Song) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao SongDao = daoSession.getSongDao();
        SongDao.update(Song);
    }

    /**
     * 查询用户列表
     */
    public List<Song> querySongList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao SongDao = daoSession.getSongDao();
        QueryBuilder<Song> qb = SongDao.queryBuilder();
        List<Song> list = qb.list();
        return list;
    }

    /**
     * 根据条件查询用户列表
     */
    public List<Song> querySongList(String Song) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao songDao = daoSession.getSongDao();
        QueryBuilder<Song> qb = songDao.queryBuilder();
        qb.where(SongDao.Properties.SongName.eq(Song));
        List<Song> list = qb.list();
        return list;
    }

    public boolean querySongListResult(String Song) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SongDao songDao = daoSession.getSongDao();
        QueryBuilder<Song> qb = songDao.queryBuilder();
        qb.where(SongDao.Properties.SongName.eq(Song));
        List<Song> list = qb.list();

        if (list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
