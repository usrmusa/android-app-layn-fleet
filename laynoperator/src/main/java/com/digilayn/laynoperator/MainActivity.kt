package com.digilayn.laynoperator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.digilayn.laynfleet.core.auth.FirebaseAuthService
import com.digilayn.laynfleet.core.domain.Products
import com.digilayn.laynfleet.core.ui.LaynFleetFlow
import com.digilayn.laynfleet.core.ui.theme.LaynFleetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaynFleetTheme {
                LaynFleetFlow(
                    product = Products.Operator,
                    googleServerClientId = getString(R.string.default_web_client_id),
                    authService = FirebaseAuthService(),
                ) { snapshot, membership ->
                    OperatorDashboard(snapshot, membership)
                }
            }
        }
    }
}
