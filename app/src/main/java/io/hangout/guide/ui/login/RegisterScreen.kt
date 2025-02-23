package io.hangout.guide.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.hangout.guide.R
import io.hangout.guide.ui.profile.ProfileViewModel
import io.hangout.guide.utils.AuthManager
import io.hangout.guide.utils.AuthRes
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterScreen(onNavigateLogin: () -> Unit, auth: AuthManager, profileViewModel: ProfileViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            Spacer(modifier = Modifier.height(200.dp))

            // Título
            Text(
                text = "Registro cuenta nueva",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de texto
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campos de texto
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de registrar
            Button(
                onClick = {
                    scope.launch{
                        signUp(name, email, password, confirmPassword, auth, context, onNavigateLogin, profileViewModel)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(text = "Registrar", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlaces de Olvidar contraseña y Registrarse
            TextButton(onClick = { onNavigateLogin() }) {
                Text("¿Ya tienes una cuenta? inicia sesión", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

private suspend fun signUp(name: String, email: String, password: String, confirmPassword: String, auth: AuthManager, context: Context, onNavigateLogin: () -> Unit, profileViewModel: ProfileViewModel) {
    if(email.isNotEmpty() && password.isNotEmpty() && makeValidations(password, confirmPassword, context)){

        when(val result = auth.createUserWithEmailAndPassword(email, password)){
            is AuthRes.Success -> {
                result.data?.let { authRes ->
                    profileViewModel.createUserProfile(authRes.uid, name, email, null)
                }
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                onNavigateLogin()
            }
            is AuthRes.Error -> {
                Toast.makeText(context, "Error en el registro", Toast.LENGTH_SHORT).show()
            }
        }

    }else{
        Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_LONG).show()
    }
}

fun makeValidations(password: String, confirmPassword: String, context: Context): Boolean{
    if (password != confirmPassword){
        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
        return false
    }

    return true
}
