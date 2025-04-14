package com.example.medilink_compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medilink_compose.BD_Files.UsuarioViewModel
import com.example.medilink_compose.Pantallas.CitasPendientesActivity
import com.example.medilink_compose.Pantallas.HistorialActivity
import com.example.medilink_compose.Pantallas.LoginActivity
import com.example.medilink_compose.Pantallas.MenuActivity
import com.example.medilink_compose.Pantallas.RegistrarCitaActivity
import com.example.medilink_compose.Pantallas.RegistrarDoctorActivity
import com.example.medilink_compose.Pantallas.RegistrarPacienteActivity
import com.example.medilink_compose.Pantallas.UsuarioActivity

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation (modifier: Modifier = Modifier){

    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login"){
            LoginActivity(modifier, navController, usuarioViewModel = usuarioViewModel)
        }

        composable("Menu"){
            MenuActivity(modifier, navController, usuarioViewModel = usuarioViewModel)
        }

        composable("usuario"){
            UsuarioActivity(modifier, navController)
        }

        composable("paciente"){
            RegistrarPacienteActivity(modifier, navController)
        }

        composable("historial"){
            HistorialActivity(modifier, navController)
        }

        composable("doctor"){
            RegistrarDoctorActivity(modifier, navController)
        }

        composable("cita"){
            RegistrarCitaActivity(modifier, navController)
        }
        composable("citaPendiente"){
            CitasPendientesActivity(modifier, navController)
        }
    }

}