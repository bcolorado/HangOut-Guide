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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.hangout.guide.R
import io.hangout.guide.utils.AuthManager
import io.hangout.guide.utils.AuthRes
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForgotPasswordScreen(
    onNavigateLogin: () -> Unit,
    auth: AuthManager
) {
    var email by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
            Spacer(modifier = Modifier.height(40.dp))

            // Título
            Text(
                text = "Recuperar contraseña",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar recuperación
            Button(
                onClick = {
                    scope.launch{
                        sendEmail(email, context, auth, onNavigateLogin)

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(text = "Enviar enlace", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlaces de Olvidar contraseña y Registrarse
            TextButton(onClick = { onNavigateLogin() }) {
                Text("¿Ya tienes una cuenta? inicia sesión", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

private suspend fun sendEmail(email: String, context: Context, auth: AuthManager, onNavigateLogin: () -> Unit) {
    if(email.isNotEmpty()){
        when(val result = auth.resetPassword(email)){
            is AuthRes.Success -> {
                Toast.makeText(context, "Correo enviado", Toast.LENGTH_SHORT).show()
                onNavigateLogin()
            }
            is AuthRes.Error -> {
                Toast.makeText(context, "Error enviando el correo", Toast.LENGTH_SHORT).show()
            }
        }

    }else{
        Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_LONG).show()
    }
}
