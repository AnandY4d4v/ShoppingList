package com.example.shoppinglist

import android.widget.Button as AndroidButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: String,
    var isEditing: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    var sItems by remember { mutableStateOf(ShoppingItemsManager.getShoppingItems()) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val customFontFamily = FontFamily(Font(R.font.prata_regular))

    // Function to save items when they change
    LaunchedEffect(sItems) {
        ShoppingItemsManager.saveShoppingItems(sItems)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Add background image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Add heading
            Text(
                text = "Grocery List",
                fontSize = 24.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )

            AndroidView(
                factory = { context ->
                    AndroidButton(context).apply {
                        text = "Add Item"
                        setBackgroundResource(R.drawable.btnshape)
                        setOnClickListener { showDialog = true }
                    }
                },
                update = { view ->
                    view.setOnClickListener { showDialog = true }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(sItems) { item ->
                    if (item.isEditing) {
                        ShoppingItemEditor(
                            item = item,
                            onEditComplete = { editedName, editedQuantity ->
                                sItems = sItems.map { currentItem ->
                                    if (currentItem.id == item.id) {
                                        currentItem.copy(
                                            isEditing = false,
                                            name = editedName,
                                            quantity = editedQuantity
                                        )
                                    } else {
                                        currentItem.copy(isEditing = false)
                                    }
                                }
                            }
                        )
                    } else {
                        ShoppingListItem(
                            item = item,
                            onEditClick = { editedItem ->
                                sItems = sItems.map { currentItem ->
                                    if (currentItem.id == editedItem.id) {
                                        currentItem.copy(isEditing = true)
                                    } else {
                                        currentItem.copy(isEditing = false)
                                    }
                                }
                            },
                            onDeleteClick = { deletedItem ->
                                sItems = sItems.filter { it.id != deletedItem.id }
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Add Shopping Item") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = itemName,
                                    onValueChange = { itemName = it },
                                    label = { Text("Item Name") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )

                                OutlinedTextField(
                                    value = itemQuantity,
                                    onValueChange = { itemQuantity = it },
                                    label = { Text("Quantity") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )
                            }
                        },
                        confirmButton = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AndroidView(
                                    factory = { context ->
                                        AndroidButton(context).apply {
                                            text = "Add"
                                            setBackgroundResource(R.drawable.btnshape)
                                            setOnClickListener {
                                                if (itemName.isNotBlank()) {
                                                    val newItem = ShoppingItem(
                                                        id = (sItems.maxByOrNull { it.id }?.id ?: 0) + 1,
                                                        name = itemName,
                                                        quantity = itemQuantity
                                                    )
                                                    sItems = sItems + newItem
                                                    showDialog = false
                                                    itemName = ""
                                                    itemQuantity = ""
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                AndroidView(
                                    factory = { context ->
                                        AndroidButton(context).apply {
                                            text = "Cancel"
                                            setBackgroundResource(R.drawable.btnshape)
                                            setOnClickListener { showDialog = false }
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: (ShoppingItem) -> Unit,
    onDeleteClick: (ShoppingItem) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.White)
            .border(
                border = BorderStroke(3.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(3)
            )
            .background(Color.White.copy(alpha = 0.7f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                modifier = Modifier.padding(20.dp),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Qty: ${item.quantity}",
                modifier = Modifier.padding(20.dp),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(
                onClick = { onEditClick(item) },
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.Black)
            }
            IconButton(
                onClick = { onDeleteClick(item) },
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Black)
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, String) -> Unit) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(2.dp, Color(0XFF018786), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(8.dp))

            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AndroidView(
                factory = { context ->
                    AndroidButton(context).apply {
                        text = "Save"
                        setBackgroundResource(R.drawable.btnshape)
                        setOnClickListener {
                            onEditComplete(editedName, editedQuantity)
                        }
                    }
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun ShoppingListPreview() {
    ShoppingListApp()
}
