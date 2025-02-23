package io.hangout.guide.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.hangout.guide.utils.AuthManager
import io.hangout.guide.R
import kotlinx.coroutines.launch
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.text.style.TextAlign
import io.hangout.guide.utils.AuthRes
import io.hangout.guide.ui.theme.outlinedTextFieldColors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit,
    auth: AuthManager,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_image),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp), // Padding solo para el contenido
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(180.dp))

            // Título
            Text(
                "Bienvenido a HangOut Guide",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón iniciar sesión
            Button(
                onClick = {
                    scope.launch{
                        emailPassSignIn(email, password, auth, context, onNavigateToHome)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text("Iniciar sesión", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlaces de Olvidar contraseña y Registrarse
            TextButton(onClick = { onNavigateForgotPassword() }) {
                Text("¿Olvidaste la contraseña?", style = MaterialTheme.typography.bodyLarge)
            }

            TextButton(onClick = { onNavigateToRegister() }) {
                Text("Registrarse", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

private suspend fun emailPassSignIn(email: String, password: String, auth: AuthManager, context: Context, onNavigateToHome: () -> Unit) {
    if(email.isNotEmpty() && password.isNotEmpty()){
        when(auth.signInWithEmailAndPassword(email, password)){
            is AuthRes.Success -> {
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                onNavigateToHome()
            }
            is AuthRes.Error -> {
                Toast.makeText(context, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
            }
        }

    }else{
        Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_LONG).show()
    }
}
