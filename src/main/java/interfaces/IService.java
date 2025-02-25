package interfaces;

import entities.DocumentAdministratif;

import java.util.List;

public interface IService<T> {
    public void addEntity(T t) ;

    public void deleteEntity(T t) ;
    public void updateEntity(T t) ;

    public List<T> getAllData() ;


    public boolean emailExists(String email) ;
}
