package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {

    private static final String _SQL_TAREAS_X_PROYECTO = "SELECT "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata._ID+" as "+ProyectoDBMetadata.TablaTareasMetadata._ID+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.TAREA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD +
            ", "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD +" as "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE +
            ", "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" as "+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS+
            " FROM "+ProyectoDBMetadata.TABLA_PROYECTO + " "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+", "+
            ProyectoDBMetadata.TABLA_USUARIOS + " "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+", "+
            ProyectoDBMetadata.TABLA_PRIORIDAD + " "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+", "+
            ProyectoDBMetadata.TABLA_TAREAS + " "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+
            " WHERE "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+"."+ProyectoDBMetadata.TablaProyectoMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE+" = "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD+" = "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = ?";

    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;

    public ProyectoDAO(Context c){
        this.dbHelper = new ProyectoOpenHelper(c);
    }

    public void open(){
        this.open(false);
    }

    public void open(Boolean toWrite){
        if(toWrite) {
            db = dbHelper.getWritableDatabase();
        }
        else{
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close(){
        db = dbHelper.getReadableDatabase();
    }

    public Cursor listaTareas(Integer idProyecto){
        Cursor cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        Integer idPry= 0;
        if(cursorPry.moveToFirst()){
            idPry=cursorPry.getInt(0);
        }
        cursorPry.close();
        Cursor cursor = null;
        Log.d("LAB05-MAIN","PROYECTO : _"+idPry.toString()+" - "+ _SQL_TAREAS_X_PROYECTO);
        cursor = db.rawQuery(_SQL_TAREAS_X_PROYECTO,new String[]{idPry.toString()});
        return cursor;
    }

    public void nuevaTarea(Tarea t){

        final String table = ProyectoDBMetadata.TABLA_TAREAS;
        final ContentValues contentValues = contentValuesFromTarea(t);

        open(true);
        db.insert(table, null, contentValues);
        close();
    }

    public void guardarTarea(Tarea t){

        final String table = ProyectoDBMetadata.TABLA_TAREAS;
        final ContentValues contentValues = contentValuesFromTarea(t);
        final String whereClause = ProyectoDBMetadata.TablaTareasMetadata._ID + "=?";
        final String[] whereArgs = { t.getId().toString() };

        open(true);
        db.update(table, contentValues, whereClause, whereArgs);
        close();
    }

    public void borrarTarea(Tarea t){

    }

    public List<Prioridad> listarPrioridades(){
        return null;
    }

    public List<Usuario> listarUsuarios(){
        return null;
    }

    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_id=?", new String[]{idTarea.toString()});
    }

    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMaximoMinutos) {
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
        return null;
    }

    public @Nullable Tarea obtenerTarea(Integer idTarea) {

        final String table = ProyectoDBMetadata.TABLA_TAREAS;
        final String[] columns = {
                ProyectoDBMetadata.TablaTareasMetadata.TAREA,
                ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,
                ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,
                ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,
                ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,
                ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,
                ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE
        };

        final String selection = ProyectoDBMetadata.TablaTareasMetadata._ID + "=?";
        final String[] selectionArgs = { idTarea.toString() };


        open(false);
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);

        boolean hasFirst = cursor.moveToFirst();
        if(!hasFirst) return null;

        Tarea ret = new Tarea(
                idTarea,
                cursor.getString(0),
                cursor.getInt(1),
                cursor.getInt(2),
                (cursor.getInt(3) == 1),
                null, //Safe to assume that there's only one Proyecto (see assignment description)
                null, //Unused for now
                obtenerUsuario(cursor.getInt(6))
        );

        close();

        return ret;
    }

    static private ContentValues contentValuesFromTarea(Tarea tarea) {

        ContentValues ret = new ContentValues();

        ret.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA, tarea.getDescripcion());
        ret.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS, tarea.getHorasEstimadas());
        ret.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS, tarea.getMinutosTrabajados());
        ret.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA, tarea.getFinalizada());
        //ret.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO, t.getProyecto().getId()); //Safe to assume that there's only one Proyecto (see assignment description)
        //ret.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD, t.getPrioridad().getId()); //Unused for now
        ret.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE, tarea.getResponsable().getId());

        return ret;
    }

    public Cursor listaUsuarios() { //Works like the provided listaTareas, but for USUARIOS rows
        String sql = "SELECT " +
                ProyectoDBMetadata.TablaUsuariosMetadata._ID + " AS _id, " + //Rename the ID column to '_id' so as not to surprise CursorAdapters
                ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO + ", " +
                ProyectoDBMetadata.TablaUsuariosMetadata.MAIL + " FROM " + ProyectoDBMetadata.TABLA_USUARIOS;

        open(false);

        return db.rawQuery(sql, null);
    }


    public @Nullable Usuario obtenerUsuario(Integer idUsuario) {

        final String table = ProyectoDBMetadata.TABLA_USUARIOS;
        final String[] columns = {
                ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO,
                ProyectoDBMetadata.TablaUsuariosMetadata.MAIL
        };

        final String selection = ProyectoDBMetadata.TablaUsuariosMetadata._ID + "=?";
        final String[] selectionArgs = { idUsuario.toString() };


        open(false);
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);

        boolean hasFirst = cursor.moveToFirst();
        if(!hasFirst) return null;

        Usuario ret = new Usuario(
                        idUsuario,
                        cursor.getString(0),
                        cursor.getString(1)
        );

        close();

        return ret;
    }

}
