package com.example.medilink_compose.BD_Files

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(
    context: Context,
    s: String,
    nothing: Nothing?,
    i: Int
) : SQLiteOpenHelper(context, "MediLink.db", null, 4) {

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

        db?.execSQL(
            """
            CREATE TABLE doctores (
                id_doctor INTEGER PRIMARY KEY AUTOINCREMENT,
                
                -- Datos personales
                nombre_doc TEXT,
                apellido_doc TEXT,
                fechaNacimiento_doc DATE,
                sexo_doc TEXT,
                cedula_doc TEXT,
                estadoCivil_doc TEXT,
                estado_doc TEXT,
                
                -- Datos de contacto
                celular_doc TEXT,
                telefono_doc TEXT,
                correo_doc TEXT,
                direccion_doc TEXT,
                
                -- Contacto adicional
                nombreContacto_doc TEXT,
                celularContacto_doc TEXT,
                cedulaContacto_doc TEXT,
                
                -- Datos profesionales
                matricula_doc TEXT,
                horaEntrada_doc TIME,
                horaSalida_doc TIME
            )
            """.trimIndent()
        )

        db?.execSQL(""" 
            CREATE TABLE citas (
                id_cita INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_cita DATE NOT NULL,
                hora_cita TIME,
                estado_cita TEXT,
                
                --Datos doctor
                id_doctor INTEGER NOT NULL,
                nombre_doc TEXT,
                apellido_doc TEXT,
                
                --Datos paciente
                id_paciente INTEGER NOT NULL,
                nombre_paciente TEXT,
                apellido_paciente TEXT,
                
                FOREIGN KEY(id_doctor) REFERENCES doctores(id_doctor) ON DELETE CASCADE,
                FOREIGN KEY(id_paciente) REFERENCES pacientes(id) ON DELETE CASCADE
            )

        """.trimIndent())


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val tablas = listOf("pacientes", "usuarios", "doctores", "citas") // Agregá aquí todas las tablas nuevas que vayas creando
        tablas.forEach { tabla ->
            db?.execSQL("DROP TABLE IF EXISTS $tabla")
        }
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

}


