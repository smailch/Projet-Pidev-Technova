package edu.pidev3a8.interfaces;

import java.util.List;

public interface IService<T>{
    public void addEntity(T t);
    public void updateEntity(T t);
    public void deleteEntity(T t);
    public List<T> getAllData();

}
