package com.example.medilink_compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation (modifier: Modifier = Modifier){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login"){
            LoginActivity(modifier, navController)
        }

        composable("paciente"){
            RegistrarPacienteActivity(modifier, navController)
        }
    }

}