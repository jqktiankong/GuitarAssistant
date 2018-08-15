package com.example.jqk.guitarassistant.music;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Song {
    @Id(autoincrement = true)
    private long id;
    @Property(nameInDb = "SONGNAME")
    private String SongName;
    @Transient
    private String initials;
    @Transient
    private String name;
    @Transient
    private int star;

    @Generated(hash = 1573032205)
    public Song(long id, String SongName) {
        this.id = id;
        this.SongName = SongName;
    }

    @Generated(hash = 87031450)
    public Song() {
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
