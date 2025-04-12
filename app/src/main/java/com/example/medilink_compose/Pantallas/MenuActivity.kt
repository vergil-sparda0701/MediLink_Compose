package com.example.medilink_compose.Pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medilink_compose.BD_Files.UsuarioViewModel
import com.example.medilink_compose.R

@Composable
fun MenuActivity(modifier: Modifier, navController: NavHostController, usuarioViewModel: UsuarioViewModel) {

    val scrollState = rememberScrollState()


    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home, "Menu"),
        NavItem("Historial", Icons.Default.Refresh, "historial"),
        NavItem("Config", Icons.Default.Settings, "")
    )

    Scaffold(

        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF00A9B0),
                contentColor = Color.White
            ) {
                NavigationBar(containerColor = Color(0xFF00A9B0)) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate(item.route) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(item.label)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = Color(0xFF00777A)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Menu Principal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.menuicon),
                contentDescription = "Imagen menu",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("paciente") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff00a9b0))
                    ) {
                        Text("Registrar paciente")
                    }

                    Button(
                        onClick = { navController.navigate("doctor") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff00a9b0))
                    ) {
                        Text("Registrar doctor")
                    }

                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff00a9b0))
                    ) {
                        Text("Registrar cita")
                    }

                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff00a9b0))
                    ) {
                        Text("Citas pendientes")
                    }
                }
            }
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector, val route: String)
