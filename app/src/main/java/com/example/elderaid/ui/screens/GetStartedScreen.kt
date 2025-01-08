package com.example.elderaid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.elderaid.R

@Composable
fun GetStartedScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF5722)) // Turuncu arka plan
    ) {
        // Yamuk gri alan
        /*Image(
            painter = painterResource(id = R.drawable.rectangle_5),
            contentDescription = "Background Shape",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp) // Görselin boyutunu ayarlayın
        )*/

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(width = 235.dp, height = 271.dp)
                    .offset(y = -60.dp)
            )

            Spacer(modifier = Modifier.height(300.dp)) // Logo ile buton arası boşluk

            // Get Started Butonu
            Button(
                onClick = {
                    navController.navigate("login")
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .size(width = 287.dp, height = 77.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alt açıklama metni
            Text(
                text = "A hand reaching out to the elderly with love...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = 60.dp)
                    .size(width = 314.dp, height = 42.dp)
            )
        }
    }
}
