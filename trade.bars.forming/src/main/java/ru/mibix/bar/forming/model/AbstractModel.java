package ru.mibix.bar.forming.model;

/**
 * Created by ser on 05.01.2016.
 */
public abstract class AbstractModel {

    private static final long serialVersionUID = 1L;

    private int id;

    public AbstractModel() { }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}