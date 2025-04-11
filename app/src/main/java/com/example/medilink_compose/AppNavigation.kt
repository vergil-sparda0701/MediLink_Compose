package com.example.medilink_compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medilink_compose.Pantallas.LoginActivity
import com.example.medilink_compose.Pantallas.MenuActivity
import com.example.medilink_compose.Pantallas.RegistrarPacienteActivity
import com.example.medilink_compose.Pantallas.UsuarioActivity

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation (modifier: Modifier = Modifier){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login"){
            LoginActivity(modifier, navController)
        }

        composable("Menu"){
            MenuActivity(modifier, navController)
        }

        composable("usuario"){
            UsuarioActivity(modifier, navController)
        }

        composable("paciente"){
            RegistrarPacienteActivity(modifier, navController)
        }
    }

}