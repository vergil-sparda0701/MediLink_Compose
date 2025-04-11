package com.example.medilink_compose.BD_Files

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(
    context: Context,
    s: String,
    nothing: Nothing?,
    i: Int
) : SQLiteOpenHelper(context, "MediLink.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE pacientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                
                -- Datos personales
                nombre TEXT,
                apellido TEXT,
                fechaNacimiento DATE,
                sexo TEXT,
                cedula TEXT,
                estadoCivil TEXT,
                estado TEXT,
                
                -- Datos de contacto
                celular TEXT,
                telefono TEXT,
                correo TEXT,
                direccion TEXT,
                
                -- Contacto adicional
                nombreContacto TEXT,
                celularContacto TEXT,
                cedulaContacto TEXT,
                
                -- Datos médicos
                tipoSangre TEXT,
                condicionMedica TEXT
            )
            """.trimIndent()
        )

        db?.execSQL(
            """
                Create table usuarios (
                id_Usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                
                -- Datos personales
                usuario TEXT,
                clave TEXT,
                rol Text
                )
             """.trimIndent()
        )


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS pacientes")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

}


