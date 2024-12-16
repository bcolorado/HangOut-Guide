package io.hangout.guide.ui.login

import android.content.Context
import android.widget.Toast
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
import io.hangout.guide.ui.theme.outlinedTextFieldColors
import io.hangout.guide.utils.AuthRes
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(onNavigateLogin: () -> Unit, auth: AuthManager) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                .height(300.dp)
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
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de texto
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = outlinedTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = outlinedTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = outlinedTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de registrar
        Button(
            onClick = {
                scope.launch{
                    signUp(email, password, confirmPassword, auth, context, onNavigateLogin)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
        ) {
            Text(text = "Registrar", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlaces de Olvidar contraseña y Registrarse
        TextButton(onClick = { onNavigateLogin() }) {
            Text("¿Ya tienes una cuenta? inicia sesión", color = Color(0xFF3F51B5))
        }
    }
}

private suspend fun signUp(email: String, password: String, confirmPassword: String, auth: AuthManager, context: Context, onNavigateLogin: () -> Unit) {
    if(email.isNotEmpty() && password.isNotEmpty() && makeValidations(password, confirmPassword, context)){

        when(val result = auth.createUserWithEmailAndPassword(email, password)){
            is AuthRes.Success -> {
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
