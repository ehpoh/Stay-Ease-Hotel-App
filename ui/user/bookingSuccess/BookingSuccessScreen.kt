package com.example.stayeasehotel.ui.user.bookingSuccess

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.stayeasehotel.R

@Composable
fun BookingSuccessScreen(
    onBackToHome: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.dp_16)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painterResource(R.drawable.green_tick_vector_png_picture),
            contentDescription = stringResource(R.string.successful_booking),
            modifier = Modifier.size(dimensionResource(R.dimen.dp_180))
        )

        Text(
            text = stringResource(R.string.booking_sent_message),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.staff_approval_message),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16)))

        // Back to Home Button
        Button(
            onClick = { onBackToHome() },
            shape = RoundedCornerShape(dimensionResource(R.dimen.dp_12)),
            modifier = Modifier
                .width(dimensionResource(R.dimen.dp_180))
        ) {
            Text(
                stringResource(R.string.back_home),
                fontSize = dimensionResource(R.dimen.sp_20).value.sp
            )
        }
    }
}