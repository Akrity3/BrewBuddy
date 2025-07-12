package com.example.brewbuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.brewbuddy.ui.theme.BrewBuddyTheme
import kotlinx.coroutines.delay
import kotlin.jvm.java

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashScreenBody()

        }
    }
}

@Composable
fun SplashScreenBody() {

    val context = LocalContext.current
    val activity = context as ComponentActivity


    val localemail = "" // to get the stored email SharedPreferences or DataStore

    LaunchedEffect(Unit)
    {
        delay(3000)
        if(localemail.toString().isEmpty()){
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            activity.finish()
        }
        else{
            val intent = Intent(context, DashboardActivity::class.java)
            context.startActivity(intent)
            activity.finish()
        }
    }









    Box(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        // background Image
        Image(
            painter = painterResource(R.drawable.splash_bg),
            contentDescription = "Splash Background",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop

        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))

        )

        // Logo
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        )
        {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp)
            )

        }
    }






}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    SplashScreenBody()
}