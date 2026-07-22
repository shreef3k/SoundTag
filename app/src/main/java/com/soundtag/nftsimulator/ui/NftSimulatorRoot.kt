package com.soundtag.nftsimulator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.soundtag.nftsimulator.domain.model.NftAvatar

@Composable
fun NftSimulatorRoot(viewModel: NftViewModel = viewModel()) {
    val nav = rememberNavController()
    val avatars by viewModel.avatars.collectAsState()
    val active = avatars.firstOrNull { it.isActive } ?: avatars.firstOrNull()
    Scaffold(
        bottomBar = {
            val route = nav.currentBackStackEntryAsState().value?.destination?.route
            NavigationBar {
                listOf("chats" to Icons.Default.Chat, "contacts" to Icons.Default.Contacts, "settings" to Icons.Default.Settings).forEach { (screen, icon) ->
                    NavigationBarItem(selected = route == screen, onClick = { nav.navigate(screen) }, icon = { Icon(icon, null) }, label = { Text(screen.replaceFirstChar { it.titlecase() }) })
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = "chats", modifier = Modifier.padding(padding)) {
            composable("chats") { ChatListScreen(active) }
            composable("contacts") { ContactsScreen(active) }
            composable("settings") { SettingsScreen(active) { nav.navigate("nft") } }
            composable("nft") { NftListScreen(avatars, viewModel::setActive, viewModel::delete, viewModel::save) }
        }
    }
}

@Composable
private fun TelegramTopBar(title: String, active: NftAvatar?) {
    Row(Modifier.fillMaxWidth().background(Color(0xFF2AABEE)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Avatar(active, 44)
        Spacer(Modifier.width(12.dp))
        Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ChatListScreen(active: NftAvatar?) = Column {
    TelegramTopBar("Chats", active)
    LazyColumn { items(listOf("Saved Messages", "NFT Collectors", "Local Bot", "Design Chat")) { ChatRow(it, active) } }
}

@Composable
private fun ContactsScreen(active: NftAvatar?) = Column {
    TelegramTopBar("Contacts", active)
    LazyColumn { items(listOf("Alice", "Bob", "NFT Support", "Ayugram Friend")) { ChatRow(it, active) } }
}

@Composable
private fun SettingsScreen(active: NftAvatar?, openNft: () -> Unit) = Column {
    TelegramTopBar("Settings", active)
    ListItem(headlineContent = { Text("Profile") }, supportingContent = { Text(active?.name ?: "No active NFT") }, leadingContent = { Avatar(active, 52) })
    ListItem(Modifier.clickable(onClick = openNft), headlineContent = { Text("Мои NFT") }, supportingContent = { Text("Create, edit and select local NFT avatars") }, leadingContent = { Icon(Icons.Default.Star, null, tint = Color(0xFF2AABEE)) })
}

@Composable
private fun ChatRow(title: String, active: NftAvatar?) = ListItem(
    headlineContent = { Text(title) },
    supportingContent = { Text("Local Telegram-style interaction") },
    leadingContent = { Avatar(active, 48) },
    trailingContent = { Text("now", color = Color.Gray) }
)

@Composable
private fun NftListScreen(avatars: List<NftAvatar>, setActive: (String) -> Unit, delete: (String) -> Unit, save: (String?, String, Long, String, String, String) -> Unit) {
    var edited by remember { mutableStateOf<NftAvatar?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    Column {
        Row(Modifier.fillMaxWidth().background(Color(0xFF2AABEE)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Мои NFT", color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = { edited = null; showEditor = true }) { Icon(Icons.Default.Add, null, tint = Color.White) }
        }
        LazyColumn { items(avatars, key = { it.id }) { nft ->
            ListItem(
                headlineContent = { Text(nft.name) },
                supportingContent = { Text(nft.properties.ifBlank { "No properties" }) },
                leadingContent = { Avatar(nft, 52) },
                trailingContent = { Row { IconButton({ setActive(nft.id) }) { Icon(if (nft.isActive) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, null) }; IconButton({ edited = nft; showEditor = true }) { Icon(Icons.Default.Edit, null) }; IconButton({ delete(nft.id) }) { Icon(Icons.Default.Delete, null) } } }
            )
        } }
    }
    if (showEditor) NftEditorDialog(edited, onDismiss = { showEditor = false }) { id, name, color, icon, props, url -> save(id, name, color, icon, props, url); showEditor = false }
}

@Composable
private fun NftEditorDialog(nft: NftAvatar?, onDismiss: () -> Unit, onSave: (String?, String, Long, String, String, String) -> Unit) {
    var name by remember { mutableStateOf(nft?.name ?: "") }
    var icon by remember { mutableStateOf(nft?.icon ?: "💎") }
    var color by remember { mutableLongStateOf(nft?.color ?: 0xFF2AABEE) }
    var props by remember { mutableStateOf(nft?.properties ?: "rarity=custom") }
    var url by remember { mutableStateOf(nft?.imageUrl ?: "") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (nft == null) "Create NFT" else "Edit NFT") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Name") })
            OutlinedTextField(icon, { icon = it.take(2) }, label = { Text("Icon/emoji") })
            OutlinedTextField(props, { props = it }, label = { Text("Properties") })
            OutlinedTextField(url, { url = it }, label = { Text("Telegram image URL") })
            Row { listOf(0xFF2AABEE, 0xFFFFC107, 0xFF8E24AA, 0xFF43A047).forEach { c -> Box(Modifier.size(36.dp).padding(4.dp).clip(CircleShape).background(Color(c)).clickable { color = c }) } }
        }
    }, confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = { onSave(nft?.id, name, color, icon, props, url) }) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun Avatar(nft: NftAvatar?, size: Int) {
    Box(Modifier.size(size.dp).clip(CircleShape).background(Color(nft?.color ?: 0xFFB0BEC5)), contentAlignment = Alignment.Center) {
        if (!nft?.imageUrl.isNullOrBlank()) AsyncImage(model = nft!!.imageUrl, contentDescription = nft.name, modifier = Modifier.fillMaxSize())
        else Text(nft?.icon ?: "👤", style = MaterialTheme.typography.titleLarge)
    }
}
